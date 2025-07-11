package com.aionemu.gameserver.network.aion.serverpackets;

import org.apache.commons.lang.StringUtils;

import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author SheppeR, Guapo, nrg
 */
public class SM_AUTO_GROUP extends AionServerPacket {
	private byte windowId;
	private byte instanceMaskId;
	private int mapId;
	private int messageId;
	private int titleId;
	private int waitTime;
	private boolean close;
	String name = StringUtils.EMPTY;
	
	public static final byte wnd_EntryIcon = 6; 
	
	public SM_AUTO_GROUP(byte instanceMaskId) {
		AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
		if (agt == null) {
			throw new IllegalArgumentException("Auto Groups Type no found for Instance MaskId: " + instanceMaskId);
		}
		
		this.instanceMaskId = instanceMaskId;
		this.messageId = agt.getNameId();
		this.titleId = agt.getTittleId();
		this.mapId = agt.getInstanceMapId();
	}

	public SM_AUTO_GROUP(byte instanceMaskId, Number windowId) {
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
	}

	public SM_AUTO_GROUP(byte instanceMaskId, Number windowId, boolean close) {
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
		this.close = close;
	}

	public SM_AUTO_GROUP(byte instanceMaskId, Number windowId, int waitTime, String name) {
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
		this.waitTime = waitTime;
		this.name = name;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(this.instanceMaskId);
		writeC(this.windowId);
		writeD(this.mapId);
		switch (this.windowId) {
			case 0: // request entry
				writeD(this.messageId);
				writeD(this.titleId);
				writeD(0);
				break;
			case 1: // waiting window
				writeD(0);
				writeD(0);
				writeD(this.waitTime);
				break;
			case 2: // cancel looking
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case 3: // pass window
				writeD(0);
				writeD(0);
				writeD(this.waitTime);
				break;
			case 4: // enter window
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case 5: // after you click enter
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case wnd_EntryIcon: // entry icon
				writeD(this.messageId);
				writeD(this.titleId);
				writeD(this.close ? 0 : 1);
				break;
			case 7: // failed window
				writeD(this.messageId);
				writeD(this.titleId);
				writeD(0);
				break;
			case 8: // on login
				writeD(0);
				writeD(0);
				writeD(this.waitTime);
				break;
		}
		writeC(0);
		writeS(this.name);
	}
}