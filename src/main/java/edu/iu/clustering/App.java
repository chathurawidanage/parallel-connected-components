package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;

public class App {
  public static void main(String[] args) throws MPIException {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int worldSize = MPI.COMM_WORLD.getSize();
  }
}
