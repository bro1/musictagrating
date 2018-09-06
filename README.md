musictagrating
==============

Move the music tag rating from incompatible tag produced by foobar2000 to more compatible rating format.

This is mostly working but the newer versions of Foobar2000 have a configuration setting allowing to write the
tags in the more compatible format in the first place anyway.

MoveMain
--------

This class processes all rated files and moves any files that are rated 3 stars or higher to a target directory.

If any of the files do not have a star rating allocated then the list of files with no ratings is reported and the files are left intact.

Usage example:
> java MoveMain c:\source_dir c:\target_dir

CopyGoodMusic
-------------

* scan the source directory
* identify all files rated 3 stars or more
* copy specified number of files in random order into the target directory      

UpdateFromDB
------------

Look at the blob3.blob (as produced by Android Rocket Player) 
sqlite3 DB and write the rating information from DB to the relevant file.

The workflow is like this:
 1. Copy files to Android phone
 1. Rate them
 1. Copy the DB to Linux machine
 1. UpdateFromDB to transfer ratings to the file

Limitation: at the moment deals with ID3V23 tags only.


Troubleshooting
---------------

Sometimes a file will contain more than one music rating tag (e.g. WMP RATING and POPULARIMETER).
We will treat the file as rated only when there is exactly one rating tag.

So far I have experienced this scenario when Banshee on Linux rates a song as 0 stars. 
Rather than removing POPULARIMETER tag Banshee writes it out as 0 stars. This tag is 
then ignored by Foobar2000 and Fooobar2000 writes out an additional new WMP Rating tag.

In order to resolve this, I have removed the POPULARIMETER tag manually using the software from mp3tag.de. 
If this is not done Banshee will use POPULARIMETER tag and will ignore the more correct WMP RATING field.