package edu.iu.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public static void logResults(String mode, int parallelism, long loadTime,
                                  long computeTime, long tiebreakTime, long totalTime) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("results.csv"), true));
        writer.write(String.format("%s,%d,%d,%d,%d,%d", mode, parallelism, loadTime, computeTime, tiebreakTime, totalTime));
        writer.newLine();
        writer.close();
    }
}
