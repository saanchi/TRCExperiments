#!/bin/sh -x

grep -e ".*\(is\|is not\|isn't\|are\|are not\|aren't\|doesn't\|does not\|don't\|dont\|cant\|can't\|can not\|n't_rb\).*" \
     -e ".*\(why\).*\(would\).*\(he\|she\|they\|NNP\).*" \
     -e ".*I.*\(cant\|can not\|can't\|n't_rb\).*\(understand\|belive\).*" \
     -e ".*\(he\|she\|they\|NNP\).*\(ruined\|spoiled\).*" \
     -e ".*I.*\(hate\).*" \
     -e ".*\(How can\|How could\).*\(he\|she\|they\|NNP\)" \
     ../data/tagged_thoughts.txt > ../data/misperception.txt
