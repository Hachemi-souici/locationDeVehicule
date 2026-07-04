# TP3 – Système de facturation de location de véhicules (RVV)

Projet Java réalisé dans le cadre du cours INF1120 (UQAM). Le programme
permet de gérer la location de véhicules hybrides et électriques, de générer
des factures et de consulter l'inventaire des véhicules disponibles.

## Auteurs

Hachemi Souici

## Persistance des données

Les données (inventaire des véhicules disponibles et factures) sont stockées
dans une base de données **SQLite** locale, `GestionLocation.db`. Ce fichier
est créé et initialisé automatiquement au premier lancement du programme
(voir `BaseDeDonnees.java`) — il ne fait donc pas partie du dépôt Git.

## Dépendance

Le pilote JDBC SQLite (`sqlite-jdbc-3.46.1.3.jar`) se trouve dans le dossier
`libs/` à la racine du projet. BlueJ ajoute automatiquement tous les `.jar`
présents dans ce dossier au classpath du projet — aucune configuration
supplémentaire n'est nécessaire après avoir cloné le dépôt.

## Comment lancer le projet

### Avec vscode En ligne de commande

1. Ouvrir vscode, puis "Ouvrir un dossiers" et sélectionner le dossier cloné.
2. Compiler le projet `javac -cp ".:libs/sqlite-jdbc-3.46.1.3.jar" *.java `
3. Lancer le projet `java -cp ".libs:sqlite-jdbc-3.46.1.3.jar" ApplicationPrincipale`

```

(Sous Windows, remplacer `:` par `;` dans le `-cp`.)
```
