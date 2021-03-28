package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shuffle {

  private int getTag(int w1, int w2) {
    ByteBuffer tagBuffer = ByteBuffer.allocate(4);
    return -1;
  }

  public NodePayload shuffle(NodePayload localGraph) throws MPIException {
    int worldSize = MPI.COMM_WORLD.getSize();
    int thisWorker = MPI.COMM_WORLD.getRank();

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

    // now communicate sizes
    for (int i = 0; i < worldSize; i++) {
      if (i != thisWorker) {
//        MPI.COMM_WORLD.iSend(
//            IntBuffer.allocate(1).put(partitions[i].size()), 1, MPI.INT, i, );
      }
    }

    return null;
  }
}
