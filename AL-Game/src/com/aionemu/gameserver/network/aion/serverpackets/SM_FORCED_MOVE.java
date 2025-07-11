package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Sweetkr
 */
public class SM_FORCED_MOVE extends AionServerPacket {

	private Creature creature;
	private int objectId;
	private float x;
	private float y;
	private float z;

	public SM_FORCED_MOVE(Creature creature, Creature target) {
		this(creature, target.getObjectId(), target.getX(), target.getY(), target.getZ());
	}
	public SM_FORCED_MOVE(Creature creature, int objectId, float x, float y, float z) {
		this.creature = creature;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(creature.getObjectId());
		writeD(objectId);//targets objectId
		writeC(16); // unk
		writeF(x);
		writeF(y);
		writeF(z);
	}
}