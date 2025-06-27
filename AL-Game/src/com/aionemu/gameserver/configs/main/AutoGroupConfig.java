package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 *
 * @author xTz
 */
public class AutoGroupConfig {

	@Property(key = "gameserver.autogroup.enable", defaultValue = "true")
	public static boolean AUTO_GROUP_ENABLE;

	@Property(key = "gameserver.startTime.enable", defaultValue = "true")
	public static boolean START_TIME_ENABLE;

	@Property(key = "gameserver.dredgion.timer", defaultValue = "120")
	public static long DREDGION_TIMER;

	@Property(key = "gameserver.dredgion2.enable", defaultValue = "true")
	public static boolean DREDGION2_ENABLE;

	@Property(key = "gameserver.dredgion.time", defaultValue = "0 0 0,12,20 ? * *")
	public static String DREDGION_TIMES;
}