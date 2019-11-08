OpenWIS Factory tests

Prerequisites:
- selenium 1.0.3
- firefox (if configured)
- ie (if configured)
- db: WARNING the DB is cleared/modified by each test

How to run:
- start openwis components

- start selenium server (not on default port to avoid conflict with ldap)
    java -jar selenium-server.jar -port 4445

- From eclipse: 
    - run as junit tests
    - some .launch has been saved; can be configured/run 

- From command line:
    - configure if needed shells (runTests.sh or runSingleTest.sh)
    - start shells

