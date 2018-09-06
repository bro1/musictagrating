import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;

public class MoveMain {

	
	public static class F {
		String from;
		String to;
		int rating;
	}
	
	static List<F> lst = new LinkedList<>();
	
	private static final class Filt implements FileFilter {
		@Override
		public boolean accept(File arg0) {
			if (arg0.isDirectory()) return true;
			
			String n = arg0.getName().toLowerCase();
			return n.endsWith(".mp3") || n.endsWith(".ogg");				
		}
	}
	
	

	public static void main(String[] args) throws Exception {

		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
			
		if (args.length  < 1) {
			System.out.println("At least one argument is expected");
			return;
		}
		
		File sd = new File(args[0]);
	
		if (!sd.isDirectory()) {
			System.out.println("The first argument must be a directory");
			return;
		}
		
		process(sd);
		
		if(verifyRatings(System.out)) {
			return;
		}
		
		
		setupTo(args[0], args[1]);
		
		copy();

	}

	private static void copy() throws IOException {
		for (F f : lst) {
			if (f.to != null) {
				System.out.println(f.from + " -> " + f.to);
				
				Path targetPath = new File(f.to).toPath();
				Files.createDirectories(targetPath.getParent());
				Files.copy(new File(f.from).toPath(),targetPath);
			}
		}
		
	}

	private static void setupTo(String fromDir, String toDir) {
		
		File fromDirFile = new File(fromDir);
		File toDirFile = new File(toDir);
		
		for (F f : lst) {
			if (f.rating >= 3) {
				System.out.print(f.from + " " + f.rating);
				File fff = new File(f.from);
				String prefix;
				if (allAbove(f)) {
					prefix = "full";
				} else {
					prefix = "cleaned";
				}

				String prn;
				if (fff.getParent().startsWith(fromDirFile.getPath())) {
					prn = fff.getParent().substring(fromDirFile.getPath().length()+1);
				} else {
					throw new IllegalArgumentException("Must match");
				}
				
				
				f.to = toDirFile.getPath() + File.separator + prn + " (" + prefix + ")" + File.separator + fff.getName();


			}
		}

	}

	private static boolean allAbove(F f) {
		
		boolean all = true;
		File fl = new File(f.from);
		String zz = fl.getParent();
		
		for (F ff : lst) {
			if (ff.from.startsWith(zz)) {
				if(new File(ff.from).getParent().equals(zz) && ff.rating < 3) {
					return false;
				}
			}
		}		
		
		return true;
	}

	public static boolean verifyRatings(PrintStream ps) {
		
		boolean noRating = false;
		
		for (F f : lst) {
			if (f.rating == -1) {	
				noRating = true;
			}
		}
		
		if (noRating){
			ps.println("# Ratings for the following files could not be determined");
		
			for (F f : lst) {
				if (f.rating == -1) {			
					ps.println(f.from);
				}
			}

		}

		return noRating;		
	}
	
	
	

	public static void process(File sd) throws Exception {
		File[] ff = sd.listFiles(new Filt());		
		for (File f : ff) {
			if (f.isDirectory()) {
				process(f);
			} else {
				translateRating1(f);
			}
		}
	}

	private static void translateRating1(File musicFile) throws Exception {
			            
		F ff = new F();
		ff.from = musicFile.getPath();
		ff.rating = -1;

		AudioFile f = null;
		
		// if the file cannot be processed  
		try {
			f = AudioFileIO.read(musicFile);
		} catch (Throwable t) {
			System.out.println("File cannot be processed: " + ff.from);
			t.printStackTrace();
			lst.add(ff);
			return;
		}
		
		String fileFormat = f.getAudioHeader().getFormat();
		
		Tag tag = f.getTag();

		if (tag != null) {
			List<TagField> ratingFieldsList = tag.getFields(FieldKey.RATING);		
				                
		    // Sometimes this will contain more than one music rating tag (e.g. WMP RATING and POPULARIMETER)
		    // We will treat the file as rated only when there is exactly one rating tag.
		    if (ratingFieldsList.size() == 1) {
		        for (TagField t : ratingFieldsList) {
		            byte[] rc = t.getRawContent();                              
		            ff.rating = getStarRatingFromByteArray(rc, fileFormat);
		        }
		    } else {
		        ff.rating = -1;
		    }
		}
	
		lst.add(ff);
	}

	private static int getStarRatingFromByteArray(byte[] rc, String fo) {
		
		int rating = -1;
		
		String kmp3 = "MPEG-1 Layer 3";
		String kmp32 = "MPEG-1 Layer 2";
		String kmp33 = "MPEG-2 Layer 3";
		String ogg = "Ogg Vorbis v1";

		byte b = rc[rc.length - 1];
		int i = b & 0xFF;
		
		if (kmp3.equals(fo) || kmp32.equals(fo) || kmp33.equals(fo)) {
		
			// Based on http://en.wikipedia.org/wiki/ID3#ID3v2_Rating_tag_issue

			     if (i >=  1 && i <= 31)  rating = 1;
			else if (i >= 32 && i <= 95) rating = 2;
			else if (i >= 96 && i <= 159) rating = 3;
			else if (i >= 160 && i <= 223) rating = 4;
			else if (i >= 224 && i <= 255) rating = 5;
			    			
		} else if (ogg.equals(fo) ) { 
			switch (rc[rc.length - 1]) {
				// ogg file ratings
				case 49: rating = 1; break;
				case 50: rating = 2; break;
				case 51: rating = 3; break;
				case 52: rating = 4; break;
				case 53: rating = 5; break;		
				default: rating = -1;
			}
		}
		
		// A bit of debug information
//		if (rating == -1) {
//			System.out.println(fo);
//			System.out.println("bytevalue = " + i);
//		}
		
		
		return rating;
	}


}
