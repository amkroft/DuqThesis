package twitterExperiment;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class JGAAP {
	String[] canonicizers;
	String[] drivers;
	String[] cullers;
	String[] classifiers;
	boolean[] classDist;
	String[] distances;
	File data;

	public JGAAP(){
		data = new File("jgaapInfo.txt");
		init();
	}
	public JGAAP(String f){
		data = new File(f);
		init();
	}

	public void init(){
		canonicizers = new String[]{};
		drivers = new String[]{};
		cullers = new String[]{};
		classifiers = new String[]{};
		distances = new String[]{};
		readFile();
	}

	public void readFile() {

			Scanner file = null;
			try {
				file = new Scanner(data);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(file == null){
				Helper.createJgaapInfo();
				try {
					file = new Scanner(data);
				} catch (FileNotFoundException e) {
					System.err.println("Could not find jgaap info file, even after creating new file.");
					e.printStackTrace();
					return;
				}
			}
			
			String holder;
			ArrayList<String> sholder = new ArrayList<String>();

			//Read in Canonicizers
			while(!(holder = file.nextLine()).equalsIgnoreCase("Canonicizers")){}
			while(!(holder = file.nextLine()).equals("")){
				sholder.add(holder);
			}
			sholder.add("");
			canonicizers = sholder.toArray(canonicizers);
			sholder.clear();

			//Read in Cullers
			file.nextLine(); //Move past Cullers heading
			while(!(holder = file.nextLine()).equals("")){
				sholder.add(holder);
			}
			sholder.add("");
			cullers = sholder.toArray(cullers);
			sholder.clear();

			//Read in Drivers
			file.nextLine();
			while(!(holder = file.nextLine()).equals("")){
				sholder.add(holder);
			}
			drivers = sholder.toArray(drivers);
			sholder.clear();

			//Read in Classifiers
			file.nextLine();
			while(!(holder = file.nextLine()).equals("")){
				sholder.add(holder);
			}
			classifiers = sholder.toArray(classifiers);
			sholder.clear();
			
			//Set the boolean flag on whether a Classifier needs a Distance function
			classDist = new boolean[classifiers.length];
			for(int i = 0; i < classifiers.length; i++){
				classDist[i] = !(classifiers[i].indexOf("~") == -1);
				if(classDist[i])
					classifiers[i] = classifiers[i].substring(0,classifiers[i].length()-1);
			}

			//Read in Distances
			file.nextLine();
			while(file.hasNext() && !(holder = file.nextLine()).equals("")){
				sholder.add(holder);
			}
			distances = sholder.toArray(distances);
			sholder.clear();
	
	}

	public String toString(){
		String holder = "";
		holder+="Canonicizers\n";
		for(String s : canonicizers){
			holder+=s+"\n";
		}
		holder+="\nCullers\n";
		for(String s : cullers){
			holder+=s+"\n";
		}
		holder+="\nDrivers\n";
		for(String s : drivers){
			holder+=s+"\n";
		}
		holder+="\nClassifiers\n";
		for(String s : classifiers){
			holder+=s+"\n";
		}
		holder+="\nDistances\n";
		for(String s : distances){
			holder+=s+"\n";
		}
		return holder;
	}
}
