This is the top level file for the syntheto DSL.

Building extension with gradle. Assumption JAVA_HOME is set and points to a JAVA 11 runtime, and there is an application called code in the path. This is the visual studio code application.

```
Assuming you are in syntheto-xtext folder
```

Build and install the extension.

```
./gradlew clean build installextension
```

Start visual studio code


```
./gradlew clean build startcode
```

This will install the vanderbilt MIDAS Syntheto extension. Current version is 0.0.41.


# Development Instructions

Development requires eclipse (latest version) and latest xtext to be installed. Eclipse should have maven installed. Assume maven in available on command line


```
mvn clean install
```

Open eclipse and import projects (in the syntheto-xtext folder) as existing maven projects. Once imported -- run the mvn install on the parent folder.










