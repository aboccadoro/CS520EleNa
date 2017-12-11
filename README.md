# Project EleNa

#### How to build Project EleNa and run its tests from the terminal:

1. Change into the Project EleNa root directory, which contains the *build.xml* build file.

2. Run `ant compile` to compile Project EleNa. The compiled class files will be in the *bin* directory.

4. Run `ant test` to run all Project EleNa unit tests.

5. Run `ant clean` whenever you want to clean up the project (i.e., delete all generated files).

#### How to run Project EleNa from the terminal:

After building the project (i.e., running `ant compile`), run: `java -jar out CS520EleNa`. The application's GUI will show up.

Or much more simply, double click CS520EleNa.jar in *out*.

Remember that graph

#### Program features:
* Things

## Troubleshooting

#### Java JDK not installed or misconfigured
If a Java JDK is not installed or properly configured on your system, you may encounter the following error: 
```
BUILD FAILED
build.xml:17 Unable to find a javac compiler;
```
Make sure that you have a JDK installed and that the JAVA_HOME environment variable is properly set.