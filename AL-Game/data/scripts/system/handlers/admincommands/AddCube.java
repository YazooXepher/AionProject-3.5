package admincommands;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * @author Kamui
 *
 */
public class AddCube extends AdminCommand {


	public AddCube() {
		super("addcube");
	}

	@Override
	public void execute(Player admin, String... params) {

		if (params.length != 1) {
			PacketSendUtility.sendMessage(admin, "Syntax: //addcube <player name>");
			return;
		}

		Player receiver = null;

		receiver = World.getInstance().findPlayer(Util.convertName(params[0]));

		if (receiver == null) {
			PacketSendUtility.sendMessage(admin, "The player "+ Util.convertName(params[0]) +" is not online.");
			return;
		}

		if (receiver != null) {
			if (receiver.getNpcExpands() < CustomConfig.BASIC_CUBE_SIZE_LIMIT) {
				CubeExpandService.expand(receiver, true);
				PacketSendUtility.sendMessage(admin, "9 cube slots successfully added to player "+receiver.getName()+"!");
				PacketSendUtility.sendMessage(receiver, "Admin "+admin.getName()+" gave you a cube expansion!");
			}
			else {
				PacketSendUtility.sendMessage(admin, "Cube expansion cannot be added to "+receiver.getName()+"!\nReason: player cube already fully expanded.");
				return;
			}
		}
	}
	
	@Override
	public void onFail(Player admin, String message) {
		PacketSendUtility.sendMessage(admin, "Syntax: //addcube <player name>");
	}
}