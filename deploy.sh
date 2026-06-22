#!/bin/bash

# Définition des variables (adaptées à ta nouvelle structure)
APP_NAME="front"
SRC_DIR="src/main/java"
BUILD_DIR="build"
LIB_DIR="lib"

# Dossier de destination dans ton framework
FRAMEWORK_LIB="../../framework/lib"

# Sous Windows (Git Bash), le classpath utilise le point-virgule ';' comme séparateur
CLASSPATH_JARS=$(echo lib/*.jar | tr ' ' ';')

echo "=== Début de la génération et déploiement dans le Framework ==="

# 1. Nettoyage et création du répertoire de build
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

# 2. Compilation des fichiers Java
echo "Compilation des fichiers sources..."
find "$SRC_DIR" -name "*.java" > sources.txt

if [ -s sources.txt ]; then
    javac -cp "$CLASSPATH_JARS" -d "$BUILD_DIR/classes" @sources.txt
    rm -f sources.txt
else
    echo "Erreur : Aucun fichier Java trouvé dans $SRC_DIR"
    rm -f sources.txt
    exit 1
fi

# 3. Copie des ressources (fichiers de configuration, etc.)
echo "Copie des ressources..."
rsync -a --exclude="*.java" "$SRC_DIR/" "$BUILD_DIR/classes/" 2>/dev/null || cp -r "$SRC_DIR"/* "$BUILD_DIR/classes/" 2>/dev/null || true

# 4. Génération du fichier .jar
echo "Création de l'archive $APP_NAME.jar..."
cd "$BUILD_DIR/classes" || exit
jar -cvf "../$APP_NAME.jar" *
cd ../..

# 5. Envoi dans le dossier lib du framework
echo "Déploiement du JAR dans le framework..."
if [ -d "$FRAMEWORK_LIB" ]; then
    cp -f "$BUILD_DIR/$APP_NAME.jar" "$FRAMEWORK_LIB/"
    echo "✔ Fichier copié avec succès dans $FRAMEWORK_LIB"
else
    echo "❌ Erreur : Le dossier destination $FRAMEWORK_LIB n'existe pas."
    echo "Vérifie le chemin relatif depuis l'endroit où tu lances le script."
    exit 1
fi

echo ""
echo "=== Opération terminée ==="
echo ""