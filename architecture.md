# Class Descriptions

*   **`AddGoodMusicToMPD.java`**: Loads positively rated music files (rating >= 3, not explicit), shuffles them, and adds them to an MPD (Music Player Daemon) playlist named "linas-good".
*   **`CopyGoodMusic.java`**: Copies positively rated music files (rating >= 3) from a source to a target directory and generates playlists for good and unrated files.
*   **`GenReports.java`**: Generates text reports (playlists) for unrated, positively rated, and negatively rated music files, including file name, rating, and full path.
*   **`MoveMain.java`**: A core utility that recursively loads music files (MP3/OGG), extracts star ratings and comments from their audio tags, verifies ratings, and facilitates copying "good" music to specific target paths. It contains the logic for translating byte array rating values.
*   **`RunMain.java`**: Reads music files, translates their star ratings into a specific numerical format (likely for ID3 POPM tags), and writes the updated rating back to the audio tags.
*   **`TestFileRatingLoading.java`**: A test class that specifically calls `MoveMain.translateRating1` for a hardcoded MP3 file to test rating loading and translation.
*   **`UpdateFromDB.java`**: Connects to an SQLite database to read song paths and their ratings, then updates the corresponding music files' audio tags with these ratings, supporting ID3v23 and ID3v24 tags.