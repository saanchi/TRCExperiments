package edu.ufl.bme.nlp;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tagger {
	
	MaxentTagger tagger;
	
	public Tagger(){
		this.tagger = new MaxentTagger(
							"model/english-left3words-distsim.tagger");
	}
	
	public String tagSentence(String sentence){
		return tagger.tagString(sentence);
	}

}
