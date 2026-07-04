#!/usr/bin/env bash
# Double-cliquer sur ce fichier dans le Finder compile automatiquement le
# projet (fichiers existants + gui/) puis lance l'interface graphique Swing.
# Le programme console reste disponible séparément via ApplicationPrincipale.
cd "$(dirname "$0")"
echo "Compilation..."
javac -cp ".:libs/sqlite-jdbc-3.46.1.3.jar" *.java gui/*.java
if [ $? -ne 0 ]; then
    echo ""
    echo "La compilation a échoué. Appuyez sur une touche pour fermer."
    read -n 1
    exit 1
fi
echo "Lancement de l'interface graphique..."
java -cp ".:gui:libs/sqlite-jdbc-3.46.1.3.jar" InterfaceGraphique
