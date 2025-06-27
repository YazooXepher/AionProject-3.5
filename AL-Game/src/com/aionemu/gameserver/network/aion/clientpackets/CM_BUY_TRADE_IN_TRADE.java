package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.TradeService;


/**
 * @author MrPoke
 *
 */
public class CM_BUY_TRADE_IN_TRADE extends AionClientPacket{

	private int sellerObjId;
	private int itemId;
	private int count;

	/**
	 * @param opcode
	 */
	public CM_BUY_TRADE_IN_TRADE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		sellerObjId = readD();
		itemId = readD();
		count = readD();
		//Have more data,  need ?:)
	}

	@Override
	protected void runImpl() {
		Player player = this.getConnection().getActivePlayer();
		if (count < 1)
			return;
		TradeService.performBuyFromTradeInTrade(player, sellerObjId, itemId, count);
	}
}