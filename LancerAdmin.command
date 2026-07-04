#!/usr/bin/env bash
# Double-cliquer sur ce fichier dans le Finder compile automatiquement le
# projet (fichiers existants + gui-admin/) puis lance l'interface
# d'administration Swing (gestion du catalogue, tarifs, stock, rabais).
cd "$(dirname "$0")"
echo "Compilation..."
javac -cp ".:libs/sqlite-jdbc-3.46.1.3.jar" *.java gui-admin/*.java
if [ $? -ne 0 ]; then
    echo ""
    echo "La compilation a échoué. Appuyez sur une touche pour fermer."
    read -n 1
    exit 1
fi
echo "Lancement de l'interface d'administration..."
java -cp ".:gui-admin:libs/sqlite-jdbc-3.46.1.3.jar" InterfaceAdmin
