package com.aionemu.gameserver.model.templates.spawns;

import com.aionemu.gameserver.utils.gametime.GameTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "TemporarySpawn")
public class TemporarySpawn {
	@XmlAttribute(name = "spawn_time") // *.*.*  hour.day.month (* == all)
	private String spawnTime;

	@XmlAttribute(name = "despawn_time") // *.*.*  hour.day.month (* == all)
	private String despawnTime;

	public String getSpawnTime() {
		return spawnTime;
	}

	public Integer geSpawnHour() {
		return getTime(spawnTime, 0);
	}

	public Integer geSpawnDay() {
		return getTime(spawnTime, 1);
	}

	public Integer getSpawnMonth() {
		return getTime(spawnTime, 2);
	}

	public Integer geDespawnHour() {
		return getTime(despawnTime, 0);
	}

	public Integer geDespawnDay() {
		return getTime(despawnTime, 1);
	}

	public Integer getDespawnMonth() {
		return getTime(despawnTime, 2);
	}

	private Integer getTime(String time, int type) {
		String result = time.split("\\.")[type];
		if (result.equals("*")) {
			return null;
		}
		return Integer.parseInt(result);
	}

	public String getDespawnTime() {
		return despawnTime;
	}

	private boolean isTime(Integer hour, Integer day, Integer month) {
		GameTime gameTime = GameTimeManager.getGameTime();
		if (hour != null && hour == gameTime.getHour()) {
			if (day == null) {
				return true;
			}
			if (day == gameTime.getDay()) {
				return month == null || month == gameTime.getMonth();
			}
		}
		return false;
	}

	public boolean canSpawn() {
		return isTime(geSpawnHour(), geSpawnDay(), getSpawnMonth());
	}

	public boolean canDespawn() {
		return isTime(geDespawnHour(), geDespawnDay(), getDespawnMonth());
	}

	public boolean isInSpawnTime() {
		GameTime gameTime = GameTimeManager.getGameTime();
		Integer spawnHour = geSpawnHour();
		Integer spawnDay = geSpawnDay();
		Integer spawnMonth = getSpawnMonth();
		Integer despawnHour = geDespawnHour();
		Integer despawnDay = geDespawnDay();
		Integer despawnMonth = getDespawnMonth();
		int curentHour = gameTime.getHour();
		int curentDay = gameTime.getDay();
		int curentMonth = gameTime.getMonth();

		if (spawnMonth != null) {
			if (!checkTime(curentMonth, spawnMonth, despawnMonth)) {
				return false;
			}
		}
		if (spawnDay != null) {
			if (!checkTime(curentDay, spawnDay, despawnDay)) {
				return false;
			}
		} 
		if (spawnMonth == null && spawnDay == null && !checkHour(curentHour, spawnHour, despawnHour)) {
			return false;
		}
		return true;
	}

	private boolean checkTime(int curentTime, int spawnTime, int despawnTime) {
		if (spawnTime < despawnTime) {
			if (!(curentTime >= spawnTime && curentTime <= despawnTime)) {
				return false;
			}
		}
		else if (spawnTime > despawnTime) {
			if (!(curentTime >= spawnTime || curentTime <= despawnTime)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkHour(int curentTime, int spawnTime, int despawnTime) {
		if (spawnTime < despawnTime) {
			if (!(curentTime >= spawnTime && curentTime < despawnTime)) {
				return false;
			}
		}
		else if (spawnTime > despawnTime) {
			if (!(curentTime >= spawnTime || curentTime < despawnTime)) {
				return false;
			}
		}
		return true;
	}
}