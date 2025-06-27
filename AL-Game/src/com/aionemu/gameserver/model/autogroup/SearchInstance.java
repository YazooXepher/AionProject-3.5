package com.aionemu.gameserver.model.autogroup;

import static ch.lambdaj.Lambda.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author xTz
 */
public class SearchInstance {

	private long registrationTime = System.currentTimeMillis();
	private byte instanceMaskId;
	private EntryRequestType ert;
	private List<Integer> members;

	public SearchInstance(byte instanceMaskId, EntryRequestType ert, Collection<Player> members) {
		this.instanceMaskId = instanceMaskId;
		this.ert = ert;
		if (members != null) {
			this.members = extract(members, on(Player.class).getObjectId());
		}
	}

	public List<Integer> getMembers() {
		return members;
	}

	public byte getInstanceMaskId() {
		return instanceMaskId;
	}

	public int getRemainingTime() {
		return (int) (System.currentTimeMillis() - registrationTime) / 1000 * 256;
	}

	public EntryRequestType getEntryRequestType() {
		return ert;
	}

	public boolean isDredgion() {
		return instanceMaskId == 1 || instanceMaskId == 2 || instanceMaskId == 3;
	}
}