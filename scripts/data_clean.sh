#!/bin/sh -x

## Attach a sequence number to the file, as expected by mallet format
##gawk -F\\t '{ print count++"\t"$1"\t"$2; }' data_uniq.txt > data_seq.txt

## Remove non printable characters 
## $1 is the input file
tr -cd '[:print:]\r\n\t' < $1  > data.dat
## Remove html tags and the junk data inside, mostly urls and line breaks
sed -i "s/<.*[\/]*>//g" data.dat

## More urls are left. Was getting difficult with shell.
## Using php for now

php replace_url.php data.dat  > withouturl.dat

## Now comes adhoc patterns e.g
## :), :(, ;), 1) etc 
## ..., ----, ****,  
sed  -i -e 's/\:)//g' -e 's/\:(//g' -e 's/;)//g' -e 's/[0-9])//g' -e 's/\.\.\.//g' -e 's/\-\-\-//g' -e 's/\*\*\*//g'  withouturl.dat

## Remove the ^M character from the files
tr -d '\015' <withouturl.dat > unixformat.dat

## Remove the sentences with characters < 25 and >1000

gawk -F\\t '{ len  = length($2); \
    if( len > 25 && len<1000  ){ \
        print $0; \
    }}' unixformat.dat > clean_data.dat

## Create separate files for each category

gawk -F\\t '{ print $0 >> "categories/"$1".dat"; }' clean_data.dat



#bin/mallet import-file --input sample-data/ep/data.txt --output sample-data/ep/input.mallet --keep-sequence --remove-stopwords

#bin/mallet train-topics  --input sample-data/ep/input.mallet --num-topics 10 --output-state topic-state.gz --output-topic-keys tutorial_keys.txt --output-doc-topics tutorial_compostion.txt
