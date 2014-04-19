import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;

public class MoveMain {

	private static final class Filt implements FileFilter {
		@Override
		public boolean accept(File arg0) {
			if (arg0.isDirectory()) return true;
			
			String n = arg0.getName().toLowerCase();
			return n.endsWith(".mp3") || n.endsWith(".ogg");				
		}
	}
	
	

	/**
	 * @param args
	 */
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

	private static void translateRating1(File testFile)
			throws Exception {
						
		AudioFile f = AudioFileIO.read(testFile);
		
		String fo = f.getAudioHeader().getFormat();
		
		Tag tag = f.getTag();
		

		List<TagField> z = tag.getFields(FieldKey.RATING);		
		
		if (z.size() == 0) {
			System.out.println(testFile);
			System.out.println("UNKNOWN");
		}
		else {
			for (TagField t : z) {
				byte[] rc = t.getRawContent();
				int i = getStarRatingFromByteArray(rc, fo);
				if (i == -1) {
					System.out.println(testFile);
					System.out.println(i);
				}
			}
		
		}
		
		
//		AudioFileIO.write(f);
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

			     if (i >=  1 && i <= 31)  rating= 1;
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
				
		if (rating == -1) {
			System.out.println(fo);
			System.out.println("bytevalue = " + i);
		}
		return rating;
	}


}
