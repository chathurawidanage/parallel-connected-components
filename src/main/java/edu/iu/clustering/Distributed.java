package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;

import java.io.File;
import java.util.logging.Logger;

public class Distributed {

    private static Logger LOG = Logger.getLogger(Distributed.class.getName());

    public static void main(String[] args) throws MPIException {
        //LogManager.getLogManager().reset();

        long t1 = System.currentTimeMillis();
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int worldSize = MPI.COMM_WORLD.getSize();
        LOG.info("Starting worker " + rank + " of " + worldSize);

        File currentDirFile = new File(".");
        String root = currentDirFile.getAbsolutePath();

//        NodePayload payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/cora/cora-" + (rank + 1) + ".txt");
        //NodePayload payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/amazon/amazon-" + (rank + 1) + ".txt");
        NodePayload payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/livejournal/li-" + (rank + 1) + ".txt");
        //NodePayload payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/small/small-" + (rank + 1) + ".txt");
        payload.compute(rank);

        //System.out.println(payload);


        Shuffle shuffle = new Shuffle();
        NodePayload shuffledPayload = shuffle.shuffle(payload);

        MPI.COMM_WORLD.barrier();
        //Utils.printStats(shuffledPayload);

        System.out.println("Time : " + (System.currentTimeMillis() - t1));
        MPI.Finalize();
    }


}
