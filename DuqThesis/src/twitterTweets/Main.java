package twitterTweets;

import tweets.generic.ManageTweets;
import tweets.generic.ManageUsers;
import tweets.generic.Statistics;
import tweets.pull.PullTweets;
import tweets.pull.PullUsers;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args == null || args.length < 1 || args[0].equalsIgnoreCase("help")){
			System.out.println("\n Please specify one of the following options:\n"
					+"users info ................ : Gather information on the Twitter users.\n"
					+"users info [] ............. : Gather information on the Twitter users in the specified file.\n"
					+"tweets .................... : Pull new tweets.\n"
					+"tweets timeline ........... : Pull tweets from collected users's timelines.\n"
					+"manage seperate ........... : Seperate out tweet information.\n"
					+"manage users tweets ....... : Create a document with tweet count per user\n"
					+"manage users clean [] ..... : Sort and reduce the specified file\n"
					+"manage users collected .... : Recreates the collectedUsers.txt file.\n"
					+"manage users difference ... : Recreates collectedUsers.txt, collectedTweets.txt and makes collectedDifference.txt\n"
					+"manage users seperate [] [] : Seperates a user ID list (the first argument) into k (the second argument) different files.\n"
					+"manage masterFile ......... : Move all pulled tweets into the masterFile.csv.\n"
					+"manage masterFile clean ... : Clean up json objects in masterFile.csv\n"
					+"manage masterFile combine . : Combine multiple masterTweets.csv files.\n"
					+"statistics tweets ......... : Get statistics on tweets.\n"
					+"statistics users .......... : Output user information into csv file.\n");
		}else if(args[0].equals("tweets")){
			if(args.length == 1)
				PullTweets.main(new String[]{"null"});
			else
				PullTweets.main(new String[]{args[1]});
		}else if(args[0].equals("manage")){
			if(args[1].equals("users")){
				String[] holder = new String[args.length-2];
				for(int i = 0; i < args.length-2; i++){
					holder[i] = args[i+2];
				}
				ManageUsers.main(holder);
			}else{
				String[] holder = new String[args.length-1];
				for(int i = 0; i < args.length-1; i++){
					holder[i] = args[i+1];
				}
				ManageTweets.main(holder);
			}
		}else if(args[0].equals("users")){
			String[] holder = new String[args.length-1];
			for(int i = 0; i < args.length-1; i++){
				holder[i] = args[i+1];
			}
			PullUsers.main(holder);
		}else if(args[0].equals("statistics") && args.length > 1){
			Statistics.main(new String[]{args[1]});
		}
	}

}
