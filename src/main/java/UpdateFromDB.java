import java.sql.Statement;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyPOPM;

public class UpdateFromDB {
	
	// TODO: make the directory parametrizable
	// TODO: make the "music-to-sort" folder name parametrizable
	// TODO: make the location parametrizable
	// TODO: check if I need to support other ID3 tag versions
	private static String dir = "/mnt/d-drive/syncthing-data/audio-video";
	
	public static void main(String[] args) throws Exception {
		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
		
		
	    Class.forName("org.sqlite.JDBC");

	    Connection connection = null;
	      // create a database connection
	      connection = DriverManager.getConnection("jdbc:sqlite:/mnt/d-drive/syncthing-data/audio-video/music-to-sort/blob3.blob");
	      Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("select * from songs");
		while (rs.next()) {
			String p = rs.getString("_path");
			int index = p.indexOf("/music-to-sort");
			if (index == -1) continue;
			String p1 = p.substring(index);
			
			int rating = rs.getInt("_rating");
			
			System.out.println(p1 + "\t" + rating);
			if (rating != 0) {
				
				File file = new File(dir + p1);
				if (file.exists()) {
					updateRating(file, rating);
				}
			}
			
		}
		
	}

	private static void updateRating(File file, int stars) throws CannotReadException, IOException, TagException,
			ReadOnlyFileException, InvalidAudioFrameException, CannotWriteException {
		AudioFile f = AudioFileIO.read(file);
		
		int popmratingvalue = RunMain.translateRating(stars);
		
		Tag tag = f.getTag();
		
		if (tag instanceof ID3v23Tag) {
			ID3v23Tag t = (ID3v23Tag) tag;
			FrameBodyPOPM frameBodyPOPM = new FrameBodyPOPM("me", popmratingvalue, 0);
			ID3v23Frame f1 = new ID3v23Frame("POPM");
			
			f1.setBody(frameBodyPOPM);
			t.setFrame(f1);
		} else {
			System.out.println("Unsupported tag type " + tag.getClass().getName());
		}
		AudioFileIO.write(f);
	}

}
