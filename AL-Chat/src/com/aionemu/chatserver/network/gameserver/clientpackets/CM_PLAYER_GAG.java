package com.aionemu.chatserver.network.gameserver.clientpackets;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.chatserver.network.gameserver.GsClientPacket;
import com.aionemu.chatserver.network.gameserver.GsConnection;
import com.aionemu.chatserver.service.ChatService;

/**
 * @author ViAl
 *
 */
public class CM_PLAYER_GAG extends GsClientPacket {


	private static final Logger log = LoggerFactory.getLogger(CM_PLAYER_LOGOUT.class);

	private int playerId;
	private long gagTime;

	public CM_PLAYER_GAG(ByteBuffer buf, GsConnection connection) {
		super(buf, connection, 0x03);
	}

	@Override
	protected void readImpl() {
		playerId = readD();
		gagTime = readQ();
	}

	@Override
	protected void runImpl() {
		ChatService.getInstance().gagPlayer(playerId, gagTime);
		log.info("Player was gagged " + playerId + " for "+(gagTime / 1000 / 60) + " minutes");
	}
}