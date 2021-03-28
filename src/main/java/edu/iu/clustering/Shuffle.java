package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;
import mpi.Request;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Shuffle {

  private static Logger LOG = Logger.getLogger(Shuffle.class.getName());

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

    TieBreak tieBreak = new TieBreak();

    for (int i = 0; i < nodes.length; i++) {
      int destination = nodes[i] % worldSize;
      if (destination != thisWorker) {
        partitions[destination].add(nodes[i]);
        partitions[destination].add(clusters[i]);
      } else {
        // build Map
        tieBreak.add(nodes[i], clusters[i]);
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

    // now we have the sizes
    for (int i = 0; i < worldSize; i++) {
      if (i != thisWorker) {
        int size = recvBuffers[i].getInt();
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
        tieBreak.add(buffer.get(), buffer.get());
      }
    }

    // now do the tie break
    tieBreak.compute();

    return null;
  }
}
