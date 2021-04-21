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

        NodePayload payload;

        if (args.length == 1) {
            String path = args[0].replace("{rank}", (rank+1) + "");
            payload = GraphBuilder.buildGraphWithEdgeList(path);

        } else if (args.length == 2) {
            String path = args[0].replace("{rank}", (rank+1) + "");
            String dataStructure = args[1];
            if (dataStructure.equals("csr")) {
                payload = GraphBuilder.buildGraphWithCSR(path);
            } else if (dataStructure.equals("adj")) {
                payload = GraphBuilder.buildGraphWithAdjMatrix(path);
            } else {
                payload = GraphBuilder.buildGraphWithEdgeList(path);
            }
        } else {
            File currentDirFile = new File(".");
            String root = currentDirFile.getAbsolutePath();

            payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/cora/cora-" + (rank + 1) + ".txt");
            //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/amazon/amazon-" + (rank + 1) + ".txt");
            //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/livejournal/li-" + (rank + 1) + ".txt");
            //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/small/small-" + (rank + 1) + ".txt");
        }

        long t2 = System.currentTimeMillis();

        payload.compute(rank);

        //System.out.println(payload);

        Shuffle shuffle = new Shuffle();
        NodePayload shuffledPayload = shuffle.shuffle(payload);

        MPI.COMM_WORLD.barrier();
        //Utils.printStats(shuffledPayload);

        String workerTag = "[" + rank + "] ";

        System.out.println(workerTag + "Time : " + (System.currentTimeMillis() - t1));
        System.out.println(workerTag + "Data Load Time : " + (t2 - t1));
        System.out.println(workerTag + "Computation Time : " + (System.currentTimeMillis() - t2));
        MPI.Finalize();
    }


}
