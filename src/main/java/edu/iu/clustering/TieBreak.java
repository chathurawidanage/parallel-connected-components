package edu.iu.clustering;

import edu.iu.clustering.executors.EdgeListBasedExecutor;

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

    public NodePayload compute() {
        LinkedHashMap<Integer, Set<Integer>> edgeList = new LinkedHashMap<>();

        nodes.values().forEach(list -> {
            for (int i = 0; i < list.size(); i++) {
                for (int j = i; j < list.size(); j++) {
                    edgeList.computeIfAbsent(list.get(i), l -> new HashSet<>()).add(list.get(j));
                    edgeList.computeIfAbsent(list.get(j), l -> new HashSet<>()).add(list.get(i));
                }
            }
        });


        int[] labels = edgeList.keySet().stream().sorted().mapToInt(key -> key).toArray();


        NodePayload payload = new NodePayload(labels, new int[labels.length], new EdgeListBasedExecutor(labels, edgeList));
        payload.compute(0);

        int[] nodesLabels = payload.getNodes();
        int[] nodeLabelGroup = payload.getClusters();
        Map<Integer, Integer> labelToGroup = new HashMap<>();
        for (int i = 0; i < nodesLabels.length; i++) {
            labelToGroup.put(nodesLabels[i], nodeLabelGroup[i]);
        }

        int[] finalNodes = new int[nodes.size()];
        int[] finalClusters = new int[nodes.size()];

        int i = 0;
        for (Integer integer : this.nodes.keySet()) {
            finalNodes[i] = integer;
            finalClusters[i++] = labelToGroup.get(this.nodes.get(integer).get(0));
        }
        return new NodePayload(finalNodes, finalClusters, null);
    }
}
