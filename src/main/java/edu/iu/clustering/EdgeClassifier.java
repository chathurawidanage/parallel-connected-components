package edu.iu.clustering;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class EdgeClassifier {

    public static void writeToFile(String sourceFile) throws IOException {
        BufferedReader reader = null;
        HashMap<String, BufferedWriter> writerHashMap = new HashMap<>();
        HashMap<String, FileWriter> fileHashMap = new HashMap<>();
        LinkedHashMap<Integer, Set<Integer>> edgeList = new LinkedHashMap<>();
        try {
            reader = new BufferedReader(new FileReader(sourceFile));
            String nextLine; //read one line at a time
            while ((nextLine = reader.readLine()) != null && !nextLine.equals("")) {
                String[] edges = nextLine.split(":");
                String edge = edges[0].replace(",", " ");
                String cluster = edges[1];
                int clusterId = Integer.parseInt(cluster.trim()) + 1;
                String dstFile = sourceFile + "-" + clusterId + ".txt";

                File file = new File(dstFile);

                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = fileHashMap.get(cluster);
                if (fw == null) {
                    fw = new FileWriter(file, true);
                    fileHashMap.put(cluster, fw);
                }

                BufferedWriter bufferedWriter = writerHashMap.get(cluster);
                if (bufferedWriter == null) {
                    bufferedWriter = new BufferedWriter(fw);
                    writerHashMap.put(cluster, bufferedWriter);
                }
                bufferedWriter.write(edge);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
            System.out.println("Exist");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writerHashMap.forEach((s, bw) -> {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void partitionNaively(String[] args) throws IOException {
        String sourceFile = args[0];
        int noOfPartitions = Integer.parseInt(args[1]);
        BufferedReader reader = null;
        HashMap<String, BufferedWriter> writerHashMap = new HashMap<>();
        HashMap<String, FileWriter> fileHashMap = new HashMap<>();
        LinkedHashMap<Integer, Set<Integer>> edgeList = new LinkedHashMap<>();
        try {
            reader = new BufferedReader(new FileReader(sourceFile));
            String nextLine; //read one line at a time
            int count = 1;
            while ((nextLine = reader.readLine()) != null && !nextLine.equals("")) {
                int mod = count % noOfPartitions;
                mod = mod +1;
                String dstFile = sourceFile + "-" + mod + ".txt";

                File file = new File(dstFile);

                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = fileHashMap.get(dstFile);
                if (fw == null) {
                    fw = new FileWriter(file, true);
                    fileHashMap.put(dstFile, fw);
                }

                BufferedWriter bufferedWriter = writerHashMap.get(dstFile);
                if (bufferedWriter == null) {
                    bufferedWriter = new BufferedWriter(fw);
                    writerHashMap.put(dstFile, bufferedWriter);
                }
                bufferedWriter.write(nextLine);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
            System.out.println("Exist");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writerHashMap.forEach((s, bw) -> {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            EdgeClassifier.writeToFile(args[0]);
        } else if (args.length ==2) {
            EdgeClassifier.partitionNaively(args);
        }
    }

}
