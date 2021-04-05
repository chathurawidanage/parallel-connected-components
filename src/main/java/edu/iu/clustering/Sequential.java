package edu.iu.clustering;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Sequential {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        File currentDirFile = new File(".");
        String root = currentDirFile.getAbsolutePath();
        NodePayload payload = GraphBuilder.buildGraphWithCSR(root + "/src/main/resources/data/amazon/com-amazon.ungraph.txt");
        payload.compute(0);

        int[] nodes = payload.getNodes();
        int[] clusters = payload.getClusters();

        Set<Integer> uniqueClus = new HashSet<>();
        for (int cluster : clusters) {
            uniqueClus.add(cluster);
        }

        System.out.println("Total nodes : " + nodes.length);
        System.out.println("Weakly connected components : " + uniqueClus.size());
        System.out.println("Time : " + (System.currentTimeMillis() - t1));
    }
}
