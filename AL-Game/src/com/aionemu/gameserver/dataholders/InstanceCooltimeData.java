package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.gameobjects.player.Player;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import com.aionemu.gameserver.model.templates.InstanceCooltime;
import com.aionemu.gameserver.services.instance.InstanceService;
import org.joda.time.DateTime;

/**
 * @author VladimirZ
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "instance_cooltimes")
public class InstanceCooltimeData {

	@XmlElement(name = "instance_cooltime", required = true)
	protected List<InstanceCooltime> instanceCooltime;

	private FastMap<Integer, InstanceCooltime> instanceCooltimes = new FastMap<Integer, InstanceCooltime>();
	private HashMap<Integer, Integer> syncIdToMapId = new HashMap<Integer, Integer>();
	/**
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (InstanceCooltime tmp : instanceCooltime) {
			instanceCooltimes.put(tmp.getWorldId(), tmp);
			syncIdToMapId.put(tmp.getId(), tmp.getWorldId());
		}
		instanceCooltime.clear();
	}

	/**
	 * @param worldId
	 * @return
	 */
	public InstanceCooltime getInstanceCooltimeByWorldId(int worldId) {
		return instanceCooltimes.get(worldId);
	}
	
	public int getWorldId(int syncId)
	{
		if (!syncIdToMapId.containsKey(syncId))
			return 0;
		return syncIdToMapId.get(syncId);
	}
	
	public long getInstanceEntranceCooltimeById(Player player, int syncId) {
		if (!syncIdToMapId.containsKey(syncId))
			return 0;
		return getInstanceEntranceCooltime(player, syncIdToMapId.get(syncId));
	}

	public long getInstanceEntranceCooltime(Player player, int worldId) {
		int instanceCooldownRate = InstanceService.getInstanceRate(player, worldId);
		long instanceCoolTime = 0;
		InstanceCooltime clt = getInstanceCooltimeByWorldId(worldId);
		if (clt != null) {
			instanceCoolTime = clt.getEntCoolTime();
			if (clt.getCoolTimeType().isDaily()) {
				DateTime now = DateTime.now();
				DateTime repeatDate = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), (int) (instanceCoolTime / 100), 0, 0);
				if (now.isAfter(repeatDate)) {
					repeatDate = repeatDate.plusHours(24);
					instanceCoolTime = repeatDate.getMillis();
				}
				else {
					instanceCoolTime = repeatDate.getMillis();
				}
			}
			else if (clt.getCoolTimeType().isWeekly()) {
				String[] days = clt.getTypeValue().split(",");
				instanceCoolTime = getUpdateHours(days, (int) (instanceCoolTime / 100));
			}
			else {
				instanceCoolTime = System.currentTimeMillis() + (instanceCoolTime * 60 * 1000);
			}
		}
		if (instanceCooldownRate != 1) {
			instanceCoolTime = System.currentTimeMillis() + ((instanceCoolTime - System.currentTimeMillis()) / instanceCooldownRate);
		}
		return instanceCoolTime;
	}

	private long getUpdateHours(String[] days, int hour) {
		DateTime now = DateTime.now();
		DateTime repeatDate = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hour, 0, 0);
		int curentDay = now.getDayOfWeek();
		for (String name : days) {
			int day = getDay(name);
			if (day < curentDay) {
				continue;
			}
			if (day == curentDay) {
				if (now.isBefore(repeatDate)) {
					return repeatDate.getMillis();
				}
			}
			else {
				repeatDate = repeatDate.plusDays(day - curentDay);
				return repeatDate.getMillis();
			}
		}
		return repeatDate.plusDays((7 - curentDay) + getDay(days[0])).getMillis();
	}

	private int getDay(String day) {
		if (day.equals("Mon")) {
			return 1;
		}
		else if (day.equals("Tue")) {
			return 2;
		}
		else if (day.equals("Wed")) {
			return 3;
		}
		else if (day.equals("Thu")) {
			return 4;
		}
		else if (day.equals("Fri")) {
			return 5;
		}
		else if (day.equals("Sat")) {
			return 6;
		}
		else if (day.equals("Sun")) {
			return 7;
		}
		throw new IllegalArgumentException("Invalid Day: " + day);
	}

	public Integer size() {
		return instanceCooltimes.size();
	}
}