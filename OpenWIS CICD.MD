## OpenWIS CI/CD Procedure

### Summary
OpenWIS is integrated with Travis-CI and SonarCloud in order to have the source code built, the code quality measured and the build artifacts delivered for deployment continuously on every commit and pull request.

Specifically, Travis-CI was integrated with OpenWIS by logging in at https://travis-ci.org/ using the credentials of a GitHub account with administrator permissions on the OpenWIS GitHub organization. This integration has created a webhook between the GitHub repository and Travis-CI, which is being triggered on specified events (i.e. **Push**, **Pull Request**, **Delete** etc.) in order to execute the CI/CD script, as it is defined in the .travis.yml file at the root folder of the project. This file is configured in such way so that the following tasks are executed: 
1) Build the application
2) Perform a Sonar analysis
3) Deliver the build artifacts
4) Perform Maven release
5) Send email notifications

By logging in at https://sonarcloud.io/ using the same GitHub account and creating a SonarCloud organization for the GitHub OpenWIS organization, SonarCloud was also integrated with OpenWIS. This integration depends on Travis-CI jobs for the Sonar analysis to be run and published on SonarCloud. As defined in the .travis.yml file, only a successful Travis-CI build job will trigger the SonarCloud analysis, with the communication between those two tools taking place using an encrypted security token. The analysis will run for the latest version of the source code and the overal Quality Gate of the project will be calculated based on the files which were modified on the last thirty (30) days.

Following the build of the artifacts, Travis-CI will create a .tar file including the 9 required artifacts for deployment and will deliver them using SSH on a remote server. This step will only take place on the branch _deliver_, which is being used for the core development, in order to avoid unstable code from pull requests to be delivered.

Moreover, the CI/CD procedure also performs releases of the project on the _master_ branch of the repository, which is being used for hosting the published versions of the project. During those releases, the current version of the project is pushed on GitHub as a tag, all the project modules are updated with the next incremental SNAPSHOT version and those changes are pushed on the _master_ and _deliver_ branches. 
In order to kick off this process, a merge must be done in the _master_ branch from the _develop_ by either accepting a pull request from _develop_ to _master_ or by executing the git commands `git merge --no-edit --no-ff develop` and  `git push` on the _master_ branch.

As a final step, Travis-CI will check if there is a change of the job status (success or failed) and will send an email notification about this change. For the first build of every new branch or pull request, an email notification will be sent since there is no history of older builds.

### Useful Links
- [Travis-CI Latest Build](https://travis-ci.org/github/OpenWIS/openwis/builds)
- [Travis-CI Pull Requests History](https://travis-ci.org/github/OpenWIS/openwis/pull_requests)
- [Travis-CI Branches History](https://travis-ci.org/github/OpenWIS/openwis/branches)
- [SonarCloud Analysis](https://sonarcloud.io/dashboard?id=OpenWIS_openwis)

### OpenWIS-libs module
This module provides the built jar files of libraries which cannot be directly fetched from public Maven repositories in order to facilitate and accelerate the build process of Travis-CI. By invoking the Maven profile _dependencies_ during the _install_ phase of the Maven lifecycle, the provided jar files will be copied in the local Maven repository, using the Maven install plugin, and the OpenWIS modules which have these libraries as dependencies will not try to download them from online repositories, as they would normally do.

In case one of those libraries is updated to a newer version which also does not provide its built jar files on any public Maven repository, those jar files should also be identified and installed using the _libs_ module.

More specifically, the steps to follow for this process are the following:
1) Invoke the command `mvn clean install -Pdependencies,openwis -Dmaven.repo.local=location/to/empty/folder` in order to build the project by downloading all the dependant jar files 
2) If there are jar files which cannot be located and you are receiving the error `Could not resolve dependencies for project io.openwis:openwis:jar:3.14.9: Could not find artifact groupId_ofTheLibrary:artifactId_ofTheLibrary:jar:versionOfTheLibrary`, find the jar file, copy it under the _libs_ folder of the _libs_ module and update the _pom.xml_ file by adding a new execution step for that library:
```
<execution>
    <id>random_id</id>
    <configuration>
        <groupId>groupId_ofTheLibrary</groupId>
        <artifactId>artifactId_ofTheLibrary</artifactId>
        <version>version_ofTheLibrary</version>
        <packaging>jar</packaging>
        <file>libs/groupId_ofTheLibrary-artifactId_ofTheLibrary-version_ofTheLibrary.jar</file>
    </configuration>
    <phase>install</phase>
    <goals>
        <goal>install-file</goal>
    </goals>
</execution>
```
3) In case this library has multiple dependencies, it is possible that those libraries will also have issues to be located, so it is adviced to also install its _pom.xml_ file before installing the jar file:
```
<execution>
    <id>random_id</id>
    <configuration>
        <groupId>groupId_ofTheLibrary</groupId>
        <artifactId>artifactId_ofTheLibrary</artifactId>
        <version>version_ofTheLibrary</version>
        <packaging>pom</packaging>
        <file>libs/groupId_ofTheLibrary-artifactId_ofTheLibrary-version_ofTheLibrary.pom</file>
    </configuration>
    <phase>install</phase>
    <goals>
        <goal>install-file</goal>
    </goals>
</execution>
```
4) Continue the process until the installation of OpenWIS is successful.

### Errors Troubleshooting
If the Travis-CI build fails, the reason of the failure must be examined by checking the log output of the Travis-CI build which will be sent by email. Common error messages and instructions on how to fix them are provided below:
1) **Error:** `Could not resolve dependencies for project io.openwis:openwis:jar:3.14.9: Could not find artifact groupId_ofTheLibrary:artifactId_ofTheLibrary:jar:versionOfTheLibrary`
   **Description:** The jar file cannot be downloaded
   **Solution:** Try re-executing the build procedure a couple of times, in case the error was caused due to a connectivity issue (i.e. Connection Timeout). If the error continues, try locate the jar file in another public Maven repository and include it in the central _pom.xml_ of the OpenWIS module under the _repositories_ configuration. Lastly, it is possible that the jar file cannot be downloaded and you will have to manually add it in _libs_ module. 
2) **Error:** `[ERROR] COMPILATION ERROR :
[ERROR] openwis\openwis-metadataportal\openwis-portal\src\main\java\org\geonetwork\map\wmc\WmcServlet.java:[1168,50] cannot find symbol: method generateId()`
   **Description:** The source code cannot be compiled
   **Solution:** Compilation errors can be produced by multiple reasons, like forgotten semicolons or misspelled variables, but they can also be for violating the rules of Java, like using a non-static variable from a static function, etc. In order to fix it, compile the project locally, fix any possible issues and push the code on GitHub. If the error persists on the Travis-CI job, make sure that Travis-CI uses the same version of Java as you do in your project and if not, specify the version at the _.travis.yml_ file. 
3) **Error:** `[ERROR] Failed to execute goal org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar (default-cli) on project openwis: Not authorized. Please check the properties sonar.login  and sonar.password.`
   **Description:** Travis-CI does not have authorization to perform a Sonar analysis
   **Solution:** In case the Sonar key expires or there is another issue considering the SonarCloud analysis authorization, you will probably face this error, which means a new Sonar key is needed. The key must be generated by an administrator of the GitHub repository or by a user who has the permission ‘analysis’ on the SonarCloud organization, using the following steps: 
   - Navigate to your [account panel](https://sonarcloud.io/account/security/) and generate a new token
   - Copy the token and encrypt it using the command `travis encrypt <key>`
   - Update the produced key in the _.travis.yml_ file under the _sonarcloud_ addon 
4) **Error:** `[ERROR] GC overhead limit exceeded`
   **Description:** The generated Travis-CI log exceeds the limited 4 MB 
   **Solution:** Travis-CI has a limitation of generating maximum 4 MB of logs per build. In case the logs exceed that limitation, the build will fail. In order to bypass this error, you have to reduce the generated Maven logs by adding the parameter `-q` at the Maven commands.
5) **Error:** `Warning: Permanently added '[secure],51.79.55.145' (ECDSA) to the list of known hosts.
Permission denied (publickey,gssapi-keyex,gssapi-with-mic).
rsync: connection unexpectedly closed (0 bytes received so far) [sender]
rsync error: error in rsync protocol data stream (code 12) at io.c(226) [sender=3.1.1]
Script failed with status 12`
   **Description:** Travis-CI is not able to SSH on GitHub server 
   **Solution:** Travis-CI uses SSH to make a connection to GitHub and push changes for the _release_ step. This errors means that the SSH key used by Travis-CI to push the changes has been revoked by mistake and in order to fix this issue, you will have to generate a new SSH key with the following steps:
   - On a Linux environment, clone the OpenWIS repository with the command `git clone https://github.com/OpenWIS/openwis`
   - Move to the cloned folder: `cd openwis`
   - Checkout to the _develop_ branch: `git fetch --all` `git checkout -b develop origin/develop`
   - Delete the existing _resources/github_deploy_key.enc_ file
   - Login to Travis-CI by providing the GitHub credentials of an account with administrator rights on the OpenWIS repository: `travis login`
   - Generate a new SSH key: `ssh-keygen -t rsa -b 4096 -C "emailaccountoftheuser" -f resources/github_deploy_key -N ''`
   - Remove the old SSH key from the [GitHub deploy keys](https://github.com/OpenWIS/openwis/settings/keys) and add the content of the _resources/github_deploy_key.pub_ file 
   - Encrypt the generated private key: `travis encrypt-file resources/github_deploy_key`
   - Copy the printed _openssl_ command and add it on the _before_install_ section of the _.travis.yml_ file
   - Remove the old _openssl_ command, related to the old _github_deploy_key_ from ._.travis.yml_ file
   - Push the _resources/github_deploy_key.enc_ file and the updated _.travis.yml_ file

### OWASP Dependency Check
OWASP Dependency-Check is a is a Software Composition Analysis (SCA) tool, which attempts to detect publicly disclosed vulnerabilities contained within the dependencies of the project. Based on the National Vulnerability Database (NVD) reports, this tool checks if known Common Platform Enumeration (CPE) identifies exists on the dependencies. The final dependency-check-report.html report, can then be found in the target folder of the parent directory and contains the list of the found vulnerabilities and suggestions for dependency versions, which resolve the mentioned issues.

In order to run the analysis, execute the command `mvn -Popenwis org.owasp:dependency-check-maven:aggregate` on the root folder of the project.

#### False Positives Vulnerabilities
Since false positives vulnerabilities are possible to be found during the analysis, the tool offers the option of suppressing these findings from the analysis by following the steps:
1) Click on the suppress button and copy the suppression text
2) Open the _owasp-suppression.xml_ file and add the text