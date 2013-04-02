package twitterTweets.generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import tweets.generic.UserHelper;

public class ManageUsers {

	public ManageUsers(){}

	/*
	 * Runs through all tweets in the masterFile and counts the number of
	 *  tweets each user ID has stored, then saves the IDs and counts to 
	 *  userTweetCount.csv
	 */
	public void countUserTweets(){
		File masterFile = new File(Extras.path+"/tweets/masterFile.csv");
		HashSet<Long> tIDs = new HashSet<Long>();
		HashMap<Long,Integer> users = new HashMap<Long,Integer>();
		try {
			Scanner in = new Scanner(masterFile);
			String holder;
			String[] holder2;
			//String[] holder2;
			Object userID;
			while(in.hasNext()){
				holder = in.nextLine().trim();
				/*if(holder.contains("}{")){
					holder2 = holder.split("}{");
				}*/
				if(holder.length() > 2){
					JSONObject j = (JSONObject)JSONValue.parse(holder);
					if(j != null){
						JSONArray j2 = (JSONArray)j.get("results");
						for(Object o : j2){
							JSONObject j3 = (JSONObject)o;
							if(tIDs.contains(j3.get("id"))){
								//do nothing
							}else{
								tIDs.add((Long)j3.get("id"));
								//System.out.println("User ID is of type: "+j3.get("from_user_id").getClass().getName());
								userID = j3.get("from_user_id");
								if(users.containsKey(userID)){
									users.put((Long)userID,users.get(userID)+1);
								}else{
									users.put((Long)userID,1);
								}
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
									JSONObject j3 = (JSONObject)o;
									if(tIDs.contains(j3.get("id"))){
										//do nothing
									}else{
										tIDs.add((Long)j3.get("id"));
										//System.out.println("User ID is of type: "+j3.get("from_user_id").getClass().getName());
										userID = j3.get("from_user_id");
										if(users.containsKey(userID)){
											users.put((Long)userID,users.get(userID)+1);
										}else{
											users.put((Long)userID,1);
										}
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

			//Users and their tweet count is now stored in HashMap 'users'

			//Output HashMap to a file
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(Extras.path+"/userTweetCount.csv")));
				for(Long key : users.keySet()){
					out.write(key.toString());
					out.write(",");
					out.write(((Integer)users.get(key)).toString());
					out.newLine();
				}
			} catch (IOException e) {
				System.err.println("Error writing to userTweetCount.csv");
				e.printStackTrace();
				System.exit(1);
			}

		} catch (FileNotFoundException e) {
			System.err.println("masterFile.csv not found.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 * Reads the userTweetCount.csv file and pulls out users with tweet count
	 * equal to or higher than the num parameter.  This method is not finished
	 * and is @deprecated by AWK file  
	 */
	@Deprecated
	public void activeUsers(int num){
		File f = new File(Extras.path+"/userTweetCount.csv");
		HashMap<Long, Integer> users = new HashMap<Long, Integer>();
		if(!f.exists() || f.isDirectory()){
			System.err.println("Error accessing userTweetCount.csv.");
			return;
		}
		try {
			Scanner in = new Scanner(f);
			long user;
			int count;
			while(in.hasNext()){
				user = new Long(in.next());
				count = new Integer(in.next());
				if(count > num){
					users.put(user,count);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("userTweetCount.csv was not found for Scanner.");
			e.printStackTrace();
		}
	}

	/*
	 * Reads user ID list file and cleans it up by removing duplicates and
	 * sorting the user IDs.
	 */
	public boolean cleanUserList(String f){
		System.out.println("Inside cleanUserFile method.");

		//Check file exists
		File userFile = new File(Extras.path + "/" + f);
		if(!userFile.exists()){
			System.err.println("File " + f + " does not exist.");
			return false;
		}

		//Read in each user ID from errorUsers.txt into eusers
		Scanner in;
		HashSet<Long> users;
		try {
			in = new Scanner(userFile);
			users = new HashSet<Long>();
			while(in.hasNext()){
				users.add(in.nextLong());
			}
			in.close();
		} catch (FileNotFoundException e1) {
			System.err.println("Error opening scanner to " + f);
			e1.printStackTrace();
			return false;
		}

		//Sort the IDs
		Long[] sortedUsers = new Long[]{}; 
		sortedUsers = users.toArray(sortedUsers);
		Arrays.sort(sortedUsers);

		//Delete existing file and recreate it
		userFile.delete();
		try {
			userFile.createNewFile();
		} catch (IOException e) {
			System.err.println("Error recreating collectedUsers.txt file.");
			e.printStackTrace();
			return false;
		}

		//Write the sorted IDs back to errorUsers.txt
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(userFile));
			for(Long l : sortedUsers){
				out.write(""+l);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Error writing IDs back to file.");
			e.printStackTrace();
			return false;
		}

		return true;
	} //End of cleanUserFile method

	//Removes saved user files which contain only the error message
	@Deprecated
	public void removeErrorUsers(){
		//Use terminal command: find . -size -2b -delete
	}

	/*
	 * Recreates the list of all users whose information has been collected.  Reads the users
	 * folder for this list.  List file is collectedUsers.txt
	 */
	public boolean redoCollectedUsers(){
		File users = new File(Extras.path + "users");
		if(!users.isDirectory()){
			System.err.println("Specified users location is not a directory.");
			return false;
		}
		File collectedUsers = new File(Extras.path + "collectedUsers.txt");

		if(collectedUsers.exists()){
			collectedUsers.delete();
		}

		try {
			collectedUsers.createNewFile();
		} catch (IOException e) {
			System.err.println("Error recreating collectedUsers.txt file.");
			e.printStackTrace();
			return false;
		}

		Set<Long> userIds = new HashSet<Long>();
		String holder;
		for(File f : users.listFiles()){
			holder = f.getName();
			userIds.add(new Long(holder.substring(holder.indexOf('_')+1,holder.indexOf('.'))));
		}

		return UserHelper.writeIds(userIds, collectedUsers,true);
	}

	/*
	 * Recreates userCollection.txt
	 */
	public boolean redoUserCollection(){
		Set<Long> users = new HashSet<Long>();

		File f = new File(Extras.path + "tweets/masterFile.csv");
		Scanner in;
		try {
			in = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.err.println("Error opening Scanner to masterFile.csv");
			e.printStackTrace();
			return false;
		}

		String holder;
		JSONArray holder2;
		JSONObject j,j2;
		while(in.hasNext()){
			holder = in.nextLine();
			if(holder != null && holder != ""){
				j = (JSONObject)JSONValue.parse(holder);
				if(j != null && j.get("results") != null){
					holder2 = (JSONArray)j.get("results");
					for(Object s : holder2){
						j2 = (JSONObject)s;
						users.add((Long)j2.get("from_user_id"));
					}
				}
			}
		}

		return UserHelper.writeIds(users, new File(Extras.path + "userCollection.txt"),true);
	}

	/*
	 * Cleans user files be redoing userCollection.txt, collectedUsers.txt, and deleting errorUsers.txt
	 */
	public boolean cleanUserLists(){
		File error = new File(Extras.path + "tweets/errorUsers.txt");
		try {
			return redoCollectedUsers() && redoUserCollection() && error.delete() && error.createNewFile();
		} catch (IOException e) {
			System.err.println("Error creating new errorUsers.txt file");
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Split the specified file of user IDs up into the specified number of subfiles
	 *  so that a number of machines can work on pulling users.
	 * The subsets are not random subsets.
	 */
	public boolean splitUsers(File f, int k){
		//Read in all user IDs
		Set<Long> userIds = UserHelper.readUserDoc(f);

		//Save subsets into files
		int each = userIds.size()/k;
		System.out.println("Size of ID array is: "+userIds.size());
		System.out.println("Value of each: " + each);
		int counter = 0;
		Long[] ids = new Long[]{};
		ids = userIds.toArray(ids);
		String holder = f.getName().substring(0,f.getName().indexOf("."));
		String holder2 = f.getName().substring(f.getName().indexOf("."));
		File out = new File(Extras.path+holder+counter+holder2);
		//BufferedWriter o = new BufferedWriter(new FileWriter(out));
		BufferedWriter o = null;
		for(int j = 0; j < ids.length ; j++){
			if(j%each == 0){
				out = new File(Extras.path+holder+(counter++)+holder2);
				try {
					if(o != null)
						o.close();
					o = new BufferedWriter(new FileWriter(out));
				} catch (IOException e) {
					System.err.println("Error opening Writer to file: " +out.getAbsolutePath());
					e.printStackTrace();
				}
			}
			try {
				o.write(""+ids[j]);
				o.newLine();
			} catch (IOException e) {
				System.err.println("Error writing to file: "+out.getAbsolutePath());
				e.printStackTrace();
			}

		}

		return true;
	}

	/*
	 * Creates a list of all collected users whose tweets are not protected.
	 */
	public boolean nonProtectedUsers(){
		File users = new File(Extras.path + "users");
		if(!users.isDirectory()){
			System.err.println("Specified users location is not a directory.");
			return false;
		}
		File collectedUsers = new File(Extras.path + "nonProtectedUsers.txt");

		if(collectedUsers.exists()){
			collectedUsers.delete();
		}

		try {
			collectedUsers.createNewFile();
		} catch (IOException e) {
			System.err.println("Error recreating nonProtectedUsers file.");
			e.printStackTrace();
			return false;
		}

		Set<Long> userIds = new HashSet<Long>();
		String holder;
		for(File f : users.listFiles()){
			holder = f.getName();
			userIds.add(new Long(holder.substring(holder.indexOf('_')+1,holder.indexOf('.'))));
		}

		return UserHelper.writeIds(userIds, collectedUsers,true);
	}

	/*
	 * Goes through masterTweets.csv and redoes collectedTweets.txt
	 */
	public void redoCollectedTweets(){
		Set<Long> users = new HashSet<Long>();

		try {
			Scanner in = new Scanner(new File("masterMasterTweets.csv"));
			JSONArray j;
			JSONObject j2;
			String holder;
			Long id;
			while(in.hasNext()){
				holder = in.nextLine();
				j = (JSONArray)JSONValue.parse(holder);
				j2 = j != null && j.size() > 0 ? (JSONObject)j.get(0) : null; //TODO: When would there never be any tweets in an object?
				j2 = j2 != null ? (JSONObject)j2.get("user") : null;
				if(j2 != null){
					try{
						id = new Long(j2.get("id").toString());
						users.add(id);
					}catch(NumberFormatException e){}
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println();
			e.printStackTrace();
		}

		UserHelper.writeIds(users,new File("collectedTweets.txt"),true);
	}

	/*
	 * Creates a list of users whose profile information has been pulled, but not their timeline.
	 */
	public void userDifference(){
		redoCollectedUsers();
		redoCollectedTweets();

		Set<Long> users = UserHelper.readUserDoc("collectedUsers.txt");
		System.out.println("Size of collected users: " + users.size());
		Set<Long> doneUsers = UserHelper.readUserDoc("collectedTweets.txt");
		System.out.println("Size of collected tweets: " + doneUsers.size());

		//Make the collectedDifference.txt file which contains users in collectedUsers.txt
		//  but not in collectedTweets.txt
		users.removeAll(doneUsers);
		UserHelper.writeIds(users,new File("collectedDifference.txt"),true);
		System.out.println("Size of collected difference: " + users.size());

	}

	public static void main(String[] args) {
		ManageUsers mu = new ManageUsers();
		if(args[0].equalsIgnoreCase("clean")){
			if(args.length > 1)
				mu.cleanUserList(args[1]);
			else
				mu.cleanUserLists();
		}else if(args[0].equalsIgnoreCase("tweets"))
			mu.countUserTweets();
		else if(args[0].equalsIgnoreCase("collected"))
			mu.redoCollectedUsers();
		else if(args[0].equalsIgnoreCase("seperate")){
			if(args.length > 2){
				try{
					mu.splitUsers(new File(args[1]),new Integer(args[2]));
				}catch(NumberFormatException e){
					System.err.println("Could not parse input into an integer.");
				}
			}
		}else if(args[0].equalsIgnoreCase("difference"))
			mu.userDifference();
	}

}
