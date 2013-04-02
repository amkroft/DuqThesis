package twitterTweets.pull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import tweets.api.TwitterAPI;
import tweets.generic.Extras;
import tweets.generic.UserHelper;


public class PullUsers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args != null && args.length > 0){
			if(args[0].equals("info")){
				if(args.length > 1){
					System.out.println("Passing user list value of : "+args[1]);
					userInfo(new File(args[1]), true);
				}else
					userInfo(true);
			}else if(args[0].equals("collect")){

			}
		}
	}

	/*
	 * Pulls user information for users in the specified user list.
	 * Calls Twitter's API for the rate limit and subsequently for each user up 
	 *  to the max limit.
	 */
	public static void userInfo(File userList, boolean debug){
		Set<Long> users;
		Set<Long> doneUsers;
		
		int numPulls = TwitterAPI.rateLimit();
		if(debug)
			System.out.println("Received rate limit of: "+numPulls);
		if(numPulls <= 0)
			return;
		//else if(debug && numPulls > 5)  //For error checking.  Reduces limit to at most 5 so this program
		//	numPulls = 5;     			//  can be run subsequently many times without going over the limit.

		if(!userList.exists()){
			System.out.println("Could not find list of users file: " + userList.getAbsolutePath());
			return;
		}

		BufferedWriter out;
		File ofile;

		//Read in the user IDs
		users = UserHelper.readUserDoc(userList);

		//Read in the users whose info has already been gathered
		doneUsers = UserHelper.readUserDoc(new File(Extras.path+"collectedUsers.txt"));

		//Read in the users which have produced an error.  Store in
		//  doneUsers since error and done users are treated the same
		doneUsers.addAll(UserHelper.readUserDoc(Extras.path+"errorUsers.txt"));

		//For each user which is in the userCollection file, but not in collectedUsers,
		//  try grabbing the information for the user
		ArrayList<String[]> information = new ArrayList<String[]>();
		for(Long s : users){
			String[] holder = new String[2];
			if(!doneUsers.contains(s)){
				//Try pulling information for the user
				holder[0]=s.toString();
				holder[1]=TwitterAPI.userInfo(s);
				if(debug)
					System.out.println("Twitter info: "+holder[1]);
				if(!holder[1].substring(0,5).equalsIgnoreCase("error"))
					information.add(holder);
				else{
					String[] holder2 = holder[1].split(" ");
					if(holder2[1].equalsIgnoreCase("404"))
						UserHelper.writeUser(s.toString(),new File(Extras.path+"errorUsers.txt"));
					else
						if(debug)
							System.out.println("Error reading information for user " + s + ". "+holder[1]);
						else
							System.err.println("Error reading information for user " + s + ". "+holder[1]);
				}
				numPulls--;
			}
			if(numPulls <= 0){break;}
		}

		/*System.out.println("ArrayList Information");
		for(String[] s : information){
			System.out.println(s[0] + " : " + s[1]);
		}*/

		//Test to make sure users folder exists
		File f = new File(Extras.path+"users");
		if((f.exists() && !f.isDirectory()) || !f.exists())
			f.mkdir();

		//Save the information collected for the users
		for(String[] s : information){
			ofile = new File(Extras.path+"users/user_"+s[0]+".json");
			if(!ofile.exists()){
				try {
					ofile.createNewFile();
					out = new BufferedWriter(new FileWriter(ofile));
					out.write(s[1]);
					out.close();
					if(debug)
						System.out.println("Finished writing information to: "+ofile.getAbsolutePath());
				} catch (IOException e) {
					System.err.println("Error writing to file for user: "+s[0]);
					e.printStackTrace();
				}
			}else if(debug){
				System.out.println("File already exists: "+ofile.getAbsolutePath());
			}
		}

		//Write each user whose information we got, into the collectedUsers file
		try {
			out = new BufferedWriter(new FileWriter(new File(Extras.path+"collectedUsers.txt"),true));
			for(String[] s : information){
				out.write(s[0]);
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Error accessing collectedUsers file.");
			e.printStackTrace();
		}
	}

	/*
	 * For using the default user list of userCollection.txt
	 */
	public static void userInfo(boolean debug){
		userInfo(new File(Extras.path+"userCollection.txt"), debug);
	} //End of method userInfo
}