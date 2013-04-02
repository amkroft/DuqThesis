package twitterTweets.generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Statistics {
	//Class to calculate statistics of the data

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 0)
			return;
		else if(args[0].equals("tweets"))
			tweetStats();
		else if(args[0].equals("users"))
			getUsers();
		else if(args[0].equals("test"))
			getTweets();
	}
	
	/*
	 * Calculates various statistics on the tweet corpus, including total tweets
	 * and max/min size.
	 */
	public static void tweetStats(){
		String[] tweets = getTweets();

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("tweetStatistics.txt")));

			out.write("Total tweets: " + tweets.length);
	
			//Get min and max length of tweets
			//System.out.println("Calculating min and max length of tweets.");
			int minLength = -1, maxLength = -1;
			String largest = "";
			for(String s : tweets){
				if(minLength == -1 || s.length() < minLength){
					minLength = s.length();
				}
				if(maxLength == -1 || s.length() > maxLength){
					maxLength = s.length();
					largest = s;
				}
			}
	
			out.write("Minimum length of tweets: " + minLength);
			out.write("Maximum length of tweets: " + maxLength);
			out.write("Largest tweet: " + largest);
	
			//Get min and max word counts of tweets
			//System.out.println("Calculating min and max word counts of tweets.");
			int minWordCount = -1, maxWordCount = -1;
			for(String s : tweets){
				String[] holder = s.split("\\s+");
				if(minWordCount == -1 || holder.length < minWordCount)
					minWordCount = holder.length;
				if(maxWordCount == -1 || holder.length > maxWordCount){
					maxWordCount = holder.length;
					largest = s;
				}
			}
	
			out.write("Minimum word count: " + minWordCount);
			out.write("Maximum word count: " + maxWordCount);
			out.write("Tweet with most words: " + largest);
	
			//Get min and max word size of tweets
			//System.out.println("Calculating min and max word sizes of tweets.");
			int minWordLength = -1, maxWordLength = -1;
			for(String s : tweets){
				String[] holder = s.split("\\s+");
				for(String t : holder){
					if(minWordLength == -1 || t.length() < minWordLength)
						minWordLength = t.length();
					if(maxWordLength == -1 || t.length() > maxWordLength){
						maxWordLength = t.length();
						largest = t;
					}
				}
			}
	
			out.write("Minimum word length: " + minWordLength);
			out.write("Maximum word length: " + maxWordLength);
			out.write("Longest word: " + largest);
			
			out.close();
		} catch (IOException e) {
			System.err.println("Error writing to statistics file.");
			e.printStackTrace();
		}
	}

	/*
	 * Pulls out the text of all the tweets and stores them in a String array.  The array
	 * is returned.
	 */
	public static String[] getTweets(){
		ArrayList<String> tweets = new ArrayList<String>();
		HashSet<Long> tweetIds = new HashSet<Long>();//To make sure not saving tweets twice

		//Get location of master file of tweets
		//String file = Extras.readString(new BufferedReader(new InputStreamReader(System.in)),"Enter location of master file of tweets: ");
		String file = "tweets/masterTweets.csv";

		//Open connection to master file
		File f = new File(file);
		Scanner in2;
		try {
			in2 = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.err.println("Error opening Scanner to masterFile.csv");
			e.printStackTrace();
			return null;
		}

		//Run through master file and store tweets in ArrayList
		String holder;
		JSONArray holder2;
		JSONObject j2;
		Long id;
		Object holder3;
		//Run through all lines of masterFile
		while(in2.hasNext()){
			holder = in2.nextLine();
			//If line is not empty
			if(holder != null && holder != ""){
				holder3 = JSONValue.parse(holder);
				//Depending on type of objects, grab array of tweets or continue to next iteration
				if(holder3 instanceof JSONObject && holder3 != null && ((JSONObject)holder3).get("results") != null){
					holder2 = (JSONArray)(((JSONObject)holder3).get("results"));
				}else if(holder3 instanceof JSONArray){
					holder2 = (JSONArray)holder3;
				}else{
					continue;
				}
				
				//Run through all results in the JSONArray holder2
				for(Object s : holder2){
					j2 = (JSONObject)s;
					id = (Long)j2.get("id");
					//System.out.println("Processing tweet " + id);
					if(id != null && !tweetIds.contains(id)){
						tweetIds.add(id);
						//Add tweet to list of tweets
						tweets.add(j2.get("text") != null ? HtmlManipulator.replaceHtmlEntities(j2.get("text").toString()) : null);
					}
				}
			}
		}

		return tweets.toArray(new String[]{});
	}

	@Deprecated
	public static void userStats(){
		HashMap<Long, String> users = getUsers();
		Set<Long> keyset = users.keySet();

		//Get booleans: contributors_enabled, default_profile, default_profile_image, geo_enabled,
		//  is_translator, notifications, profile_background_tile, profile_use_background_image, protected, 
		//  show_all_inline_media, verified
		int trueCount = 0, nullCount = 0;
		//contributors_enabled
		for(Long l : keyset){
			JSONObject j = (JSONObject)JSONValue.parse(users.get(l));
			if(j != null){
				String s = j.get("contributors_enabled") == null ? j.get("contributors_enabled").toString() : null;
				if(s == null || s.equalsIgnoreCase("null")){
					nullCount++;
				}else if(s.equalsIgnoreCase("true")){
					trueCount++;
				}
			}
		}

		//Get ints: favourites_count, followers_count, follow_request_sent, friends_count, listed_count, statuses_count

		//Get hex colors: profile_background_color, profile_link_color, profile_sidebar_border_color,
		//  profile_sidebar_fill_color, profile_text_color

		//Other info: created_at, lang, location, name, profile_background_image_url, profile_image_url, 
		//  status, time_zone, url
	}

	@Deprecated
	public static void userTable(){
		HashMap<Long, String> users = getUsers();
		Long[] keyset = users.keySet().toArray(new Long[]{});

		String[][] userTable = new String[keyset.length][32];

		for(int i = 0; i < keyset.length; i++){
			JSONObject j = (JSONObject)JSONValue.parse(users.get(keyset[i]));
			if(j != null){
				userTable[i][0] = keyset[i].toString();
				userTable[i][1] = j.get("contributors_enabled") != null ? j.get("contributors_enabled").toString() : "null";
				userTable[i][2] = j.get("default_profile") != null ? j.get("default_profile").toString() : "null";
				userTable[i][3] = j.get("default_profile_image") != null ? j.get("default_profile_image").toString() : "null";
				userTable[i][4] = j.get("geo_enabled") != null ? j.get("geo_enabled").toString() : "null";
				userTable[i][5] = j.get("is_translator") != null ? j.get("is_translator").toString() : "null";
				userTable[i][6] = j.get("notifications") != null ? j.get("notifications").toString() : "null";
				userTable[i][7] = j.get("profile_background_tile") != null ? j.get("profile_background_tile").toString() : "null";
				userTable[i][8] = j.get("profile_use_background_image") != null ? j.get("profile_use_background_image").toString() : "null";
				userTable[i][9] = j.get("protected") != null ? j.get("protected").toString() : "null";
				userTable[i][10] = j.get("show_all_inline_media") != null ? j.get("show_all_inline_media").toString() : "null";
				userTable[i][11] = j.get("verified") != null ? j.get("verified").toString() : "null";
				userTable[i][12] = j.get("favourites_count") != null ? j.get("favourites_count").toString() : "null";
				userTable[i][13] = j.get("followers_count") != null ? j.get("followers_count").toString() : "null";
				userTable[i][14] = j.get("follow_request_sent") != null ? j.get("follow_request_sent").toString() : "null";
				userTable[i][15] = j.get("friends_count") != null ? j.get("friends_count").toString() : "null";
				userTable[i][16] = j.get("listed_count") != null ? j.get("listed_count").toString() : "null";
				userTable[i][17] = j.get("statuses_count") != null ? j.get("statuses_count").toString() : "null";
				userTable[i][18] = j.get("profile_background_color") != null ? j.get("profile_background_color").toString() : "null";
				userTable[i][19] = j.get("profile_link_color") != null ? j.get("profile_link_color").toString() : "null";
				userTable[i][20] = j.get("profile_sidebar_border_color") != null ? j.get("profile_sidebar_border_color").toString() : "null";
				userTable[i][21] = j.get("profile_sidebar_fill_color") != null ? j.get("profile_sidebar_fill_color").toString() : "null";
				userTable[i][22] = j.get("profile_text_color") != null ? j.get("profile_text_color").toString() : "null";
				userTable[i][23] = j.get("created_at") != null ? j.get("created_at").toString() : "null";
				userTable[i][24] = j.get("lang") != null ? j.get("lang").toString() : "null";
				userTable[i][25] = j.get("location") != null ? j.get("location").toString() : "null";
				userTable[i][26] = j.get("name") != null ? j.get("name").toString() : "null";
				userTable[i][27] = j.get("profile_background_image_url") != null ? j.get("profile_background_image_url").toString() : "null";
				userTable[i][28] = j.get("profile_image_url") != null ? j.get("profile_image_url").toString() : "null";
				userTable[i][29] = j.get("status") != null ? j.get("status").toString() : "null";
				userTable[i][30] = j.get("time_zone") != null ? j.get("time_zone").toString() : "null";
				userTable[i][31] = j.get("url") != null ? j.get("url").toString() : "null";
			}
		}

		System.out.println("Finished organizing data into a table.");

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("userInfo.csv")));
			for(int i = 0; i < userTable.length; i++){
				for(int j = 0; j < userTable[i].length; j++){
					out.write(userTable[i][j]);
					if(j < userTable[i].length-1){
						out.write("\t");
					}
				}
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Error opening BufferedWriter or writing to userInfo.csv file");
			e.printStackTrace();
		}

		System.out.println("Table written.");

	}

	public static HashMap<Long, String> getUsers(){
		System.out.println("In getUsers methods.");
		//Get location of folder of tweets
		//String file = Extras.readString(new BufferedReader(new InputStreamReader(System.in)),"Enter location of directory of users: ");
		String file = "users/";

		//Make sure it is a directory
		File f = new File(file);
		/*while(!f.isDirectory()){
			System.err.println("Location is not a directory.  Please try again.");
			file = Extras.readString(new BufferedReader(new InputStreamReader(System.in)),"Enter location of master file of tweets: ");
			f = new File(file);
		}*/

		System.out.println("Running through " + f.listFiles().length + " user files.");
		HashMap<Long, String> users = new HashMap<Long, String>();


		BufferedWriter out;
		File o = new File("userInfo.csv");
		if(o.exists())
			o.delete();
		try {
			out = new BufferedWriter(new FileWriter(o, true));
			out.write("id\tcontributors_enabled\tdefault_profile\tdefault_profile_image\tgeo_enabled");
			out.write("\tis_translator\tprofile_background_tile\tprofile_use_background_image");
			out.write("\tprotected\tshow_all_inline_media\tverified\tfavourites_count\tfollowers_count");
			out.write("\tfriends_count\tlisted_count\tstatuses_count\tprofile_background_color_hex\tprofile_background_color");
			out.write("\tprofile_link_color_hex\tprofile_link_color\tprofile_sidebar_border_color_hex\tprofile_sidebar_border_color" +
					"\tprofile_sidebar_fill_color_hex\tprofile_sidebar_fill_color");
			out.write("\tprofile_text_color_hex\tprofile_text_color\tcreated_at\tlang\tname\ttime_zone\tutc_offset\turl");
			out.newLine();

			for(File s : f.listFiles()){
				Scanner in;
				String stuff = "";
				try{
					in = new Scanner(s);
					while(in.hasNext()){stuff += in.nextLine();}
					in.close();
					
					writeJSON(out,s.getName().substring(s.getName().indexOf("_")+1,s.getName().indexOf(".")),stuff);
				}catch (FileNotFoundException e) {
					System.err.println("Could not find file: " + s.getName());
					e.printStackTrace();
				}
			}//End of for loop through user files

			out.close();
		} catch (IOException e) {
			System.err.println("Error writing to userInfo.csv file.");
			e.printStackTrace();
		}

		//System.out.println("Finished reading all user files.");
		System.out.println("Finished writing all information.");
		return users;
	}

	public static void writeJSON(BufferedWriter out, String id, String stuff){
		JSONArray j1 = (JSONArray)JSONValue.parse(stuff);
		JSONObject j = j1 != null ? (JSONObject)j1.get(0) : null;
		String holder;
		if(j != null){
			try{
				out.write(id + "\t");
				out.write(j.get("contributors_enabled") != null ? j.get("contributors_enabled").toString() : "null"); //2
				out.write("\t");
				out.write(j.get("default_profile") != null ? j.get("default_profile").toString() : "null"); //3
				out.write("\t");
				out.write(j.get("default_profile_image") != null ? j.get("default_profile_image").toString() : "null"); //4
				out.write("\t");
				out.write(j.get("geo_enabled") != null ? j.get("geo_enabled").toString() : "null"); //5
				out.write("\t");
				out.write(j.get("is_translator") != null ? j.get("is_translator").toString() : "null"); //6
				out.write("\t");
				//out.write(j.get("notifications") != null ? j.get("notifications").toString() : "null"); //7
				//out.write("\t");
				out.write(j.get("profile_background_tile") != null ? j.get("profile_background_tile").toString() : "null"); //8
				out.write("\t");
				out.write(j.get("profile_use_background_image") != null ? j.get("profile_use_background_image").toString() : "null"); //9
				out.write("\t");
				out.write(j.get("protected") != null ? j.get("protected").toString() : "null"); //10
				out.write("\t");
				out.write(j.get("show_all_inline_media") != null ? j.get("show_all_inline_media").toString() : "null"); //11
				out.write("\t");
				out.write(j.get("verified") != null ? j.get("verified").toString() : "null"); //12
				out.write("\t");
				out.write(j.get("favourites_count") != null ? j.get("favourites_count").toString() : "null"); //13
				out.write("\t");
				out.write(j.get("followers_count") != null ? j.get("followers_count").toString() : "null"); //14
				out.write("\t");
				//out.write(j.get("follow_request_sent") != null ? j.get("follow_request_sent").toString() : "null"); //15
				//out.write("\t");
				out.write(j.get("friends_count") != null ? j.get("friends_count").toString() : "null"); //16
				out.write("\t");
				out.write(j.get("listed_count") != null ? j.get("listed_count").toString() : "null"); //17
				out.write("\t");
				out.write(j.get("statuses_count") != null ? j.get("statuses_count").toString() : "null"); //18
				out.write("\t");
				holder = j.get("profile_background_color") != null ? j.get("profile_background_color").toString().toUpperCase() : "null";
				out.write(holder); //19
				out.write("\t");
				out.write(holder.equals("null") ? "null" : Extras.rgbColorToString("0x"+holder));
				out.write("\t");
				holder = j.get("profile_link_color") != null ? j.get("profile_link_color").toString().toUpperCase() : "null";
				out.write(holder); //20
				out.write("\t");
				out.write(holder.equals("null") ? "null" : Extras.rgbColorToString("0x" + holder));
				out.write("\t");
				holder = j.get("profile_sidebar_border_color") != null ? j.get("profile_sidebar_border_color").toString().toUpperCase() : "null";
				out.write(holder); //21
				out.write("\t");
				out.write(holder.equals("null") ? "null" : Extras.rgbColorToString("0x" + holder));
				out.write("\t");
				holder = j.get("profile_sidebar_fill_color") != null ? j.get("profile_sidebar_fill_color").toString().toUpperCase() : "null";
				out.write(holder); //22
				out.write("\t");
				out.write(holder.equals("null") ? "null" : Extras.rgbColorToString("0x" + holder));
				out.write("\t");
				holder = j.get("profile_text_color") != null ? j.get("profile_text_color").toString().toUpperCase() : "null";
				out.write(holder); //23
				out.write("\t");
				out.write(holder.equals("null") ? "null" : Extras.rgbColorToString("0x" + holder));
				out.write("\t");
				out.write(j.get("created_at") != null ? j.get("created_at").toString() : "null"); //24 TODO: Format to just month and year
				out.write("\t");
				out.write(j.get("lang") != null ? j.get("lang").toString() : "null"); //25
				out.write("\t");
				/*out.write(j.get("location") != null ? j.get("location").toString().trim().replace("\\s+"," ") : "null"); //26
				out.write("\t");*/
				out.write(j.get("name") != null ? cleanName(j.get("name").toString()).toLowerCase() : "null"); //27
				out.write("\t");
				/*out.write(j.get("profile_background_image_url") != null ? j.get("profile_background_image_url").toString() : "null"); //28
				out.write("\t");
				out.write(j.get("profile_image_url") != null ? j.get("profile_image_url").toString() : "null"); //29
				out.write("\t");
				out.write(j.get("status") != null ? j.get("status").toString() : "null");
				out.write("\t");*/
				out.write(j.get("time_zone") != null ? j.get("time_zone").toString() : "null"); //30
				out.write("\t");
				out.write(j.get("utc_offset") != null ? j.get("utc_offset").toString() : "null");
				out.write("\t");
				out.write(j.get("url") != null ? Extras.getDomain(j.get("url").toString().toLowerCase()) : "null"); //31
				out.newLine();
			} catch (IOException e) {
				System.err.println("Error writing to userInfo.csv file");
				e.printStackTrace();
			}
		}
	}
	
	public static String cleanName(String name){
		return name.split("\\s+")[0].trim();
	}
}
