package twitterExperiment;

public class Testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NameToGender stuff = new NameToGender();
		System.out.println("Ashley: "+stuff.nameToGender("Ashley"));
		System.out.println("Kim: "+stuff.nameToGender("Kim"));
		System.out.println("Leslie: "+stuff.nameToGender("Leslie"));
		System.out.println("Terry: "+stuff.nameToGender("Terry"));
		System.out.println("Morgan: "+stuff.nameToGender("Morgan"));
		
	}

}
