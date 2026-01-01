@echo off
REM Gradle wrapper script for Windows

SET DIRNAME=%~dp0
SET DIRNAME=%DIRNAME:~0,-1%

SET GRADLE_HOME=%DIRNAME%\gradle\wrapper
SET GRADLE_VERSION=7.0

IF NOT EXIST "%GRADLE_HOME%\gradle-wrapper.jar" (
    echo "Gradle wrapper jar not found. Please run 'gradlew' from the command line."
    exit /b 1
)

java -jar "%GRADLE_HOME%\gradle-wrapper.jar" %*