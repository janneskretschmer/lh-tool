[![Build Status](https://travis-ci.org/janneskretschmer/lh-tool.svg?branch=master)](https://travis-ci.org/janneskretschmer/lh-tool)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=lh-tool&metric=bugs)](https://sonarcloud.io/dashboard?id=lh-tool)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=lh-tool&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=lh-tool)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=lh-tool&metric=code_smells)](https://sonarcloud.io/dashboard?id=lh-tool)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/848f01d70fe748aebbc667062c587d8c)](https://www.codacy.com/app/janneskretschmer/lh-tool?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=janneskretschmer/lh-tool&amp;utm_campaign=Badge_Grade)
# API Documentation
[Swagger](http://localhost:8080/lh-tool/swagger-ui.html)
# Local Config
## Eclipse
### Checkout
	1. Open the Import dialog in Eclipse (right click in Project Explorer > Import...)
	2. Select "Projects from Git"
	3. Select "Clone URI"
	4. Enter "[https://github.com/janneskretschmer/lh-tool.git](https://github.com/janneskretschmer/lh-tool.git)" at URI
	5. Click "Finish"
	6. When the New Project dialog opens select "Java Project"
	7. Enter a project name
	8. Set the folder lh-tool in your local repository as Location
	9. Use at least JDK 11.0.1
	10. Click Finish

### Maven
	1. Right click the project and select Configure > Convert to Maven Project
	2. Open Update Maven Project dialog (right click on project > Maven > Update Project ...)
	3. Click OK

### Tomcat
	1. Open Servers View (in the java EE perspective it's a tab on the buttom)
	2. Open New Server dialog (right click in Servers View > New > Server)
	3. Select Apache > Tomcat v9.0 Server
	4. Click Next
	5. Enter the installation directory of a tomcat 9 server
	6. Select at least JDK 11.0.1 as JRE
	7. Click Next
	8. Click Finish
	9. Double click the server
	10. Disable Server Options > "Modules auto reload by default"
	11. Save
	12. Right click on server > Add and Remove...
	13. add LH-Tool
	14. Click Finish

#### Workarounds
	1. Open debug preferences (Window > Preferences > Java Debug)
	2. Disable "Use advanced source lookup (JRE 1.5 and higher)"
	3. Apply
	4. Right click on Project > Properties > Deployment Assembly > Add...
	5. Folder > target > classes
	6. Click Finish
	7. Change the Deploy Path of classes to "WEB-INF/classes"
	8. Click Apply and Close
	9. If JUnit-Tests fail
	10. Help > Install new Software
	11. Work with: [http://download.eclipse.org/eclipse/updates/4.9-P-builds/P20181003-1015/](http://download.eclipse.org/eclipse/updates/4.9-P-builds/P20181003-1015/)
	12. Follow the instructions for installation

#### Start
	1. Start Server in Debug Mode (Select Server and click on the bug in the upper right corner of the view)
	2. Test Server by calling [http://localhost:8080/lh-tool/rest/info/heartbeat](http://localhost:8080/lh-tool/rest/info/heartbeat)

### React
	1. Right click the project > Run As > npm Install
	2. Test by calling [http://localhost:8080/lh-tool/web](http://localhost:8080/lh-tool/web)
	3. Desable JSON Validation at Window > Preferences > Validation

## MySQL
	1. Install and configure mysql-server
	2. Adapt and execute the following statements:<br>
	  CREATE DATABASE IF NOT EXISTS lhtool;<br>
	  CREATE USER IF NOT EXISTS '<name>'@'localhost' IDENTIFIED BY '<password>';<br>
	  GRANT ALL ON lhtool.* TO '<name>'@'localhost';
	3. Make a copy of /src/main/resources/hibernate.properties.example in the same directory, called "hibernate.properties"
	3. Update the credentials in hibernate.properties (Make sure that you never commit them!)
	4. Open the Git-Staging-view in Eclipse
	5. Right click on hibernate.properties > Assume unchanged
	6. It should disappear from the view
	7. Never commit your credentials from hibernate.properties!
