TEST_NAME=$1

echo "Run test $TEST_NAME"

#SELENIUM_SERVER_WIN=172.25.31.114
#SELENIUM_SERVER_WIN=10.211.55.3
#SELENIUM_SERVER_WIN=172.25.31.114
SELENIUM_SERVER_WIN=localhost
BROWSER_URL=http://localhost:8080/

DATE_DIR=`date "+%Y-%m-%d-%H%M"`
mkdir -p results/$DATE_DIR

rm -rf target/surefire*

mvn surefire-report:report -DseleniumServer=$SELENIUM_SERVER_WIN -Dbrowser=*firefox -DbrowserUrl=$BROWSER_URL -DprojectDir=. -DadminWebapp=openwis-portal -DuserWebapp=openwis-portal -Dtest=$TEST_NAME

#mvn surefire-report:report -DprojectDir=. -Dtest=$TEST_NAME

mkdir results/$DATE_DIR/win-firefox
cp target/surefire-reports/* results/$DATE_DIR/win-firefox
cp target/site/surefire-report.html results/$DATE_DIR/win-firefox

#mvn surefire-report:report -DseleniumServer=$SELENIUM_SERVER_WIN -Dbrowser=*iexplore -DbrowserUrl=$BROWSER_URL -DprojectDir=. -DadminWebapp=openwis-portal -DuserWebapp=openwis-portal -Dtest=$TEST_NAME
#mkdir results/$DATE_DIR/win-iexplore
#cp target/surefire-reports/* results/$DATE_DIR/win-iexplore
#cp target/site/surefire-report.html results/$DATE_DIR/win-iexplore
