#!/bin/sh -x

mkdir -p data
BASE_URL="http://api.wefeelfine.org:8080/ShowFeelings?display=xml&returnfields=sentence&limit=1500&feeling="

while read line
do
    feeling=`echo $line | cut -f1 -d' '` 
    nitmes=`echo $line | cut -f2 -d' '`
    echo $BASE_URL$feeling"\t"$feeling
    wget -O data/$feeling.txt $BASE_URL$feeling
    sleep 20
done < list.dat
