#!/bin/sh -x

## Regular expression for Black and White

grep -e ".*\(never\|ever\|always\|not possilbe\|impossible\).*" \
     -e ".*\(no one\|anyone\).*" \
     -e ".*\(too\).*\(_jj\)" \
     -e ".*\(no\).*\(_nn\)" \
     ../data/tagged_thoughts.txt > ../data/black_and_white.txt
