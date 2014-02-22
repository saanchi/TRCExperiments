package edu.ufl.bme.sentiwordnet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.TypedDependency;
import edu.ufl.bme.nlp.Parser;
import edu.ufl.bme.nlp.Tagger;

public class SentiWordNetBasedLabel {

	static SentiWordNet sw;
	static Tagger tagger = new Tagger();
	static Parser parser = new Parser();
	static HashMap<String, Boolean> isStopWordMap ;
	static HashMap<String, String>  abvMap; // Abbrevation map for stanford POS tags to senti wordnet POS tags
	static{
		isStopWordMap = new HashMap<String, Boolean>();
		BufferedReader br = null;
		try {
			 br = new BufferedReader(new FileReader("data/stop_word_list.dat"));
			 String sentence = null;
			 while((sentence = br.readLine()) != null){
				 isStopWordMap.put(sentence.toLowerCase(), new Boolean(true));
			 }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		// Initialize the stanford POS tags map to senti word net tags map
		abvMap = new HashMap<String, String>();
		try {
			 br = new BufferedReader(new FileReader("data/postags_list.dat"));
			 String sentence = null;
			 while((sentence = br.readLine()) != null){
				 // Parse it
				 String arr[] = sentence.split("=");
				 abvMap.put(arr[0].toLowerCase(), arr[1].toLowerCase());
				 arr = null;
			 }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		// Sentiwordnet initialization
		 try {
			sw = new SentiWordNet("data/swn.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SentiWordNetBasedLabel(){
		tagger = new Tagger();
	}
	
	public static double[] getScore( String sentence ){
		double score[] = {0.0, 0.0, 0.0 };
		// get the POS tags of the sentence.
		String taggedSentece = tagger.tagSentence(sentence);
		// Parse the tags with space
		String arr[] = taggedSentece.split(" ");
		int len = arr.length;
		int countNegative = 0;
		double influentialWordScore = 0.0;
		int influentialWordCount = 0;
		// Separate word wit tag
		for( int i=0; i<len; i++){
			String wordTag[] = arr[i].split("_");
			// check if word is in stop word list
			String word = wordTag.length > 1 ? wordTag[0].toLowerCase() : "";
			String tag  = wordTag.length > 1 ? wordTag[1].toLowerCase() : "";
			if( isStopWordMap.containsKey(word) || word.isEmpty()) continue;
			if( !abvMap.containsKey(tag) || tag.isEmpty()) continue;
			double wordScore = sw.extract(word, abvMap.get(tag));
			score[0] = score[0] + wordScore;
			if( wordScore < 0) ++countNegative;
			if( wordScore > 0) --countNegative;
			
			if( Math.abs(wordScore) > 0.1){
				influentialWordScore += wordScore;
				influentialWordCount++;
			}
			
			wordTag = null;
		}
		score[0] = score[0]/len;
		score[1] = countNegative;
		score[2] = influentialWordScore/influentialWordCount;
		arr = null;
		return score;
	}
	
	public static int getDependencyScore( String sentences ){
		// Parse the tags with space
		String sentenceArr[] = sentences.split("\\.");
		int len = sentenceArr.length;
		int countNegative = 0;
		HashMap<String, Boolean> subjectMap  = null;
		HashMap<String, Boolean> negativeMap = null;
		String taggedSentence = null;
		// Iterate over the sentences
		for( int i=0; i<len; i++){
			subjectMap   = new HashMap<String, Boolean>();
			negativeMap  = new HashMap<String, Boolean>();
			// Get the parse tree
			Collection<TypedDependency> td = parser.getTypedDependency(sentenceArr[i]);
			Iterator<TypedDependency> itr = td.iterator();
			// Get the POS tag information
			taggedSentence = tagger.tagSentence(sentenceArr[i]);
		    while( itr.hasNext()){
		    	TypedDependency tdd = itr.next();
	    		String wordTag[] = tdd.gov().toString().toLowerCase().split("-");
				String word = wordTag.length > 1 ? wordTag[0].toLowerCase() : "";
		    	// Extract only the subject
		    	if(tdd.reln().toString().equalsIgnoreCase("nsubj")){
		    		// Extract the dependency
		    		// store in map
		    		//System.out.println( "dep" + tdd.dep().toString().toLowerCase());
		    		subjectMap.put(word, true);
		    	}
		    	// Extract the negative part
		    	if(tdd.reln().toString().equalsIgnoreCase("neg")){
		    		// Extract the gov
		    		// store in map
		    		//System.out.println( "gov" + tdd.gov().toString().toLowerCase());
		    		negativeMap.put(word, true);
		    	}
		    	wordTag = null;
		    	word    = null;
		    }
		    // Iterate over the tagged sentence.
		    // check the subject map to check if its a subject 
		    // check if its a positive word or negative word
		    String wordArr[] = taggedSentence.split(" ");
		    for( int j=0; j<wordArr.length ; j++  ){
		    	String wordTag[] = wordArr[j].split("_");
				String word = wordTag.length > 1 ? wordTag[0].toLowerCase() : "";
				String tag  = wordTag.length > 1 ? wordTag[1].toLowerCase() : "";
				if( !abvMap.containsKey(tag) || tag.isEmpty()) continue;  // get the POS abbr. for the senti wordnet
				//System.out.println(word);
				if( subjectMap.containsKey(word)){                             // if its a subject word
					double wordScore = sw.extract(word, abvMap.get(tag));
					if( wordScore < 0)  ++countNegative;
					else if( wordScore > 0 && negativeMap.containsKey(word)) ++countNegative;
					else --countNegative;
				}	
		    }
		    // Reset the maps and other structures for next sentence
		    negativeMap = null; subjectMap = null;
		    td = null; 			taggedSentence = null;
           /////////////////////// END OF Sentence loop ////////////////////////////
		}
		////////////////////////// END of The whole data block //////////////////////
		return countNegative;
	}
	
	
	public static double getMaxIndex( double arr[]){
		double max = 0.0;
		int index = 0;
		for( int i=0; i<arr.length; i++){
			if( arr[i] > max ){
				index = i;
				max   = arr[i];
			}
		}
		return index;
	}
	
	public static String getLabel( double score[], int rule ){
		switch(rule){
			case 1 : if( score[0] > 0) return "positive";
					 else return "negative";
			case 2 : if( score[1] < 0) return "negative";
					 else return "positive";
			case 3 : if( score[2] > 0.1 ) return "positive";
					 else return "negative";
			default : return "negative";
		}
	}
	
	public static void main(String args[]) throws IOException{
		
		String fileName          = "data/clean_data.dat";
		String outputFileName1   = "data/output1.dat"; // Average of the p-n. if p-n > 0 its +ive else -ive
		//String outputFileName2   = "data/output2.dat"; // if number of -ive words are more label it as -ive else +ive
		//String outputFileName3   = "data/output3.dat"; // if influential word score >.01 make it positive else negative
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(outputFileName1));
		//BufferedWriter bw2 = new BufferedWriter(new FileWriter(outputFileName2));
		//BufferedWriter bw3 = new BufferedWriter(new FileWriter(outputFileName3));
		
		String sentence = "";
		//double score[] = {0.0, 0.0, 0.0 };
		int score = 0;
		String label   = "";
		while ((sentence = br.readLine()) != null) {
			// Parse the sentence.
			// First part is the label. Second part is sentence which we need
			String arr[] = sentence.split("\t");
			//score        = SentiWordNetBasedLabel.getScore(arr[1]);
			score          = SentiWordNetBasedLabel.getDependencyScore(arr[1]); 
			// Write the sentence with original label \t +ive/-ive \t and the actual sentence
			//label        = getLabel(score, 1);
			if( score < 0 ) label = "negative";
			else            label = "positive";
			bw1.write( score + "\t" + label + "\t" + sentence + "\n");
			/*label = getLabel(score, 2);
			bw2.write( score[1] + "\t" + label + "\t" + sentence + "\n");
			label = getLabel(score, 1);
			bw3.write( score[2] + "\t" + label + "\t" + sentence + "\n");*/
			arr   = null;
			label = null;
		}
		//String sent = "2nd Appointment. I sat there wondering if she realized that I'd beaten eight levels in Candy Crush since the hour started.";
		//SentiWordNetBasedLabel.getScore(sent);
		br.close();
		bw1.close();
		//bw2.close();
		//bw3.close();
	}
}
