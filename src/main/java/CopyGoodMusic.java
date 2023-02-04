import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.io.Files;


public class CopyGoodMusic {

	public static void main(String[] args) throws Exception {

		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
			
		if (args.length  < 3) {
			System.out.println("The following arguments are expected.");
			System.out.println("  1 source directory");
			System.out.println("  2 unrated playlist (to be created)");
			System.out.println("  3 good playlist (to be created)");
			System.out.println("  4 target directory");
			System.out.println("  5 number of files to copy");
			return;
		}
		
		File sd = new File(args[0]);
	
		if (!sd.isDirectory()) {
			System.out.println("The first argument must be a directory");
			return;
		}
		
		
		File unratedFilesPlaylist =  new File(args[1]);			
		File goodFilesPlaylist = new File(args[2]);
		boolean flagCopy = false;
		
		
		File targetDir = null;
		int numberOfFilesToCopy = 0;
		
		if (args.length >= 4) {
			// If this flag is set we will copy files, otherwise we'll just create playlists and will skip copying the files
			flagCopy = true;
		}
		
		if (flagCopy) {
			targetDir = new File(args[3]);
	
			if (!targetDir.exists()) {
				targetDir.mkdir();
			}
			
			if (targetDir.exists()) {
				if (!targetDir.isDirectory()) {
					System.out.println("Target is not a directory");
					return;
				}			
			} else {
				System.out.println("Target directory does not exist");
				return;
			}
	
			numberOfFilesToCopy = Integer.parseInt(args[4]); 
		}
		MoveMain.loadFilesAndRatingsRecursively(sd);		
				
		MoveMain.verifyRatings(new PrintStream(unratedFilesPlaylist));
		printPositiveRatings(new PrintStream(goodFilesPlaylist));

		if (flagCopy) {
			copyFilesToTargetDir(targetDir, numberOfFilesToCopy);
		}
	}
	

	static void copyFilesToTargetDir(File targetDir, int numberOfFilesToCopy) {

		List<MoveMain.F> positiveList = MoveMain.lst.stream()
			.filter(f -> f.rating >= 3)
			.collect(Collectors.collectingAndThen(
				Collectors.toList(),
				 collected -> {Collections.shuffle(collected); return collected;}));

		int numFiles = positiveList.size() < numberOfFilesToCopy ? positiveList.size() : numberOfFilesToCopy;
		for(MoveMain.F f : positiveList.subList(0, numFiles)) {

			File fromFile = new File(f.from);
			String to = targetDir.getAbsolutePath() + File.separator + fromFile.getName();
			File toFile = new File(to);

			if (!toFile.exists()) {
				try {
					Files.copy(new File(f.from), toFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("File with this name already exists - skipping the file: " + toFile);
			}
		}

	}
	
	/**
	 * Prints songs that have positive ratings into a print stream (essentially creating a playlist)
	 */
	public static boolean printPositiveRatings(PrintStream ps) {
		
		boolean noRating = false;
		
		ps.println("#Positive ratings");
	
		for (MoveMain.F f : MoveMain.lst) {
			if (f.rating >= 3) {			
				ps.println(f.from);
			}
		}

		return noRating;		
	}
	
	

}
