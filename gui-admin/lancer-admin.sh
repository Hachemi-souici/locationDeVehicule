#!/usr/bin/env bash
# Compile automatiquement tout le projet (fichiers existants + gui-admin/) puis
# lance l'interface d'administration Swing. Le programme console et la GUI
# client restent disponibles séparément (ApplicationPrincipale, gui/lancer-gui.sh).
set -e
cd "$(dirname "$0")/.."
echo "Compilation..."
javac -cp ".:libs/sqlite-jdbc-3.46.1.3.jar" *.java gui-admin/*.java
echo "Lancement de l'interface d'administration..."
java -cp ".:gui-admin:libs/sqlite-jdbc-3.46.1.3.jar" InterfaceAdmin
