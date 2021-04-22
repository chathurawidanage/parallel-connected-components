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
        LOG.info("Loading graph.... into Adjacency matrix");
        CSVReader reader = null;
        LinkedHashMap<Integer, Node> edgeList = new LinkedHashMap<>();
        try {
            reader = new CSVReader(new FileReader(filePath));
            String[] nextLine; //read one line at a time
            int count = 0;
            while ((nextLine = reader.readNext()) != null) {
                for (String token : nextLine) {
                    String[] edge = token.split("\\s+");
                    int startNode = Integer.parseInt(edge[0].trim());
                    int endNode = Integer.parseInt(edge[1].trim());
                    Node stNode = edgeList.get(startNode);
                    Node enNode  = edgeList.get(endNode);
                    if (stNode == null) {
                        stNode = new Node(count,startNode);
                        count++;
                    }
                    if (enNode == null) {
                        enNode = new Node(count, endNode);
                        count++;
                    }
                    stNode.addArrayList(enNode);
                    enNode.addArrayList(stNode);

                    edgeList.put(startNode,stNode);
                    edgeList.put(endNode,enNode);
                }
            }
            int[][] adjacencyMatrix = new int[edgeList.keySet().size()][edgeList.keySet().size()];
            List<Integer> listKeys = new ArrayList<>(edgeList.keySet());
            int[] nodes = listKeys.stream().mapToInt(key -> key).toArray();
            listKeys.forEach(key -> {
                Node node = edgeList.get(key);
                node.getArrayList().forEach(value -> {
                    adjacencyMatrix[node.myIndex][value.myIndex] = 1;
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
        LOG.info("Loading graph.... into CSR");
        CSVReader reader = null;
        LinkedHashMap<Integer, Node> edgeList = new LinkedHashMap<>();
        try {
            reader = new CSVReader(new FileReader(filePath));
            String[] nextLine; //read one line at a time
            int count = 0;
            while ((nextLine = reader.readNext()) != null) {
                for (String token : nextLine) {
                    String[] edge = token.split("\\s+");
                    int startNode = Integer.parseInt(edge[0].trim());
                    int endNode = Integer.parseInt(edge[1].trim());
                    Node stNode = edgeList.get(startNode);
                    Node enNode  = edgeList.get(endNode);
                    if (stNode == null) {
                        stNode = new Node(count,startNode);
                        count++;
                    }
                    if (enNode == null) {
                        enNode = new Node(count, endNode);
                        count++;
                    }
                    stNode.addArrayList(enNode);
                    enNode.addArrayList(stNode);

                    edgeList.put(startNode,stNode);
                    edgeList.put(endNode,enNode);

//                    edgeList.computeIfAbsent(startNode, list -> new HashMap<>()).put(endNode);
//                    edgeList.computeIfAbsent(endNode, list -> new HashMap<>()).put(startNode);
                }
            }
            LOG.info("Read file to the memory.");

            int[] offsets = new int[edgeList.keySet().size()];
            System.out.println("Offset size " + offsets.length);
            List<Integer> edges = new ArrayList<>();
            List<Integer> listKeys = new ArrayList<>(edgeList.keySet());
            int[] nodes = listKeys.stream().mapToInt(key -> key).toArray();

            int offsetValue = 0;
            LOG.info("Building CSR format for " + nodes.length + " nodes");
            int fivePercent = (int) (nodes.length * 0.05);
            for (int i = 0; i < nodes.length; i++) {
                offsets[i] = offsetValue;
//                ArrayList<Integer> list = edgeList.get(nodes[i]);
                Node node = edgeList.get(nodes[i]);
                node.getArrayList().forEach((val) -> {
                    edges.add(val.myIndex);
                });
                offsetValue = offsetValue +  node.getArrayList().size();
                if (i % fivePercent == 0) {
                    LOG.info("Build progress : " + Math.floor(i * 100 / nodes.length) + "%");
                }
            }
            Executor executor = new CSRBasedExecutor(nodes, offsets, edges.stream().mapToInt(key -> key).toArray());
            LOG.info("Graph loaded!");
            return new NodePayload(nodes, new int[nodes.length], executor);
        } catch (
                IOException e) {
            LOG.log(Level.SEVERE, "Failed to load the graph", e);
        }
        return null;
    }

    public static NodePayload buildGraphWithEdgeList(String filePath) {
        LOG.info("Loading graph.... into edge list");
        CSVReader reader = null;
        Map<Integer, Set<Integer>> edgeList = new HashMap<>();
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

            int[] nodes = edgeList.keySet().stream().mapToInt(key -> key).toArray();
            Executor executor = new EdgeListBasedExecutor(nodes, edgeList);
            LOG.info("Graph loaded!");
            return new NodePayload(nodes, new int[nodes.length], executor);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to load the graph", e);
        }
        return null;
    }


}
