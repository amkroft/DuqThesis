package twitterExperiment;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import com.jgaap.backend.AutoPopulate;
import com.jgaap.generics.AnalysisDriver;
import com.jgaap.generics.Canonicizer;
import com.jgaap.generics.DistanceFunction;
import com.jgaap.generics.EventCuller;
import com.jgaap.generics.EventDriver;
import com.jgaap.generics.NeighborAnalysisDriver;

/*
 * Helper class that helps with various activities that need done
 *  for the Main Twitter Experiment engine.
 */
public class Helper {

	public static void main(String[] args){
		Helper.createJgaapInfo();
	}

	/*
	 * Method to create the jgaapInfo.txt file which contains all canonicizers, cullers,
	 *  event drivers, analysis drivers, and distance functions used in JGAAP.
	 *  
	 *  @return Whether the creation was successful.
	 */
	public static boolean createJgaapInfo(){
		//canonicizer|canonicizer,EventDriver|key:value|key:value,culler|culler,analysis|key:value|key:value,distance|key:value
		try {
			System.setErr((new PrintStream(new File("testingOut.txt"))));
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("jgaapInfo.txt")));

			out.write("JGAAP");
			out.newLine();
			out.newLine();

			out.write("Canonicizers");
			out.newLine();

			List holder = AutoPopulate.getCanonicizers();
			for(int i = 0; i < holder.size(); i++){
				out.write(((Canonicizer)holder.get(i)).displayName());
				out.newLine();
			}

			out.newLine();
			out.write("Cullers");
			out.newLine();
			holder = AutoPopulate.getEventCullers();
			for(int i = 0; i < holder.size(); i++){
				out.write(((EventCuller)holder.get(i)).displayName());
				out.newLine();
			}

			out.newLine();
			out.write("Event Drivers");
			out.newLine();
			holder = AutoPopulate.getEventDrivers();
			for(int i = 0; i < holder.size(); i++){
				out.write(((EventDriver)holder.get(i)).displayName());
				out.newLine();
			}

			out.newLine();
			out.write("Classifiers");
			out.newLine();
			holder = AutoPopulate.getAnalysisDrivers();
			for(int i = 0; i < holder.size(); i++){
				out.write(((AnalysisDriver)holder.get(i)).displayName());
				out.write(holder.get(i) instanceof NeighborAnalysisDriver ? "~" : "");
				out.newLine();
			}

			out.newLine();
			out.write("Distances");
			out.newLine();
			holder = AutoPopulate.getDistanceFunctions();
			for(int i = 0; i < holder.size(); i++){
				out.write(((DistanceFunction)holder.get(i)).displayName());
				out.newLine();
			}
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error opening new error stream.");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.err.println("Error writing to JGAAP info file.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * Method to read user input.
	 * 
	 * @param in 	  The BufferedReader currently reading user input.
	 * @param message The message to be displayed to the user to prompt their input.
	 * @return		  The user input.
	 */
	public static String readString(BufferedReader in, String message){
		String data = null;
		while(data == null){
			System.out.print(message);
			try {
				data = in.readLine();
			} catch (IOException e) {
				System.err.println("An error occured when reading your input.  Please try again.");
				//e.printStackTrace();
			}
		}
		return data;
	}

	/*
	 * Method to read user input and parse it to a boolean.
	 * Requires the user to enter Y, N, Yes, or No.
	 * 
	 * @param in 	  The BufferedReader currently reading user input.
	 * @param message The message to be displayed to the user to prompt their input.
	 * @return 		  The boolean of the user input.
	 */
	public static boolean readBool(BufferedReader in, String message){
		String s = readString(in, message);
		while(!s.equalsIgnoreCase("Y") && !s.equalsIgnoreCase("N") && !s.equalsIgnoreCase("Yes") && !s.equalsIgnoreCase("No")){
			System.out.println("Please enter either Y or N.");
			s = readString(in, message);
		}
		return s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("Yes");
	}

	/*
	 * Method to read user input and parse it to a File.
	 * If exists is true, requires the inputed File to exist.
	 * If exists is false, requires the inputed File to not exist or asks if should delete existing file.
	 * 
	 * @param in 	  The BufferedReader currently reading user input.
	 * @param message The message to be displayed to the user to prompt their input.
	 * @param exists  Whether the file should exist or not.
	 * @return 		  The File inputed by the user.
	 */
	public static File readFile(BufferedReader in, String message, boolean exists){
		String s = readString(in, message);
		File f = new File(s);
		if(exists){
			while(!f.exists()){
				System.out.println("File does not exist.  Please try again.");
				s = readString(in, message);
				f = new File(s);
			}
			return f;
		}else{
			while(f.exists()){
				if(readBool(in, "File already exists.  Would you like it deleted? (Y/N) : ")){
					f.delete();
					return f;
				}else{
					s = readString(in, message);
					f = new File(s);
				}
			}
			return f;
		}
	}

	/*
	 * Method to read a File from the user which is a directory.
	 * If exists is true, requires the directory to exist.
	 * If exists is false, requires the directory to not exist and creates a new one.
	 * 
	 * @param in 	  The BufferedReader currently reading user input.
	 * @param message The message to be displayed to the user to prompt their input.
	 * @param exists  Whether the directory should exist or not.
	 */
	public static File readDir(BufferedReader in, String message, boolean exists){
		File f = null;
		if(exists){ //Want Directory to exist
			f = readFile(in, message, exists);
			while(!f.isDirectory()){
				System.out.println("File is not a directory.  Please try again.");
				f = readFile(in, message, exists);
			}
		}else{  //Want Directory to not exist
			do{
				f = new File(readString(in, message));
				if(!f.exists()){
					f.mkdir();
					return f;
				}else{
					if(readBool(in, "Directory already exists.  Would you like to delete it? (Y/N) : ") && readBool(in, "Are you sure? (Y/N) : ")){
						if(deleteDir(f)){
							f.mkdir();
							return f;
						}else{
							System.out.println("Could not delete directory.  Please specify a different directory.");
						}
					}
				}
			}while(f.exists());
		}
		return f;
	}

	public static Integer readInt(BufferedReader in, String message){
		String s = readString(in, message);
		Integer i = null;
		while(i == null){
			try{
				i = new Integer(s);
			}catch(NumberFormatException e){
				System.out.println("Could not parse input to an Integer.  Please try again.");
				s = readString(in, message);
			}
		}
		return i;
	}

	/*
	 * Method to recursively delete a directory.
	 * Delete all sub-directories and all files in directory.
	 * 
	 * @param f The directory to be deleted.
	 * @return  Whether the delete was successful or not.
	 */
	public static boolean deleteDir(File f){
		if(f.isDirectory()){
			for(File g : f.listFiles()){
				if(!deleteDir(g))
					return false;
			}
		}
		return f.delete();
	}

	/*
	 * Overloaded method so that a String representation of the color can be passed
	 *  instead of a Color class.  Must be a string that can be parsed using Color's decode
	 *  method.
	 *  
	 *  @param c The String representation of the color
	 *  @return  The English word representation.
	 */
	public static String rgbColorToString(String c){
		try{
			return rgbColorToString(Color.decode(c));
		}catch(NumberFormatException e){
			return "";
		}
	}

	/*
	 * Converts a Color to an English word representation of the Color.
	 * I.e. 0xFFFFFF converts to "White"
	 * 
	 * @param c The Color to be converted
	 * @return  The English word representation.
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
			//System.out.println("Current distance: " + oldDist);
			for(int i = 1; i < cs.length; i++){
				newDist = colorDistance(c,cs[i]);
				//System.out.println("New distance: " + newDist);
				if(oldDist > newDist){
					index = i;
					oldDist = newDist;
				}
				//System.out.println("Index is: "+ index);
			}
			return labels[index];
			//return "Color";
		}
	}

	/*
	 * Calculates the distance between two Colors.
	 * 
	 * @param c1 The first Color.
	 * @param c2 The second Color.
	 * @return   The distance between the two Colors.
	 */
	public static double colorDistance(Color c1, Color c2){
		return Math.pow(new Double(Math.pow(c1.getRed()-c2.getRed(),2)+Math.pow(c1.getGreen()-c2.getGreen(),2)+Math.pow(c1.getBlue()-c2.getBlue(),2)),1.0/3.0);
	}
}
