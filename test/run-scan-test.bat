@echo off
chcp 65001 > nul
set "APP_NAME=Test1"
set "SRC_DIR=src\main\java"
set "WEB_DIR=src\main\webapps"
set "BUILD_DIR=build"
set "LIB_DIR=..\lib"

REM !!! MODIFIEZ CE CHEMIN AVEC LE VRAI CHEMIN DE VOTRE TOMCAT !!!
set "TOMCAT_WEBAPPS=C:\xampp\tomcat\webapps"

echo Nettoyage de l'ancien build...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"

echo Creation de la structure JEE...
mkdir "%BUILD_DIR%\WEB-INF\classes"
mkdir "%BUILD_DIR%\WEB-INF\lib"

echo Copie des fichiers web (JSP, ressources)...
if exist "%WEB_DIR%" (
    xcopy /E /Y "%WEB_DIR%\*" "%BUILD_DIR%\" > nul
)

REM --- SECURITE WEB.XML ---
if exist "%BUILD_DIR%\web.xml" (
    echo [CORRECTION] Deplacement de web.xml vers WEB-INF/web.xml...
    move /Y "%BUILD_DIR%\web.xml" "%BUILD_DIR%\WEB-INF\" > nul
)
REM ------------------------

echo Compilation des classes Java...
setlocal enabledelayedexpansion
set "CP="
for %%i in (%LIB_DIR%\*.jar) do set "CP=!CP!;%%i"

dir /s /b "%SRC_DIR%\*.java" > sources.txt
javac -cp "!CP!" -d "%BUILD_DIR%\WEB-INF\classes" @sources.txt
del sources.txt

echo Copie de front.jar dans WEB-INF/lib...
if exist "%LIB_DIR%\front.jar" (
    copy /Y "%LIB_DIR%\front.jar" "%BUILD_DIR%\WEB-INF\lib\" > nul
) else (
    echo [ERREUR] front.jar introuvable dans %LIB_DIR%.
)

echo Creation du fichier WAR...
cd "%BUILD_DIR%"
jar -cvf "..\%APP_NAME%.war" * > nul
cd ..

echo Deploiement vers Tomcat...
if exist "%TOMCAT_WEBAPPS%" (
    copy /Y "%APP_NAME%.war" "%TOMCAT_WEBAPPS%\" > nul
    echo Deploiement termine avec succes !
) else (
    echo [ERREUR] Le chemin "%TOMCAT_WEBAPPS%" n'existe pas.
    echo Le fichier %APP_NAME%.war a ete cree dans ce dossier mais n'a pas pu etre copie dans Tomcat.
)
echo Fin.
