package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import org.apache.commons.lang.StringUtils;

/**
 * @author Lyahim, ATracer, xTz
 */
public class SM_GROUP_INFO extends AionServerPacket {
	private LootGroupRules lootRules;
	private int groupId;
	private int leaderId;
	private TeamType type;

	public SM_GROUP_INFO(PlayerGroup group) {
		groupId = group.getObjectId();
		leaderId = group.getLeader().getObjectId();
		lootRules = group.getLootGroupRules();
		type = group.getTeamType();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(groupId);
		writeD(leaderId);
		writeD(lootRules.getLootRule().getId());
		writeD(lootRules.getMisc());
		writeD(lootRules.getCommonItemAbove());
		writeD(lootRules.getSuperiorItemAbove());
		writeD(lootRules.getHeroicItemAbove());
		writeD(lootRules.getFabledItemAbove());
		writeD(lootRules.getEthernalItemAbove());
		writeD(lootRules.getAutodistribution().getId());
		writeD(0x02);
		writeC(0x00);
		writeD(type.getType());
		writeD(type.getSubType());
		writeD(0x00); // message id
		writeS(StringUtils.EMPTY); // name
	}
}