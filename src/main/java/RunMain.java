import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTagField;

public class RunMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

//		printRating("data/(09) Skorost - Mumij Trol.mp3");
//		translateRating1("data/Terrorvision - 01 - Dya Wanna Go Faster.mp3");
		
		File sd = new File("translate");
		File[] ff = sd.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				String n = arg0.getName().toLowerCase();
				return n.endsWith(".mp3") || n.endsWith(".ogg");				
			}
		});
		
		for (File f : ff) {						
			translateRating1(f);
		}
		

	}

	private static void translateRating1(File testFile)
			throws Exception {
						
		AudioFile f = AudioFileIO.read(testFile);
		
		Tag tag = f.getTag();
		
		int rt = getStarRating(tag);
		
		int commonRating = 0;
		if (testFile.getName().toLowerCase().endsWith(".ogg")) {
			commonRating = rt * 20;
		} else {
			commonRating = translateRating(rt);
		}
		
		
		tag.deleteField(FieldKey.RATING);
		tag.addField(FieldKey.RATING, Integer.toString(commonRating));
		
		
		AudioFileIO.write(f);
	}

	public static int translateRating(int rt) {
		
		int rating; 
		
		switch(rt) {
			case 0: rating = 0; break;
			case 1: rating = 1; break;
			case 2: rating = 64; break;
			case 3: rating = 128; break;
			case 4: rating = 196; break;
			case 5: rating = 255; break;
			default: rating = 0;
		}
		

		return rating;
	}

	private static int getStarRating(Tag tag) {
		int starRating = 0;
		

		Iterator<TagField> fi = tag.getFields();

		while (fi.hasNext()) {
			TagField ff = fi.next();

			if (ff instanceof VorbisCommentTagField) {
				VorbisCommentTagField vt = (VorbisCommentTagField) ff;
				
				System.out.println(vt.getId());
				
				if (vt.getId().equals("RATING")) {
					String ratingStars = vt.getContent();
					
					int starRatingTemp = -1;
					
					try {
						starRating = Integer.parseInt(ratingStars);
					} catch (RuntimeException e) {
						System.out.println("ignoring exception");
					} 
					
					if (starRatingTemp >= 0 && starRatingTemp <= 5) {
						starRating = starRatingTemp; 
					}				

				}
			}
			
			
			if (ff.getId().equals("TXXX")) {
				
				if (ff instanceof ID3v23Frame) {
					ID3v23Frame fr = (ID3v23Frame) ff;

					if (fr.getBody().getBriefDescription()
							.contains("\"rating\"")) {
						String ratingStars = fr.getContent();
						
						int starRatingTemp = -1;
						
						try {
							starRating = Integer.parseInt(ratingStars);
						} catch (RuntimeException e) {
							System.out.println("ignoring exception");
						} 
						
						if (starRatingTemp >= 0 && starRatingTemp <= 5) {
							starRating = starRatingTemp; 
						}				
					}
				}
			}

		}
		
		 return starRating;
	}

	private static void printRating(String fileName)
			throws CannotReadException, IOException, TagException,
			ReadOnlyFileException, InvalidAudioFrameException {
		File testFile = new File(fileName);
		AudioFile f = AudioFileIO.read(testFile);
		Tag tag = f.getTag();
		AudioHeader h = f.getAudioHeader();

		String r = tag.getFirst(FieldKey.RATING);
		System.out.println(fileName + " = " + r);
	}

}
