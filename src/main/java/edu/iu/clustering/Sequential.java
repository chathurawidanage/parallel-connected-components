package edu.iu.clustering;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Sequential {
  public static void main(String[] args) {
    File currentDirFile = new File(".");
    String root = currentDirFile.getAbsolutePath();
    NodePayload payload = GraphBuilder.buildGraphWithAdjMatrix(root + "/src/main/resources/cora.txt");
    payload.compute(0);

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
