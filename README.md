# Local Config
## Eclipse
### Checkout
1. Open the Import dialog in Eclipse (right click in Project Explorer > Import...)
2. Select "Projects from Git"
3. Select "Clone URI"
4. Enter "https://github.com/janneskretschmer/lh-tool.git" at URI
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

Workarounds
1. Open debug preferences (Window > Preferences > Java Debug)
2. Disable "Use advanced source lookup (JRE 1.5 and higher)"
3. Apply
4. Right click on Project > Properties > Deployment Assembly > Add...
5. Folder > target > classes
6. Click Finish
7. Change the Deploy Path of classes to "WEB-INF/classes"
8. Click Apply and Close

Start
1. Start Server in Debug Mode (Select Server and click on the bug in the upper right corner of the view)
2. Test Server by calling http://localhost:8080/lh-tool/rest/info/heartbeat
