#!/bin/sh -x

## Regular expression for emotional reasoning

grep -e ".*\I.*want.*to.*\(quit\|end\).*" \
	 -e ".*\(holy\|shit\|swear\|fuck\).*" \
      ../data/tagged_thoughts.txt > ../data/emotional_reasoning.txt
