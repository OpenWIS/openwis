#OpenWIS OpenDJ Configuration

The following manual steps are required to configure the Password Validation and Policy for OpenWIS via the OpenDJ cli tools.

> Note: The majority of this can also be scripted via puppet via the cli tools

## OpenDJ – Config 

### Set the Password Character Set Validation

Step 1 : Create a dsconfig.properties file with the validation rules (replacing the variables <%= => with their values)

Step 2 : Copy the dsconfig.properties file to the opendj directory


    hostname                      = <%= @opendj_hostname %>
    port                          = 4445
    bindDN                        = cn=Directory Manager
    bindPassword                  = <%= @opendj_root_password %>
    validator-name                = "OpenWIS Character Set Password Validator"
    allow-unclassified-characters = true
    enabled                       = true
    character-set                 = 1:abcdefghijklmnopqrstuvwxyz
    character-set                 = 1:ABCDEFGHIJKLMNOPQRSTUVWXYZ
    character-set                 = 1:0123456789
    character-set                 = 1:!\"#\$%&\'\(\)*+,-./:\;\\<=\>?@[\\]^_\`{\|}~
    min-character-sets            = 3
    type                          = character-set
 
Step 2: Run the dsconfig tool as the openwis user which is located under the opendj/bin directory

    ${opendj_dir}/dsconfig --cli --acceptLicense --no-prompt --propertiesFilePath ${opendj_dir}/dsconfig.properties


### Set the Password Length Validation

TODO

> More information about the properties can be located @ 
http://docs.oracle.com/cd/E19476-01/821-0507/password-validator.html


### Apply the Password Validator to Password Policy

-	Select Password Policy (28)
-	Select View and edit an existing Password Policy (3)
-	Select Default Password Policy (1)
-	Select  password-validator (25)
-	Select Add one or more values (2) 
-	Select the password validators you wish to add i.e. Character Set and Length-Based Password Validator

Once all the required properties are set you should see the ‘password-validator’ screen updated (see below).
 
       Property                                   		Value(s)
       ----------------------------------------------------------------------
       password-validator                         Character Set, Length-Based Password Validator


### Force Password Change

Following properties will need to be set to force users to change their password within the given time limit;

-	Select Password Policy (28)
-	Select View and edit an existing Password Policy (3)
-	Select Default Password Policy (1)
-	Select grace-login-count (9) and set the value i.e. 3
-	Select password-expiration-warning-interval (21) and set the value i.e. 5 days
-	Select require-change-by-time and set the date/time the user password expires i.e. 20140113130000Z

Once all the required properties are set you should see password policy updated (see below).


      Property                                   		Value(s)
      ----------------------------------------------------------------------
      grace-login-count                          	3
      password-expiration-warning-interval        5 d
      require-change-by-time                     	20160113130000Z

      
### Setting Up Account Lock Out

-	Select Password Policy (28)
-	Select View and edit an existing Password Policy (3)
-	Select Default Password Policy (1)
-	Select lockout-failure-count (14) and set the number of entries a user is allowed before the account is locked out i.e. 5
-	Select lockout-duration(13) and set how long the user should be locked out for i.e. 15 minutes 

Once all the required properties are set you should see password policy updated (see below).

      Property                                   		Value(s)
      ----------------------------------------------------------------------
      lockout-duration                           		2 m
      lockout-failure-count                      		5

> More information about the above properties can be located @
http://docs.oracle.com/cd/E19476-01/821-0507/password-policy.html


## Setting up Authentication Chaining 

TODO - Is this still needed ?

## Apply validation to Portals

TODO - Looks like this is a hack in the Javascript