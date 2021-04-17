package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;
import mpi.Request;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.logging.Logger;

public class Shuffle {

    private static final Logger LOG = Logger.getLogger(Shuffle.class.getName());

    private void waitForCompletion(List<Request> pendingRequests) throws MPIException {
        for (Request pendingRequest : pendingRequests) {
            pendingRequest.waitFor();
        }
        pendingRequests.clear();
    }

    public NodePayload shuffle(NodePayload localGraph) throws MPIException {
        int worldSize = MPI.COMM_WORLD.getSize();
        int thisWorker = MPI.COMM_WORLD.getRank();

        // todo replace with primitive arrays if there is a significant impact on performance
        List<Integer>[] partitions = new List[worldSize];
        for (int i = 0; i < worldSize; i++) {
            partitions[i] = new ArrayList<>();
        }

        int[] nodes = localGraph.getNodes();
        int[] clusters = localGraph.getClusters();

        TieBreak localNodeTieBreak = new TieBreak();

        for (int i = 0; i < nodes.length; i++) {
            int destination = nodes[i] % worldSize;
            if (destination != thisWorker) {
                partitions[destination].add(nodes[i]);
                partitions[destination].add(clusters[i]);
            } else {
                // build Map
                localNodeTieBreak.add(nodes[i], clusters[i]);
            }
        }

        ByteBuffer[] recvBuffers = new ByteBuffer[worldSize];

        List<Request> pendingRequests = new ArrayList<>();

        // now communicate sizes
        for (int i = 0; i < worldSize; i++) {
            if (i != thisWorker) {
                Request sendReq = MPI.COMM_WORLD.iSend(
                    ByteBuffer.allocateDirect(Integer.SIZE).putInt(partitions[i].size()), 1, MPI.INT, i, thisWorker);
                recvBuffers[i] = ByteBuffer.allocateDirect(Integer.SIZE);
                Request recvReq = MPI.COMM_WORLD.iRecv(recvBuffers[i], 1, MPI.INT, i, i);
                pendingRequests.add(sendReq);
                pendingRequests.add(recvReq);
            }
        }

        // wait for communication
        waitForCompletion(pendingRequests);

        Map<Integer, Integer> recvSizes = new HashMap<>();

        // now we have the sizes
        for (int i = 0; i < worldSize; i++) {
            if (i != thisWorker) {
                int size = recvBuffers[i].getInt();
                recvSizes.put(i, size);
                LOG.info("Worker " + i + " will send " + size + " to worker " + thisWorker);
                ByteBuffer sendingBuffer = ByteBuffer.allocateDirect(Integer.SIZE * partitions[i].size());
                for (Integer integer : partitions[i]) {
                    sendingBuffer.putInt(integer);
                }
                Request sendReq = MPI.COMM_WORLD.iSend(sendingBuffer, partitions[i].size(), MPI.INT, i, thisWorker);
                recvBuffers[i] = ByteBuffer.allocateDirect(Integer.SIZE * size);

                Request recvReq = MPI.COMM_WORLD.iRecv(recvBuffers[i], size, MPI.INT, i, i);
                pendingRequests.add(sendReq);
                pendingRequests.add(recvReq);
            }
        }

        this.waitForCompletion(pendingRequests);

        // adding incoming labels to the tie break
        for (int i = 0; i < worldSize; i++) {
            if (i != thisWorker) {
                IntBuffer buffer = recvBuffers[i].asIntBuffer();
                for (int j = 0; j < recvSizes.get(i) / 2; j++) {
                    int node = buffer.get();
                    int part = buffer.get();
                    localNodeTieBreak.add(node, part);
                }
            }
        }

        // now do the tie break
        NodePayload intermediateGroups = localNodeTieBreak.computeGroups(thisWorker);

        LOG.info("Intermediate groups computed.");

        // each node send node 1 how many data will be sent
        pendingRequests.clear();
        if (thisWorker == 0) {
            for (int i = 1; i < worldSize; i++) {
                recvBuffers[i] = ByteBuffer.allocateDirect(Integer.SIZE);
                pendingRequests.add(MPI.COMM_WORLD.iRecv(recvBuffers[i], 1, MPI.INT, i, i));
            }
        } else {
            pendingRequests.add(MPI.COMM_WORLD.iSend(
                ByteBuffer.allocateDirect(Integer.SIZE).putInt(intermediateGroups.getNodes().length),
                1,
                MPI.INT,
                0,
                thisWorker
            ));
        }
        this.waitForCompletion(pendingRequests);

        pendingRequests.clear();
        recvSizes.clear();

        TieBreak finalLabelTrieBreak = new TieBreak();
        int[] labels = intermediateGroups.getNodes();
        int[] labelGroup = intermediateGroups.getClusters();

        LOG.info("Exchanging intermediate group information...");

        if (thisWorker == 0) {
            for (int i = 1; i < worldSize; i++) {
                int size = recvBuffers[i].getInt() * 2;// for label and group
                recvSizes.put(i, size);
                recvBuffers[i] = ByteBuffer.allocateDirect(Integer.SIZE * size);
                pendingRequests.add(MPI.COMM_WORLD.iRecv(recvBuffers[i], size, MPI.INT, i, i));
            }

            for (int i = 0; i < labels.length; i++) {
                finalLabelTrieBreak.add(labels[i], labelGroup[i]);
            }
        } else {
            int sendSize = 2 * labels.length;

            ByteBuffer sendBuffer = ByteBuffer.allocateDirect(Integer.SIZE * sendSize);
            for (int i = 0; i < labels.length; i++) {
                sendBuffer.putInt(labels[i]);
                sendBuffer.putInt(labelGroup[i]);
            }

            pendingRequests.add(MPI.COMM_WORLD.iSend(
                sendBuffer,
                sendSize,
                MPI.INT,
                0,
                thisWorker
            ));
        }

        this.waitForCompletion(pendingRequests);

        LOG.info("Doing the final tie break....");

        if (thisWorker == 0) {
            for (int i = 1; i < worldSize; i++) {
                ByteBuffer recvBuffer = recvBuffers[i];
                for (int j = 0; j < recvSizes.get(i) / 2; j++) {
                    finalLabelTrieBreak.add(recvBuffer.getInt(), recvBuffer.getInt());
                }
            }
            LOG.info("Done loading data to tie break...");
            NodePayload finalNodeGroups = finalLabelTrieBreak.compute();
            Utils.printStats(finalNodeGroups);
        }


        return intermediateGroups;
    }
}
