package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.dao.PlayerPasskeyDAO;
import com.aionemu.gameserver.model.account.CharacterPasskey;
import com.aionemu.gameserver.model.account.CharacterPasskey.ConnectType;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHARACTER_SELECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_CHARACTER;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.player.PlayerEnterWorldService;
import com.aionemu.gameserver.services.player.PlayerService;

/**
 * @author ginho1
 */
public class CM_CHARACTER_PASSKEY extends AionClientPacket {

	private int type;
	private String passkey;
	private String newPasskey;

	public CM_CHARACTER_PASSKEY(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		type = readH(); // 0:new, 2:update, 3:input
		try {
			passkey = new String(readB(32), "UTF-16le");
			if (type == 2)
				newPasskey = new String(readB(32), "UTF-16le");
		}
		catch (Exception e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		AionConnection client = getConnection();
		CharacterPasskey chaPasskey = client.getAccount().getCharacterPasskey();

		switch (type) {
			case 0:
				chaPasskey.setIsPass(false);
				chaPasskey.setWrongCount(0);
				DAOManager.getDAO(PlayerPasskeyDAO.class).insertPlayerPasskey(client.getAccount().getId(), passkey);
				client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));
				break;
			case 2:
				boolean isSuccess = DAOManager.getDAO(PlayerPasskeyDAO.class).updatePlayerPasskey(client.getAccount().getId(),
					passkey, newPasskey);

				chaPasskey.setIsPass(false);
				if (isSuccess) {
					chaPasskey.setWrongCount(0);
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));
				}
				else {
					chaPasskey.setWrongCount(chaPasskey.getWrongCount() + 1);
					checkBlock(client.getAccount().getId(), chaPasskey.getWrongCount());
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));
				}
				break;
			case 3:
				boolean isPass = DAOManager.getDAO(PlayerPasskeyDAO.class).checkPlayerPasskey(client.getAccount().getId(),
					passkey);

				if (isPass) {
					chaPasskey.setIsPass(true);
					chaPasskey.setWrongCount(0);
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));

					if (chaPasskey.getConnectType() == ConnectType.ENTER)
						PlayerEnterWorldService.startEnterWorld(chaPasskey.getObjectId(), client);
					else if (chaPasskey.getConnectType() == ConnectType.DELETE) {
						PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(chaPasskey.getObjectId());

						PlayerService.deletePlayer(playerAccData);
						client.sendPacket(new SM_DELETE_CHARACTER(chaPasskey.getObjectId(), playerAccData
							.getDeletionTimeInSeconds()));
					}
				}
				else {
					chaPasskey.setIsPass(false);
					chaPasskey.setWrongCount(chaPasskey.getWrongCount() + 1);
					checkBlock(client.getAccount().getId(), chaPasskey.getWrongCount());
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));
				}
				break;
		}
	}

	/**
	 * @param accountId
	 * @param wrongCount
	 */
	private void checkBlock(int accountId, int wrongCount) {
		if (wrongCount >= SecurityConfig.PASSKEY_WRONG_MAXCOUNT) {
			// TODO : Change the account to be blocked
			LoginServer.getInstance().sendBanPacket((byte) 2, accountId, "", 60 * 8, 0);
		}
	}
}