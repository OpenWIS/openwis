#!/bin/sh
# Stop script for OpenWIS FTP replication 
FTP_REPLICATION_CONF_DIR=/var/opt/openwis/replication

touch $FTP_REPLICATION_CONF_DIR/stop-replicator


echo "Waiting for FTP Replicator to stop..."

FTP_REPLICATOR_PIDFILE=$FTP_REPLICATION_CONF_DIR/replicator.pid

FTP_REPLICATOR_PID=`ps anx | grep "FTPReplicator" | grep -v grep  | awk '{print $1}'` 
echo $FTP_REPLICATOR_PID > $FTP_REPLICATOR_PIDFILE


SLEEP=20
while [ $SLEEP -ge 0 ]; do
  kill -0 `cat $FTP_REPLICATOR_PIDFILE` >/dev/null 2>&1
  if [ $? -gt 0 ]; then
    rm $FTP_REPLICATOR_PIDFILE
    break
  fi
  
  sleep 1
  SLEEP=`expr $SLEEP - 1 `
done

if [ -f "$FTP_REPLICATOR_PIDFILE" ]; then
  echo "Killing: `cat $FTP_REPLICATOR_PIDFILE`"
  kill -9 `cat $FTP_REPLICATOR_PIDFILE`
  rm $FTP_REPLICATOR_PIDFILE
fi

exit 0
