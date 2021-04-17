#!/bin/bash
wget https://snap.stanford.edu/data/bigdata/communities/com-amazon.ungraph.txt.gz
gzip -d com-amazon.ungraph.txt.gz
tail -n +5 com-amazon.ungraph.txt > com-amazon.ungraph.txt.new
rm com-amazon.ungraph.txt
mv com-amazon.ungraph.txt.new com-amazon.ungraph.txt
split -l231469 --numeric-suffixes=1 --suffix-length=1 --additional-suffix=".txt"  com-amazon.ungraph.txt "amazon-"