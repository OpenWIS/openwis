 
#SELENIUM_SERVER_WIN=10.211.55.3
SELENIUM_SERVER_WIN=172.25.31.114
BROWSER_URL=http://192.168.1.108:8080/

DATE_DIR=`date "+%Y-%m-%d-%H%M"`
mkdir -p results/$DATE_DIR

mvn test -DseleniumServer=$SELENIUM_SERVER_WIN -Dbrowser=*firefox -DbrowserUrl=$BROWSER_URL
mkdir results/$DATE_DIR/win-firefox
cp target/surefire-reports/* results/$DATE_DIR/win-firefox

mvn test -DseleniumServer=$SELENIUM_SERVER_WIN -Dbrowser=*iexplore -DbrowserUrl=$BROWSER_URL
mkdir results/$DATE_DIR/win-iexplore
cp target/surefire-reports/* results/$DATE_DIR/win-iexplore

