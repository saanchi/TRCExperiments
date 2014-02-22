#!/bin/sh -x

## As the crawled data is not neatly ararnged because of line breaks. 
## Arrange it nicely in the format :
## category\tline

mv data_formatted.txt data_formatted.txt.bkp

gawk -F\\t 'BEGIN{ while( getline < "all_categories.dat" ){ arr[$1] = 1;}} {\
  if( arr[$1] == 1 ) { \
      flag = 1; \
      cat=$1;  \
  } \
  if( flag == 1  ){  \
      if( str != "" ){ \
          print cat"\t"str > "data_formatted.txt"; \
          str ="";  \
      } \
      str = str$2; flag=0;  cat=""; \
  } \
  else{ str = str" "$0; } \
  }' data.txt

## Select only the categories which we want

gawk -F\\t 'BEGIN{ while( getline < "categories.dat" ){ arr[$1] = 1;}} {\
    if( arr[$1] == 1 ){ \
        print $0; \
    }\
 }' data_formatted.txt > data_categories.dat

## Sort and extract the uniques
sort data_categories.dat | uniq > data_uniq.txt

## Get the frequency of each category
gawk -F\\t '{ freq[$1]++; } END{ for( x in freq ){ print x"\t"freq[x];  }}' data_uniq.txt > result.txt

## Get the char count of the data set in following buckets:
## <25, 25-100, 100-250, 250-500, 500-1000, >1000

gawk -F\\t '{ len  = length($2); \
    if( len <= 25) x++; \
    if( len > 25 && len <= 100) y++; \
    if( len > 100 && len <= 250) z++;\
    if( len > 250 && len <= 500) w++; \
    if( len > 500 && len <=1000 ) u++; \
    if( len >1000) v++; } \
   END{ print x"\t"y"\t"z"\t"w"\t"u"\t"v;}' data_uniq.txt >> result.txt


