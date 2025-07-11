package com.aionemu.gameserver.model;

/**
 * @author lyahim
 */
public enum EmotionType {
	UNK(-1),
	SELECT_TARGET(0),
	JUMP(1),
	SIT(2),
	STAND(3),
	CHAIR_SIT(4),
	CHAIR_UP(5),
	START_FLYTELEPORT(6),
	LAND_FLYTELEPORT(7),
	WINDSTREAM(8),
	WINDSTREAM_END(9),
	WINDSTREAM_EXIT(10),
	WINDSTREAM_START_BOOST(11),
	WINDSTREAM_END_BOOST(12),
	FLY(13),
	LAND(14),
	RIDE(15),
	RIDE_END(16),
	DIE(18),
	RESURRECT(19),
	EMOTE(21),
	END_DUEL(22), // What? Duel? It's the end of a emote
	ATTACKMODE(24), // Attack mode, by game
	NEUTRALMODE(25), // Attack mode, by game
	WALK(26),
	RUN(27),
	OPEN_DOOR(31),
	CLOSE_DOOR(32),
	OPEN_PRIVATESHOP(33),
	CLOSE_PRIVATESHOP(34),
	START_EMOTE2(35), // It's not "emote". Triggered after Attack Mode of npcs
	POWERSHARD_ON(36),
	POWERSHARD_OFF(37),
	ATTACKMODE2(38), // It's the Attack toggled by player
	NEUTRALMODE2(39), // It's Neutral toggled by player
	START_LOOT(40),
	END_LOOT(41),
	START_QUESTLOOT(42),
	END_QUESTLOOT(43),
	START_FEEDING(50),
	END_FEEDING(51),
	WINDSTREAM_STRAFE(52),
	START_SPRINT(53),
	END_SPRINT(54);
	private int id;

	private EmotionType(int id) {
		this.id = id;
	}

	public int getTypeId() {
		return id;
	}

	public static EmotionType getEmotionTypeById(int id) {
		for (EmotionType emotionType : values()) {
			if (emotionType.getTypeId() == id)
				return emotionType;
		}
		return UNK;
	}
}