package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;

import java.util.Random;
import java.util.logging.Logger;

public class App {

  private static Logger LOG = Logger.getLogger(App.class.getName());

  public static void main(String[] args) throws MPIException {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int worldSize = MPI.COMM_WORLD.getSize();
    LOG.info("Starting worker " + rank + " of " + worldSize);

    Random random = new Random(rank);
    int size = random.nextInt(100);

    LOG.info("Size of data in " + rank + " : " + size);
    int[] nodes = new int[size];
    int[] clusters = new int[size];
    for (int i = 0; i < size; i++) {
      nodes[i] = Math.abs(random.nextInt());
    }
    NodePayload payload = new NodePayload(nodes, clusters);

    Shuffle shuffle = new Shuffle();
    shuffle.shuffle(payload);

    MPI.Finalize();
  }
}
