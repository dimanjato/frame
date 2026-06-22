@echo off
chcp 65001 > nul
set "APP_NAME=front"
set "SRC_DIR=."
set "BUILD_DIR=build"
set "LIB_DIR=lib"
set "FRAMEWORK_LIB=C:\S5\framework\frame\framework\lib"

echo === Début de la génération et déploiement dans le Framework ===

:: 1. Nettoyage et création du répertoire de build
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%\classes"

:: 2. Trouver et compiler les fichiers Java
echo Compilation des fichiers sources...
dir /s /b "%SRC_DIR%\*.java" > sources.txt

:: Vérifier si le fichier sources.txt n'est pas vide
for %%I in (sources.txt) do if %%~zI==0 (
    echo Erreur : Aucun fichier Java trouvé dans %SRC_DIR%
    del sources.txt
    exit /b 1
)

:: Compilation avec toutes les libs du dossier lib/ (gère le point-virgule automatiquement)
javac -cp "%LIB_DIR%\*" -d "%BUILD_DIR%\classes" @sources.txt
del sources.txt

:: 3. Copie des ressources (fichiers xml, properties s'il y en a)
echo Copie des ressources...
xcopy "%SRC_DIR%" "%BUILD_DIR%\classes" /s /e /y /exclude:*.java 2>nul || ver > nul

:: 4. Génération du fichier .jar
echo Création de l'archive %APP_NAME%.jar...
cd "%BUILD_DIR%\classes"
jar -cvf "..\%APP_NAME%.jar" *
cd ..\..

:: 5. Envoi dans le dossier lib du framework
echo Déploiement du JAR dans le framework...
if exist "%FRAMEWORK_LIB%" (
    copy /y "%BUILD_DIR%\%APP_NAME%.jar" "%FRAMEWORK_LIB%\"
    echo ✔ Fichier copie avec succes dans %FRAMEWORK_LIB%
) else (
    echo ❌ Erreur : Le dossier destination %FRAMEWORK_LIB% n'existe pas.
    exit /b 1
)

echo.
echo === Opération terminée ===
echo.