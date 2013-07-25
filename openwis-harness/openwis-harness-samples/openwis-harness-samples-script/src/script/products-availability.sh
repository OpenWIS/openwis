#!/bin/sh

# Return all products available since a timestamp
echo "=== Products Availability ==="

# Retrieve arguments
TIMESTAMP=$1
OUTPUT_FILE=$2

echo "Write all products URN since $TIMESTAMP to file $OUTPUT_FILE"

# Write some dummy urns into the output file
URNS=( 'urn:x-wmo:md:int.wmo.wis::EOXF42MSGG' 'urn:x-wmo:md:int.wmo.wis::TGXF24MSFE' )

for ((i=0; i< ${#URNS[@]} ; i++)); do
	echo ${URNS[${i}]} >> $OUTPUT_FILE
done