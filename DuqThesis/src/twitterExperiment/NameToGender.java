package twitterExperiment;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/*
 * Classifies a first name based on the U.S. census records on frequency of names for each gender.
 * Uses a 95% confidence interval for classifying names which appear in both genders.
 */

public class NameToGender {
	Set<String> males;
	Set<String> females;
	
	public NameToGender(){
		males = new HashSet<String>();
		females = new HashSet<String>();
		readFiles();
	}
	
	private void readFiles(){
		//Holder lists
		HashMap<String, Double> ma = new HashMap<String, Double>();
		HashMap<String, Double> fe = new HashMap<String, Double>();
		String[] holder2;
		
		//Read in list of male names
		try {
			Scanner m = new Scanner(new File("male.txt"));
			while(m.hasNext()){
				holder2 = m.nextLine().toLowerCase().split("\\s+");
				if(holder2.length > 1)
					ma.put(holder2[0], new Double(holder2[1]));
			}
			m.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file male.txt.  Males list will be empty.");
			e.printStackTrace();
		}
		
		//Read in list of female names
		try {
			Scanner m = new Scanner(new File("female.txt"));
			while(m.hasNext()){
				holder2 = m.nextLine().toLowerCase().split("\\s+");
				if(holder2.length > 1)
					fe.put(holder2[0], new Double(holder2[1]));
			}
			m.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file female.txt.  Females list will be empty.");
			e.printStackTrace();
		}
		
		//Add any names that appear in only one list or have a 95% confidence to their respective sets
		for(String k : ma.keySet()){
			if(!fe.containsKey(k) || ((ma.get(k)/(ma.get(k)+fe.get(k))) > .95)){
				males.add(k);
			}
		}
		
		for(String k : fe.keySet()){
			if(!ma.containsKey(k) || ((fe.get(k)/(ma.get(k) + fe.get(k))) > .95)){
				females.add(k);
			}
		}
	}
	
	public char nameToGender2(String name){
		name = name.toLowerCase();
		if(males.contains(name)){
			if(females.contains(name))
				return 'U';
			else
				return 'M';
		}else if(females.contains(name)){
			return 'F';
		}
		return 'U';
	}
	
	public String nameToGender(String name){
		return "" + nameToGender2(name);
	}
	
}
