#!/bin/sh
#
# Dispatch incoming files for ingestion and replication.
#

DISPATCH_PID="dispatch.pid"

# useINotify = 0 if inotifywait is found
useINotify="`which inotifywait > /dev/null; echo $?`"


rootFolder="/var/opt/openwis"

incomingFolder="$rootFolder/harness/incoming"
ingestingFolder="$rootFolder/harness/ingesting"
sendingFolder="$rootFolder/replication/sending"
sendingLocalFolder="$sendingFolder/local"
desinationsFolder="$sendingFolder/destinations"

dispatchLogFile="$rootFolder/replication/dispatch.log"
logsEnabled="false"

# Dispatch file given as parameter ($1)
dispatchFile() 
{
	incomingFile=$1
	filename=`basename $incomingFile`
	
	if [ $logsEnabled = "true" ]; then
		echo "`date +%Y-%m-%d_%H-%M-%S`: dispatching file $1" >> $dispatchLogFile
	fi
	
	# Create a hard link in ingesting folder
	ln $incomingFile $ingestingFolder

	# Move to sending / local folder
	mv $incomingFile $sendingLocalFolder
	
	# Create links in replication destinations
	destinations=`find $desinationsFolder/* -type d`
	for d in $destinations
	do
		ln -s $sendingLocalFolder/$filename $d
	done
}

# Scan incoming folder
scanIncomingFolder() {
	files=`find $incomingFolder -not -name "*.tmp" -and -not -name ".*" -type f -size +0`
	for f in $files
	do
		dispatchFile $f
	done
}

# Check if dispatch did not crash (with PID file)
if [ -e $DISPATCH_PID ]
then
  ps -p `cat $DISPATCH_PID` &> /dev/null
  if [ $? = 0 ]
  then
    echo "Dispatch still running"
    exit 0
  else
    echo "Dispatch crashed, cleaning remaining PID file"
    rm $DISPATCH_PID
  fi
fi


if [ $useINotify == "0" ] ; then

	echo "Installing INofityWait to scan $incomingFolder"
	inotifywait -qmre CREATE,MOVED_TO --format '%w%f' --exclude ".tmp" $incomingFolder | while read f; do dispatchFile "$f"; done &
		
	ps -ef |grep inotifywait | grep -v "grep" |awk '{ print $2 }' > $DISPATCH_PID; 

	# Scan incoming folder for files that were already there
	echo "Scanning $incomingFolder for files already present before INotify"
	scanIncomingFolder

else

	# Storing dispatch PID file for stopping
	echo $$ > $DISPATCH_PID
	
	echo "Installing find based scanner of $incomingFolder"
	while [ true ]
	do
		scanIncomingFolder
		sleep 0.3
	
	done

fi