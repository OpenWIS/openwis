#!/usr/bin/env python
#
#   Generate cache files
#

from datetime import datetime
import os.path
import random
import shutil


# Options
WORKING_DIR = "/tmp"
CACHE_DROP_DIR = "/data/openwis/harness/ingesting"

# Generates a random string
def rand_str(length):
    return "".join([ chr(random.randrange(0x61,0x7a)) for x in range(length) ])


# Writes a cache file to the destination directory.  Returns the filename
def write_cache_file(ttaaiicccc, destDir):
    ts = datetime.utcnow()
    yygggg = ts.strftime("%d%H%M")
    dateStamp = ts.strftime("%Y%m%d%H%M%S")
    centre = ttaaiicccc[6:]
    ext = "txt"
    randStr = rand_str(16)

    testFilename = "A_%s%s_C_%s_%s_%s.%s" % (ttaaiicccc, yygggg, centre, dateStamp, randStr, ext)
    testContent = rand_str(256)

    targetFile = os.path.join(destDir, testFilename)
    with open(targetFile, "w") as f:
        f.write("Filename: " + testFilename + "\n")
        f.write("TTAAIICCCC: " + ttaaiicccc + "\n")
        f.write("\n")
        f.write("Some random content:\n")
        f.write("\n")
        f.write(testContent + "\n")

    return targetFile


# Writes a cache file using the standard configuration
def add_cache_file(ttaaiicccc):

    # Write it to the working directory first
    workingFile = write_cache_file(ttaaiicccc, WORKING_DIR)

    # Move it to the drop directory
    shutil.copy(workingFile, CACHE_DROP_DIR)

    print os.path.basename(workingFile)


# --------------------------------------------------------------------------
#

for i in range(50):
    add_cache_file("IUPK14AMMC")
    add_cache_file("FTPF21NTAA")
    add_cache_file("HUTB15EGRR")
    add_cache_file("IUAX01WSSS")
    add_cache_file("UDIO01AMMC")
    add_cache_file("UQMS21WMKK")
    add_cache_file("HRUM50LFPW")
