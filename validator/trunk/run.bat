echo off

set JAVA_HOME=C:\Programs\openjdk-12.0.1_windows-x64_bin\jdk-12.0.1\bin\

%JAVA_HOME%java.exe -version

REM for Java 6/7/8/11/12
%JAVA_HOME%java.exe %JAVA_OPTS% -cp .\lib -jar .\mzIdentMLValidator-1.4.35-SNAPSHOT.jar

REM for Java 9/10
REM java.exe %JAVA_OPTS% --add-modules java.xml.bind -cp .\lib -jar .\mzIdentMLValidator-1.4.35-SNAPSHOT.jar

pause
