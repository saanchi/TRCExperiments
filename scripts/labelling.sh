#!/bin/sh -x

## Regular Expression for labelling

grep -e ".*I.*am.*\(useless\|horrible\|ugly\|fat\|coward\|waste\|bad\).*" \
     -e ".*I.*\(can not\|can't\|n't_rb\).*" \
     -e ".*I.*\(runied\|screwed\|wasted\|waste\|bad\|horrible\).*" \
     -e ".*I.*\(have no\|don't\|do not\|have not\|haven't\|n't_rb\).*control" \
        ../data/tagged_thoughts.txt > ../data/labelling.txt
