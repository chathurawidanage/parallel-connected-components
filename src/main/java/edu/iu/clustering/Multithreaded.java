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
                File currentDirFile = new File(".");
                String root = currentDirFile.getAbsolutePath();
                NodePayload payload = GraphBuilder.buildGraph(root + "/src/main/resources/cora-" + threadId + ".txt");
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

        tieBreak.compute();

        System.out.println("Time : " + (System.currentTimeMillis() - t1));
        executorService.shutdown();
    }
}
