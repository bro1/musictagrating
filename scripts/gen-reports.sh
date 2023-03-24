java -cp /home/bro1/projects/musictagrating/target/classes:/home/bro1/.m2/repository/org/jaudiotagger/2.0.3/jaudiotagger-2.0.3.jar:/home/bro1/projects/musictagrating/lib/sqlite-jdbc-3.23.1.jar GenReports "/home/bro1/syncthing-data/main-phone-media/rate" report-unrated.txt report-good.txt report-bad.txt


bash append.sh report-good.txt > report-good-id.txt
bash append.sh report-bad.txt > report-bad-id.txt
