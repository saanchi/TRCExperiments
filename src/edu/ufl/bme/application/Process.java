package edu.ufl.bme.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.ufl.bme.parsing.TRParse;
import edu.ufl.bme.regexmatch.RegexEngine;

public class Process  {


	public TRParse tParse;
	public RegexEngine regex;
	
	public Process(){
		tParse = new TRParse();
		regex  = new RegexEngine();
	}
	
	/**
	 * Run the application in text mode
	 * @param sentence
	 * @return
	 */
	public String runTextMode( String sentence ){
		ArrayList<String> tagged = regex.runRegex(sentence);
		StringBuilder output = new StringBuilder();
		Iterator<String> itr = tagged.iterator();
		while( itr.hasNext()){
			output.append(itr.next() + "," );
		}
		return output.toString();
	}

	/**
	 * Run the application in file mode.
	 * Takes input the input file name on which to run the tagger and regex engine
	 * @throws IOException 
	 * 
	 */
	
	public void runFileMode( String inputFilePath, String fileName  ) throws IOException{
		// Create a tagged file 
		// Get file path 
		String arr[]  = inputFilePath.split(fileName); // Only for linux now. No windows
		String taggedFileName = arr[0] + "/"  + "tagged.txt";
		String outputFileName = arr[0] + "/"  + "result.txt";
		String inputFileName  = arr[0] + "/"  + fileName;
		tParse.tagFile(inputFileName, taggedFileName );
		regex.runRegexFile( taggedFileName, outputFileName );
		compareResults( inputFileName, outputFileName );
	}
	
	/**
	 * Takes two files as arguments and compute precision/recall.
	 * Also writes to a file sentence tagged wrongly.
	 * 1st part contains the actual sentence.
	 * 2nd part contains either 'Y' or 'emotion' denoting whether distortion present or not
	 * 3rd part contains ',' separated values of labels
	 * 4th part contains ',' separated values of sub-labels
	 */
	
	// ToDo : One MARATHON function. Need to chop it down. Cut! cut! cut!
	public void compareResults( String inputFile, String resultFile ){
		String outputFileName = "results.txt";
		
		// Needed for the calculation of overall precision and recall
		double totSubLabels, precisionSubLabels, recallSubLabels;
		totSubLabels = precisionSubLabels = recallSubLabels = 0.0;
		double correctDetected, totDetected;
		correctDetected = totDetected =0;
		
		// For calculation of precision and recall according to sub-label
		HashMap<String, Integer> countInputSublabel   = new HashMap<String,Integer>();
		HashMap<String, Integer> countRegexSublabel   = new HashMap<String, Integer>();
		HashMap<String, Integer> countCorrectSublabel = new HashMap<String, Integer>();
		
		BufferedReader br1, br2; br1 = br2 = null;
		BufferedWriter bw =  null;
	    String line1, line2; line1 = line2 = "";
	    HashMap<String,Boolean> inputSubLabelMap = new HashMap<String,Boolean>();
	    try{
		    br1 = new BufferedReader(new FileReader(new File(inputFile)));
		    br2 = new BufferedReader(new FileReader(new File(resultFile)));
		    bw =  new BufferedWriter(new FileWriter(new File(outputFileName)));
		    while(( line1 = br1.readLine()) != null ){
				// Split the line. And extract corresponding parts 
		    	String arr1[] = line1.split(";");
				String arr2[] = line2.split(";");
				String sentence1     = arr1.length > 0 ? arr1[0] : "";
				String sentence2     = arr2.length > 0 ? arr2[0] : "";
				String isDistortion1 = arr1.length > 1 ? arr1[1] : "";
				String isDistortion2 = arr2.length > 1 ? arr2[1] : "";
				String label1        = arr1.length > 2 ? arr1[2] : "";
				String label2        = arr2.length > 2 ? arr2[2] : "";
				String subCategory1  = arr1.length > 3 ? arr1[3] : "";
				String subCategory2  = arr2.length > 3 ? arr2[3] : "";
				// sub-Labels are separated by ','
				// For each sub-label present put an entry in List. Increase the count for precision/recall calculation
				String sublabel11[]     = subCategory1.split(",");
				if( sublabel11.length > 1){
					for( int i=0; i<sublabel11.length; i++ ){
						totSubLabels++; // for overall recall calculation
						inputSubLabelMap.put(sublabel11[i], true);
						// update the count of the sub-label in the map
						if( countInputSublabel.containsKey(sublabel11[i])){
							Integer count = countInputSublabel.get(sublabel11[i]);
							countInputSublabel.put(sublabel11[i], count++);
						}
						else countInputSublabel.put(sublabel11[i], 1);
					}
				}
				// Check 1: if the regexEngine correctly categorize the sentence to be distortion. If there is a discrepancy
				// here don't even move ahead to check labels etc.
				if( !isDistortion1.equalsIgnoreCase(isDistortion2)){
					// Print the sentence into file
					bw.write( sentence1 + Constants.OUTPUT_FIELD_DELIM + isDistortion1 + Constants.OUTPUT_FIELD_DELIM + isDistortion2
							+  label1 + Constants.OUTPUT_FIELD_DELIM + label2 + Constants.OUTPUT_FIELD_DELIM 
							+  subCategory1 + Constants.OUTPUT_FIELD_DELIM + subCategory2 + "\n" );
				}
				else{ // Parse the labels and sub-labels from the regex file and  input file
					String sublabel21[]  = subCategory2.split(",");
					StringBuilder subLabelRegexString = new StringBuilder();
					StringBuilder subLabelInputString = new StringBuilder();
					// Check if sublabel from regex file is same as input or not.
					if( sublabel21.length > 1){
						for( int i=0; i<sublabel21.length; i++ ){
							totDetected++;  // total sublabels detected by regex for overall precision calculation
							// Now update count of individual sub labels
							if( countRegexSublabel.containsKey(sublabel21[i])){
								Integer count = countRegexSublabel.get(sublabel21[i]);
								countRegexSublabel.put(sublabel21[i], count++);
							}
							else{
								countRegexSublabel.put(sublabel21[i], 1);
							}
							// Now check if the sub-labels are correctly tagged
							if( inputSubLabelMap != null && inputSubLabelMap.containsKey(sublabel21[i])){
								correctDetected++;  					// Increase the correct count.
								inputSubLabelMap.remove(sublabel21[i]);	//Remove the sublabel from input map
								// increase the count of sublabel's correct detected for individual label's precision/recall calculation
								if( countCorrectSublabel != null && countCorrectSublabel.containsKey(sublabel21[i])){
									Integer count = countCorrectSublabel.get(sublabel21[i]);
									countCorrectSublabel.put(sublabel21[i], count++);
								}
								else{
									countCorrectSublabel.put( sublabel21[i], 1);
								}
							}
							else {
								subLabelRegexString.append(sublabel21[i]); // Unmatched sub labels are supposed to be printed to file
							}
						}
					}
					// Iterate over the unmatched sublabels of input string
					Iterator<String> itr = inputSubLabelMap.keySet().iterator();
					while( itr.hasNext()){
						subLabelInputString.append(inputSubLabelMap.get(itr.next()));
					}
					// Finally Print the unmatched parts of sentence into file
					bw.write( sentence1 + Constants.OUTPUT_FIELD_DELIM + isDistortion1 + Constants.OUTPUT_FIELD_DELIM + isDistortion2
							+  label1 + Constants.OUTPUT_FIELD_DELIM + label2 + Constants.OUTPUT_FIELD_DELIM 
							+  subLabelInputString.toString() + Constants.OUTPUT_FIELD_DELIM +  subLabelRegexString  + "\n" );
				}
				// Make GC's life easier. set everything null
				arr1   = null; arr2   = null; sentence1 = null; sentence2 = null; isDistortion1 = null; isDistortion2 = null;
				label1 = null; label2 = null; subCategory1 = null; subCategory2 = null;				
		    }
		 // Ahh! Finally the time to calculate Precision/ Recall
		    // Overall precision
		    if( totDetected != 0 && totSubLabels != 0) {
		    	precisionSubLabels = correctDetected/totDetected;
		    	recallSubLabels    = correctDetected/totSubLabels;
		    }
	    	bw.write(  "Overall" + Constants.FIELD_DELIM + precisionSubLabels + Constants.FIELD_DELIM + recallSubLabels + "\n" );
	    	System.out.println("Overall" + Constants.FIELD_DELIM + precisionSubLabels + Constants.FIELD_DELIM + recallSubLabels + "\n" );
		    // Calculate for every sub label
		    Iterator<String> itr = countInputSublabel.keySet().iterator();
		    while( itr.hasNext()){
		    	double precision = 0.0;
		    	double recall    = 0.0;
		    	String sublabel = itr.next();
		    	int countTotal, countDetected, countCorrect;
		    	countTotal    = countInputSublabel.get(sublabel);
		    	countDetected = countRegexSublabel.containsKey(sublabel)   ? countRegexSublabel.get(sublabel)   : 0;
		    	countCorrect  = countCorrectSublabel.containsKey(sublabel) ? countCorrectSublabel.get(sublabel) : 0;
		    	if( countDetected != 0 && countTotal != 0 ){
		    		precision  = countCorrect/countDetected;
		    		recall     = countCorrect/countTotal;
		    	}
		    	// Okay now write this also to file
		    	bw.write(  sublabel + Constants.FIELD_DELIM + precision + Constants.FIELD_DELIM + recall + "\n" );
		    	System.out.println(sublabel + Constants.FIELD_DELIM + precision + Constants.FIELD_DELIM + recall + "\n");
		    }
		}
	    catch( Exception e){
	    	e.printStackTrace();
	    }
	    finally {
			try {
				if (br1 != null)br1.close();
				if (br2 != null)br2.close();
				if( bw != null)bw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	    
	}
	
	
}