#!/bin/sh -x

## Regular expressions for classifying "Shoulds And Musts"

grep -e ".*I.*\(won't\|wont\|n't_rb\|will not\).*\(allow\|permit\|let\).*" \
     -e ".*subj.*\(need\|should\|must\).*\(has\|have\).*to.*"  ../data/tagged_thoughts.txt > ../data/shoulds_musts.txt
