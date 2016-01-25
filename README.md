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