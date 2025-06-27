/*
 * This file is part of InPanic Core <Ver:3.1>.
 *
 * InPanic-Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * InPanic-Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with InPanic-Core. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class AdvCustomConfig {

	/**
	 * Cube Size
	 */
	@Property(key = "gameserver.cube.size", defaultValue = "0")
	public static int CUBE_SIZE;

	@Property(key = "gameserver.notquest.craftskill", defaultValue = "false")
	public static boolean NOT_CRAFTSKILL_QUEST;

	/**
	 * InGameShop Limit
	 */
	@Property(key = "gameserver.gameshop.limit", defaultValue = "false")
	public static boolean GAMESHOP_LIMIT;

	@Property(key = "gameserver.gameshop.category", defaultValue = "0")
	public static byte GAMESHOP_CATEGORY;

	@Property(key = "gameserver.gameshop.limit.time", defaultValue = "60")
	public static long GAMESHOP_LIMIT_TIME;

	/**
	 * Siege Auto Race
	 */
	@Property(key = "gameserver.auto.source.race", defaultValue = "false")
	public static boolean AUTO_SOURCE_RACE;

	@Property(key = "gameserver.auto.source.id", defaultValue = "4011,4021;4031,4041")
	public static String AUTO_SOURCE_LOCID;

	@Property(key = "gameserver.auto.siege.race", defaultValue = "false")
	public static boolean SIEGE_AUTO_RACE;

	@Property(key = "gameserver.auto.siege.id", defaultValue = "2011,2021;3011,3021")
	public static String SIEGE_AUTO_LOCID;

	@Property(key = "gameserver.craft.delaytime,rate", defaultValue = "2")
	public static Integer CRAFT_DELAYTIME_RATE;
}
