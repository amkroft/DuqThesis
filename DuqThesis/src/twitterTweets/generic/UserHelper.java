package twitterTweets.generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class UserHelper {
	
	//Class holds methods to help the reading and writing of files containing
	// Twitter user IDs

	/*
	 * Write the set of user IDs to the specified file.
	 */
	public static boolean writeIds(Set<Long> ids, File file, boolean delOld){
		//If specified to delete old file, do so
		if(delOld && file.exists()){
			file.delete();
		}

		//Open writer to file
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			System.err.println("Error opening writer to collectedUsers.txt file");
			e.printStackTrace();
			return false;
		}

		//Sort the array
		Long[] userIds2 = new Long[]{}; 
		userIds2 = ids.toArray(userIds2);
		Arrays.sort(userIds2);

		//Write array to the file
		for(Long l : userIds2){
			try {
				out.write(l.toString()+"\n");
			} catch (IOException e) {
				System.err.println("Error writing user ID:" + l.toString());
				e.printStackTrace();
			}			
		}

		//Close the out stream
		try {
			out.close();
		} catch (IOException e) {
			System.err.println("Error closing out stream.");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/*
	 * Takes a user ID and writes the ID to the given file.
	 * Used when pulling user information so error users can be written right away.
	 */
	public static boolean writeUser(String user, File f){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f,true));
			out.newLine();
			out.write(user);
			out.close();
			return true;
		} catch (IOException e) {
			System.err.println("Error writing the user "+user+" to errorUsers.txt");
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Reads the file which contains a list of user IDs and returns
	 *  the Set containing each user ID in the file.
	 */
	public static Set<Long> readUserDoc(File file){
		Set<Long> u = new HashSet<Long>();
		String holder;
		if(file.exists() && !file.isDirectory()){
			try {
				Scanner in = new Scanner(file);
				while(in.hasNext()){
					holder = in.nextLine();
					try{
						u.add(new Long(holder));
					}catch(NumberFormatException e){
						System.err.println("Could not parse " + holder + " to a Long.");
					}
				}
			} catch (FileNotFoundException e) {
				System.err.println("Error reading file: "+file.getName());
				e.printStackTrace();
			}
		}
		return u;
	}

	public static Set<Long> readUserDoc(String file){
		return readUserDoc(new File(file));
	}
}
