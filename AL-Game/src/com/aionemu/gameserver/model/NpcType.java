package com.aionemu.gameserver.model;

/**
 * @author Luno modified by Wakizashi (CHEST)
 */
public enum NpcType {
	/** These are regular monsters */
	ATTACKABLE(0),
	/** These are Peace npc */
	PEACE(2),
	/** These are monsters that are pre-aggressive */
	AGGRESSIVE(8),
	// unk
	INVULNERABLE(10),
	/** These are non attackable NPCs */
	NON_ATTACKABLE(38),
	
	UNKNOWN(54);

	private int someClientSideId;

	private NpcType(int id) {
		this.someClientSideId = id;
	}

	public int getId() {
		return someClientSideId;
	}
}