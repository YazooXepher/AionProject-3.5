package quest.inggison;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author VladimirZ
 */
public class _11031CanIEatIt extends QuestHandler {

	private final static int questId = 11031;

	public _11031CanIEatIt() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 798959 };
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		qe.registerQuestItem(182206724, questId);
		qe.registerQuestNpc(798959).addOnQuestStart(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		if (sendQuestNoneDialog(env, 798959, 4762))
			return true;

		final Player player = env.getPlayer();

		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (env.getTargetId() == 798959) {
				if (env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == 1009)
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		if (qs.getStatus() == QuestStatus.START) {
			switch (env.getTargetId()) {
				case 798959:
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							else if (var == 1)
								return sendQuestDialog(env, 1352);
						case CHECK_COLLECTED_ITEMS:
							if (var == 0) {
								if (QuestService.collectItemCheck(env, true)) {
									qs.setQuestVarById(0, var + 1);
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return true;
								}
								else
									return sendQuestDialog(env, 10001);
							}
						case STEP_TO_2:
							if (var == 1) {
								if (!giveQuestItem(env, 182206724, 1))
									return true;
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
							}
							return true;
					}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		if (id != 182206724)
			return HandlerResult.UNKNOWN;
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 1000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);
				removeQuestItem(env, 182206724, 1);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
			}
		}, 1000);
		return HandlerResult.SUCCESS;
	}
}