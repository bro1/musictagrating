#!/bin/bash

# Open an input file
# input file is a tab delimited file containing the following columns
# 1. file name e.g. this is a file.mp3
# 2. star rating 1-5 e.g. 3
# 3. /home/someone/syncthing-data/rate/album/this is a file.mp3
#
# The script will output the following columns:
# 1.    artist - song name (as extracted by mp3info)
# 2-4.  all columns from original input file
# 5. URL to search for that song on Youtube Music
# 6. URL to search for that song on Spotify music


input_file=$1

while read line; do
    # Execute a command and store the output in a variable
    fn=$(echo "$line" | cut -f 3)
    command_output=$(mp3info -p "%a - %t\n"  "$fn")

    url=$(mp3info -p "%a - %t\n"  "$fn" | jq -sRr @uri)

    # Print the original line with the command output appended
    echo -e "$command_output\t$line\thttps://music.youtube.com/search?q=$url\thttps://open.spotify.com/search/$url"
done < <(tail -n +2 $input_file)
