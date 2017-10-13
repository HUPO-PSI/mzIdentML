echo off

REM for Java 6/7/8
REM java.exe %JAVA_OPTS% -cp .\lib -jar .\mzIdentMLValidator-1.4.29-SNAPSHOT.jar

REM for Java 9
java.exe %JAVA_OPTS% --add-modules java.xml.bind -cp .\lib -jar .\mzIdentMLValidator-1.4.29-SNAPSHOT.jar

pause
