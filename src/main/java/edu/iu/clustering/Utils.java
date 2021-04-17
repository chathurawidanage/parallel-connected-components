package edu.iu.clustering;

import java.util.HashSet;
import java.util.Set;

public class Utils {
    public static void printStats(NodePayload payload) {
        int[] nodes = payload.getNodes();
        int[] clusters = payload.getClusters();

        Set<Integer> uniqueClus = new HashSet<>();
        for (int cluster : clusters) {
            uniqueClus.add(cluster);
        }

        System.out.println("Total nodes : " + nodes.length);
        System.out.println("Weakly connected components : " + uniqueClus.size());
    }
}
