package twitterExperiment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int choice;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		boolean hardcode = args != null && args.length > 0 && args[0].equalsIgnoreCase("hardCode"); 
		
		if(hardcode){
			choice = 1;
		}else{
			//Give menu of options
			System.out.println("Which of the following options do you want to do:\n"
					+"1. Create a corpus.\n"
					+"2. Create document file.\n"
					+"3. Create experiment file.\n"
					+"4. Combine results files.");
			choice = Helper.readInt(in,"Enter number of your selection: ");
			while(choice <= 0 || choice > 4) {
				System.err.println("Please enter either 1, 2, 3, or 4.");
				choice = Helper.readInt(in,"Enter number of your selection: ");
			}
		}

		switch(choice){
		case 1: //Create a corpus
			CreateCorpus c = new CreateCorpus(in);
			c.create(hardcode);
			break;
		case 2: //Create document file
			createDocument(in);
			break;
		case 3:
			createExperiment(in);
			break;
		case 4:
			combineExperiments(in);
			break;
		}
	}

	public static void combineExperiments(BufferedReader in){
		File first = Helper.readFile(in,"Enter location of first experiment results file: ",true);
		File second = Helper.readFile(in,"Enter location of second experiment results file: ",true);
		File third = Helper.readFile(in,"Enter location of third experiment results file: ",true);

		HashMap<String, String[]> firstMap = readExperimentMap(first);
		HashMap<String, String[]> secondMap = readExperimentMap(second);
		HashMap<String, String[]> thirdMap = readExperimentMap(third);

		Set<String> keys = firstMap.keySet();
		String[] holder;
		int mcount = 0, fcount = 0, tcount = 0, totalFiles = 0;
		for(String s : keys){
			holder = firstMap.get(s);
			if(holder[0].equals("M"))
				mcount++;
			else
				fcount++;
			holder = secondMap.get(s);
			if(holder[0].equals("M"))
				mcount++;
			else
				fcount++;
			holder = thirdMap.get(s);
			if(holder[0].equals("M"))
				mcount++;
			else
				fcount++;

			System.out.print("File: " + s);
			System.out.print(" Classifcation~ M: " + mcount);
			System.out.print(" F: " + fcount);
			String result = (fcount >= mcount ? "F" : "M");
			System.out.print(" Result: " + result);
			System.out.println(" Actual: " + holder[1]);
			if(result.equals(holder[1]))
				tcount++;

			mcount = 0;
			fcount = 0;
			totalFiles++;
		}
		System.out.println("Total correct: " + tcount);
		System.out.println("Final percent accurace: " + tcount/new Double(totalFiles));
	}

	public static HashMap<String, String[]> readExperimentMap(File f){
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		String file = "", classified, actual = "";
		String holder;
		Scanner read;
		try {
			read = new Scanner(f);
			while(read.hasNext()){
				holder = read.nextLine();
				if(holder.contains(".txt")){
					file = holder.split(" ")[0];
					actual = file.split("-")[1];
				}else if(holder.contains("1. ") && !file.equals("")){
					classified = holder.split(" ")[1];
					map.put(file, new String[]{classified, actual});
					file = "";
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("");
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * Uses user input to create the experiment file for a JGAAP experiment.
	 * Calls the getJgaapInfo method to get JGAAP info from user.
	 * 
	 * @param in  The BufferedReader currently reading user input.
	 */
	public static void createExperiment(BufferedReader in){
		//Open connection to file that contains JGAAP info
		File info = new File("testingOut.txt");
		//If file doesn't exist, create it
		if(!info.exists()){
			System.out.println("Creating JGAAP information document.  Please wait...");
			Helper.createJgaapInfo();
		}

		//Get location to save experiment file
		File f = null;
		boolean cont = false;
		while(!cont){
			f = new File(Helper.readString(in,"Enter location to save experiment file, including filename: "));
			if(f.isDirectory()){
				System.out.println("Location is a directory.  Location needs to be a filename.  Please try again.");
			}else if(f.exists()){
				System.out.println("File already exists. Continuing will result in the existing file being overwritten.");
				cont = Helper.readString(in, ("Would you like to continue? (Y/N) : ")).equalsIgnoreCase("Y");
				if(cont){
					f.delete();
					cont = true;
				}
			}else{
				cont = true;
			}
		}

		try {
			//Open a BufferedWriter to the experiment file
			BufferedWriter out = new BufferedWriter(new FileWriter(f));

			//Get Experiment name
			String experimentName = Helper.readString(in,"Enter name of experiment suite : ");
			out.write(experimentName);
			out.newLine();

			//Get JGAAP experiment info
			String jgaap = getJgaapInfo(in);
			out.write(jgaap);
			while(Helper.readString(in, "Would you like to build another experiment? (Y/N) : ").equalsIgnoreCase("Y")){
				out.newLine();
				jgaap = getJgaapInfo(in);
				out.write(jgaap);
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Error opening write stream to experiment file.");
			e.printStackTrace();
		}

		//Sample command
		//nohup ant cli -Dcli="-ee f.getAbsolutePath()" &
		System.out.println("To run the experiment, use the following command:");
		System.out.println("nohup ant cli -Dcli=\"-ee "+f.getAbsolutePath()+"\" &");
	}	

	/* Uses user input to create the document file for a JGAAP experiment.
	 * Should be run after having created a corpus of documents.
	 * 
	 * @param in The BufferedReader which is currently reading user input.
	 */
	public static void createDocument(BufferedReader in){
		//Get location of corpus to be used
		File corpus = new File(Helper.readString(in, "Enter location of corpus of documents: "));
		while(!corpus.isDirectory()){
			System.err.println("Location given is not a directory.  Please try again.");
			corpus = new File(Helper.readString(in, "Enter location of corpus of documents: "));
		}
		int totalFiles = corpus.listFiles().length;
		
		//Note: An AWK program was created to reduce a documents.csv file to a fixed number of docs per author

		//Get percentage of documents to create a subset
		int subset = 0;
		while(subset <= 0){
			try{
				subset = new Integer(Helper.readString(in,"Enter percentage of documents to be used in experiment, in integer form, in parts per 10,000.  I.e. 115 is 1.15% : "));
			}catch(NumberFormatException e){
				System.err.println("Could not parse your input into an integer.  Please try again.");
			}
		}

		//Get percentage to train on
		int train = 0;
		while(train <= 0){
			try{
				train = new Integer(Helper.readString(in,"Enter percentage of documents to train on, in integer form: "));
			}catch(NumberFormatException e){
				System.err.println("Could not parse your input into an integer.  Please try again.");
			}
		}

		boolean combine = Helper.readBool(in, "Do you want training docs combined into one file? (Y/N) : ");

		//Note: Two AWK programs were created to remove authors with only one document.

		//Get location to save doc file
		File loc = new File(Helper.readString(in,"Enter location to save document file: "));
		while(!loc.isDirectory()){
			System.err.println("Location given is not a directory.  Please try again.");
			loc = new File(Helper.readString(in,"Enter location to save document file: "));
		}

		//Create document file
		Random generator = new Random(System.currentTimeMillis());
		int trainCount = 0, testCount = 0;
		int trainFCount = 0, trainMCount = 0;
		BufferedWriter out;
		HashMap<String, String> holder = new HashMap<String, String>();
		String chara, text;
		try {
			out = new BufferedWriter(new FileWriter(loc+"/documents-"+corpus.getName()+"-Train"+train+"-Total"+subset+".csv"));
			System.out.println("Total of " + totalFiles + " files being read.");
			System.out.println("Saving only " + subset/10000.0 + " percent of files.");
			System.out.println("Training on " + train/100.0 + " percent of files.");
			for(File f : corpus.listFiles()){
				if(!f.getName().contains("metadata")){
					chara = f.getName().substring(f.getName().indexOf("-")+1,f.getName().indexOf("-id"));
					//Test for subset precentage
					if(generator.nextDouble() < subset/10000.0){
						if(generator.nextDouble() < train/100.0){ //Training data
							if(combine){
								text = "";
								Scanner i = new Scanner(f);
								while(i.hasNext()){text+=i.nextLine();}
								i.close();
								if(holder.containsKey(chara)){
									holder.put(chara, holder.get(chara)+"\n"+text);
								}else{
									holder.put(chara, text);
								}
							}else{
								out.write(chara);
								out.write(","+f.getAbsolutePath());
								out.write(","+f.getName());
								out.newLine();
							}
							trainCount++;
							if(chara.equalsIgnoreCase("f"))
								trainFCount++;
							else
								trainMCount++;
						}else{ //Test data
							out.write(","+f.getAbsolutePath());
							out.write(","+f.getName());
							out.newLine();
							testCount++;
						}
					}
				}
			}
			//Write new combined files
			if(combine){
				File docLoc = new File(loc + "/" + corpus.getName() + "Docs");
				docLoc.mkdir();
				for(String s : holder.keySet()){
					File combineDoc = new File(docLoc.getAbsolutePath() + "/"+ s + "_combined_doc.txt");
					BufferedWriter out2 = new BufferedWriter(new FileWriter(combineDoc));
					out2.write(holder.get(s));
					out2.close();
					out.write(s);
					out.write(","+combineDoc.getAbsolutePath());
					out.write(","+combineDoc.getName());
					out.newLine();
				}
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Error writing document file(s).");
			e.printStackTrace();
		}
		System.out.println("Wrote " + trainCount + " docs to train on.");
		System.out.println("Wrote " + testCount + " docs to test on.");
	}

	/*
	 * Reads user input to create a JGAAP experiment file.
	 * 
	 * @param in The BufferedReader currently reading user input.
	 */
	public static String getJgaapInfo(BufferedReader in){
		//Format of output needed:
		//canonicizer|canonicizer,EventDriver|key:value|key:value,culler|culler,analysis|key:value|key:value,distance|key:value

		//Get label for the experiment. 
		String holder = Helper.readString(in, "Enter label for this experiment: ") + ",";

		//JGAAP class holds information on the JGAAP software, so create an instance.
		JGAAP j = new JGAAP();

		System.out.println("Please choose the JGAAP options you want for the experiment.");

		//Get Canonicizers
		//List all canonicizers in a numbered list
		System.out.println("Choose which Canonicizers you want to use:");
		for(int i = 0; i < j.canonicizers.length; i++){
			if(!j.canonicizers[i].equals(""))
				System.out.println(i+". "+j.canonicizers[i]);
			else
				System.out.println(i+". none");
		}
		int index = 0;
		boolean cont = false;
		do{
			//Read integer choice
			while(index == 0){
				try{
					index = new Integer(Helper.readString(in, "Enter integer choice: "));
					if(index < 1 || index > j.canonicizers.length){
						System.err.println("Please enter an integer between 1 and " + j.canonicizers.length);
						index = 0;
					}else{
						holder += j.canonicizers[index] + "|";			
					}
				}catch(NumberFormatException e){
					System.err.println("Could not parse your input to an integer.  Please try again.");
				}
			}

			//Ask if more canonicizers are to be added
			if(!j.canonicizers[index].equals(""))
				cont = Helper.readString(in, "Do you want to add on another canonicizer? (Y/N) : ").equalsIgnoreCase("Y");
			if(cont){index = 0;}
		}while(cont);
		//Remove last | and add on a ,
		holder = holder.substring(0,Math.max(0,holder.length()-1)) + ",";


		//Get Event Driver and keys
		//TODO: Keys
		System.out.println("Choose which Event Driver to use:");
		for(int i = 0; i < j.drivers.length; i++){
			System.out.println(i+". "+j.drivers[i]);
		}
		index = 0;
		//Read integer choice
		while(index == 0){
			try{
				index = new Integer(Helper.readString(in, "Enter integer choice: "));
				if(index < 1 || index > j.drivers.length){
					System.err.println("Please enter an integer between 1 and " + j.drivers.length);
					index = 0;
				}else{
					holder += j.drivers[index] + ",";			
				}
			}catch(NumberFormatException e){
				System.err.println("Could not parse your input to an integer.  Please try again.");
			}
		}


		//Get Cullers
		System.out.println("Choose which Cullers you want to use:");
		for(int i = 0; i < j.cullers.length; i++){
			if(!j.canonicizers[i].equals(""))
				System.out.println(i+". "+j.cullers[i]);
			else
				System.out.println(i+". none");
		}
		index = 0;
		cont = false;
		do{
			//Read integer choice
			while(index == 0){
				try{
					index = new Integer(Helper.readString(in, "Enter integer choice: "));
					if(index < 1 || index > j.cullers.length){
						System.err.println("Please enter an integer between 1 and " + j.cullers.length);
						index = 0;
					}else{
						holder += j.cullers[index] + "|";			
					}
				}catch(NumberFormatException e){
					System.err.println("Could not parse your input to an integer.  Please try again.");
				}
			}

			//Ask if more cullers are to be added
			if(!j.cullers[index].equals(""))
				cont = Helper.readString(in, "Do you want to add on another culler? (Y/N) : ").equalsIgnoreCase("Y");
			if(cont){index = 0;}
		}while(cont);
		//Remove last | and add on a ,
		holder = holder.substring(0,Math.max(0,holder.length()-1)) + ",";

		//Get Classifiers
		//TODO: Keys
		System.out.println("Choose which Classifier to use:");
		for(int i = 0; i < j.classifiers.length; i++){
			System.out.println(i+". "+j.classifiers[i]);
		}
		index = 0;
		//Read integer choice
		while(index == 0){
			try{
				index = new Integer(Helper.readString(in, "Enter integer choice: "));
				if(index < 1 || index > j.classifiers.length){
					System.err.println("Please enter an integer between 1 and " + j.classifiers.length);
					index = 0;
				}else{
					holder += j.classifiers[index] + ",";			
				}
			}catch(NumberFormatException e){
				System.err.println("Could not parse your input to an integer.  Please try again.");
			}
		}


		//Get Distance function
		//TODO: Keys
		System.out.println("Choose which Distance function to use:");
		for(int i = 0; i < j.distances.length; i++){
			System.out.println(i+". "+j.distances[i]);
		}
		index = 0;
		//Read integer choice
		while(index == 0){
			try{
				index = new Integer(Helper.readString(in, "Enter integer choice: "));
				if(index < 1 || index > j.distances.length){
					System.err.println("Please enter an integer between 1 and " + j.distances.length);
					index = 0;
				}else{
					holder += j.distances[index] + ",";			
				}
			}catch(NumberFormatException e){
				System.err.println("Could not parse your input to an integer.  Please try again.");
			}
		}

		//Get location of document csv
		File csv = null;
		while(csv == null || !csv.exists()){
			csv = new File(Helper.readString(in, "Enter location of document csv: "));
		}
		holder += csv.getAbsolutePath();

		return holder;
	}

	/*
	 * Runs through all tweets in the masterFile and counts the number of
	 *  tweets each characteristic has stored.  The counts are then returned.
	 *  
	 *  @param file       The location of the master file of tweets.
	 *  @param userToChar The map of user ID -> characteristic.
	 *  @return           The map of charateristic -> tweet count.
	 */
	public static HashMap<String,Integer> countUserTweets(String file, HashMap<Long, String> userToChar){
		File masterFile = new File(file);
		//HashSet<Long> tIDs = new HashSet<Long>();
		HashMap<String,Integer> charCount = new HashMap<String,Integer>();
		try {
			Scanner in = new Scanner(masterFile);
			String holder, charHolder;
			String[] holder2;
			Long userID;
			while(in.hasNext()){
				holder = in.nextLine().trim();
				/*if(holder.contains("}{")){
					holder2 = holder.split("}{");
				}*/
				if(holder.length() > 2){ //Test for empty lines
					JSONObject j = (JSONObject)JSONValue.parse(holder);
					if(j != null){ //Check to make sure the read string parsed to a JSONObject
						JSONArray j2 = (JSONArray)j.get("results");
						for(Object o : j2){
							userID = (Long)((JSONObject)o).get("from_user_id");
							charHolder = userToChar.get(userID);
							if(charCount.containsKey(charHolder)){
								charCount.put(charHolder,charCount.get(charHolder)+1);
							}else{
								charCount.put(charHolder,1);
							}
						}
					}else{
						//Try splitting the read object on }{ string in case two JSON objects are next to each other.
						holder2 = holder.split("\\}\\{");
						for(int i = 0; i < holder2.length; i++){
							if(i==0)
								holder2[i] = holder2[i]+"}";
							else if(i==holder2.length-1)
								holder2[i] = "{"+holder2[i];
							else
								holder2[i] = "{" + holder2[i] + "}";
						}

						//Run through all objects
						for(String s : holder2){
							j = (JSONObject)JSONValue.parse(s);
							if(j != null){
								JSONArray j2 = (JSONArray)j.get("results");
								for(Object o : j2){
									userID = (Long)((JSONObject)o).get("from_user_id");
									charHolder = userToChar.get(userID);
									if(charCount.containsKey(charHolder)){
										charCount.put(charHolder,charCount.get(charHolder)+1);
									}else{
										charCount.put(charHolder,1);
									}
								}
							}else{
								System.err.println("Error with JSON object.\n"+holder);
							}
						}
					}
					//break;
				}
			}

		} catch (FileNotFoundException e) {
			System.err.println("masterFile.csv not found.");
			e.printStackTrace();
			System.exit(1);
		}
		return charCount;
	}
}
