#!/bin/bash

set -o nounset
set -o errexit

# Check directory of OpenAM deploy war.
OPEN_AM_WAR_DIR="<%= @tomcat_dir %>/webapps/openam"
BASE_DIR=`pwd`

echo "Customizing OpenAM Pages ..."

# Copy All JSP Files

FROM_JSP_DIR="$BASE_DIR/jsp"
TO_JSP_DIR="$OPEN_AM_WAR_DIR/config/auth/default"

cp $FROM_JSP_DIR/* $TO_JSP_DIR

# Copy All Images

FROM_IMG_DIR="$BASE_DIR/images"
TO_IMG_DIR1="$OPEN_AM_WAR_DIR/com_sun_web_ui/images"
TO_IMG_DIR2="$OPEN_AM_WAR_DIR/password/images"
TO_IMG_DIR3="$OPEN_AM_WAR_DIR/images"

cp $FROM_IMG_DIR/* $TO_IMG_DIR1
cp $FROM_IMG_DIR/* $TO_IMG_DIR2
cp $FROM_IMG_DIR/* $TO_IMG_DIR3

# Copy CSS file
FROM_CSS_DIR="$BASE_DIR/css"
TO_CSS_DIR="$OPEN_AM_WAR_DIR/css"

cp $FROM_CSS_DIR/* $TO_CSS_DIR

echo "Done"
