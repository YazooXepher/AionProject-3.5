package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.BrokerService;

/**
 * @author kosyak
 */
public class CM_REGISTER_BROKER_ITEM extends AionClientPacket {

	@SuppressWarnings("unused")
	private int brokerId;
	private int itemUniqueId;
	private long price;
	private int itemCount;

	public CM_REGISTER_BROKER_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		this.brokerId = readD();
		this.itemUniqueId = readD();
		this.price = readQ();
		this.itemCount = readH();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		if (player.isTrading() || price < 1 || itemCount < 1)
			return;

		BrokerService.getInstance().registerItem(player, itemUniqueId, itemCount, price);
	}
}