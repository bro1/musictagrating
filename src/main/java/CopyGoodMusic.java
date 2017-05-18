import java.io.File;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CopyGoodMusic {

	public static void main(String[] args) throws Exception {

		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
			
		if (args.length  < 5) {
			System.out.println("At least one argument is expected");
			return;
		}
		
		File sd = new File(args[0]);
	
		if (!sd.isDirectory()) {
			System.out.println("The first argument must be a directory");
			return;
		}
		
		
		File unratedFilesPlaylist =  new File(args[1]);
		File goodFilesPlaylist = new File(args[2]);
		File targetDir = new File(args[3]);
		int numberOfFilesToCopy = Integer.parseInt(args[4]); 
		
		MoveMain.process(sd);		
				
		MoveMain.verifyRatings(new PrintStream(unratedFilesPlaylist));
		printPositiveRatings(new PrintStream(goodFilesPlaylist));
		
		// TODO: copy the files to the target directory			

	}
	
	
	public static boolean printPositiveRatings(PrintStream ps) {
		
		boolean noRating = false;
		
			ps.println("Positive ratings");
		
			for (MoveMain.F f : MoveMain.lst) {
				if (f.rating >= 3) {			
					ps.println(f.from);
				}
			}



		return noRating;		
	}
	
	

}
