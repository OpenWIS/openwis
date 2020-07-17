# OpenDJ

## Search

Search the attribute pwdFailureTime, pwdAccountLockedTime and sunAMAuthInvalidAttemptsData for account cosmin
```./ldapsearch --port 1389 --baseDN dc=opensso,dc=java,dc=net --bindDN "cn=Directory Manager" --bindPassword password uid=cosmin sunAMAuthInvalidAttemptsData pwdFailureTime pwdAccountLockedTime```

Search all accounts
```
./ldapsearch --port 1389 --baseDN dc=opensso,dc=java,dc=net --bindDN "cn=Directory Manager" --bindPassword password uid=*
```

## Enable / disable account

- list account status
```./manage-account -D "cn=directory manager" -w password get-account-is-disabled   --targetDN "uid=cosmin,ou=people,dc=opensso,dc=java,dc=net"```


## Password policy

- Show password policy
```./dsconfig -D "cn=directory manager" -w password -n get-password-policy-prop   --policy-name "Default Password Policy"```

- Set lock counts
```./dsconfig -D "cn=directory manager" -w password -n set-password-policy-prop   --policy-name "Default Password Policy" --set "lockout-failure-count:2"```

- Force user to change password if password has been reseted
```./dsconfig -D "cn=directory manager" -w password -n set-password-policy-prop   --policy-name "Default Password Policy" --set "force-change-on-reset:true"```

- Set password expire date to 5 days and warning to 2 days
```
./dsconfig -h localhost -p 4444 -D "cn=Directory Manager" -w password -n set-password-policy-prop --policy-name "Default Password Policy" --set "max-password-age: 5 days" --set "password-expiration-warning-interval: 2 days"
```

- set the attribute name for last login timestamp
```
./dsconfig -h localhost -p 4444 -D "cn=Directory Manager" -w password -n set-password-policy-prop --policy-name "Default Password Policy" --set "last-login-time-attribute:OpenWISLastLoginTime" --set "last-login-time-format:yyyy-MM-dd'T'HH:mm:ss"
```
> This commands tells to OpenDJ to update the OpenWISLastLoginTime at each login. The date format is specified in last-login-time-format

## Password operation

- Reset password
```./ldappasswordmodify -h localhost -p 1389 -D "cn=Directory Manager" -w password --authzID u:cosmin```


## Export / Import

- Export
```
./export-ldif --port 4444 --hostname localhost --bindDN "cn=Directory Manager" --bindPassword password --includeBranch dc=opensso,dc=java,dc=net --backendID userRoot --ldifFile backup.ldif --trustAll
```
## Password validators

See more:
http://www.fedji.com/blogs/forgerock/configuring-password-validator-in-forgerock-opendj-3/

- List all the validators available
```
./dsconfig list-password-validators -h localhost -p 1389 --bindDN "cn=Directory Manager" --bindPassword password --no-prompt --trustAll
```

- List validator properties
```$xslt
./dsconfig get-password-validator-prop --validator-name "Length-Based Password Validator" -h localhost -p 4444 --bindDN "cn=Directory Manager" --bindPassword password --no-prompt --trustAll
```


# Create WISMET password policy

#### Configure password validators

- Length password validator
```$xslt
./dsconfig set-password-validator-prop --validator-name "Length-Based Password Validator" --set min-password-length:12 -h localhost -p 4444 --bindDN "cn=Directory Manager" --bindPassword password --no-prompt --trustAll
```

- Dictionary validator
> The dictionary validator must be enabled. We use the default validator
```$xslt
/dsconfig set-password-validator-prop --validator-name "Dictionary" --set enabled:true -h localhost -p 4444 --bindDN "cn=Directory Manager" --bindPassword password --no-prompt --trustAll
```

- Attribute value validator
```$xslt
./dsconfig set-password-validator-prop --validator-name "Attribute Value" --set check-substrings:false --set match-attribute:uid -h localhost -p 4444 --bindDN "cn=Directory Manager" --bindPassword password --no-prompt --trustAll
```

Set the validators to password policy:
```$xslt
./dsconfig -D "cn=directory manager" -w password -n set-password-policy-prop   --policy-name "Default Password Policy" --set password-validator:"Length-Based Password Validator" --set password-validator:"Dictionary" --set password-validator:"Attribute Value" --set password-validator:"Character Set"
```
#### Set password history count to 3(generations) and password-history-duration:365d
```$xslt
./dsconfig -D "cn=directory manager" -w password -n set-password-policy-prop   --policy-name "Default Password Policy" --set password-history-count:3 --set password-history-duration:365d
```

#### Set lockout-failure-count to 10.
```$xslt
./dsconfig -D "cn=directory manager" -w password -n set-password-policy-prop   --policy-name "Default Password Policy" --set lockout-failure-count:10
```

#### Set password encryption
```$xslt
./dsconfig -D "cn=directory manager" -w password -n set-password-policy-prop   --policy-name "Default Password Policy" --set default-password-storage-scheme:"PBKDF2"
```

### Set password expire period to 365 days
```$xslt
./dsconfig -D "cn=directory manager" -w password -n set-password-policy-prop   --policy-name "Default Password Policy" --set max-password-age:365d --set password-expiration-warning-interval:7d
```

### Set expire-passwords-without-warning to true
```dtd
./dsconfig -D "cn=directory manager" -w password -n set-password-policy-prop   --policy-name "Default Password Policy" --set expire-passwords-without-warning:true
```
> The default value do not allow password expiration at the end of password expiration. If user tries to login after the password expired it will be asked to change the password.

#### WISMET Password policty 
```$xslt
[ec2-user@ip-172-32-2-29 bin]$ ./dsconfig -D "cn=directory manager" -w password -n get-password-policy-prop   --policy-name "Default Password Policy"
Property                                  : Value(s)
------------------------------------------:------------------------------------
account-status-notification-handler       : -
allow-expired-password-changes            : false
allow-user-password-changes               : true
default-password-storage-scheme           : PBKDF2
deprecated-password-storage-scheme        : -
expire-passwords-without-warning          : true
force-change-on-add                       : false
force-change-on-reset                     : true
grace-login-count                         : 0
idle-lockout-interval                     : 0 s
last-login-time-attribute                 : OpenWISLastLoginTime
last-login-time-format                    : yyyy-MM-dd'T'HH:mm:ss
lockout-duration                          : 0 s
lockout-failure-count                     : 10
lockout-failure-expiration-interval       : 0 s
max-password-age                          : 52 w 1 d
max-password-reset-age                    : 0 s
min-password-age                          : 0 s
password-attribute                        : userPassword
password-change-requires-current-password : false
password-expiration-warning-interval      : 51 w 1 d
password-generator                        : Random Password Generator
password-history-count                    : 3
password-history-duration                 : 52 w 1 d
password-validator                        : Attribute Value, Dictionary,Character Set
                                          : Length-Based Password Validator
previous-last-login-time-format           : -
require-change-by-time                    : -
require-secure-authentication             : false
require-secure-password-changes           : false
```


