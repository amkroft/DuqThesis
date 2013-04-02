package twitterTweets.generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ManageTweets {

	/**
	 * Main method just switches to correct method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args != null && args.length > 0){
			if(args[0].equals("seperate"))
				separateTweets();
			else if(args[0].equals("masterFile"))
				if(args.length == 1){
					masterFile();
				}else if(args[1].equals("clean")){
					cleanMaster();
				}else if(args[1].equals("combine")){
					if(args.length > 2)
						combineMaster(new File(args[2]));
					else
						combineMaster(new File("masterTweets"));
				}
		}
	}

	public static void separateTweets(){
		String path = Extras.path + "tweets/originals";
		File location = new File(path);
		String holder1 = "";
		JSONArray holder4;
		Scanner in;
		BufferedWriter out;
		File n;
		if(location.isDirectory()){
			for(String f : location.list()){
				n = new File(path + "/" + f);
				try {
					in = new Scanner(n);
					holder1 = "";
					while(in.hasNext()){holder1 += in.nextLine();}
					in.close();
					JSONObject j = (JSONObject)JSONValue.parse(holder1);
					if(j != null && j.get("results") != null){
						holder4 = (JSONArray)j.get("results");
						JSONObject j2;
						for(Object s : holder4){
							j2 = (JSONObject)s;
							System.out.println(j2.get("id")+"\n");
							if(n.getName().contains("-47--122-")){
								//Seattle, Washington tweets
								out = new BufferedWriter(new FileWriter(new File(Extras.path + "tweets/seperatedTweets/byLocation/Seattle-"
										+j2.get("id")+".txt")));
								out.write((String)j2.get("text"));
								out.close();
							}else if(n.getName().contains("-40--80-")){
								//Pittsburgh, PA tweets
								out = new BufferedWriter(new FileWriter(new File(Extras.path + "tweets/seperatedTweets/byLocation/Pittsburgh-"
										+j2.get("id")+".txt")));
								out.write((String)j2.get("text"));
								out.close();
							}else{
								//Tweets by user
								out = new BufferedWriter(new FileWriter(new File(Extras.path + "tweets/seperatedTweets/byUser/"
										+j2.get("from_user_id")+"-"+j2.get("id")+".txt")));
								out.write((String)j2.get("text"));
								out.close();
							}
						}
					}else{
						if(j == null){
							out = new BufferedWriter(new FileWriter(new File(Extras.path + "tweets/seperatedTweets/errorFiles.txt"),true));
							out.write("JSONObject is null for file " + f + "\n");
							System.out.println("JSONObject is null.");
						}else{
							System.out.println("No results for type name \"results\"");
						}
					}
				} catch (FileNotFoundException e) {
					System.out.println("Error reading file: "+n.getName());
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Error writing to new file.");
					e.printStackTrace();
				}
				//break; //If working with only first file for now
			}
		}
	}

	public static void separateTweetsByLocation(){
		String path = Extras.path + "tweets/originals";
		File location = new File(path);
		String holder1 = "";
		JSONArray holder4;
		Scanner in;
		BufferedWriter out;
		File n;
		if(location.isDirectory()){
			for(String f : location.list()){
				n = new File(path + "/" + f);
				try {
					in = new Scanner(n);
					holder1 = "";
					while(in.hasNext()){holder1 += in.nextLine();}
					in.close();
					JSONObject j = (JSONObject)JSONValue.parse(holder1);
					if(j != null && j.get("results") != null){
						holder4 = (JSONArray)j.get("results");
						JSONObject j2;
						for(Object s : holder4){
							j2 = (JSONObject)s;
							//System.out.println(j2.get("id")+"\n");
							if(n.getName().contains("-47--122-")){
								//Seattle, Washington tweets
								out = new BufferedWriter(new FileWriter(new File(Extras.path + "tweets/seperatedTweets/byLocation/Seattle-"
										+j2.get("id")+".txt")));
								out.write((String)j2.get("text"));
								out.close();
							}else if(n.getName().contains("-40--80-")){
								//Pittsburgh, PA tweets
								out = new BufferedWriter(new FileWriter(new File(Extras.path + "tweets/seperatedTweets/byLocation/Pittsburgh-"
										+j2.get("id")+".txt")));
								out.write((String)j2.get("text"));
								out.close();
							}else{
								//Tweets by user
								//Do nothing
							}
						}
					}else{
						if(j == null){
							out = new BufferedWriter(new FileWriter(new File(Extras.path + "tweets/seperatedTweets/errorFiles.txt"),true));
							out.write("JSONObject is null for file " + f + "\n");
							//System.out.println("JSONObject is null.");
							out.close();
						}else{
							System.out.println("No results for type name \"results\"");
						}
					}
				} catch (FileNotFoundException e) {
					System.out.println("Error reading file: "+n.getName());
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Error writing to new file.");
					e.printStackTrace();
				}
				//break; //Working with only first file for now
			}
		}
	}

	/*
	 * This method is deprecated by the TwitterExperiments program.
	 */
	@Deprecated
	public static void createPburghVSSeattleCSV(boolean rand){
		String path = Extras.path + "tweets/seperatedTweets/byLocation";
		File location = new File(path);
		int pcount = 0;
		int scount = 0;
		if(rand){
			for(File f : location.listFiles()){
				if(f.getName().contains("burgh"))	pcount++;
				else if(f.getName().contains("Seattle")) scount++;
			}
		}
		BufferedWriter out;

		String author;
		try {

			if(location.isDirectory()){
				if(!rand){
					out = new BufferedWriter(new FileWriter(new File(Extras.path + "tweets/experiments/PburghVSSeattleDocs.csv")));
					boolean pburgh = false;
					boolean seattle = false;
					for(File f : location.listFiles()){
						author = f.getName().substring(0,f.getName().indexOf("-"));
						if(author.equals("Pittsburgh")&&!pburgh){
							out.write(",");
							pburgh = true;
						}else if(author.equals("Seattle")&&!seattle){
							out.write(",");
							seattle = true;
						}else{
							out.write(author+",");						
						}
						out.write(f.getAbsolutePath()+",");
						out.write(f.getName()+"\n");
					}
					out.close();
				}else{
					//Create two random numbers
					Random generator = new Random(System.currentTimeMillis());
					int pburgh = generator.nextInt(pcount);
					int seattle = generator.nextInt(scount);
					out = new BufferedWriter(new FileWriter(new File(Extras.path + "tweets/experiments/PburghVSSeattleDocs-"+pburgh+"-"+seattle+".csv")));
					//Reuse pcount and scount variables to be counters as we loop through files
					pcount = 0;
					scount = 0;

					for(File f : location.listFiles()){
						author = f.getName().substring(0,f.getName().indexOf("-"));
						if(author.equals("Pittsburgh")){
							pcount++;
							if(pcount == pburgh)
								out.write(",");
							else
								out.write(author+",");
						}else if(author.equals("Seattle")){
							scount++;
							if(scount==seattle)
								out.write(",");
							else
								out.write(author+",");					
						}
						out.write(f.getAbsolutePath()+",");
						out.write(f.getName()+"\n");
					}
					out.close();
				}
			}
		} catch (IOException e) {
			System.err.println("Error opening writer to documents.csv.");
			e.printStackTrace();
		}
	}

	/*
	 * Takes all tweets in the originals folder and moves them into the masterFile.csv
	 */
	public static void masterFile(){
		//First separate the tweets by location
		separateTweetsByLocation();
		//System.out.println("Finished pulling out tweets by location.");

		File folder = new File(Extras.path + "tweets/originals");
		if(!folder.exists() || !folder.isDirectory()){
			System.err.println("Error accessing directory: "+Extras.path+"tweets/originals");
			return;
		}
		try {
			File master = new File(Extras.path + "tweets/masterFile.csv");
			master.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(master, true));
			out.newLine();
			out.newLine();
			Scanner in;
			String holder;
			for(File f : folder.listFiles()){
				//System.out.println("Reading file: "+f.getName());
				in = new Scanner(f);
				holder = "";
				while(in.hasNext()){holder += in.nextLine();}
				out.write(holder);
				out.newLine();
				out.newLine();
				f.deleteOnExit();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Error writing to masterFile.csv");
			e.printStackTrace();
		}

	}

	/*
	 * Cleans any entries in the masterFile.csv that have two json objects next to each other.
	 */
	public static void cleanMaster(){
		File oldf = new File(Extras.path + "tweets/masterFile.csv");
		File newf = new File(Extras.path + "tweets/newMasterFile.csv");
		if(newf.exists()){
			newf.delete();
			try {
				newf.createNewFile();
			} catch (IOException e) {
				System.err.println("Error creating newMasterFile.csv for cleaning of Master file.");
				e.printStackTrace();
				System.exit(1);
			}
		}

		try {
			Scanner in = new Scanner(oldf);
			BufferedWriter out = new BufferedWriter(new FileWriter(newf));
			while(in.hasNext()){
				out.write(in.nextLine().replace("}{", "}\n\n{"));
			}
			out.close();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error opening Scanner to masterFile.csv");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error opening BufferedWriter to newMasterFile.csv");
			e.printStackTrace();
			System.exit(1);
		}

		boolean test = oldf.delete();
		if(!test){
			System.err.println("Error deleting old masterFile.csv");
		}else{
			test = newf.renameTo(new File(Extras.path+"tweets/masterFile.csv"));
			if(!test){
				System.err.println("Error renaming newMasterFile.csv to masterFile.csv");
			}
		}
	}

	//Creates an experiment
	@Deprecated
	public static void highTweeters(){
		//From higherThanXXXTweets.csv
		//Separate tweets into Extras.path+"/tweets/experiments/higherThanXXXTweets/"
		//Create higherThanXXXTweetsDocs.csv in Extras.path+"/tweets/experiments/"
		//Want 90% to train and 10% to test

		HashSet<Long> userIDs = new HashSet<Long>();

		//Read the higher Tweeters from higherThanXXXTweets.csv and store user IDs in userIDs
		try {
			Scanner usersIn = new Scanner(new File(Extras.path+"/higherThan100Tweets.csv"));
			while(usersIn.hasNext()){
				userIDs.add(usersIn.nextLong());
			}
			usersIn.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error reading higherThan100Tweets.csv");
			e.printStackTrace();
			return;
		}

		//Create directory to hold all the tweets by users in userIDs
		File dir = new File(Extras.path+"/tweets/experiments/higherThan100Tweets");
		if(!(dir.exists() && dir.isDirectory())){
			if(!dir.mkdir()){
				System.err.println("Error creating higherThan100Tweets directory.");
				return;
			}
		}

		ArrayList<String[]> tweets = new ArrayList<String[]>();

		//Pull out tweets by authors in userIDs and put them in individual files
		try {
			Scanner masterF = new Scanner(new File(Extras.path+"/tweets/masterFile.csv"));
			String holder;
			while(masterF.hasNext()){
				holder = masterF.nextLine().trim();
				if(holder.length() > 2){
					JSONObject j = (JSONObject)JSONValue.parse(holder);
					if(j != null){
						//Array of tweet objects
						JSONArray j2 = (JSONArray)j.get("results");
						for(Object o : j2){
							//This is a single tweet object
							JSONObject j3 = (JSONObject)o;
							//Want to pull out the tweet body, user ID and tweet ID
							if(userIDs.contains(j3.get("from_user_id")))
								tweets.add(new String[]{j3.get("id").toString(),j3.get("from_user_id").toString(),j3.get("text").toString()});
						}
					}
				}
			}
			masterF.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error reading masterFile.csv");
			e.printStackTrace();
		}

		//Save all the tweets in Extras.path+"/tweets/experiments/higherThanXXXTweets/"
		BufferedWriter out;
		for(String[] s : tweets){
			try {
				out = new BufferedWriter(new FileWriter(Extras.path+"/tweets/experiments/higherThan100Tweets/"+"u-"+s[1]+"-id-"+s[0]+".txt"));
				out.write(s[2]);
				out.close();
			} catch (IOException e) {
				System.err.println("Error writing to file: "+"u-"+s[1]+"-id-"+s[0]+".txt");
				e.printStackTrace();
			}
			
		}

		//Create higherThanXXXTweetsDocs.csv in Extras.path+"/tweets/experiments/"
		//Need 90% as training and 10% as test
		Random generator = new Random(System.currentTimeMillis());
		try {
			out = new BufferedWriter(new FileWriter(new File(Extras.path+"/tweets/experiments/higherThan100TweetsDocs.csv")));
			for(String[] s : tweets){
				if(generator.nextDouble() <= .1){
					out.write(",");
				}else{
					out.write(s[1]+",");
				}
				out.write(Extras.path+"/tweets/experiments/higherThan100Tweets/"+"u-"+s[1]+"-id-"+s[0]+".txt");
				out.write(","+"u-"+s[1]+"-id-"+s[0]+".txt");
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Error writing docs file.");
			e.printStackTrace();
		}
	}

	/*
	 * Takes several masterTweets.csv files and combines them into one master file.
	 * Used on the masterTweets.csv file created when pulling user's timelines.
	 * Used when multiple machines pulled timelines, hence multiple masterTweets.csv files.
	 * Also cleans out all objects which cannot be parsed to a JSONArray.
	 */
	public static void combineMaster(File f){
		//Get location of masterTweet.csv files
		/*String loc = Extras.readString(new BufferedReader(new InputStreamReader(System.in)), "Enter folder containing master tweet files: ");
		File f = new File(loc);
		if(!f.exists() || !f.isDirectory()){
			System.out.println("Entered location does not exist or is not a directory.  Please retry.");
			loc = Extras.readString(new BufferedReader(new InputStreamReader(System.in)), "Enter folder containing master tweet files: ");
		}*/
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("masterMasterTweets.csv")));
			BufferedWriter error = new BufferedWriter(new FileWriter(new File("masterTweetsErrors.csv")));
			for(File g : f.listFiles()){
				Scanner in = new Scanner(g);
				String holder;
				while(in.hasNext()){
					holder = in.nextLine();
					JSONArray j = (JSONArray)JSONValue.parse(holder);
					if(j != null){
						out.write(holder);
						out.newLine();
					}else if(!holder.trim().equals("")){
						error.write(holder);
						error.newLine();
					}
				}
				in.close();
			}
			out.close();
			error.close();
		} catch (IOException e) {
			System.err.println("Error writing to new master files.");
			e.printStackTrace();
		}
	}//End of combineMaster method
}
