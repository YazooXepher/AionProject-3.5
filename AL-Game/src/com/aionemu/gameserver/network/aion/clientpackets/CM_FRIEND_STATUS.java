package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_STATUS;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Packet received when a user changes his buddylist status
 * 
 * @author Ben
 */
public class CM_FRIEND_STATUS extends AionClientPacket {

	private final Logger log = LoggerFactory.getLogger(CM_FRIEND_STATUS.class);
	// The users new status
	private byte status;

	public CM_FRIEND_STATUS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		status = (byte) readC();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		Status statusEnum = Status.getByValue(status);
		if (statusEnum == null) {
			log.warn("received unknown status id " + status);
			statusEnum = Status.ONLINE;
		}
		activePlayer.getFriendList().setStatus(statusEnum, activePlayer.getCommonData());
		PacketSendUtility.sendPacket(activePlayer, new SM_FRIEND_STATUS(status));
	}
}