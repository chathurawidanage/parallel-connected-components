package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;
import mpi.Request;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Shuffle {

  private static Logger LOG = Logger.getLogger(Shuffle.class.getName());

  /**
   * This method assumes that the world size is less than 127 and fits into a short
   *
   * @param w1 worker id of the first worker
   * @param w2 worker id of the second worker
   * @return tag for the communication between w1 and w2
   */
  public int getTag(int w1, int w2) {
    int tag = Math.min(w1, w2) | Math.max(w1, w2) << 16;
    return tag;
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

    Map<Integer, Integer> clusterMap = new HashMap<>();

    for (int i = 0; i < nodes.length; i++) {
      int destination = nodes[i] % worldSize;
      if (destination != thisWorker) {
        partitions[destination].add(nodes[i]);
        partitions[destination].add(clusters[i]);
      } else {
        // build Map
        clusterMap.put(nodes[i], clusters[i]);
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
    for (Request pendingRequest : pendingRequests) {
      pendingRequest.waitFor();
    }

    // clear old requests
    pendingRequests.clear();

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

    return null;
  }
}
