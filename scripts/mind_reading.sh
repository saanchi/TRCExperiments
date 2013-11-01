#!/bin/sh -x

## Regular Expressions for matching "Mind Reading" Patterns

grep -e ".*\(he\|she\|they\|NNP\).*\(will\|must\).*be.*"  \
     -e ".*I.*\(think\|thought\|assume\).*" \
     -e ".*\(he\|she\|they\|NNP\).*\(does not\|doesnt\|doesn\|n't_rb\).*\(like\|love\).*me.*" \
     -e ".*why.*\(he\|she\|they\|NNP\).*\(does not\|doesnt\|doesn\|n't_rb\).*\(like\|love\).*me.*" \
     -e ".*\(he\|she\|they\|NNP\).*\(hates\).*" \
     -e ".*\(he\|she\|they\|NNP\).*jj.*" \
     ../data/tagged_thoughts.txt  > ../data/mind_reading.txt
