echo off

REM for Java 6/7/8
REM java.exe %JAVA_OPTS% -cp .\lib -jar .\mzIdentMLValidator-1.4.30-SNAPSHOT.jar

REM for Java 9/10
java.exe %JAVA_OPTS% --add-modules java.xml.bind -cp .\lib -jar .\mzIdentMLValidator-1.4.30-SNAPSHOT.jar

pause
