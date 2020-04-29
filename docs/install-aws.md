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

