package edu.iu.clustering;

import edu.iu.clustering.executors.EdgeListBasedExecutor;

import java.util.*;
import java.util.logging.Logger;

public class TieBreak {

    private static final Logger LOG = Logger.getLogger(TieBreak.class.getName());

    private final HashMap<Integer, Set<Integer>> nodes = new HashMap<>();

    public void add(int node, int cluster) {
        this.nodes.computeIfAbsent(node, n -> new HashSet<>()).add(cluster);
    }

    public synchronized void syncAdd(int node, int cluster) {
        this.add(node, cluster);
    }

    /**
     * This method will be used by distributed mode
     */
    public NodePayload computeGroups(int workerId) {
        LOG.info("Computing groups of " + nodes.size() + " nodes");

        LinkedHashMap<Integer, Set<Integer>> edgeList = new LinkedHashMap<>();
        nodes.values().forEach(set -> {
            List<Integer> list = new ArrayList<>(set);
            Collections.sort(list);
            if (list.size() > 4) {
                System.out.println("Impossible!!" + list.size() + " , " + new HashSet<>(list).size());
            }
            for (int i = 0; i < list.size(); i++) {
                for (int j = i; j < list.size(); j++) {
                    edgeList.computeIfAbsent(list.get(i), l -> new HashSet<>()).add(list.get(j));
                    edgeList.computeIfAbsent(list.get(j), l -> new HashSet<>()).add(list.get(i));
                }
            }
        });

        LOG.info("Edge list computed.");

        int[] labels = edgeList.keySet().stream().sorted().mapToInt(key -> key).toArray();

        NodePayload payload = new NodePayload(labels, new int[labels.length], new EdgeListBasedExecutor(labels, edgeList));
        payload.compute(workerId);
        return payload;
    }

    public NodePayload compute() {
        return this.compute(0);
    }

    public NodePayload compute(int workerId) {
        NodePayload payload = this.computeGroups(workerId);

        int[] nodesLabels = payload.getNodes();
        int[] nodeLabelGroup = payload.getClusters();
        Map<Integer, Integer> labelToGroup = new HashMap<>();
        Map<Integer, Integer> lowestLabelOfGroup = new HashMap<>();
        for (int i = 0; i < nodesLabels.length; i++) {
            labelToGroup.put(nodesLabels[i], nodeLabelGroup[i]);
            if (!lowestLabelOfGroup.containsKey(nodeLabelGroup[i]) || nodesLabels[i] < lowestLabelOfGroup.get(nodeLabelGroup[i])) {
                lowestLabelOfGroup.put(nodeLabelGroup[i], nodesLabels[i]);
            }
        }

        int[] finalNodes = new int[nodes.size()];
        int[] finalClusters = new int[nodes.size()];

        int i = 0;
        for (Integer integer : this.nodes.keySet()) {
            finalNodes[i] = integer;
            int anyLabel = this.nodes.get(integer).iterator().next();
            finalClusters[i++] = lowestLabelOfGroup.get(labelToGroup.get(anyLabel));
        }
        return new NodePayload(finalNodes, finalClusters, null);
    }
}
