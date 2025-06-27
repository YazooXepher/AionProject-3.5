package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author ATracer
 */
public class CM_GODSTONE_SOCKET extends AionClientPacket {

	private int npcObjectId;
	private int weaponId;
	private int stoneId;

	public CM_GODSTONE_SOCKET(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		this.npcObjectId = readD();
		this.weaponId = readD();
		this.stoneId = readD();
	}

	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();

		VisibleObject obj = activePlayer.getKnownList().getObject(npcObjectId);
		if(obj != null && obj instanceof Npc && MathUtil.isInRange(activePlayer, obj, 7)) {
			ItemSocketService.socketGodstone(activePlayer, weaponId, stoneId);
		}
	}
}