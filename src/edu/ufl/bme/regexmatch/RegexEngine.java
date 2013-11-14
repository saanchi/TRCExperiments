package edu.ufl.bme.regexmatch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import edu.ufl.bme.application.Constants;

public class RegexEngine {

	public static final String REGEX_FILE = "config/regex.txt";
	public static final String LABELS_FILE= "config/labels.txt";
	static final ClassLoader loader = RegexEngine.class.getClassLoader();
	
	// contains the label and the list of patterns that describe the label
	public HashMap<String, ArrayList<Pattern>> labelPatternMap;
	public static HashMap<String, String> subLabelLabelMap;  // Defines a map entry for sublabel and its corresponding sublabel 
	
	/**
	 * Load the pattern file and the sub-label to label mapping file in memory
	 */
	public RegexEngine(){
		loadLabelMap();
		loadPatternsMap();
	}
	
	/**
	 * Read the labels.txt file into a map
	 */
	public void loadLabelMap(){
		subLabelLabelMap = new HashMap<String, String>();
		BufferedReader br = null;
		String line = "";
	    try{
	    	// Read file containing Labels and their corresponding sub-labels
	    	InputStream is = loader.getResourceAsStream(LABELS_FILE);
	    	InputStreamReader ir = new InputStreamReader(is);
	    	br = new BufferedReader (ir);
			while(( line = br.readLine()) != null ){
				// Line contains two values
				// First is the sub-label and the its corresponding label
				String arr[]    = line.split( Constants.FIELD_DELIM );
				String label    = arr.length > 0 ? arr[0] : null;
				String sublabel = arr.length > 1 ? arr[1] : null;
				if( label == null || sublabel == null ) continue;
				subLabelLabelMap.put( sublabel, label);
			}
	    }catch( Exception e){
	    	e.printStackTrace();
	    }
	    finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Read the regex file and load all the patterns into a map
	 * @throws IOException 
	 */
	public void loadPatternsMap() {
		labelPatternMap = new HashMap<String, ArrayList<Pattern>>();
		BufferedReader br = null;
		String line = "";
	    try{
	    	InputStream is = loader.getResourceAsStream(REGEX_FILE);
	    	InputStreamReader ir = new InputStreamReader(is);
	    	br = new BufferedReader (ir);
	    	while(( line = br.readLine()) != null ){
				// Line contains two values
				// First is the sub-label it belongs to and the other is regular expression
				String arr[] = line.split( Constants.FIELD_DELIM );
				String label = arr.length > 0 ? arr[0] : null;
				String regex = arr.length > 1 ? arr[1] : null;
				Pattern pattern = null;
				if (label == null && regex == null )	continue;
				if( regex != null && label != null )
					pattern = Pattern.compile(regex);
				// if regex already has an entry in the map append to the list
				if( pattern != null && labelPatternMap.containsKey(label)){
					ArrayList<Pattern> patternList = labelPatternMap.get(label);
					patternList.add(pattern);
				}
				else { // create the list and add
					ArrayList<Pattern> patternList = new ArrayList<Pattern>();
					patternList.add(pattern);
					labelPatternMap.put(label, patternList);
				}
			}
	    }catch( Exception e){
	    	e.printStackTrace();
	    }
	    finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Runs the sentence over all the regular expressions
	 * and returns the list of labels it got matched against 
	 * @param sentence
	 * @returns ArrayList<String> 
	 */
	public ArrayList<String> runRegex( String sentence ){
		
		ArrayList<String> labels = new  ArrayList<String>();
		Iterator<String> itr = labelPatternMap.keySet().iterator();
		while( itr.hasNext() ){
			String label = itr.next();
			ArrayList<Pattern> list = labelPatternMap.get(label);
			Iterator<Pattern> itr1 = list.iterator();
			while(itr1.hasNext()){
				Pattern pattern = itr1.next();
				// If matched against the pattern
				if( pattern.matcher(sentence).matches()){
					labels.add(label);
				}
			}
		}
		return labels;
	}
	
	/**
	 * Run regex's over the input file and create a second file with the similar format
	 * but tagged based on the regex engine findings.
	 * @param fileName
	 */
	public void runRegexFile( String inputFile, String taggedFile  ){
		BufferedReader br = null;
		BufferedWriter bw = null;
		String line = "";
		ArrayList<String> subLabels = new ArrayList<String>();
	    try{ 
	    	br = new BufferedReader ( new FileReader( new File(inputFile)));
	    	bw = new BufferedWriter( new FileWriter( new File( taggedFile )));
	    	while(( line = br.readLine()) != null ){
				String arr[] 		 = line.split( Constants.FIELD_DELIM );
				String sentence 	 = arr.length > 0 ? arr[0] : "";
				subLabels            = runRegex(arr[0]);
				String isDistortion = "Y" ;
				// if sublabels is empty it means regex engine did not detected any distortion
				if( subLabels.isEmpty()){
					isDistortion = "Emotion";
				}
				StringBuilder sublabels = new StringBuilder(); // Now create a string for labels and sublabels
				StringBuilder labels    = new StringBuilder();
				Iterator<String> itr = subLabels.iterator();
				while(itr.hasNext()){
					String sublabel = itr.next();
					String label    = subLabelLabelMap.get( sublabel );
					sublabels.append( sublabel +  Constants.LABEL_DELIM);
					labels.append(label + Constants.LABEL_DELIM);
					sublabel = null; label = null;
				}
				// Write to file
				bw.write( sentence + Constants.FIELD_DELIM + isDistortion 
						           + Constants.FIELD_DELIM + labels.toString() 
						           + Constants.FIELD_DELIM + subLabels.toString() + "\n" );
				// Explicitly setting variables to null. Trying to reduce dependency on GC
				subLabels = null; labels = null; sentence = null; isDistortion = null; arr = null;
			}
	    }catch( Exception e){
	    	e.printStackTrace();
	    }
	    finally {
			try {
				if (br != null)br.close();
				if (bw != null)bw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
	
	public static void main(String args[]) throws IOException{
		RegexEngine regengine = new RegexEngine();
		String sentence = "I think you ought to know I'm feeling very depressed.";
		ArrayList<String> labelled = regengine.runRegex(sentence);
		Iterator itr = labelled.iterator();
		while( itr.hasNext()){
			System.out.println(itr.next());
		}
	}
	
	 
}
