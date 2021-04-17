package edu.iu.clustering;

import com.opencsv.CSVReader;
import edu.iu.clustering.executors.AdjMatrixBasedExecutor;
import edu.iu.clustering.executors.CSRBasedExecutor;
import edu.iu.clustering.executors.EdgeListBasedExecutor;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphBuilder {

    private static final Logger LOG = Logger.getLogger(GraphBuilder.class.getName());

    public static NodePayload buildGraphWithAdjMatrix(String filePath) {
        CSVReader reader = null;
        LinkedHashMap<Integer, ArrayList<Integer>> edgeList = new LinkedHashMap<>();
        try {
            reader = new CSVReader(new FileReader(filePath));
            String[] nextLine; //read one line at a time
            while ((nextLine = reader.readNext()) != null) {
                for (String token : nextLine) {
                    String[] edge = token.split("\\s+");
                    int startNode = Integer.parseInt(edge[0].trim());
                    int endNode = Integer.parseInt(edge[1].trim());
                    edgeList.computeIfAbsent(startNode, list -> new ArrayList<>()).add(endNode);
                    edgeList.computeIfAbsent(endNode, list -> new ArrayList<>()).add(startNode);
                }
            }
            int[][] adjacencyMatrix = new int[edgeList.keySet().size()][edgeList.keySet().size()];
            List<Integer> listKeys = new ArrayList<>(edgeList.keySet());
            int[] nodes = listKeys.stream().mapToInt(key -> key).toArray();
            listKeys.forEach(key -> {
                ArrayList<Integer> list = edgeList.get(key);
                list.forEach(value -> {
                    adjacencyMatrix[listKeys.indexOf(key)][listKeys.indexOf(value)] = 1;
                });
            });
            Executor executor = new AdjMatrixBasedExecutor(adjacencyMatrix);
            return new NodePayload(nodes, new int[nodes.length], executor);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to load the graph", e);
        }
        return null;
    }


    public static NodePayload buildGraphWithCSR(String filePath) {
        LOG.info("Loading graph....");
        CSVReader reader = null;
        LinkedHashMap<Integer, ArrayList<Integer>> edgeList = new LinkedHashMap<>();
        try {
            reader = new CSVReader(new FileReader(filePath));
            String[] nextLine; //read one line at a time
            while ((nextLine = reader.readNext()) != null) {
                for (String token : nextLine) {
                    String[] edge = token.split("\\s+");
                    int startNode = Integer.parseInt(edge[0].trim());
                    int endNode = Integer.parseInt(edge[1].trim());
                    edgeList.computeIfAbsent(startNode, list -> new ArrayList<>()).add(endNode);
                    edgeList.computeIfAbsent(endNode, list -> new ArrayList<>()).add(startNode);
                }
            }
            LOG.info("Read file to the memory.");

            int[] offsets = new int[edgeList.keySet().size()];
            List<Integer> edges = new ArrayList<>();
            List<Integer> listKeys = new ArrayList<>(edgeList.keySet());
            int[] nodes = listKeys.stream().mapToInt(key -> key).toArray();

            int offsetValue = 0;
            LOG.info("Building CSR format for " + nodes.length + " nodes");
            int fivePercent = (int) (nodes.length * 0.05);
            for (int i = 0; i < nodes.length; i++) {
                offsets[i] = offsetValue;
                ArrayList<Integer> list = edgeList.get(listKeys.get(listKeys.indexOf(nodes[i])));
                list.forEach(val -> {
                    edges.add(listKeys.indexOf(val));
                });
                offsetValue = offsetValue + list.size();
                if (i % fivePercent == 0) {
                    LOG.info("Build progress : " + Math.floor(i * 100 / nodes.length) + "%");
                }
            }
            Executor executor = new CSRBasedExecutor(offsets, edges.stream().mapToInt(key -> key).toArray());
            LOG.info("Graph loaded!");
            return new NodePayload(nodes, new int[nodes.length], executor);
        } catch (
            IOException e) {
            LOG.log(Level.SEVERE, "Failed to load the graph", e);
        }
        return null;
    }

    public static NodePayload buildGraphWithEdgeList(String filePath) {
        LOG.info("Loading graph....");
        CSVReader reader = null;
        LinkedHashMap<Integer, Set<Integer>> edgeList = new LinkedHashMap<>();
        try {
            reader = new CSVReader(new FileReader(filePath));
            String[] nextLine; //read one line at a time
            while ((nextLine = reader.readNext()) != null) {
                for (String token : nextLine) {
                    String[] edge = token.split("\\s+");
                    int startNode = Integer.parseInt(edge[0].trim());
                    int endNode = Integer.parseInt(edge[1].trim());
                    edgeList.computeIfAbsent(startNode, list -> new HashSet<>()).add(endNode);
                    edgeList.computeIfAbsent(endNode, list -> new HashSet<>()).add(startNode);
                }
            }
            LOG.info("Read file to the memory.");

            int[] nodes = edgeList.keySet().stream().sorted().mapToInt(key -> key).toArray();
            Executor executor = new EdgeListBasedExecutor(nodes, edgeList);
            LOG.info("Graph loaded!");
            return new NodePayload(nodes, new int[nodes.length], executor);
        } catch (
            IOException e) {
            LOG.log(Level.SEVERE, "Failed to load the graph", e);
        }
        return null;
    }
}
