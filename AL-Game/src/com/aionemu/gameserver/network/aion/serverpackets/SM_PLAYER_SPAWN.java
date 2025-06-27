package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * This packet is notify client what map should be loaded.
 *
 * @author -Nemesiss-
 */
public class SM_PLAYER_SPAWN extends AionServerPacket {

	/**
	 * Player that is entering game.
	 */
	private final Player player;

	/**
	 * Constructor.
	 *
	 * @param player
	 */
	public SM_PLAYER_SPAWN(Player player) {
		super();
		this.player = player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(player.getWorldId());
		writeD(player.getWorldId()); // world + chnl
		writeD(0x00); // unk
		writeC(WorldMapType.getWorld(player.getWorldId()).isPersonal() ? 1 : 0);
		writeF(player.getX()); // x
		writeF(player.getY()); // y
		writeF(player.getZ()); // z
		writeC(player.getHeading()); // heading
		writeD(0); // new 2.5
		writeD(0); // new 2.5
		writeD(0); // new 2.7
		writeC(0); // 0 or 1 new 3.0
	}
}