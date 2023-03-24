#!/bin/bash

input_file=$1

while read line; do
    # Execute a command and store the output in a variable
    fn=$(echo "$line" | cut -f 3)
    command_output=$(mp3info -p "%a - %t\n"  "$fn")

    url=$(mp3info -p "%a - %t\n"  "$fn" | jq -sRr @uri)

    # Print the original line with the command output appended
    echo -e "$command_output\t$line\thttps://music.youtube.com/search?q=$url\thttps://open.spotify.com/search/$url"
done < <(tail -n +2 $input_file)
