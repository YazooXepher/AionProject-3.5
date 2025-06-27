@echo off
TITLE Aion Ascension - Game Server Console
Color 4
SET PATH="..\JavaJDK\bin"

:START
CLS

echo.

echo Starting Aion Ascension Game Server.

echo.

REM -------------------------------------
REM Default parameters for a basic server.
java -Xms2048m -Xmx12288m -XX:MaxHeapSize=12288m -Xdebug -XX:MaxNewSize=24m -XX:NewSize=24m -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseConcMarkSweepGC -XX:-UseSplitVerifier -ea -javaagent:./libs/al-commons-1.3.jar -cp ./libs/*;./libs/AL-Game.jar com.aionemu.gameserver.GameServer
REM -------------------------------------
SET CLASSPATH=%OLDCLASSPATH%

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
if ERRORLEVEL 0 goto end

REM Restart...
:restart
echo.
echo Administrator Restart ...
echo.
goto start

REM Error...
:error
echo.
echo Server terminated abnormaly ...
echo.
goto end

REM End...
:end
echo.
echo Server terminated ...
echo.
pause