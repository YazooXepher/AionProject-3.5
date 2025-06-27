package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.team.legion.LegionMemberEx;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.HousingService;

import java.util.List;

/**
 * @author Simple
 */
public class SM_LEGION_MEMBERLIST extends AionServerPacket {

	private static final int OFFLINE = 0x00;
	private static final int ONLINE = 0x01;
	private boolean isFirst;
	private List<LegionMemberEx> legionMembers;

	/**
	 * This constructor will handle legion member info when a List of members is given
	 * 
	 * @param ArrayList
	 *          <LegionMemberEx> legionMembers
	 */
	public SM_LEGION_MEMBERLIST(List<LegionMemberEx> legionMembers, boolean isFirst) {
		this.legionMembers = legionMembers;
		this.isFirst = isFirst;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		int size = legionMembers.size();
		int x = 1;
		writeC(isFirst ? 1 : 0);
		writeH((65536 - size));
		for (LegionMemberEx legionMember : legionMembers) {
			if (x > size)
				break;
			writeD(legionMember.getObjectId());
			writeS(legionMember.getName());
			writeC(legionMember.getPlayerClass().getClassId());
			writeD(legionMember.getLevel());
			writeC(legionMember.getRank().getRankId());
			writeD(legionMember.getWorldId());
			writeC(legionMember.isOnline() ? ONLINE : OFFLINE);
			writeS(legionMember.getSelfIntro());
			writeS(legionMember.getNickname());
			writeD(legionMember.getLastOnline());
			
			int address = HousingService.getInstance().getPlayerAddress(legionMember.getObjectId());
			if (address > 0) {
				House house = HousingService.getInstance().getPlayerStudio(legionMember.getObjectId());
				if (house == null)
					house = HousingService.getInstance().getHouseByAddress(address);
				writeD(address);
				writeD(house.getSettingFlags() >> 8);
			}
			else {
				writeD(0);
				writeD(0);
			}
			writeC(1); // unk 3.5
			writeC(0);
			writeC(0);
			writeC(0);
			x++;
		}
	}
}