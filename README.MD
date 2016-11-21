![OpenWIS](http://openwis.io/cms/images/openwis.jpg)

OpenWIS is an open source implementation of the WMO Information System (WIS) that aims to perform the three functions required by the WIS, i.e. GISC, DCPC and NC.

OpenWIS is originally developed by AKKA Information System (France) on behalf of OpenWIS Association (with UKMO, MF, BoM, KMA, and MFI as the founding members).
For more information on the OpenWIS project and OpenWIS association, please visit the [OpenWIS Project Website](http://openwis.github.io/openwis-documentation/).


## Getting Started

To try out OpenWIS on our PC, you will need:

- [Java SE JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html): Oracle JDK 1.8
- [Apache Maven 3](https://maven.apache.org/download.cgi): Latest version of Maven 3
- [VirtualBox](https://www.virtualbox.org/wiki/Downloads): Latest version
- [Vagrant](https://www.vagrantup.com/downloads.html): Latest version

Then, run the following commands:

    $ export MAVEN_OPT="-Xms256m -Xmx512m -XX:MaxPermSize=256m"
    $ git clone https://github.com/OpenWIS/openwis.git
    $ cd openwis
    $ mvn clean exec:exec
    $ mvn clean install -P openwis -Dfile.encoding=UTF-8
    $ mvn clean install -P user -Dfile.encoding=UTF-8
    $ mvn clean install -P admin -Dfile.encoding=UTF-8
    $ vagrant up
    $ ( cd openwis-tools/load-test-data ; mvn -P openwis exec:java )

Then, follow the link to the [User Portal](http://localhost:8060/openwis-user-portal/) to get a taste of OpenWIS.

## Installing

To install OpenWIS on your system, you can either [download a binary release](http://repository-openwis-association.forge.cloudbees.com/openwis/), or
build from source. 

Once you have the OpenWIS artefacts, follow the instructions in the
[Installation Guide](https://github.com/OpenWIS/openwis/blob/develop/docs/IG-OpenWIS-1-v02%209-MigrationOpenAM-v3.doc?raw=true).  OpenWIS can run in a
variety of configurations so have a think about how you would like to install it before proceeding.

OpenWIS can integrate in all sorts of possible ways through the use of *harnesses*.  For information on how to build these harnesses, please take
a look at [Developing OpenWIS Harnesses](https://github.com/OpenWIS/openwis/wiki/Developing-OpenWIS-Harnesses).

## Contributing

OpenWIS is continuously being improved by the joint effort of meteorological agencies around the world.  If you would like to help out, please
take a look at our Contributor Agreement.

If you would like to know more about developing for OpenWIS, take a look at the [OpenWIS wiki](https://github.com/OpenWIS/openwis/wiki).

If you are interested in joining the OpenWIS association, please get in touch with the association via the contact details on the [project web-page](http://openwis.github.io/openwis-documentation/).

## License

OpenWIS is release under the GNU General Public License version 3.