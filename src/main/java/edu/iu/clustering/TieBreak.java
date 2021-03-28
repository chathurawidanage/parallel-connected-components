package edu.iu.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TieBreak {

  private HashMap<Integer, List<Integer>> nodes = new HashMap<>();

  public void add(int node, int cluster) {
    nodes.computeIfAbsent(node, n -> new ArrayList<>()).add(cluster);
  }

  public void compute() {

  }
}
