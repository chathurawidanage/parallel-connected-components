#!/bin/bash
wget https://snap.stanford.edu/data/roadNet-CA.txt.gz
gzip -d roadNet-CA.txt.gz
tail -n +5 roadNet-CA.txt > roadNet-CA.txt.new
rm roadNet-CA.txt
mv roadNet-CA.txt.new roadNet-CA.txt
split -l8670298 --numeric-suffixes=1 --suffix-length=1 --additional-suffix=".txt"  roadNet-CA.txt "li-"