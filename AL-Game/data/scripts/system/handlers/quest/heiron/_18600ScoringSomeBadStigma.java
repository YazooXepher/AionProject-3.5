package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Report-To-Quest Start: Perento (204500) Take the paper voucher (182213000) to Koruchinerk (798321) Go to New Heiron
 * Gate and meet Herthia (205228) Bring the Fake Stigma (182213001) to Perento
 * 
 * @author vlog
 * @modified Gigi
 */
public class _18600ScoringSomeBadStigma extends QuestHandler {

	private final static int _questId = 18600;
	private final static int[] _npcs = { 204500, 798321, 205228 };

	public _18600ScoringSomeBadStigma() {
		super(_questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204500).addOnQuestStart(_questId);
		for (int npc_id : _npcs)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(_questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(_questId);

		if (targetId == 204500) // Perento
		{
			if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else if (env.getDialogId() == 1002) {
					giveQuestItem(env, 182213000, 1);
					return sendQuestStartDialog(env);
				}
				else
					return sendQuestStartDialog(env);
			}
			if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 2375);
				else if (env.getDialogId() == 1009) {
					removeQuestItem(env, 182213001, 1);
					return sendQuestEndDialog(env);
				}
				else
					return sendQuestEndDialog(env);
			}
		}
		if (targetId == 798321) // Koruchinerk
		{
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1352);
				if (env.getDialog() == QuestDialog.STEP_TO_1)
					return defaultCloseDialog(env, 0, 1, 0, 0, 182213000, 1); // 1
			}
		}
		if (targetId == 205228) // Herthia
		{
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1693);
				else if (env.getDialog() == QuestDialog.STEP_TO_2) {
					qs.setQuestVar(3);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
			}
		}
		return false;
	}
}