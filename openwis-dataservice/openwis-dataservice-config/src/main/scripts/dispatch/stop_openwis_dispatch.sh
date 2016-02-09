#!/bin/sh
#
# Dispatch incoming files for ingestion and replication.
#

DISPATCH_PID=dispatch.pid

if [ -e $DISPATCH_PID ]; then
	kill -9 `cat $DISPATCH_PID`
	rm $DISPATCH_PID
fi

