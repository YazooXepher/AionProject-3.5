package com.aionemu.loginserver.network.gameserver.serverpackets;

import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;
import com.aionemu.loginserver.service.ptransfer.PlayerTransferRequest;
import com.aionemu.loginserver.service.ptransfer.PlayerTransferResultStatus;
import com.aionemu.loginserver.service.ptransfer.PlayerTransferTask;

/**
 * @author KID
 */
public class SM_PTRANSFER_RESPONSE extends GsServerPacket {
	private PlayerTransferResultStatus result;
	private Account account;
	private PlayerTransferRequest request;
	private int taskId;
	private String reason;
	private PlayerTransferTask task;

	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, int taskId) {
		this.result = result;
		this.taskId = taskId;
	}

	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, PlayerTransferRequest request) {
		this.result = result;
		this.request = request;
		this.account = request.targetAccount;
		this.taskId = request.taskId;
	}
	
	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, int taskId, String reason) {
		this.result = result;
		this.taskId = taskId;
		this.reason = reason;
	}

	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, PlayerTransferTask task) {
		this.result = result;
		this.task = task;
	}

	@Override
	protected void writeImpl(GsConnection con) {
		writeC(12);
		writeD(result.getId());
		switch(result) {
			case SEND_INFO:
				writeD(request.targetAccountId);
				writeD(taskId);
				writeS(request.name);
				writeS(account.getName());
				writeD(request.db.length);
				writeB(request.db);
				break;
			case OK:
				writeD(taskId);
				break;
			case ERROR:
				writeD(taskId);
				writeS(reason);
				break;
			case PERFORM_ACTION:
				writeC(task.sourceServerId);
				writeC(task.targetServerId);
				writeD(task.sourceAccountId);
				writeD(task.targetAccountId);
				writeD(task.playerId);
				writeD(task.id);
				break;
		}
	}
}