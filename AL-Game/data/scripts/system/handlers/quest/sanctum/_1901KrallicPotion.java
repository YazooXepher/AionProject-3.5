package quest.sanctum;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author edynamic90
 */
public class _1901KrallicPotion extends QuestHandler {

	private final static int questId = 1901;

	public _1901KrallicPotion() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203830).addOnQuestStart(questId);
		qe.registerQuestNpc(203830).addOnTalkEvent(questId);
		qe.registerQuestNpc(798026).addOnTalkEvent(questId);
		qe.registerQuestNpc(798025).addOnTalkEvent(questId);
		qe.registerQuestNpc(203131).addOnTalkEvent(questId);
		qe.registerQuestNpc(798003).addOnTalkEvent(questId);
		qe.registerQuestNpc(203864).addOnTalkEvent(questId);
		/* qe.setQuestItemIds(182204115).add(questId); */
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 203830)// Marmeia
		{
			if (env.getDialog() == QuestDialog.START_DIALOG)
				return sendQuestDialog(env, 1011);
			else
				return sendQuestStartDialog(env);
		}
		else {
			if (targetId == 203864) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2375);
				else if (env.getDialogId() == 1009 && qs.getStatus() != QuestStatus.COMPLETE
					&& qs.getStatus() != QuestStatus.NONE) {
					qs.setQuestVar(7);
					updateQuestStatus(env);
					qs.setStatus(QuestStatus.REWARD);
					return sendQuestEndDialog(env);
				}
				else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
					if (env.getDialog() == QuestDialog.USE_OBJECT)
						return sendQuestDialog(env, 3398);
					return sendQuestEndDialog(env);
				}
			}
			else if (qs != null) {
				if (qs.getStatus() == QuestStatus.START) {
					int var = qs.getQuestVarById(0);
					switch (targetId) {
						case 798026:// Kunberunerk
							switch (env.getDialog()) {
								case START_DIALOG:
									if (var == 0)
										return sendQuestDialog(env, 1352);
									else if (var == 5)
										return sendQuestDialog(env, 3057);
								case SELECT_ACTION_1438:
									Storage inventory = player.getInventory();
									if (inventory.tryDecreaseKinah(10000)) {
										qs.setQuestVarById(0, var + 1);
										updateQuestStatus(env);
										PacketSendUtility
											.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
										return true;
									}
									else
										return sendQuestDialog(env, 1523);
								case STEP_TO_1:// oui 10000
									qs.setQuestVarById(0, var + 1);// var==1
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return true;
								case STEP_TO_2:// non
									qs.setQuestVarById(0, var + 1);// var==1
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return true;
								case STEP_TO_7:
									qs.setQuestVarById(0, var + 1);// var==5
									qs.setStatus(QuestStatus.REWARD);
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return true;
								default:
									return sendQuestStartDialog(env);
							}
						case 798025:// Mapireck
							switch (env.getDialog()) {
								case START_DIALOG:
									if (var == 1)
										return sendQuestDialog(env, 1693);
									else if (var == 4)
										return sendQuestDialog(env, 2716);
								case STEP_TO_3:
									qs.setQuestVarById(0, var + 1);// var==2
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return true;
								case STEP_TO_6:
									removeQuestItem(env, 182206000, 1);
									qs.setQuestVarById(0, var + 1);// var==5
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return true;
							}
						case 203131:// Maniparas
							switch (env.getDialog()) {
								case START_DIALOG:
									return sendQuestDialog(env, 2034);
								case STEP_TO_4:
									qs.setQuestVarById(0, var + 1);// var==3
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return true;
							}
						case 798003:// Gaphyrk
							switch (env.getDialog()) {
								case START_DIALOG:
									return sendQuestDialog(env, 2375);
								case STEP_TO_5:
									if (player.getInventory().getItemCountByItemId(182206000) == 0)
										if (!giveQuestItem(env, 182206000, 1))
											return true;
									qs.setQuestVarById(0, var + 1);// var==4
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return true;
							}
					}
				}
			}
			return false;
		}
	}
}