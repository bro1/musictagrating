import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bff.javampd.playlist.Playlist;
import org.bff.javampd.server.MPD;

import com.google.common.io.Files;


public class AddGoodMusicToMPD {

	public static void main(String[] args) throws Exception {			

		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
		
		
		
		MPD z = new MPD.Builder().server("moode.local").build();
		z.getPlaylist().clearPlaylist();
		
		
		
		if (args.length  < 2) {
			System.out.println("The following arguments are expected.");
			System.out.println("  1 source directory");
			System.out.println("  2 hostname");
			return;
		}
		
		File sd = new File(args[0]);
	
		if (!sd.isDirectory()) {
			System.out.println("The first argument must be a directory");
			return;
		}
		
		
		MoveMain.loadFilesAndRatingsRecursively(sd);		
				
//		MoveMain.verifyRatings(new PrintStream(unratedFilesPlaylist));
//		printPositiveRatings(new PrintStream(goodFilesPlaylist));


		addToMPD(z);

	}
	

	static void addToMPD(MPD z) {
		
		Playlist play = z.getPlaylist();

		List<MoveMain.F> positiveList = MoveMain.lst.stream()
			.filter(f -> {return f.rating >= 3 && !f.isExplicit();})
			.collect(Collectors.collectingAndThen(
				Collectors.toList(),
				 collected -> {Collections.shuffle(collected); return collected;}));

		for(MoveMain.F f : positiveList) {

			File fromFile = new File(f.from);
			String tofile = fromFile.getAbsolutePath().replace("/media/nas-music-smb/", "NAS/music/");
			try {
				play.addSong(tofile, true);
			} catch (Throwable t) {
				System.out.println("Cannot add " + tofile);
				t.printStackTrace();
			}
			
		}
		
		play.deletePlaylist("linas-good");
		play.savePlaylist("linas-good");

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
