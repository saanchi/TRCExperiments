package edu.ufl.bme.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

public class RegularExpressionsMatch {

	public static void main( String args[] ){
		String regex =  ".*abc.*(mno|xyz).*";
		Pattern patern = Pattern.compile(regex);
		String sentence = "ABC for a abc it is an mno for all that i know";
		System.out.println( patern.matcher(sentence).matches());
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> faltoo = new ArrayList<String>();
		faltoo.add("farzi");
		map.put("bekaar", faltoo);
		ArrayList<String> temp =  new ArrayList<String>();
		temp = map.get("bekaar");
		temp.add("bakwas");
		ArrayList<String> test = new ArrayList<String>();
		test = map.get("bekaar");
		Iterator<String> itr = test.iterator();
		while(itr.hasNext()){
			System.out.println(itr.next());
		}
	}
	
}
