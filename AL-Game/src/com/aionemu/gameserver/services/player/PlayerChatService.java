package com.aionemu.gameserver.services.player;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Source
 */
public class PlayerChatService {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger("CHAT_LOG");

	/**
	 * This method will control players msg
	 *
	 * @param player
	 */
	public static boolean isFlooding(final Player player) {
		player.setLastMessageTime();

		if (player.floodMsgCount() > SecurityConfig.FLOOD_MSG) {
			player.setGagged(true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FLOODING);
			player.getController().cancelTask(TaskId.GAG);
			player.getController().addTask(TaskId.GAG, ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					player.setGagged(false);
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CAN_CHAT_NOW);
				}

			}, 2 * 60000L));

			return true;
		}

		return false;
	}

	public static void chatLogging(Player player, ChatType type, String message) {
		switch (type) {
			case GROUP:
				log.info(String.format("[MESSAGE] - GROUP <%d>: [%s]> %s", player.getCurrentTeamId(), player.getName(),
						message));
				break;
			case ALLIANCE:
				log.info(String.format("[MESSAGE] - ALLIANCE <%d>: [%s]> %s", player.getCurrentTeamId(), player.getName(),
						message));
				break;
			case GROUP_LEADER:
				log.info(String.format("[MESSAGE] - LEADER_ALERT: [%s]> %s", player.getName(), message));
				break;
			case LEGION:
				log.info(String.format("[MESSAGE] - LEGION <%s>: [%s]> %s", player.getLegion().getLegionName(),
						player.getName(), message));
				break;
			case LEAGUE:
			case LEAGUE_ALERT:
				log.info(String.format("[MESSAGE] - LEAGUE <%s>: [%s]> %s", player.getCurrentTeamId(), player.getName(),
						message));
				break;
			case NORMAL:
			case SHOUT:
				if (player.getRace() == Race.ASMODIANS)
					log.info(String.format("[MESSAGE] - ALL (ASMO): [%s]> %s", player.getName(), message));
				else
					log.info(String.format("[MESSAGE] - ALL (ELYOS): [%s]> %s", player.getName(), message));
				break;
			default:
				if (player.isGM())
					log.info(String.format("[MESSAGE] - ALL (GM): [%s]> %s", player.getName(), message));
				break;
		}
	}
}