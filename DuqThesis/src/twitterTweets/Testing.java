package twitterTweets;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import twitterTweets.generic.Extras;

public class Testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*String s = "testing}{testing";
		String[] s2 = s.split("\\}\\{") ;
		//s.replace("}{", "}\n\n{");
		for(String i : s2){
			System.out.println(i);
		}
		//System.out.println(s.substring(0,5).equalsIgnoreCase("error"));
		 */

		/*ArrayList<String> stuff = new ArrayList<String>();
		System.out.println(stuff.toString());
		stuff.add("testing1");
		stuff.add("testing2");
		stuff.add("testing3");
		System.out.println(stuff.toString());
		System.out.println(arrayToString(stuff));*/

		/*String test = "[{\"created_at\":\"Thu Jul 19 21:48:39 +0000 2012\",\"id\":226071075769950208,\"id_str\":\"226071075769950208\",\"text\":\"@MattFaix http:\\/\\/t.co\\/o5tWG9nC\",\"source\":\"web\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":637073834,\"in_reply_to_user_id_str\":\"637073834\",\"in_reply_to_screen_name\":\"MattFaix\",\"user\":{\"id\":637180018,\"id_str\":\"637180018\",\"name\":\"Steve Cotter\",\"screen_name\":\"cottermedian\",\"location\":\"Pittsburgh, PA\",\"url\":null,\"description\":\"\",\"protected\":false,\"followers_count\":2,\"friends_count\":14,\"listed_count\":0,\"created_at\":\"Mon Jul 16 18:29:14 +0000 2012\",\"favourites_count\":0,\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":false,\"verified\":false,\"statuses_count\":1,\"lang\":\"en\",\"contributors_enabled\":false,\"is_translator\":false,\"profile_background_color\":\"1A1B1F\",\"profile_background_image_url\":\"http:\\/\\/a0.twimg.com\\/images\\/themes\\/theme9\\/bg.gif\",\"profile_background_image_url_https\":\"https:\\/\\/si0.twimg.com\\/images\\/themes\\/theme9\\/bg.gif\",\"profile_background_tile\":false,\"profile_image_url\":\"http:\\/\\/a0.twimg.com\\/sticky\\/default_profile_images\\/default_profile_6_normal.png\",\"profile_image_url_https\":\"https:\\/\\/si0.twimg.com\\/sticky\\/default_profile_images\\/default_profile_6_normal.png\",\"profile_link_color\":\"2FC2EF\",\"profile_sidebar_border_color\":\"181A1E\",\"profile_sidebar_fill_color\":\"252429\",\"profile_text_color\":\"666666\",\"profile_use_background_image\":true,\"show_all_inline_media\":false,\"default_profile\":false,\"default_profile_image\":true,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"retweet_count\":0,\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false}]";
		JSONArray holder = (JSONArray)JSONValue.parse(test);
		JSONObject j, j2;
		Long id;
		String text;
		for(Object s : holder){
			j = (JSONObject)s;
			j2 = j == null ? null : (JSONObject)j.get("user");
			if(j != null && j2 != null){
				id = j2.get("id") == null ? null : new Long(j2.get("id").toString());
				text = j.get("text") == null ? null : j.get("text").toString();
				if(id != null & text != null){
					System.out.println(id + " : " + text);
				}
			}
		}*/
		
		//String s = Extras.rgbColorToString(Color.decode("0x592712"));
		//System.out.println(s);
		
		/*String test = "herb";
		NameToGender holder = new NameToGender();
		System.out.println(holder.nameToGender(test));*/
		
		File f = new File("testingOut.txt");
		System.out.println(f.getName().substring(0,f.getName().indexOf(".")));
	}


	public static String arrayToString(@SuppressWarnings("rawtypes") ArrayList list){
		String holder = "[";
		for(Object o : list){
			holder += "["+o.toString()+"],";
		}
		holder = holder.substring(0,holder.length()-1) + "]";
		return holder;
	}
	
	//Old method from Extras class
	public static String rgbColorToCommonString(String c){
		try{
			return rgbColorToCommonString(Color.decode(c));
		}catch(NumberFormatException e){
			return "null";
		}
	}
	
	/*
	 * Does not seem to work as well as rgbColorToString
	 * Old method from Extras class
	 */
	public static String rgbColorToCommonString(Color c){
		Color[] cs = new Color[]{Color.decode("0x00FFFF"),Color.decode("0x000000"),Color.decode("0x0000FF"),Color.decode("0xFF00FF"),
				Color.decode("0x808080"),Color.decode("0x008000"),Color.decode("0x00FF00"),Color.decode("0x800000"),Color.decode("0x000080"),
				Color.decode("0x808000"),Color.decode("0x800080"),Color.decode("0xFF0000"),Color.decode("0xC0C0C0"),Color.decode("0x008080"),
				Color.decode("0xFFFFFF"),Color.decode("0xFFFF00")};
		String[] labels = new String[]{"Aqua","Black","Blue","Fuchsia","Gray","Green","Lime","Maroon","Navy","Olive",
				"Purple","Red","Silver","Teal","White","Yellow"};
		int index = 0;
		double oldDist, newDist;
		oldDist = Extras.colorDistance(c,cs[0]);
		for(int i = 1; i < cs.length; i++){
			newDist = Extras.colorDistance(c,cs[i]);
			if(oldDist > newDist){
				index = i;
				oldDist = newDist;
			}
		}
		return labels[index];
	}
	
}
