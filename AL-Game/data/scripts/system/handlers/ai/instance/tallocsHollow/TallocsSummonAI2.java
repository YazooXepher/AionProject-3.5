package ai.instance.tallocsHollow;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM_IN_SUMMON;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xTz
 */
@AIName("tallocssummon")
public class TallocsSummonAI2 extends NpcAI2 {

	private AtomicBoolean isTransformed = new AtomicBoolean(false);

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 59 && isTransformed.compareAndSet(false, true)) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			if (player.getSummon() != null) { // to do remove
				PacketSendUtility.sendMessage(player, "please dismiss your summon first.");
				return true;
			}

			Summon summon = new Summon(getObjectId(), new SummonController(), getSpawnTemplate(), getObjectTemplate(), getObjectTemplate().getLevel(), 0);
			player.setSummon(summon);
			summon.setMaster(player);
			summon.setTarget(player.getTarget());
			summon.setKnownlist(getKnownList());
			summon.setEffectController(new EffectController(summon));
			summon.setPosition(getPosition());
			summon.setLifeStats(getLifeStats());
			PacketSendUtility.sendPacket(player, new SM_TRANSFORM_IN_SUMMON(player, getObjectId()));
			PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(getObjectId(), 0, 38, 0));
			summon.setState(1);
			PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, EmotionType.START_EMOTE2, 0, summon.getObjectId()));
		}
		return true;
	}

	@Override
	protected void handleDialogStart(Player player) {
		if (!isTransformed.get()) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
		}
	}
}