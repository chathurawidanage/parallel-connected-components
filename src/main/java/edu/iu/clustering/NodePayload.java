package edu.iu.clustering;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

public class NodePayload {

    private static Logger LOG = Logger.getLogger(NodePayload.class.getName());

    private final int[] nodes;
    private final int[] clusters;
    private Executor executor;

    public NodePayload(int[] nodes, int[] clusters, Executor executor) {
        this.nodes = nodes;
        this.clusters = clusters;
        this.executor = executor;
    }

    public void compute(int workerId) {
        LOG.info("Computing clusters...");
        boolean[] clusteredNodes = new boolean[this.nodes.length];
        Arrays.fill(clusteredNodes, false);
        int clusterId = workerId << 24 | 1; // 8 bits for workerId, 24 for cluster Id -> 16777215 maximum clusters
        for (int i = 0; i < clusteredNodes.length; i++) {
            if (!clusteredNodes[i]) {
                Set<Integer> visited = executor.runBFS(this.nodes.length, i);
                //LOG.info("Looping...");
                for (Integer index : visited) {
                    this.clusters[index] = clusterId;
                    clusteredNodes[index] = true;
                }
                clusterId++;

            }
//            if (i % 100 == 0) {
//                LOG.info("Computed " + Math.floor(i * 100 / clusteredNodes.length) + "%");
//            }
        }
    }

    public int[] getNodes() {
        return nodes;
    }

    public int[] getClusters() {
        return clusters;
    }

    @Override
    public String toString() {
        return "NodePayload{" +
            "nodes=" + Arrays.toString(nodes) +
            ", clusters=" + Arrays.toString(clusters) +
            '}';
    }
}
