package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.stats.calc.StatOwner;

/**
 * @author ATracer modified by Wakizashi
 */
public class ItemStone implements StatOwner {

	private int itemObjId;

	private int itemId;

	private int slot;

	private PersistentState persistentState;

	public static enum ItemStoneType {
		MANASTONE,
		GODSTONE,
		FUSIONSTONE;
	}

	/**
	 * @param itemObjId
	 * @param itemId
	 * @param slot
	 * @param persistentState
	 */
	public ItemStone(int itemObjId, int itemId, int slot, PersistentState persistentState) {
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.slot = slot;
		this.persistentState = persistentState;
	}

	/**
	 * @return the itemObjId
	 */
	public int getItemObjId() {
		return itemObjId;
	}

	/**
	 * @return the itemId
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @param slot
	 *          the slot to set
	 */
	public void setSlot(int slot) {
		this.slot = slot;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * @return the pState
	 */
	public PersistentState getPersistentState() {
		return persistentState;
	}

	/**
	 * @param persistentState
	 */
	public void setPersistentState(PersistentState persistentState) {
		switch (persistentState) {
			case DELETED:
				if (this.persistentState == PersistentState.NEW)
					this.persistentState = PersistentState.NOACTION;
				else
					this.persistentState = PersistentState.DELETED;
				break;
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
					break;
			default:
				this.persistentState = persistentState;
		}
	}
}