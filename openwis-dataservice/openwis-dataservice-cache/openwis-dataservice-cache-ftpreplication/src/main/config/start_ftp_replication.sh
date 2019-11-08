#!/bin/sh
# Start script for OpenWIS FTP replication 

# Setup classpath
FTP_REPLICATION_DIR=/home/openwis/FtpReplication
LIB=$FTP_REPLICATION_DIR/lib

NATIVE_LIB=$FTP_REPLICATION_DIR/jnotify-0.94/64-bit_RedHat_5-5
#NATIVE_LIB=$FTP_REPLICATION_DIR/jnotify-0.94/64-bit_Linux
#NATIVE_LIB=$FTP_REPLICATION_DIR/jnotify-0.94

MAIN_CLASS=org.openwis.dataservice.replication.ftp.FTPReplicator

for jarFile in $LIB/*.jar; do
	CLASSPATH="$CLASSPATH:$jarFile"
done

CLASSPATH=$CLASSPATH:$FTP_REPLICATION_DIR

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi


# Setup start options
nohup "$JAVA" -Djava.library.path=$NATIVE_LIB $JAVA_OPTS -classpath "$CLASSPATH" $MAIN_CLASS &
