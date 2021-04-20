package edu.iu.clustering;

import mpi.MPI;
import mpi.MPIException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class App {

    private static Logger LOG = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int worldSize = MPI.COMM_WORLD.getSize();
        LOG.info("Starting worker " + rank + " of " + worldSize);

        File currentDirFile = new File(".");
        String root = currentDirFile.getAbsolutePath();
//        NodePayload payload = GraphBuilder.buildGraphWithAdjMatrix(root + "/src/main/resources/data/cora/cora.txt");

//        NodePayload payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/cora/cora.txt");
        NodePayload payload = GraphBuilder.buildGraphWithCSR(root + "/src/main/resources/data/amazon/com-amazon.ungraph.txt");

        payload.compute(rank);

        int[] clusters = payload.getClusters();
//        for (int i = 0; i < clusters.length; i++) {
//            System.out.println("i " + i + " cluster " + clusters[i]);
//        }
        Set<Integer> uniqueClus = new HashSet<>();
        for (int cluster : clusters) {
            uniqueClus.add(cluster);
        }

        System.out.println("Total nodes : " + payload.getNodes().length);
        System.out.println("Weakly connected components : " + uniqueClus.size());

        Shuffle shuffle = new Shuffle();
        shuffle.shuffle(payload);

        MPI.Finalize();
    }


}
