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
		
		HashMap<String,Integer> map = new HashMap<String, Integer>();
		map.put("faltoo", 1);
		Iterator<String> itr  = map.keySet().iterator();
		while( itr.hasNext()){
			if(itr.next().equalsIgnoreCase("faltoo")){
				Integer count = map.get("faltoo");
				count++;
				map.put("faltoo", count);
			}
		}
		System.out.println(map.get("faltoo"));
	}
	
}
