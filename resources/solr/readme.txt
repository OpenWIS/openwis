To add solr core execute the following commands as a solr_user not as root user. 
uncompress the file openwis_solr_conf.tar.gz
<solr_home>/bin/solr create_core -c <core_name> -d <configFolder> -V


example: 
sudo -i -u solr
/opt/solr/bin/solr create_core -c core1 -d  openwis_solr_conf -V


if swiching to solr user is not activated use the following: 
runuser -l solr -c '/opt/solr/bin/solr create_core -c core1 -d  openwis_solr_conf -V'

