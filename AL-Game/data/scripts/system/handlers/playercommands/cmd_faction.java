package playercommands;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.player.PlayerChatService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.world.World;
import org.apache.commons.lang.StringUtils;

/**
 * @author Shepper, modified: bobobear
 */
public class cmd_faction extends PlayerCommand {

	public cmd_faction() {
		super("faction");
	}

	@Override
	public void execute(Player player, String... params) {
		Storage sender = player.getInventory();

		if (!CustomConfig.FACTION_CMD_CHANNEL) {
			PacketSendUtility.sendMessage(player, "The command is disabled.");
			return;
		}

		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax .faction <message>");
			return;
		}

		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't talk in Prison.");
			return;
		}
		else if (player.isGagged()) {
			PacketSendUtility.sendMessage(player, "You are gaged, you can't talk.");
			return;
		}

		if (!CustomConfig.FACTION_FREE_USE) {
			if (sender.getKinah() > CustomConfig.FACTION_USE_PRICE)
				sender.decreaseKinah(CustomConfig.FACTION_USE_PRICE);
			else {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
				return;
			}
		}

		if (PlayerChatService.isFlooding(player)) {
			return;
		}

		String message = StringUtils.join(params, " ");
		String LogMessage = message;

		if (CustomConfig.FACTION_CHAT_CHANNEL) {
			ChatType channel = ChatType.CH1;

			for (Player listener : World.getInstance().getAllPlayers()) {
				if (listener.getAccessLevel() > 1) {
					PacketSendUtility.sendPacket(listener, new SM_MESSAGE(player.getObjectId(),
							(player.getRace() == Race.ASMODIANS ? "(A) " : "(E) ") + player.getName(), message, channel));
				}
				else if (listener.getRace() == player.getRace()) {
					PacketSendUtility.sendPacket(listener, new SM_MESSAGE(player.getObjectId(),
							player.getName(), message, channel));
				}
			}
		}
		else {
			message = player.getName() + ": " + message;
			for (Player a : World.getInstance().getAllPlayers()) {
				if (a.getAccessLevel() > 1) {
					PacketSendUtility.sendMessage(a, (player.getRace() == Race.ASMODIANS ? "[A] " : "[E] ") + message);
				}
				else if (a.getRace() == player.getRace()) {
					PacketSendUtility.sendMessage(a, message);
				}
			}
		}

		if (LoggingConfig.LOG_FACTION) {
			PlayerChatService.chatLogging(player, ChatType.NORMAL, "[Faction Msg] " + LogMessage);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}