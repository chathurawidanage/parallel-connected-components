package edu.iu.clustering;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Multithreaded {
    public static void main(String[] args) throws InterruptedException {
        long t1 = System.currentTimeMillis();

        int threads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        final TieBreak tieBreak = new TieBreak();

        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final int threadId = i + 1;
            executorService.submit(() -> {
                NodePayload payload;

                if (args.length == 1) {
                    String path = args[0].replace("${rank}", threadId + "");
                    payload = GraphBuilder.buildGraphWithEdgeList(path);
                } else {
                    File currentDirFile = new File(".");
                    String root = currentDirFile.getAbsolutePath();

                    payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/cora/cora-" + (threadId + 1) + ".txt");
                    //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/amazon/amazon-" + (threadId + 1) + ".txt");
                    //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/livejournal/li-" + (threadId + 1) + ".txt");
                    //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/small/small-" + (threadId + 1) + ".txt");
                }

                payload.compute(threadId);

                int[] nodes = payload.getNodes();
                int[] clusters = payload.getClusters();
                for (int j = 0; j < nodes.length; j++) {
                    tieBreak.syncAdd(nodes[j], clusters[j]);
                }
                latch.countDown();
            });
        }
        latch.await();

        NodePayload compute = tieBreak.compute();
        Utils.printStats(compute);

        System.out.println("Time : " + (System.currentTimeMillis() - t1));
        executorService.shutdown();
    }
}
