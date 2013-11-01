#!/bin/sh -x

grep -e "I.*\(do not\|dont\|don't\).*\(like\|hate\).*" \
     ../data/tagged_thoughts.txt > ../data/tunnel_vision.txt
