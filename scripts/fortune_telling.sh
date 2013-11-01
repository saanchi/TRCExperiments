#!/bin/sh -x

## Regular Expressions pattern for Fortune Telling 

grep -e ".*subj.*\(will\|gonna\|going to\|might\|may\).*" \
     -e ".*I.*\(hope\|wish\).*" \
     -e ".*I.*\(have\).*\(nothing\|nowhere\|no one\|\).*" \
     ../data/tagged_thoughts.txt > ../data/fortune_telling.txt
