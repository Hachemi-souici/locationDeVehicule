@echo off
rem Compile automatiquement tout le projet (fichiers existants + gui/) puis
rem lance l'interface graphique Swing. Le programme console reste disponible
rem separement via ApplicationPrincipale.
cd /d "%~dp0.."
echo Compilation...
javac -cp ".;libs/sqlite-jdbc-3.46.1.3.jar" *.java gui/*.java
if errorlevel 1 exit /b 1
echo Lancement de l'interface graphique...
java -cp ".;gui;libs/sqlite-jdbc-3.46.1.3.jar" InterfaceGraphique
