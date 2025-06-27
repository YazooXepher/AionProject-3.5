package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.HousingFlags;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.MaintenanceTask;
import com.aionemu.gameserver.model.town.Town;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.TownService;
import org.joda.time.DateTime;

/**
 * @author Rolandas
 */
public class SM_HOUSE_OWNER_INFO extends AionServerPacket {

	private Player player;
	private House activeHouse;

	public SM_HOUSE_OWNER_INFO(Player player, House activeHouse) {
		this.player = player;
		this.activeHouse = activeHouse;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (activeHouse == null) {
			writeD(0);
			writeD(player.isInHousingStatus(HousingFlags.BUY_STUDIO_ALLOWED) ? 355000 : 0); // studio building id
		} else {
			writeD(activeHouse.getAddress().getId());
			writeD(activeHouse.getBuilding().getId());
		}
		writeC(player.getHousingStatus());
		int townLevel = 1;
		if(activeHouse != null && activeHouse.getAddress().getTownId() != 0) {
			Town town = TownService.getInstance().getTownById(activeHouse.getAddress().getTownId());
			townLevel = town.getLevel();
		}
		writeC(townLevel);
		// Maintenance bill weeks left ?, if 0 maintenance date is in red
		if (activeHouse == null || !activeHouse.isFeePaid() || activeHouse.getNextPay() == null) {
			writeC(0);
		} else {
			long paytime = activeHouse.getNextPay().getTime();
			float diff = paytime - ((long) MaintenanceTask.getInstance().getRunTime() * 1000);
			if (diff < 0) {
				writeC(0);
			} else {
				int weeks =  (int) (Math.round(diff / MaintenanceTask.getInstance().getPeriod()));
				if(DateTime.now().getDayOfWeek() != 7) //Hack for auction Day, client counts sunday to new week
				   weeks++;
				writeC(weeks);
			}
		}

		// Second house info ?
		writeD(0);
		writeD(0);
		writeD(0);
		writeC(0);
		writeC(0);
		writeC(0); // 3.5
		writeC(0); // 3.5 unk
	}
}