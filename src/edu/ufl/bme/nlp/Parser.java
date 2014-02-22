package edu.ufl.bme.nlp;

import java.util.Collection;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class Parser {

	LexicalizedParser lp;
	
	public Parser(){
		
		String grammar = "model/englishPCFG.ser.gz";
	    String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
	    lp = LexicalizedParser.loadModel(grammar, options);
	}
	
	public Collection<TypedDependency> getTypedDependency( String sentence ){
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    Tree parse;
	    GrammaticalStructure gs;
		parse = lp.parse( sentence );
		gs = gsf.newGrammaticalStructure(parse);
	    Collection<TypedDependency> tdl = gs.typedDependencies();
	    return tdl;
	}
	
	
	
}
