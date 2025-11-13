@echo off
echo Compiling Java source files...

javac -cp "lib/mysql-connector-j-9.2.0.jar;." src\model\*.java src\view\*.java src\viewmodel\*.java src\config\*.java src\Main.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)

echo Compilation successful.
echo Running the game...

java -cp "lib/mysql-connector-j-9.2.0.jar;src" Main
pause
