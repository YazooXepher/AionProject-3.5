@ECHO off
TITLE Aion Ascension - Login Server Console
Color 3
SET PATH="..\JavaJDK\bin"

:start
CLS
echo.

echo Starting Aion Ascension Login Server.
echo.
REM -------------------------------------
REM Default parameters for a basic server.
java -Xms64m -Xmx128m -server -cp ./libs/*;AL-Login.jar com.aionemu.loginserver.LoginServer
REM
REM -------------------------------------

SET CLASSPATH=%OLDCLASSPATH%

if ERRORLEVEL 1 goto error
goto end
:error
echo.
echo Login Server Terminated Abnormaly, Please Verify Your Files.
echo.
:end
echo.
echo Login Server Terminated.
echo.
pause