#!/bin/sh -x

## Get the count of each disorder 

black_and_white_count=`grep -ic "black and white" ../data/tagged_thoughts.txt`
echo $black_and_white_count
labelling_count=`grep -ic "labeling" ../data/tagged_thoughts.txt`
fortune_telling_count=`grep -ic "fortune telling" ../data/tagged_thoughts.txt`
making_guesses_count=`grep -ic "making guesses" ../data/tagged_thoughts.txt`
misperception_count=`grep -ic "misperception" ../data/tagged_thoughts.txt`
mindreading_count=`grep -ic "mindreading" ../data/tagged_thoughts.txt`
should_must_count=`grep -ic "shoulds and musts" ../data/tagged_thoughts.txt`
magnification_count=`grep -ic "magnification" ../data/tagged_thoughts.txt`
overgeneralization_count=`grep -ic "overgeneralization" ../data/tagged_thoughts.txt`
