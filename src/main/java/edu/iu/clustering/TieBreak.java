package edu.iu.clustering;

import java.util.*;

public class TieBreak {

    private HashMap<Integer, List<Integer>> nodes = new HashMap<>();
    private Set<Integer> clusters = new HashSet<>();

    public void add(int node, int cluster) {
        this.nodes.computeIfAbsent(node, n -> new ArrayList<>()).add(cluster);
        this.clusters.add(cluster);
    }

    public synchronized void syncAdd(int node, int cluster) {
        nodes.computeIfAbsent(node, n -> new ArrayList<>()).add(cluster);
    }

    public void compute() {
        List<Set<Integer>> clusterSets = new ArrayList<>();

        int[][] clusterGraph = new int[clusters.size()][clusters.size()];

    }
}
