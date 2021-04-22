package edu.iu.clustering;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sequential {
    private static final Logger LOG = Logger.getLogger(Sequential.class.getName());

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        NodePayload payload;

        if (args.length == 1) {
            payload = GraphBuilder.buildGraphWithEdgeList(args[0]);
        } else if (args.length == 2) {
            String dataStructure = args[1];
            if (dataStructure.equals("csr")) {
                payload = GraphBuilder.buildGraphWithCSR(args[0]);
            } else if (dataStructure.equals("adj")) {
                payload = GraphBuilder.buildGraphWithAdjMatrix(args[0]);
            } else {
                payload = GraphBuilder.buildGraphWithEdgeList(args[0]);
            }
        } else {
            File currentDirFile = new File(".");
            String root = currentDirFile.getAbsolutePath();
            //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/amazon/com-amazon.ungraph.txt");
            payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/cora/cora.txt");
            //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/small/small.txt");
            //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/livejournal/com-lj.ungraph.txt");
        }
        long t2 = System.currentTimeMillis();
        payload.compute(0);
        long t3 = System.currentTimeMillis();
        Utils.printStats(payload);
        System.out.println("Time : " + (t3 - t1));
        System.out.println("Data loading time : " + (t2 - t1));
        try {
            Utils.logResults(
                "sequential",
                1,
                (t2 - t1),
                (t3 - t2),
                0,
                (t3 - t1)
            );
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to write results", e);
        }
    }
}
