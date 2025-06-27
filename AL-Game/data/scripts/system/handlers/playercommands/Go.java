package playercommands;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
//import com.aionemu.gameserver.services.global.FFAStruct;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * @rework Imaginary, Maestros
 */
 
public class Go extends PlayerCommand {

	public Go() {
		super("go");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params.length != 0) {
			onFail(player, null);
			return;
		}
		if (player.isAttackMode()) {
			PacketSendUtility.sendMessage(player, "Nicht waehrend des Kampfes moeglich!");
			return;
		}
		if (player.getPosition().getMapId() == WorldMapType.TIAMARANTA_EYE_2.getId()) {
			PacketSendUtility.sendMessage(player, "Nicht auf einer PvP Map moeglich!");
			return;
		}
		if (player.getLevel() > 1 && player.getLevel() < 55) {
			PacketSendUtility.sendMessage(player, "Dein Level ist zu niedrig fuer dieses Kommando!");
			return;
		}/*
		if (player.isInFFAPVP() && player.getWorldId() == FFAStruct.worldId) {
			return;
		}*/

		if (player.getRace() == Race.ELYOS && !player.isInPrison()) {
			goTo(player, WorldMapType.TIAMARANTA_EYE_2.getId(), 753, 134, 1196);
		}
		else if (player.getRace() == Race.ASMODIANS && !player.isInPrison()) {
			goTo(player, WorldMapType.TIAMARANTA_EYE_2.getId(), 754, 1459, 1196);
		} else {
			return;
		}
	}

	private static void goTo(final Player player, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType())
			TeleportService2.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z);
		else
			TeleportService2.teleportTo(player, worldId, x, y, z);
	}

	private static int getInstanceId(int worldId, Player player) {
		if (player.getWorldId() == worldId) {
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
			if (registeredInstance != null)
				return registeredInstance.getInstanceId();
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, player);
		return newInstance.getInstanceId();
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: .go");
	}
}