package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;


/**
 * In this packet client is sending Mac Address - haha.
 * 
 * @author -Nemesiss-, KID
 */
public class CM_MAC_ADDRESS extends AionClientPacket {
	/**
	 * Mac Addres send by client in the same format as: ipconfig /all [ie:
	 * xx-xx-xx-xx-xx-xx]
	 */
	private String	macAddress;

	/**
	 * Constructs new instance of <tt>CM_MAC_ADDRESS </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_MAC_ADDRESS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		readC();
		short counter = (short)readH();
		for(short i = 0; i < counter; i++)
			readD();
		macAddress = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		if(BannedMacManager.getInstance().isBanned(macAddress)) {
			//TODO some information packets
			this.getConnection().closeNow();
			LoggerFactory.getLogger(CM_MAC_ADDRESS.class).info("[MAC_AUDIT] "+macAddress+" ("+this.getConnection().getIP()+") was kicked due to mac ban");
		}
		else
			this.getConnection().setMacAddress(macAddress);
	}
}