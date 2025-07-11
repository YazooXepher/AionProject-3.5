package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Xitanium
 */
public class _1361FindingDrinkingWater extends QuestHandler {

	private final static int questId = 1361;

	public _1361FindingDrinkingWater() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestItem(182201326, questId); // Empty Bucket
		qe.registerQuestNpc(203943).addOnQuestStart(questId); // Turiel start
		qe.registerQuestNpc(203943).addOnTalkEvent(questId); // Turiel talk
		qe.registerQuestNpc(700173).addOnTalkEvent(questId); // Water tank
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		if (id != 182201326) // Empty Bucket
			return HandlerResult.UNKNOWN;
		if (!player.isInsideZone(ZoneName.get("LF2_ITEMUSEAREA_Q1361")))
			return HandlerResult.UNKNOWN;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return HandlerResult.UNKNOWN;
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);
				player.getInventory().decreaseByObjectId(itemObjId, 1);
				giveQuestItem(env, 182201327, 1);
				qs.setQuestVar(1);
				updateQuestStatus(env);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203943) // Turiel
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else if (env.getDialogId() == 1002) {
					if (giveQuestItem(env, 182201326, 1))
						return sendQuestStartDialog(env);
					else
						return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) // Reward
		{
			if (env.getDialog() == QuestDialog.START_DIALOG)
				return sendQuestDialog(env, 2375);
			else if (env.getDialogId() == 1009) {
				qs.setQuestVar(2);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return sendQuestEndDialog(env);
			}
			else
				return sendQuestEndDialog(env);
		}

		else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
			switch (targetId) {
				case 700173: { // Water Tank
					if (qs.getQuestVarById(0) == 1 && env.getDialog() == QuestDialog.USE_OBJECT) {
						return useQuestObject(env, 1, 1, true, 0, 0, 0, 182201327, 1); // reward
					}
				}
			}
		}
		return false;
	}
}