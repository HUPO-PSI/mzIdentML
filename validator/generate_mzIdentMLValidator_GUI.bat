@echo off

set OutDir=C:\Users\mayerg97\Documents\GitHub\HUPO-PSI\mzIdentML\validator\build\classes\
set JAR_FILE=%OutDir%mzIdentMLValidator-1.4.35-SNAPSHOT.jar
set MANIFEST_FILE=C:\Users\mayerg97\Documents\GitHub\HUPO-PSI\mzIdentML\validator\manifest.mf

cd %OutDir%
REM del %JAR_FILE%

"C:\Programs\openjdk-12.0.1_windows-x64_bin\jdk-12.0.1\bin\jar.exe" -cmvf %MANIFEST_FILE% %JAR_FILE% *

@echo on
pause
