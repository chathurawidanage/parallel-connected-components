package edu.iu.clustering;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Multithreaded {
    private static final Logger LOG = Logger.getLogger(Multithreaded.class.getName());

    public static void main(String[] args) throws InterruptedException {
        long t1 = System.currentTimeMillis();

        int threads = 4;
        if (args.length == 1) {
            threads = Integer.parseInt(args[0]);
        }

        LOG.info("Running with " + threads + " threads.");

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        final TieBreak tieBreak = new TieBreak();

        CountDownLatch latch = new CountDownLatch(threads);

        final long[] dataloadTimes = new long[threads];
        final long[] computeTimes = new long[threads];

        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                NodePayload payload;

                if (args.length == 2) {
                    String path = args[1].replace("{rank}", threadId + "");
                    payload = GraphBuilder.buildGraphWithEdgeList(path);
                } else if (args.length == 3) {
                    String path = args[1].replace("{rank}", threadId + "");
                    String dataStructure = args[2];
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

                    payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/cora/cora-" + (threadId + 1) + ".txt");
                    //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/amazon/amazon-" + (threadId + 1) + ".txt");
                    //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/livejournal/li-" + (threadId + 1) + ".txt");
                    //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/small/small-" + (threadId + 1) + ".txt");
                }

                dataloadTimes[threadId] = System.currentTimeMillis();

                payload.compute(threadId);

                int[] nodes = payload.getNodes();
                int[] clusters = payload.getClusters();
                for (int j = 0; j < nodes.length; j++) {
                    tieBreak.syncAdd(nodes[j], clusters[j]);
                }
                latch.countDown();
                computeTimes[threadId] = System.currentTimeMillis();
            });
        }
        latch.await();
        long t2 = System.currentTimeMillis();
        NodePayload compute = tieBreak.compute();
        Utils.printStats(compute);

        System.out.println("Time : " + (System.currentTimeMillis() - t1));
        for (int i = 0; i < threads; i++) {
            System.out.println("Thread " + i);
            System.out.println("\tData Load Times : " + (dataloadTimes[i] - t1));
            System.out.println("\tThread Compute Times : " + (computeTimes[i] - dataloadTimes[i]));
        }
        System.out.println("Tie break time : " + (System.currentTimeMillis() - t2));

        executorService.shutdown();
    }
}
