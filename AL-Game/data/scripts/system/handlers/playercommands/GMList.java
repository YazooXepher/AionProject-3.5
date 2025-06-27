package playercommands;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author Maestros
 */
 
public class GMList extends PlayerCommand {

	public GMList() {
		super("gms");
	}

	@Override
	public void execute(Player player, String... params) {
		final List<Player> admins = new ArrayList<Player>();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player object) {
				if (object.getAccessLevel() > 0 && object.getFriendList().getStatus() != FriendList.Status.OFFLINE) {
					admins.add(object);
				}
			}
		});

		if (admins.size() > 0) {
			PacketSendUtility.sendMessage(player, "====================");
			if (admins.size() == 1)
				PacketSendUtility.sendMessage(player, "Der folgende Support ist online: ");
			else
				PacketSendUtility.sendMessage(player, "Die folgenden Supporter sind online: ");

			for (Player admin : admins) {

				/*if (AdminConfig.ADMIN_TAG_ENABLE)

				{
					String gmTag = null;
					if (admin.getAccessLevel() == 1) {
						gmTag = AdminConfig.ADMIN_TAG_1.trim();
					}
					else if (admin.getAccessLevel() == 2) {
						gmTag = AdminConfig.ADMIN_TAG_2.trim();
					}
					else if (admin.getAccessLevel() == 3) {
						gmTag = AdminConfig.ADMIN_TAG_3.trim();
					}
					else if (admin.getAccessLevel() == 4) {
						gmTag = AdminConfig.ADMIN_TAG_4.trim();
					}
					else if (admin.getAccessLevel() == 5) {
						gmTag = AdminConfig.ADMIN_TAG_5.trim();
					}
					else if (admin.getAccessLevel() == 6) {
						gmTag = AdminConfig.ADMIN_TAG_6.trim();
					}
					else if (admin.getAccessLevel() == 7) {
						gmTag = AdminConfig.ADMIN_TAG_7.trim();
					}
					else if (admin.getAccessLevel() == 8) {
						gmTag = AdminConfig.ADMIN_TAG_8.trim();
					}
					else if (admin.getAccessLevel() == 9) {
						gmTag = AdminConfig.ADMIN_TAG_9.trim();
					}
					else if (admin.getAccessLevel() == 10) {
						gmTag = AdminConfig.ADMIN_TAG_10.trim();
					}*/
					PacketSendUtility.sendMessage(player, String.format(admin.getName()));
				}
			PacketSendUtility.sendMessage(player, "====================");
			}
		else {
			PacketSendUtility.sendMessage(player, "Es ist kein Support online!");
		}
	}
}