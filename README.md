Classification of thoughts records of patients suffering from cognitive distortion.

Various tools and sources and experiments are being conducted to gauze the effictiveness :

-> A simple regular expression checker based on the visible rules 
   rules based on presence of words, presence of some grammatical constructs etc. eg. 
        grep -e ".*\(he\|she\|they\|NNP\).*\(will\|must\).*be.*"  \
             -e ".*I.*\(think\|thought\|assume\).*" \
             -e ".*\(he\|she\|they\|NNP\).*\(does not\|doesnt\|doesn\|n't_rb\).*\(l    ike\|love\).*me.*" \
             -e ".*why.*\(he\|she\|they\|NNP\).*\(does not\|doesnt\|doesn\|n't_rb\)    .*\(like\|love\).*me.*" \
-> Data is fetched by crawling experienceProject website for experiences and confessions for some select categories.
-> Used senti word net to get an idea of the quality of the data set.
