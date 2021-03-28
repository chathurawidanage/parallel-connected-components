package edu.iu.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodePayload {

    private int[] nodes;
    private int[] clusters;
    private int[][] graph;

    public NodePayload(int[] nodes, int[] clusters, int[][] graph) {
        this.nodes = nodes;
        this.clusters = clusters;
        this.graph = graph;
    }


    public void compute() {
        boolean[] clusteredNodes = new boolean[this.nodes.length];
        Arrays.fill(clusteredNodes, false);
        int clusterId = 1;
        for (int i = 0; i < clusteredNodes.length; i++) {
            if (!clusteredNodes[i]) {
                boolean[] visited = this.runBFS(i);
                for (int j = 0; j < visited.length; j++) {
                    if (visited[j]) {
                        this.clusters[j] = clusterId;
                        clusteredNodes[j] = true;
                    }
                }
                clusterId++;
            }

        }
    }


    public int[] getNodes() {
        return nodes;
    }

    public int[] getClusters() {
        return clusters;
    }

    private boolean[] runBFS(int start) {
        boolean[] visited = new boolean[this.nodes.length];
        Arrays.fill(visited, false);
        List<Integer> q = new ArrayList<>();
        q.add(start);
        // Set source as visited
        visited[start] = true;

        int vis;
        while (!q.isEmpty()) {
            vis = q.get(0);

            // Print the current node
            q.remove(q.get(0));

            // For every adjacent vertex to
            // the current vertex
            for (int i = 0; i < this.nodes.length; i++) {
                if (this.graph[vis][i] == 1 && (!visited[i])) {

                    // Push the adjacent node to
                    // the queue
                    q.add(i);

                    // Set
                    visited[i] = true;
                }
            }
        }
        return visited;

    }


}
