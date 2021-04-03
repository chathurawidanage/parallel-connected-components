package edu.iu.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodePayload {

    private final int[] nodes;
    private final int[] clusters;
    private Executor executor;

    public NodePayload(int[] nodes, int[] clusters, Executor executor) {
        this.nodes = nodes;
        this.clusters = clusters;
        this.executor = executor;
    }

    public void compute(int workerId) {
        boolean[] clusteredNodes = new boolean[this.nodes.length];
        Arrays.fill(clusteredNodes, false);
        int clusterId = workerId << 24 | 1; // 8 bits for workerId, 24 for cluster Id -> 16777215 maximum clusters
        for (int i = 0; i < clusteredNodes.length; i++) {
            if (!clusteredNodes[i]) {
                boolean[] visited = executor.runBFS(this.nodes.length, i);
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

}
