package com.aionemu.gameserver.model;

/**
 * @author zdead
 */
public class Petition {

	private final int petitionId;
	private final int playerObjId;
	private final PetitionType type;
	private final String title;
	private final String contentText;
	private final String additionalData;
	private final PetitionStatus status;

	public Petition(int petitionId) {
		this.petitionId = petitionId;
		this.playerObjId = 0;
		this.type = PetitionType.INQUIRY;
		this.title = "";
		this.contentText = "";
		this.additionalData = "";
		this.status = PetitionStatus.PENDING;
	}

	public Petition(int petitionId, int playerObjId, int petitionTypeId, String title, String contentText,
		String additionalData, int petitionStatus) {
		this.petitionId = petitionId;
		this.playerObjId = playerObjId;
		switch (petitionTypeId) {
			case 256:
				type = PetitionType.CHARACTER_STUCK;
				break;
			case 512:
				type = PetitionType.CHARACTER_RESTORATION;
				break;
			case 768:
				type = PetitionType.BUG;
				break;
			case 1024:
				type = PetitionType.QUEST;
				break;
			case 1280:
				type = PetitionType.UNACCEPTABLE_BEHAVIOR;
				break;
			case 1536:
				type = PetitionType.SUGGESTION;
				break;
			case 65280:
				type = PetitionType.INQUIRY;
				break;
			default:
				type = PetitionType.INQUIRY;
				break;
		}
		this.title = title;
		this.contentText = contentText;
		this.additionalData = additionalData;
		switch (petitionStatus) {
			case 0:
				status = PetitionStatus.PENDING;
				break;
			case 1:
				status = PetitionStatus.IN_PROGRESS;
				break;
			case 2:
				status = PetitionStatus.REPLIED;
				break;
			default:
				status = PetitionStatus.PENDING;
				break;
		}
	}

	public int getPlayerObjId() {
		return playerObjId;
	}

	public int getPetitionId() {
		return petitionId;
	}

	public PetitionType getPetitionType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getContentText() {
		return contentText;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public PetitionStatus getStatus() {
		return status;
	}
}