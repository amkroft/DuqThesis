package twitterTweets.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Class for all calls to Twitter's API.
 * 
 * @author Amanda M. Kroft
 * 
 */
public class TwitterAPI {
	
	/*
	 * Method to get the rate limit for Twitter's search API.
	 * Used with getting user information such as profile information and timeline.
	 * 
	 * @return	The number of times the API can be called again from this IP
	 */
	public static int rateLimit(){
		//Sample return object:
		//{"reset_time_in_seconds":1325998506,"remaining_hits":147,"reset_time":"Sun Jan 08 04:55:06 +0000 2012","hourly_limit":150}
		try {
			URL limit = new URL("http://api.twitter.com/1/account/rate_limit_status.json");
			Scanner in = new Scanner(limit.openStream());
			String data = "";
			while(in.hasNext()){data+=in.nextLine();}
			String[] data2 = data.split(",");
			for(String s : data2){
				if(s.contains("remaining_hits")){
					return new Integer(s.split(":")[1].split("}")[0]);
				}
			}
			return 0;
		} catch (MalformedURLException e) {
			System.out.println("Error reading URL for rate limit.");
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			System.out.println("Error reading URL for rate limit.");
			e.printStackTrace();
			return 0;
		}
	}
	
	/*
	 * Overloaded methods so can pass a String or int of the user ID instead of a Long.
	 * See comment for userInfo(Long) for more information.
	 * 
	 * @param 	user The String representation of the Twitter user's ID
	 * @return	The json object in String form of the user's profile information.
	 */
	public static String userInfo(String user){
		try{
			Long l = new Long(user);
			return userInfo(l);
		}catch(NumberFormatException e){
			return "Error BadUserID";
		}
	}
	public static String userInfo(int user){
		return userInfo(""+user);
	}
		
	/*
	 * Gets user profile information for the specified user.
	 * See https://dev.twitter.com/docs/api/1/get/users/lookup
	 * for more information.
	 * 
	 * @param 	user The Twitter user's ID
	 * @return	The json object in String form of the user's profile information
	 */
	public static String userInfo(Long user){
		try {
			URL site = new URL("http://api.twitter.com/1/users/lookup.json?user_id="+user);
			HttpURLConnection http = (HttpURLConnection)site.openConnection();

			//Get status code.  If error code, return Error and code.
			int statusCode = http.getResponseCode();
			if(statusCode >= 400)
				return "Error " + statusCode;

			//Scanner reader = new Scanner(site.openStream()); //NOTE: DO NOT DO THIS.  Opens another connection and drops rate limit
			// by one again since variable http already has a stream open to the webpage.  Below does not open another stream/connection.
			Scanner reader = new Scanner(http.getInputStream());
			String data = "";
			//Read the user's information
			while(reader.hasNext()){data+=reader.nextLine();}
			return data;
		} catch (MalformedURLException e) {
			return "Error MalformedURL";
		} catch (FileNotFoundException e){
			return "Error FileNotFound";
		} catch (IOException e) {
			return "Error IO";
		}
	}

	/*
	 * Calls Twitter's tweet search API with the specified query.
	 * See https://dev.twitter.com/docs/api/1/get/search
	 * for information on how to construct a query. 
	 * 
	 * @param 	query The search to be appended onto the URL, i.e. "q=Steelers"
	 * @return	The resulting tweets from the search in json form
	 */
	public static String search(String query){
		try {
			//Open connection to Twitter's API
			URL site = new URL("http://search.twitter.com/search.json?"+query);
			HttpURLConnection http = (HttpURLConnection)site.openConnection();
			
			//Get status code.  If error code, return Error and code.
			int statusCode = http.getResponseCode();
			if(statusCode >= 400)
				return "Error " + statusCode;
			
			//Read text from URL and return it.
			String text = "";
			Scanner reader = new Scanner(http.getInputStream());
			while (reader.hasNext()) {text += reader.nextLine();}
			return text;
		} catch (MalformedURLException e) {
			return "Error MalformedURL";
		} catch (IOException e) {
			return "Error IO";
		}
	}
	
	/*
	 * Calls Twitter's user search API for the timeline of the specified user.
	 * Uses option of max number of tweets of 200 which is the max allowed.
	 * See https://dev.twitter.com/docs/api/1/get/statuses/user_timeline
	 * for more information on getting a user's timeline.
	 * 
	 * @param 	l The Twitter user's ID
	 * @return	The resulting tweets from the user's timeline in json form.
	 */
	public static String timeline(Long l){
		try {
			String text = "";
			URL site = new URL("http://api.twitter.com/1/statuses/user_timeline.json?user_id="+l+"&count=200");
			HttpURLConnection http = (HttpURLConnection)site.openConnection();
			
			//Get status code.  If error status, return Error and code
			int statusCode = http.getResponseCode();
			if(statusCode >= 400)
				return "Error " + statusCode;
			
			//Get the text from the URL and return it
			Scanner reader = new Scanner(http.getInputStream());
			while(reader.hasNext()){text += reader.nextLine();}
			return text;
		} catch (MalformedURLException e) {
			return "Error MalformedURL";
		} catch (IOException e) {
			return "Error IO";
		}
	}
	
}
