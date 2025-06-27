:######################################################################## 
:# File name: BuildAll.bat
:# Edited Last By: Magenik 
:# V 1.0 1
:########################################################################
@ECHO off
@COLOR 0B
SET MODE=clean package
SET TITLE=Build

TITLE Aion Lightning Emulator %TITLE% All
:MENU
CLS
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^*                 Aion Lightning Emulator - %TITLE% All Panel              ^* 
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^*                                                                          ^*
ECHO   ^*                     1 - Build All Server                                 ^*
ECHO   ^*                                                                          ^*
ECHO   ^*                     2 - Quit                                             ^*
ECHO   ^*                                                                          ^*
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
:ENTER
SET /P Ares= Type your option and press ENTER : 
IF %Ares%==1 GOTO FULL
IF %Ares%==2 GOTO QUIT
:FULL
cd ..\AL-Login
start /WAIT /B ..\tools\Ant\bin\ant clean dist
cd ..\AL-Chat
start /WAIT /B ..\tools\Ant\bin\ant clean dist
cd ..\AL-Game
start /WAIT /B ..\tools\Ant\bin\ant clean dist
cd ..\AL-Commons
start /WAIT /B ..\tools\Ant\bin\ant clean dist
GOTO :QUIT

:LoginServer
cd ..\AL-Login
start /WAIT /B ..\tools\Ant\bin\ant clean dist
GOTO :QUIT

:GameServer
cd ..\AL-Game
start /WAIT /B ..\tools\Ant\bin\ant clean dist
GOTO :QUIT

:ChatServer
cd ..\AL-Chat
start /WAIT /B ..\tools\Ant\bin\ant clean dist
GOTO :QUIT

:CommonsServer
cd ..\AL-Commons
start /WAIT /B ..\tools\Ant\bin\ant clean dist
GOTO :QUIT

:QUIT
exit
