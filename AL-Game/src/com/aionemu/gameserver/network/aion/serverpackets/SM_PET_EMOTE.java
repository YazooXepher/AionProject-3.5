package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.PetEmote;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author ATracer
 */
public class SM_PET_EMOTE extends AionServerPacket {

	private Pet pet;
	private PetEmote emote;
	private final float x, y, z, x2, y2, z2;
	private final byte heading;
	private int emotionId, param1;

	public SM_PET_EMOTE(Pet pet, PetEmote emote) {
		this(pet, emote, 0, 0, 0, (byte) 0);
	}

	public SM_PET_EMOTE(Pet pet, PetEmote emote, float x, float y, float z, byte h) {
		this(pet, emote, x, y, z, 0, 0, 0, h);
	}

	public SM_PET_EMOTE(Pet pet, PetEmote emote, float x, float y, float z, float x2, float y2, float z2, byte h) {
		this.pet = pet;
		this.emote = emote;
		this.x = x;
		this.y = y;
		this.z = z;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.heading = h;
	}

	public SM_PET_EMOTE(Pet pet, PetEmote emote, int emotionId, int param1) {
		this(pet, emote);
		this.emotionId = emotionId;
		this.param1 = param1;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(pet.getObjectId());
		writeC(emote.getEmoteId());
		switch (emote) {
			case MOVE_STOP:
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				break;
			case MOVETO:
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				writeF(x2);
				writeF(y2);
				writeF(z2);
				break;
			default:
				writeC(emotionId);
				writeC(param1); // happinessAdded?
				break;
		}
	}
}