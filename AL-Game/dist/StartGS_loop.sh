#!/bin/bash

err=1
until [ $err == 0 ];
do
	[ -d log/ ] || mkdir log/
	[ -f log/console.log ] && mv log/console.log "log/backup/`date +%Y-%m-%d_%H-%M-%S`_console.log"
	java -Xms2048m -Xmx12288m -Xbootclasspath/p:libs/jsr166-1.7.0.jar -ea -javaagent:./libs/al-commons-1.3.jar -cp ./libs/*:AL-Game.jar com.aionemu.gameserver.GameServer > log/console.log 2>&1
	err=$?
	gspid=$!
	echo ${gspid} > gameserver.pid
	sleep 10
done