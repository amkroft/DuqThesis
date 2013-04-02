package twitterExperiment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class CreateCorpus {
	BufferedReader in;

	//Hard Coded values
	String characteristic;
	String altCharacteristic;
	boolean charToGender;
	boolean removeNullGender;
	boolean charToColor;
	String userDirectory;
	String masterFileLoc;
	String searchOrTimeline;
	String corporaFolder;
	boolean charMap;
	boolean light;
	int randomPercent;

	public CreateCorpus(BufferedReader i){
		in = i;
	}

	public CreateCorpus(){
		in = new BufferedReader(new InputStreamReader(System.in));
	}

	public void create(boolean hardCode){
		//If hard coded, read the hard coded variables in
		//Return to end program if error reading file
		if(hardCode && !readHard()){
			return;
		}

		//Get the characteristic to test on
		String chara;
		if(!hardCode){
			chara = getUserChar();
			System.out.println("Chosen characteristic is: " + chara);
		}else{
			chara = characteristic;
		}

		//Get the mapping between users and their characteristic value
		HashMap<Long, String> map = userCharMap(chara, hardCode);
		System.out.println("Size of Hashmap: " + map.size());

		if(hardCode)chara = altCharacteristic;

		boolean lighter;
		if(!hardCode)
			lighter = Helper.readBool(in, "Would you like to use the method with the lighter memory load? (Y/N) : ");
		else
			lighter = light;

		if(lighter)
			lightSaveTweets(chara, map, hardCode);
		else{
			//Get the mapping between tweets and their characteristic values
			ArrayList<String[]> tweetCharMap = tweetCharMap(map, hardCode);
			System.out.println("Size of ArrayList: " + tweetCharMap.size());

			//Save the tweets in a corpus folder
			saveTweets(tweetCharMap, chara, hardCode);

			//Save a light version of the corpus
			saveTweets(lightTweetCharMap(tweetCharMap),chara+"_light", hardCode);			
		}
	}

	/*
	 * Read the hardCode.txt file to get hard coded variables read in.
	 * 
	 * @return Whether the read was successful.
	 */
	public boolean readHard(){
		try {
			Scanner in = new Scanner(new File("hardCode.txt"));
			String[] holder;
			while(in.hasNext()){
				holder = in.nextLine().split("=");
				if(holder[0].equalsIgnoreCase("characteristic"))
					characteristic = holder[1];
				else if(holder[0].equalsIgnoreCase("altcharacteristic"))
					altCharacteristic = holder[1];
				else if(holder[0].equalsIgnoreCase("charToGender"))
					charToGender = holder[1].equalsIgnoreCase("true");
				else if(holder[0].equalsIgnoreCase("removeNullGender"))
					removeNullGender = holder[1].equalsIgnoreCase("true");
				else if(holder[0].equalsIgnoreCase("charToColor"))
					charToColor = holder[1].equalsIgnoreCase("true");
				else if(holder[0].equalsIgnoreCase("userDirectory"))
					userDirectory = holder[1];
				else if(holder[0].equalsIgnoreCase("masterFileLoc"))
					masterFileLoc = holder[1];
				else if(holder[0].equalsIgnoreCase("searchOrTimeline"))
					searchOrTimeline = holder[1];
				else if(holder[0].equalsIgnoreCase("corporaFolder"))
					corporaFolder = holder[1];
				else if(holder[0].equalsIgnoreCase("charMap"))
					charMap = holder[1].equalsIgnoreCase("true");
				else if(holder[0].equalsIgnoreCase("light"))
					light = holder[1].equalsIgnoreCase("true");
				else if(holder[0].equalsIgnoreCase("randomPercent")){
					try{
						randomPercent = new Integer(holder[1]);
					}catch(NumberFormatException e){
						System.err.println("Error reading value for randomPercent.  Using 100.");
						randomPercent = 100;
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Error reading hardCode.txt file.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * Gets the Twitter user characteristic to base the corpus on from the user.
	 * 
	 * @return The characteristic chosen by the user.
	 */
	public String getUserChar(){
		Scanner sample;
		String sampleTweet = "";
		JSONArray j = null;

		//Get and read a file containing a sample of a user profile information
		while(j==null){
			try {
				sample = new Scanner(new File(Helper.readString(in,"Enter the location of a sample user information JSON object: ")));
				while(sample.hasNext()){sampleTweet+=sample.nextLine();}
				sample.close();
				j = (JSONArray)JSONValue.parse(sampleTweet);
				if(j == null){
					System.out.println("The data in the file you specified could not be converted to a JSON object." +
					"\nPlease enter a different file.");
				}
			} catch (FileNotFoundException e) {
				System.out.println("The file you entered could not be found.  Please retry.");
				//e.printStackTrace();
			}
		}

		Set keys = ((JSONObject)j.toArray()[0]).keySet();

		System.out.println("Which of the following characteristics of Twitter users\n" +
		" do you want to test on?");
		int count = 1;
		for(Object o : keys){
			System.out.println(" * "+o.toString());
			count++;
		}
		//Get choice from user
		String characteristic = Helper.readString(in,"Enter your selection: ");
		while(!keys.contains(characteristic)){
			System.out.println("Error: Invalid characteristic entered.  Please try again.");
			characteristic = Helper.readString(in,"Enter your selection: ");
		}

		return characteristic;
	}

	/*
	 * Create and return the map of the user IDs to the characteristic value.
	 * 
	 * @param characteristic The chosen characteristic of the Twitter user.
	 * @param hardCode 		 Whether to read user input or use hard coded values.
	 * @return 				 The map of user IDs to characteristic value.
	 */
	public HashMap<Long, String> userCharMap(String characteristic, boolean hardCode){
		//If characteristic is "name" or something_color ask if they want to use custom mapping
		boolean name = false, color = false, remove = false;
		if(!hardCode){
			if(characteristic.equals("name")){
				name = Helper.readBool(in, "Characteristic is name.  Do you want to map to gender? (Y/N) : ");
				if(name){
					remove = Helper.readBool(in, "Do you want unknown gender data to be removed? (Y/N) ");
				}
			}else if(characteristic.contains("color")){
				color = Helper.readBool(in, "Characteristic is a color.  Do you want to map to descriptive text? (Y/N) : ");
			}
		}else{
			name = charToGender;
			remove = removeNullGender;
			color = charToColor;
		}

		//Get location of directory of user files
		File userFile;
		if(!hardCode){
			userFile  = Helper.readDir(in, "Enter location of directory of user profile files: ", true);
		}else{
			userFile = new File(userDirectory);
		}

		//Create the map
		HashMap<Long,String> users = new HashMap<Long,String>();
		NameToGender n = name ? n = new NameToGender() : null;
		Scanner s;
		String holder = "";
		JSONArray j;
		JSONObject j2;
		for(File f : userFile.listFiles()){
			try {
				s = new Scanner(f);
				holder = "";
				while(s.hasNext())
					holder += s.nextLine();
				s.close();
				j = (JSONArray) JSONValue.parse(holder);
				j2 = j != null ? (JSONObject)(j.toArray()[0]) : null;
				if(j2 != null){
					if(!name && !color)
						users.put((Long)j2.get("id"),j2.get(characteristic) != null ? j2.get(characteristic).toString() : "");
					else if(name){
						holder = j2.get(characteristic) != null ? n.nameToGender(j2.get(characteristic).toString()) : "";
						users.put((Long)j2.get("id"),holder.equals("U") && remove ? "" : holder);
					}else //if(color)
						users.put((Long)j2.get("id"),j2.get(characteristic) != null ? Helper.rgbColorToString(j2.get(characteristic).toString()) : "");
				}
			} catch (FileNotFoundException e) {
				System.err.println("Error opening Scanner to " + f.getName());
				e.printStackTrace();
			}
		}

		return users;
	}

	/*
	 * Take in an array of tweets and removes the ones with single characteristic.
	 * 
	 * @param tweetCharMap The list of tweets and their characteristic.
	 * @return			   The reduced list.
	 */
	public ArrayList<String[]> lightTweetCharMap(ArrayList<String[]> tweetCharMap){
		//String[] contains: tweet ID, tweet body, characteristic value
		ArrayList<String[]> lightMap = new ArrayList<String[]>();

		//Read the whole list and count the number of tweets per characteristic
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		for(String[] s : tweetCharMap){
			if(counts.containsKey(s[2])){
				counts.put(s[2], counts.get(s[2])+1);
			}else{
				counts.put(s[2],1);
			}
		}

		//Save each tweet which is associated to a characteristic that has a tweet count > 1
		for(String[] s : tweetCharMap){
			if(counts.get(s[2])>1){
				lightMap.add(s);
			}
		}

		return lightMap;
	}

	/*
	 * Create and return the array of tweet body to characteristic value
	 */
	public ArrayList<String[]> tweetCharMap(HashMap<Long,String> userCharMap, boolean hardCode){
		ArrayList<String[]> tweetCharMap = new ArrayList<String[]>();
		//String[] will contain: tweet ID, tweet body, characteristic value

		//Get location of master file of tweets
		File f;
		if(!hardCode)
			f = Helper.readFile(in,"Enter location of master file of tweets: ", true);
		else
			f = new File(masterFileLoc);

		//Open connection to master file
		Scanner in2;
		try {
			in2 = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.err.println("Error opening Scanner to master file of tweets.");
			e.printStackTrace();
			return null;
		}

		//Ask for format of tweets
		String type;
		if(!hardCode){
			type = Helper.readString(in, "Are the tweets from a search or a user's timeline? (Enter 'search' or 'timeline'): ");
			if(!type.equals("search") && !type.equals("timeline")){
				System.out.println("Please enter either 'search' or 'timeline'");
				type = Helper.readString(in, "Are the tweets from a search or a user's timeline? (Enter 'search' or 'timeline'): ");
			}
		}else{
			type=searchOrTimeline;
		}

		//
		String holder, text;
		JSONArray holder2;
		JSONObject j,j2;
		Long id;
		while(in2.hasNext()){
			holder = in2.nextLine().trim();
			if(holder != null && holder != ""){
				if(type.equals("search")){
					j = (JSONObject)JSONValue.parse(holder);
					if(j != null && j.get("results") != null){
						holder2 = (JSONArray)j.get("results");
						for(Object s : holder2){
							j2 = (JSONObject)s;
							id = (Long)j2.get("from_user_id");
							if(userCharMap.containsKey(id) && id != null && j2.get("id") != null)
								tweetCharMap.add(new String[]{j2.get("id").toString(),
										j2.get("text") != null ? j2.get("text").toString() : null,
												userCharMap.get(id)});
						}
					}
				}else{
					holder2 = (JSONArray)JSONValue.parse(holder);
					if(holder2 != null){
						for(Object s : holder2){
							j = (JSONObject)s;
							j2 = j == null ? null : (JSONObject)j.get("user");
							if(j != null && j2 != null){
								id = j2.get("id") == null ? null : new Long(j2.get("id").toString());
								text = j.get("text") == null ? null : j.get("text").toString();
								if(id != null & text != null && userCharMap.containsKey(id)){
									tweetCharMap.add(new String[]{j.get("id") == null ? null : j.get("id").toString(),text,userCharMap.get(id)});
								}
							}
						}
					}else{
						System.out.println("Could not parse tweets to JSONArray:");
						System.out.println(holder);
					}
				}
			}
		}

		return tweetCharMap;
	}

	/*
	 * Saves the tweet information into individual files to create a corpus.
	 * 
	 * @param tweetCharMap  List of tweets, their IDs, and their user characteristic value.
	 * @param charName		The chosen characteristic of the Twitter users.
	 * @param hardCode		Whether to use hard coded values or not.
	 * @return				Whether the save was successful or not.
	 */
	public boolean saveTweets(ArrayList<String[]> tweetCharMap, String charName, boolean hardCode){
		//Get location of corpora folder
		String corpora;
		if(!hardCode)
			corpora = Helper.readDir(in, "Enter location of corpora folder: ", true).getAbsolutePath();
		else
			corpora = corporaFolder;
		
		//Ask about saving only a percentage of the whole corpus
		int percent;
		if(hardCode)
			percent = randomPercent;
		else
			percent = Helper.readInt(in, "What percentage of the total tweets do you want to save in the corpus? : ");

		//Open connection to corpora folder
		File cor;
		if(percent < 100){
			cor = new File(corpora+"/"+charName + "_" + percent);
		}else{
			cor = new File(corpora+"/"+charName);
		}

		//If folder already exists, confirm whether to continue or not.
		if(!hardCode){
			String confirm = null;
			boolean flag = cor.exists();
			if(flag){
				System.out.println("The corpus folder already exists.");
				confirm = Helper.readString(in, "Would you like to continue? (Y/N) : ");
				if(!confirm.equalsIgnoreCase("Y"))
					return false;
			}else{
				//If directory doesn't exist, create it
				System.out.println("Corpus folder does not exist.  Creating it.");
				cor.mkdir();
			}
		}else{
			if(!cor.exists())
				cor.mkdir();
		}

		//Create a mapping?
		boolean map;
		if(!hardCode)
			map = Helper.readBool(in,"Do you want a mapping created for the characteristic values? (Y/N) : ");
		else 
			map = charMap;

		//Create the mapping
		HashMap<String, String> theMap = null;
		if(map){
			theMap = new HashMap<String, String>();
			int count = 1;
			for(String[] s : tweetCharMap){
				if(!theMap.containsValue(s[2])){
					theMap.put(s[2],"x"+count);
					s[2] = "x"+(count++);
				}else{
					s[2] = theMap.get(s[2]);
				}
			}
		}

		//Run through all tweets and create a document for them
		//File name ex: char-[char_value]-id-[tweet_id].txt
		Random generator = new Random(System.currentTimeMillis());
		File f;
		BufferedWriter out;
		for(String[] s : tweetCharMap){
			if(!s[2].equals("") && generator.nextDouble() < percent/100.0){ //Make sure the characteristic exists  TODO: Maybe ask user about this?
				f = new File(cor.getAbsolutePath()+"/"+"char-"+s[2]+"-id-"+s[0]+".txt");
				try {
					//Check if file exists.  If it does, do nothing.
					if(!f.exists()){
						f.createNewFile();
						out = new BufferedWriter(new FileWriter(f));
						out.write(s[1]);
						out.close();
					}
				} catch (IOException e) {
					System.err.println("Error creating or writing to file for tweet " + s[0]);
					e.printStackTrace();
				}
			}
		}

		//Save the mapping to a file called metadata.csv in the same folder as the tweets
		if(map){
			Set<String> keys = theMap.keySet();
			try {
				out = new BufferedWriter(new FileWriter(new File(cor.getAbsolutePath()+"/metadata.csv")));
				for(String s : keys){
					out.write(theMap.get(s)+":"+s);
					out.newLine();
				}
				out.close();
			} catch (IOException e) {
				System.err.println("Error writing mapping file.");
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	/*
	 * Reads all tweets and stores them in a corpus folder without storing all tweets in java memory.
	 * Does almost the same work as tweetCharmap and saveTweets except does not store all the tweets
	 *  in a list in java memory.  Because of this, cannot create a light corpus of tweets.
	 *  
	 *  @param chara    The chose characteristic of the Twitter user.
	 *  @param userChar The map of user ID to user characteristic value.
	 *  @param hardCode Whether to use hard coded values or read user input.
	 */
	public void lightSaveTweets(String chara, HashMap<Long, String> userChar, boolean hardCode){
		//Get characteristic, master file of tweets, users folder, and corpus folder
		File corpora;
		File file;
		if(hardCode){
			file = new File(masterFileLoc);
			corpora = new File(corporaFolder);
		}else{
			file = Helper.readFile(in,"Enter location of master file of tweets: ", true);
			corpora = Helper.readDir(in, "Enter location of corpora folder: ", true);
		}

		//Open connection to master file
		Scanner in2;
		try {
			in2 = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.err.println("Error opening Scanner to master file of tweets.");
			e.printStackTrace();
			return;
		}

		//Ask for format of tweets
		boolean timeline;
		if(hardCode)
			timeline = searchOrTimeline.equalsIgnoreCase("timeline");
		else if(Helper.readBool(in, "Are the tweets from a user's timeline? (Y/N) : "))
			timeline = true;
		else if(Helper.readBool(in, "Are the tweets from a search? (Y/N) : "))
			timeline = false;
		else{
			System.out.println("No other tweets formats supported at this time.  Exiting program.");
			return;
		}

		//Ask about saving only a percentage of the whole corpus
		int percent;
		if(hardCode)
			percent = randomPercent;
		else
			percent = Helper.readInt(in, "What percentage of the total tweets do you want to save in the corpus? : ");

		//Open connection to corpora folder
		File cor;
		if(percent < 100){
			cor = new File(corpora+"/"+chara + "_" + percent);
		}else{
			cor = new File(corpora+"/"+chara);
		}

		//If folder already exists, confirm whether to continue or not.
		if(!hardCode){
			String confirm = null;
			boolean flag = cor.exists();
			if(flag){
				System.out.println("The corpus folder already exists.");
				confirm = Helper.readString(in, "Would you like to continue? (Y/N) : ");
				if(!confirm.equalsIgnoreCase("Y"))
					return;
			}else{
				//If directory doesn't exist, create it
				System.out.println("Corpus folder does not exist.  Creating it.");
				cor.mkdir();
			}
		}else{
			if(!cor.exists())
				cor.mkdir();
		}

		//Create a mapping?
		boolean map;
		if(!hardCode)
			map = Helper.readBool(in,"Do you want a mapping created for the characteristic values? (Y/N) : ");
		else 
			map = charMap;

		//Create the mapping
		HashMap<String, String> theMap = null;
		int count = 1;
		if(map){
			theMap = new HashMap<String, String>();
		}

		//File name ex: char-[char_value]-id-[tweet_id].txt
		Random generator = new Random(System.currentTimeMillis());
		while(in2.hasNext()){
			String holder = in2.nextLine().trim();
			if(holder != ""){
				//Pull out tweet body, ID and user ID
				ArrayList<String[]> listHolder = getTweets(holder,timeline);
				///Each String[] contains: tweet body, tweet ID, from user ID
				for(String[] s : listHolder){
					String chars = userChar.get(new Long(s[2])); 
					if(chars != "" && generator.nextDouble() < percent/100.0){  //Check for random subset
						File f = new File(cor.getAbsolutePath()+"/"+"char-"+chars+"-id-"+s[1]+".txt");
						//Save to map if needed
						if(map){
							if(!theMap.containsValue(chars)){
								theMap.put(s[2],"x"+count);
								s[2] = "x"+(count++);
							}else{
								s[2] = theMap.get(s[2]);
							}
						}
						//Save the tweet
						saveTweet(f,s[0]);
						//TODO: Do something if it fails?
					}
				}
			}
		}

		//Save the mapping to a file called metadata.csv in the same folder as the tweets
		if(map){
			Set<String> keys = theMap.keySet();
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(cor.getAbsolutePath()+"/metadata.csv")));
				for(String s : keys){
					out.write(theMap.get(s)+":"+s);
					out.newLine();
				}
				out.close();
			} catch (IOException e) {
				System.err.println("Error writing mapping file.");
				e.printStackTrace();
				return;
			}
		}
	}

	/*
	 * Takes a tweet json object and seperates out the tweets along with their ID and from user ID.
	 * 
	 * @param body 	  The json object.
	 * @param timline Whether or not the json object is from a timeline or not.
	 * @return		  The tweet bodies, IDs, and from user IDs in a list
	 */
	public ArrayList<String[]> getTweets(String body, boolean timeline){
		ArrayList<String[]> list = new ArrayList<String[]>();

		JSONArray holder2;
		JSONObject j,j2;
		Long id;
		if(body != null && body != ""){
			if(!timeline){
				j = (JSONObject)JSONValue.parse(body);
				if(j != null && j.get("results") != null){
					holder2 = (JSONArray)j.get("results");
					for(Object s : holder2){
						j2 = (JSONObject)s;
						id = (Long)j2.get("from_user_id");
						list.add(new String[]{j2.get("text") == null ? null : j2.get("text").toString(),
								j2.get("id") == null ? null : j2.get("id").toString(),id == null ? null : id.toString()});
					}
				}
			}else{
				holder2 = (JSONArray)JSONValue.parse(body);
				if(holder2 != null){
					for(Object s : holder2){
						j = (JSONObject)s;
						j2 = j == null ? null : (JSONObject)j.get("user");
						if(j != null && j2 != null){
							list.add(new String[]{j.get("text") == null ? null : j.get("text").toString(),
									j.get("id") == null ? null : j.get("id").toString(),j2.get("id") == null ? null : j2.get("id").toString()});
						}
					}
				}else{
					System.out.println("Could not parse tweets to JSONArray:");
					System.out.println(body);
				}
			}
		}

		return list;
	}

	/*
	 * Saves a tweet to the specified file.
	 * 
	 * @param f    The file in which to save the tweet.
	 * @param body The body/text of the tweet.
	 * @return	   Whether the save was successful or not.
	 */
	public boolean saveTweet(File f, String body){
		if(f.exists())
			return false;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(body);
			out.close();
		} catch (IOException e) {
			System.err.println("Error writing tweet file.");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
