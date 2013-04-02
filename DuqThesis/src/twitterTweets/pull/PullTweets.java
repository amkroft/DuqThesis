/**
 * 
 */
package twitterTweets.pull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Set;

import tweets.api.TwitterAPI;
import tweets.generic.Extras;
import tweets.generic.UserHelper;


/**
 * @author Amanda Kroft
 *
 */

//https://api.twitter.com/1/statuses/show.json?id=149587380398071800

public class PullTweets {

	String text;
	Scanner reader;
	BufferedWriter output;
	File file;
	Calendar date;

	public PullTweets(){
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PullTweets program = new PullTweets();
		if(args.length > 0 && args[0].equals("timeline")){
			program.pullUserTimeline();
		}else{
			program.pullAlphabet();
			//Seattle, Washington
			//47.598755,-122.34375
			program.pullLocation("47,-122");
			//Pittsburgh, Pennsylvania
			//40.438586,-80.013428
			program.pullLocation("40,-80");
		}
	}

	/*
	 * Pulls tweets for each letter of the alphabet.
	 */
	private void pullAlphabet(){
		char letter;
		//for(int i = 97; i <= 122; i++){ //Run through alphabet
		for(int i = 97; i <= 97; i++){ //Run through alphabet
			letter = (char)i;

			//Call the Twitter API for the tweets
			text = TwitterAPI.search("lang=en&q="+letter);

			//Create the file to write results to
			date = Calendar.getInstance();
			///file = new File(System.getProperty("user.dir")+"/tweets/tweets-"+letter+"-"+date.getTimeInMillis()+".json");
			file = new File(Extras.path+"tweets/originals/tweets-"+letter+"-"+date.getTimeInMillis()+".json");
			try {
				file.createNewFile();
			} catch (IOException e1) {
				System.err.println("Error creating file: "+file.getAbsolutePath());
				e1.printStackTrace();
				System.exit(1);
			}

			//Write the results to the file
			try {
				output = new BufferedWriter(new FileWriter(file));
				output.write(text);
				output.close();
			} catch (IOException e) {
				System.err.println("Error writing file.");
				e.printStackTrace();
				System.exit(1);
			}

			//Write the user IDs to the collective file of users
			file = new File(Extras.path+"userCollection.txt");

			//If userCollections file does not exist, create it
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e1) {
					System.err.println("Error creating file: "+file.getAbsolutePath());
					e1.printStackTrace();
					System.exit(1);
				}
			}

			//Write the new users to the collections
			try {
				output = new BufferedWriter(new FileWriter(file, true));
				//output.write(text);
				String[] holder = text.split(",");
				for(String s : holder){
					if(s.contains("\"from_user_id\"")){
						output.write(s.split(":")[1]+"\n");
					}
				}
				output.close();
			} catch (IOException e) {
				System.err.println("Error writing file.");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/*
	 * Pulls tweets located around a certain location.
	 * @param loc String of latitude,longitude of location
	 */
	//TODO: Save the collected users names in userCollection.txt
	private void pullLocation(String loc){
		text = TwitterAPI.search("lang=en&q=a&geocode="+loc+",20mi");

		//Create the file to write results to
		date = Calendar.getInstance();
		//file = new File(System.getProperty("user.dir")+"/tweets/tweets-"+loc.replaceAll(",","-")+"-"+date.getTimeInMillis()+".json");
		file = new File(Extras.path+"tweets/originals/tweets-"+loc.replaceAll(",","-")+"-"+date.getTimeInMillis()+".json");
		try {
			file.createNewFile();
		} catch (IOException e1) {
			System.err.println("Error creating file: "+file.getAbsolutePath());
			e1.printStackTrace();
			System.exit(1);
		}

		//Write the results to the file
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.write(text);
			output.close();
		} catch (IOException e) {
			System.err.println("Error writing file.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void pullUserTimeline(){
		boolean debug = true;
		
		int rateLimit = TwitterAPI.rateLimit();
		if(debug)System.out.println("Recieved rate limit of "+rateLimit);

		//Read and store the total list of collected users
		Set<Long> collectedUsers = UserHelper.readUserDoc(new File("collectedUsers.txt"));
		
		//Read and store the users whose tweets have already been pulled
		Set<Long> holder = UserHelper.readUserDoc(new File("collectedTweets.txt"));

		//Read and store the list of protected users
		holder.addAll(UserHelper.readUserDoc(new File("protectedUsers.txt")));

		//Remove all the users whose recent tweets have already been pulled 
		// and the users who are protected from the collectedUsers set
		collectedUsers.removeAll(holder);
		if(debug)System.out.println("Size of collectedUsers after removing protectedUsers and collectedTweets: " + collectedUsers.size());
		
		if(rateLimit <= 0) return;
		
		//Run through the list of collected users and pull tweets for them
		int count = 0;
		try {
			BufferedWriter uout = new BufferedWriter(new FileWriter(new File("collectedTweets.txt"),true));
			String holder2;
			for(Long l : collectedUsers){
				holder2 = TwitterAPI.timeline(l);
				rateLimit--;
				if(!holder2.split(" ")[0].equalsIgnoreCase("Error")){
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter(new File("masterTweets.csv"),true));
						out.newLine();
						out.newLine();
						out.write(holder2);
						out.close();
						uout.write(l.toString());
						uout.newLine();
						count++;
					} catch (IOException e) {
						System.err.println("Error writing to masterTweets.csv file.");
						e.printStackTrace();
					}
				}else if(holder2.contains("401")){
					if(debug)System.out.println("Got 401 for user " + l + ". Saving user to protectedUsers.txt");
					UserHelper.writeUser(l.toString(), new File("protectedUsers.txt"));
				}else if(debug){
					System.out.println("Got error for user " + l + ":\n" + holder2);
				}
				if(rateLimit <= 0){
					uout.close();
					if(debug)System.out.println("Pulled " + count + " user timelines.");
					return;
				}
			} //End of for loop through collectedUsers
		} catch (IOException e1) {
			System.err.println("Error writing to collectedTweets.txt");
			e1.printStackTrace();
		}
		if(debug){
			System.out.println("Pulled " + count + " user timelines.");
			System.out.println("Pulled all users possible in collectedUsers.txt");
		}
	}//End of pullUserTimeline method

}
