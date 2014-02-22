package edu.ufl.bme.parsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class TRParse {

	LexicalizedParser lp;
	MaxentTagger tagger;
	
	public TRParse(){
	
		String grammar = "model/englishPCFG.ser.gz";
	    String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
	    lp = LexicalizedParser.loadModel(grammar, options);
	    // Initialize the tagger
	    //tagger = new MaxentTagger( "model/wsj-0-18-bidirectional-distsim.tagger");
	    tagger = new MaxentTagger( "model/english-left3words-distsim.tagger");
	}
	
		/**
	   * parser turns a Sentence into tokens and then parse
	   * trees.  
	   * @throws IOException 
	   */
	public String getTagged( String sentence ) throws IOException{
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    Tree parse;
	    GrammaticalStructure gs;
		// Extract POS tags
		String tagged = tagger.tagString(sentence);
		// Parse the sentence to extract dependency structure
		parse = lp.parse( sentence );
		gs = gsf.newGrammaticalStructure(parse);
	    Collection<TypedDependency> tdl = gs.typedDependencies();
		Iterator<TypedDependency> itr = tdl.iterator();
	    while( itr.hasNext()){
	    	TypedDependency td = itr.next();
	    	// Extract only the subject
	    	System.out.println( td.reln() + " " + td.gov() + " " + td.dep());
	    	if(td.reln().toString().equalsIgnoreCase("nsubj")){
	    		// Extract the noun part of subject
	    		String arr1[] = td.dep().toString().split("-");
	    		// Tag the sentence with subj.
	    		tagged = tagged.replace( arr1[0], arr1[0] + "_subj");
	    	}
	    }
	    return tagged;
	}
	
	/**
	   * parser turns a file into tokens and then parse
	   * trees.  Note that the trees are printed by calling pennPrint on
	   * the Tree object.
		 * @throws IOException 
	   */
	public void tagFile( String inputFileName, String ouputFileName ) throws IOException{
	  // Read file containing sentences
		BufferedReader br = null;
		BufferedWriter bw = null; 
	    String line = "";
	    try{
		    br = new BufferedReader(new FileReader(new File(inputFileName)));
		    bw = new BufferedWriter(new FileWriter(new File(ouputFileName)));
		    while(( line = br.readLine()) != null ){
				// Split the line. first part contains the actual sentence.
				String arr[] = line.split("\t");
				String sentence     = arr.length > 0 ? arr[0] : "";
				String isDistortion = arr.length > 1 ? arr[1] : "";
				String label        = arr.length > 2 ? arr[2] : "";
				String subCategory  = arr.length > 3 ? arr[3] : "";
				String tagged       = getTagged(sentence);
				// output into file
			    bw.write(tagged.toLowerCase() + "\t" + isDistortion.toLowerCase() + "\t" + label.toLowerCase() + 
			    		"\t" +  subCategory.toLowerCase() + "\n");
			}
	    }
	    catch( Exception e){
	    	e.printStackTrace();
	    }
	    finally {
			try {
				if (br != null)br.close();
				if( bw != null)bw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException {
	
		String sentences = "She got really mean and I guess I didn't really know her.	";
		String fileName = "data/labelled_thoughts.csv";
		TRParse trp = new TRParse();
		String arr[] = sentences.split("\\." );
		for( int i=0; i<arr.length; i++){
			String tagged = trp.getTagged(arr[i]);
			System.out.println( " x" + tagged);
		}
    }

}
