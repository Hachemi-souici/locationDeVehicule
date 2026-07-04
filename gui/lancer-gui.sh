#!/usr/bin/env bash
# Compile automatiquement tout le projet (fichiers existants + gui/) puis
# lance l'interface graphique Swing. Le programme console reste disponible
# séparément via ApplicationPrincipale.
set -e
cd "$(dirname "$0")/.."
echo "Compilation..."
javac -cp ".:libs/sqlite-jdbc-3.46.1.3.jar" *.java gui/*.java
echo "Lancement de l'interface graphique..."
java -cp ".:gui:libs/sqlite-jdbc-3.46.1.3.jar" InterfaceGraphique
