package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;

import java.io.File;
import java.util.logging.Logger;

public class Distributed {

    private static Logger LOG = Logger.getLogger(Distributed.class.getName());

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int worldSize = MPI.COMM_WORLD.getSize();
        LOG.info("Starting worker " + rank + " of " + worldSize);

        File currentDirFile = new File(".");
        String root = currentDirFile.getAbsolutePath();

        NodePayload payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/cora/cora-" + (rank + 1) + ".txt");

        payload.compute(rank);


        Shuffle shuffle = new Shuffle();
        NodePayload shuffledPayload = shuffle.shuffle(payload);

        Utils.printStats(shuffledPayload);

        MPI.Finalize();
    }


}
