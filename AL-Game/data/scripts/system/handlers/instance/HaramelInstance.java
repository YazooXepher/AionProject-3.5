package instance;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Undertrey
 */
@InstanceID(300200000)
public class HaramelInstance extends GeneralInstanceHandler {

	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		if (player == null) {
			return;
		}
		switch (npc.getNpcId()) {
			case 216922:
				npc.getController().onDelete();
				PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 457));
				switch (player.getPlayerClass()) {
					case GLADIATOR:
					case TEMPLAR:
						spawn(700829, 224.137f, 268.608f, 144.898f, (byte) 90); // chest warrior
						break;
					case ASSASSIN:
					case RANGER:
						spawn(700830, 224.137f, 268.608f, 144.898f, (byte) 90); // chest scout
						break;
					case SORCERER:
					case SPIRIT_MASTER:
						spawn(700831, 224.137f, 268.608f, 144.898f, (byte) 90); // chest mage
						break;
					case CLERIC:
					case CHANTER:
						spawn(700832, 224.137f, 268.608f, 144.898f, (byte) 90); // chest cleric
						break;
				}
		}
	}
		
	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}