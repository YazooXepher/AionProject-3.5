package com.aionemu.gameserver.model.gameobjects;

import java.sql.Timestamp;
import com.aionemu.gameserver.model.gameobjects.LetterType;

/**
 * @author kosyachok
 */
public class Letter extends AionObject {

	private int recipientId;
	private Item attachedItem;
	private long attachedKinahCount;
	private String senderName;
	private String title;
	private String message;
	private boolean unread;
	private boolean express;
	private Timestamp timeStamp;
	private PersistentState persistentState;
	private LetterType letterType;

	/**
	 * @param objId
	 * @param attachedItem
	 * @param attachedKinah
	 * @param title
	 * @param message
	 * @param senderId
	 * @param senderName
	 * @param timeStamp
	 *          new letter constructor
	 */
	public Letter(int objId, int recipientId, Item attachedItem, long attachedKinahCount, String title, String message,
		String senderName, Timestamp timeStamp, boolean unread, LetterType letterType) {
		super(objId);

		if(letterType == LetterType.EXPRESS || letterType == LetterType.BLACKCLOUD)
			this.express = true;
		else
			this.express = false;

		this.recipientId = recipientId;
		this.attachedItem = attachedItem;
		this.attachedKinahCount = attachedKinahCount;
		this.title = title;
		this.message = message;
		this.senderName = senderName;
		this.timeStamp = timeStamp;
		this.unread = unread;
		this.persistentState = PersistentState.NEW;
		this.letterType = letterType;
	}

	@Override
	public String getName() {
		return String.valueOf(attachedItem.getItemTemplate().getNameId());
	}

	public int getRecipientId() {
		return recipientId;
	}

	public Item getAttachedItem() {
		return attachedItem;
	}

	public long getAttachedKinah() {
		return attachedKinahCount;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public String getSenderName() {
		return senderName;
	}

	public LetterType getLetterType() {
		return letterType;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setReadLetter() {
		this.unread = false;
		this.persistentState = PersistentState.UPDATE_REQUIRED;
	}

	public boolean isExpress() {
		return express;
	}

	public void setExpress(boolean express) {
		this.express = express;
		this.persistentState = PersistentState.UPDATE_REQUIRED;
	}

	public void setLetterType(LetterType letterType) {
		this.letterType = letterType;
		if(letterType == LetterType.EXPRESS || letterType == LetterType.BLACKCLOUD)
			this.express = true;
		else
			this.express = false;
	}

	public PersistentState getLetterPersistentState() {
		return persistentState;
	}

	public void removeAttachedItem() {
		this.attachedItem = null;
		this.persistentState = PersistentState.UPDATE_REQUIRED;
	}

	public void removeAttachedKinah() {
		this.attachedKinahCount = 0;
		this.persistentState = PersistentState.UPDATE_REQUIRED;
	}

	public void delete() {
		this.persistentState = PersistentState.DELETED;
	}

	public void setPersistState(PersistentState state) {
		this.persistentState = state;
	}

	/**
	 * @return the timeStamp
	 */
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
}