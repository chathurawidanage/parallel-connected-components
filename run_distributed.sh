#!/bin/bash
mvn clean install
mpirun -np 4 java -cp target/clustering-1.0-SNAPSHOT-jar-with-dependencies.jar edu.iu.clustering.Distributed