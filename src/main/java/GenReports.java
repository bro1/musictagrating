import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.io.Files;


public class GenReports {

	public static void main(String[] args) throws Exception {

		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
			
		if (args.length  < 3) {
			System.out.println("The following arguments are expected.");
			System.out.println("  1 source directory");
			System.out.println("  2 unrated report (to be created)");
			System.out.println("  3 good report (to be created)");
			System.out.println("  4 bad report (to be created)");
			return;
		}
		
		File sd = new File(args[0]);
	
		if (!sd.isDirectory()) {
			System.out.println("The first argument must be a directory");
			return;
		}
		
		
		File unratedFilesPlaylist =  new File(args[1]);			
		File goodFilesPlaylist = new File(args[2]);
		File badFilesPlaylist = new File(args[3]);
				
		MoveMain.process(sd);		
				
		MoveMain.verifyRatings(new PrintStream(unratedFilesPlaylist));
		printPositiveRatings(new PrintStream(goodFilesPlaylist));
		printBadRatings(new PrintStream(badFilesPlaylist));
	}
		
	/**
	 * Prints songs that have positive ratings into a print stream (essentially creating a playlist)
	 */
	public static boolean printPositiveRatings(PrintStream ps) {
		
		boolean noRating = false;
		
		ps.println("File\trating\tfull path");
	
		for (MoveMain.F f : MoveMain.lst) {
			if (f.rating >= 3) {			
				ps.println(new File(f.from).getName()  + "\t" + f.rating + "\t" + f.from);
			}
		}

		return noRating;		
	}

	
	
	/**
	 * Prints songs that have positive ratings into a print stream (essentially creating a playlist)
	 */
	public static boolean printBadRatings(PrintStream ps) {
		
		boolean noRating = false;
		
		ps.println("File\trating\tfull path");
	
		for (MoveMain.F f : MoveMain.lst) {
			if (f.rating < 3 && f.rating != -1) {			
				ps.println(new File(f.from).getName()  + "\t" + f.rating + "\t" + f.from);
			}
		}

		return noRating;		
	}
	
	

}
