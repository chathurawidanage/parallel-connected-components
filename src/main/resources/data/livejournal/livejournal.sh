#!/bin/bash
wget https://snap.stanford.edu/data/bigdata/communities/com-lj.ungraph.txt.gz
gzip -d com-lj.ungraph.txt.gz
tail -n +5 com-lj.ungraph.txt > com-lj.ungraph.txt.new
rm com-lj.ungraph.txt
mv com-lj.ungraph.txt.new com-lj.ungraph.txt
split -l8670298 --numeric-suffixes=1 --suffix-length=1 --additional-suffix=".txt"  com-lj.ungraph.txt "li-"