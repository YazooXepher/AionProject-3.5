package com.aionemu.gameserver.skillengine.effect;

/**
 * @author ATracer
 */
public enum AbnormalState {
	BUFF(0),
	POISON(1),
	BLEED(2),
	PARALYZE(4),
	SLEEP(8),
	ROOT(16), // ?? cannot move ?
	BLIND(32),
	UNKNOWN(64),
	DISEASE(128),
	SILENCE(256),
	FEAR(512), // Fear I
	CURSE(1024),
	CHAOS(2056),
	STUN(4096),
	PETRIFICATION(8192),
	STUMBLE(16384),
	STAGGER(32768),
	OPENAERIAL(65536),
	SNARE(131072),
	SLOW(262144),
	SPIN(524288),
	BIND(1048576),
	DEFORM(2097152), // (Curse of Roots I, Fear I)
	CANNOT_MOVE(4194304), // (Inescapable Judgment I)
	NOFLY(8388608), // cannot fly
	KNOCKBACK(16777216),//simple_root
	HIDE(536870912), // hide 33554432

	/**
	 * Compound abnormal states
	 */
	CANT_ATTACK_STATE(SPIN.id | SLEEP.id | STUN.id | STUMBLE.id | STAGGER.id
		| OPENAERIAL.id | PARALYZE.id | FEAR.id | CANNOT_MOVE.id),
	CANT_MOVE_STATE(SPIN.id | ROOT.id | SLEEP.id | STUMBLE.id | STUN.id | STAGGER.id
		| OPENAERIAL.id | PARALYZE.id | CANNOT_MOVE.id),
	DISMOUT_RIDE(SPIN.id | ROOT.id | SLEEP.id | STUMBLE.id | STUN.id | STAGGER.id
		| OPENAERIAL.id | PARALYZE.id | CANNOT_MOVE.id | FEAR.id | SNARE.id);

	private int id;

	private AbnormalState(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static AbnormalState getIdByName(String name) {
		for (AbnormalState id : values()) {
			if (id.name().equals(name))
				return id;
		}
		return null;
	}
	
	public static AbnormalState getStateById(int id) {
		for (AbnormalState as : values()) {
			if (as.getId() == id)
				return as;
		}
		return null;
	}
}