#!/bin/bash
#curl -O  https://github.com/fabiopetroni/VGP/blob/master/dist/VGP.jar
echo $1
echo $2
echo $3
echo $4
FILE="modified.txt"
# command: sh partitioner.sh file.txt  3 hdrf  cora.txt
# replace spaces with tabs
sed $'s/  */\t/' $1 > $FILE
java -jar VGP.jar $FILE   $2   -algorithm $3  -lambda 3 -threads 4  -output $4
