package com.aionemu.gameserver.model.autogroup;

import com.aionemu.commons.taskmanager.AbstractLockManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import java.util.ArrayList;
import java.util.List;
import javolution.util.FastList;

/**
 *
 * @author xTz
 */
public class LookingForParty extends AbstractLockManager {

	private List<SearchInstance> searchInstances = new ArrayList<SearchInstance>();
	private Player player;
	private long startEnterTime;
	private long penaltyTime;

	public LookingForParty(Player player, byte instanceMaskId, EntryRequestType ert) {
		this.player = player;
		searchInstances.add(new SearchInstance(instanceMaskId, ert, ert.isGroupEntry()
				? player.getPlayerGroup2().getOnlineMembers() : null));
	}

	public int unregisterInstance(byte instanceMaskId) {
		super.writeLock();
		try {
			for (SearchInstance si : searchInstances) {
				if (si.getInstanceMaskId() == instanceMaskId) {
					searchInstances.remove(si);
					return searchInstances.size();
				}
			}
			return searchInstances.size();
		}
		finally {
			super.writeUnlock();
		}
	}

	public List<SearchInstance> getSearchInstances() {
		FastList<SearchInstance> tempList = FastList.newInstance();
		for (SearchInstance si : searchInstances) {
			tempList.add(si);
		}
		return tempList;
	}

	public void addInstanceMaskId(byte instanceMaskId, EntryRequestType ert) {
		super.writeLock();
		try {
			searchInstances.add(new SearchInstance(instanceMaskId, ert, ert.isGroupEntry()
					? player.getPlayerGroup2().getOnlineMembers() : null));
		}
		finally {
			super.writeUnlock();
		}
	}

	public SearchInstance getSearchInstance(byte instanceMaskId) {
		super.readLock();
		try {
			for (SearchInstance si : searchInstances) {
				if (si.getInstanceMaskId() == instanceMaskId) {
					return si;
				}
			}
			return null;
		}
		finally {
			super.readUnlock();
		}
	}

	public boolean isRegistredInstance(byte instanceMaskId) {
		for (SearchInstance si : searchInstances) {
			if (si.getInstanceMaskId() == instanceMaskId) {
				return true;
			}
		}
		return false;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setPenaltyTime() {
		penaltyTime = System.currentTimeMillis();
	}

	public boolean hasPenalty() {
		return System.currentTimeMillis() - penaltyTime <= 10000;
	}

	public void setStartEnterTime() {
		startEnterTime = System.currentTimeMillis();
	}

	public boolean isOnStartEnterTask() {
		return System.currentTimeMillis() - startEnterTime <= 120000;
	}
}