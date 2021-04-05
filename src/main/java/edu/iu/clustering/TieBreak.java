package edu.iu.clustering;

import edu.iu.clustering.executors.AdjMatrixBasedExecutor;

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
        LinkedHashMap<Integer, ArrayList<Integer>> edgeList = new LinkedHashMap<>();

        nodes.values().forEach(list -> {
            for (int i = 0; i < list.size(); i++) {
                for (int j = i; j < list.size(); j++) {
                    edgeList.computeIfAbsent(list.get(i), l -> new ArrayList<>()).add(list.get(j));
                    edgeList.computeIfAbsent(list.get(j), l -> new ArrayList<>()).add(list.get(i));
                }
            }
        });

        int[][] adjacencyMatrix = new int[edgeList.keySet().size()][edgeList.keySet().size()];
        List<Integer> listKeys = new ArrayList<>(edgeList.keySet());

        int[] labels = listKeys.stream().mapToInt(key -> key).toArray();
        listKeys.forEach(key -> {
            ArrayList<Integer> list = edgeList.get(key);
            list.forEach(value -> {
                adjacencyMatrix[listKeys.indexOf(key)][listKeys.indexOf(value)] = 1;
            });
        });

        NodePayload payload = new NodePayload(labels, new int[labels.length], new AdjMatrixBasedExecutor(adjacencyMatrix));
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
    }
}
