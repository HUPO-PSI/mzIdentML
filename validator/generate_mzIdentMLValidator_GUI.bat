@echo off

set OutDir=C:\Users\mayerg97\SW_Projekte\Member\psi-pi\validator\build\classes\
set JAR_FILE=%OutDir%mzIdentMLValidator-1.4.13-SNAPSHOT.jar
set MANIFEST_FILE=C:\Users\mayerg97\SW_Projekte\Member\psi-pi\validator\manifest.mf

cd %OutDir%
del %JAR_FILE%

"C:\Program Files\Java\jdk1.7.0_75\bin\jar.exe" -cmvf %MANIFEST_FILE% %JAR_FILE% *

@echo on
pause