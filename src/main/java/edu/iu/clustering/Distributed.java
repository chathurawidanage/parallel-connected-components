package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Distributed {

    private static Logger LOG = Logger.getLogger(Distributed.class.getName());

    private static void run(String args[], boolean last) throws MPIException {
        long t1 = System.currentTimeMillis();
        int rank = MPI.COMM_WORLD.getRank();
        int worldSize = MPI.COMM_WORLD.getSize();
        LOG.info("Starting worker " + rank + " of " + worldSize);

        NodePayload payload;

        if (args.length == 1) {
            String path = args[0].replace("{rank}", (rank + 1) + "");
            payload = GraphBuilder.buildGraphWithEdgeList(path);

        } else if (args.length == 2) {
            String path = args[0].replace("{rank}", (rank + 1) + "");
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

        long t3 = System.currentTimeMillis();

        //System.out.println(payload);

        Shuffle shuffle = new Shuffle();
        NodePayload shuffledPayload = shuffle.shuffle(payload);

        MPI.COMM_WORLD.barrier();
        //Utils.printStats(shuffledPayload);

        String workerTag = "[" + rank + "] ";

        long t4 = System.currentTimeMillis();

        System.out.println(workerTag + "Data Load Time : " + (t2 - t1));
        System.out.println(workerTag + "Computation Time : " + (t3 - t2));
        System.out.println(workerTag + "Tie Breaking Time : " + (t4 - t3));
        System.out.println(workerTag + "Total Time : " + (t4 - t1));

        long[] timings = new long[]{
            (t2 - t1),
            (t3 - t2),
            (t4 - t3),
            (t4 - t1)
        };

        long[] minTimes = new long[timings.length];
        long[] maxTimes = new long[timings.length];
        long[] sumTimes = new long[timings.length];

        MPI.COMM_WORLD.allReduce(timings, sumTimes, timings.length, MPI.LONG, MPI.SUM);
        MPI.COMM_WORLD.allReduce(timings, maxTimes, timings.length, MPI.LONG, MPI.MAX);
        MPI.COMM_WORLD.allReduce(timings, minTimes, timings.length, MPI.LONG, MPI.MIN);

        if (rank == 0 && last) {
            try {
                Utils.logResults(
                    "distributed-min",
                    worldSize,
                    minTimes[0],
                    minTimes[1],
                    minTimes[2],
                    minTimes[3]
                );
                Utils.logResults(
                    "distributed-avg",
                    worldSize,
                    sumTimes[0] / worldSize,
                    sumTimes[1] / worldSize,
                    sumTimes[2] / worldSize,
                    sumTimes[3] / worldSize
                );
                Utils.logResults(
                    "distributed-max",
                    worldSize,
                    maxTimes[0],
                    maxTimes[1],
                    maxTimes[2],
                    maxTimes[3]
                );
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Failed to write the results to the file", e);
            }
        }
    }

    public static void main(String[] args) throws MPIException {
        //LogManager.getLogManager().reset();

        MPI.Init(args);
        for (int i = 0; i < 25; i++) {
            run(args, i == 24);
        }

        MPI.Finalize();
    }


}
