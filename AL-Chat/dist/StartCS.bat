@ECHO off
TITLE Aion Ascension - Chat Server Console
Color 2
SET PATH="..\JavaJDK\bin"

:START
CLS
echo.

echo Starting Aion Ascension Chat Server 
echo.
REM -------------------------------------
REM Default parameters for a basic server.
JAVA -Xms128m -Xmx256m -server -cp ./libs/*;AL-Chat.jar com.aionemu.chatserver.ChatServer
REM
REM -------------------------------------
SET CLASSPATH=%OLDCLASSPATH%

IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO Chat Server has terminated abnormaly!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO Chat Server is terminated!
ECHO.
PAUSE
EXIT