package twitterTweets.generic;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class contains all generic extra methods needed for the rest of the
 * program.
 * 
 * @author Amanda M. Kroft
 * 
 */
public class Extras {

	//Stores the working directory for use among the rest of the program.
	public static String path = System.getProperty("user.dir")+"/";

	/*
	 * Reads the user input for the given message form the given stream connection.
	 * 
	 * @param 	message The message to be displayed to the user as a prompt.
	 * @param 	in		The BufferedReader currently reading user input.
	 * @return	The String read from the user.
	 */
	public static String readString(BufferedReader in, String message){
		String data = null;
		while(data == null){
			System.out.print(message);
			try {
				data = in.readLine();
			} catch (IOException e) {
				System.err.println("Error reading your input.  Please try again.");
				e.printStackTrace();
			}
		}
		return data;
	}
	
	/*
	 * Pulls out the domain of a URL.
	 * 
	 * @param 	url The String representation of the URL.
	 * @return	The String representation of the domain.
	 */
	public static String getDomain(String url){
		if(url.equals("null"))
			return "null";
		try {
			return((new URL(url)).getHost());
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			return "null";
		}
	}
	
	/*
	 * Converts a Color to a String interpretation of the Color.
	 * For example, the RGB value FFFFFF is converted to "White"
	 * 
	 * @param 	c The Color to be converted.
	 * @return	The String interpretation of the Color.
	 */
	public static String rgbColorToString(Color c){
		int r, g, b;
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		if(Math.abs(r-g) <= 25 && Math.abs(r-b) <= 25 && Math.abs(g-b) <= 25){
			if(r <= 25 && b <= 25 && g <= 25)
				return "Black";
			else if(r >= 230 && b >= 230 && g >= 230)
				return "White";
			else
				return "Gray";
		}else{
			//TODO: Name what I currently call Pink, Magenta?
			//TODO: Should I just call Magenta, Purple?
			//TODO: Divide down Blue into sub colors?
			Color[] cs = new Color[]{Color.decode("0xFF0000"),Color.decode("0xFF7F00"),Color.decode("0xFFFF00"),Color.decode("0x7FFF00"),
					Color.decode("0x00FF00"),Color.decode("0x00FF7F"),Color.decode("0x00FFFF"),Color.decode("0x007FFF"),
					Color.decode("0x0000FF"),Color.decode("0x7F00FF"),Color.decode("0xFF00FF"),Color.decode("0xFF007F"),
					Color.decode("0xFF7F7F"),Color.decode("0x7FFF7F"),Color.decode("0x7F7FFF"),
					Color.decode("0xFFFF7F"),Color.decode("0xFF7FFF"),Color.decode("0x7FFFFF"),
					Color.decode("0x7F0000"),Color.decode("0x007F00"),Color.decode("0x00007F"),
					Color.decode("0x7F7F00"),Color.decode("0x7F007F"),Color.decode("0x007F7F")};
			String[] labels = new String[]{"Red","Orange","Yellow","Yellow-Green",
					"Green","Blue-Green","Blue","Blue",
					"Blue","Purple","Magenta","Pink",
					"Red","Green","Blue",
					"Yellow","Magenta","Blue",
					"Red","Green","Blue",
					"Yellow","Magenta","Blue"};
			int index = 0;
			double oldDist, newDist;
			oldDist = colorDistance(c,cs[0]);
			for(int i = 1; i < cs.length; i++){
				newDist = colorDistance(c,cs[i]);
				if(oldDist > newDist){
					index = i;
					oldDist = newDist;
				}
			}
			return labels[index];
		}
	}
	
	/*
	 * Overloaded method for rgbColorToString
	 * Allows a String representation of the Color to be passed instead. 
	 */
	public static String rgbColorToString(String c){
		try{
			return rgbColorToString(Color.decode(c));
		}catch(NumberFormatException e){
			return "null";
		}
	}

	/*
	 * Calculates the distance between two colors.
	 */
	public static double colorDistance(Color c1, Color c2){
		return Math.pow(new Double(Math.pow(c1.getRed()-c2.getRed(),2)+Math.pow(c1.getGreen()-c2.getGreen(),2)+Math.pow(c1.getBlue()-c2.getBlue(),2)),1.0/3.0);
	}

}
