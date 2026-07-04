@echo off
rem Compile automatiquement tout le projet (fichiers existants + gui-admin/) puis
rem lance l'interface d'administration Swing.
cd /d "%~dp0.."
echo Compilation...
javac -cp ".;libs/sqlite-jdbc-3.46.1.3.jar" *.java gui-admin/*.java
if errorlevel 1 exit /b 1
echo Lancement de l'interface d'administration...
java -cp ".;gui-admin;libs/sqlite-jdbc-3.46.1.3.jar" InterfaceAdmin
