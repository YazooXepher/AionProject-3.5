package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.Collection;

import javolution.util.FastList;

import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ABYSS_ARTIFACT_INFO extends AionServerPacket {

	private Collection<SiegeLocation> locations;

	public SM_ABYSS_ARTIFACT_INFO(Collection<SiegeLocation> collection) {
		this.locations = collection;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		FastList<SiegeLocation> validLocations = new FastList<SiegeLocation>();
		for (SiegeLocation loc : locations) {
			if (((loc.getType() == SiegeType.ARTIFACT) || (loc.getType() == SiegeType.FORTRESS))
				&& (loc.getLocationId() >= 1011) && (loc.getLocationId() < 2000))
				validLocations.add(loc);
		}
		writeH(validLocations.size());
		for (SiegeLocation loc : validLocations) {
			writeD(loc.getLocationId());
			writeD(0); // unk
			writeD(0); // unk
		}
	}
}