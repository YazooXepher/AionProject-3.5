package quest.greater_stigma;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author kecimis
 */
public class _4936SecretOfTheGreaterStigma extends QuestHandler {

	private final static int questId = 4936;
	private final static int[] npc_ids = { 204051, 204837 };

	/*
	 * 204051 - Vergelmir 204837 - Hresvelgr
	 */

	public _4936SecretOfTheGreaterStigma() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204051).addOnQuestStart(questId); // Vergelmir
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204051)// Vergelmir
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);

			}
			return false;
		}

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204051)// Vergelmir
			{
				return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START) {

			if (targetId == 204051 && var == 1)// Vergelmir
			{
				switch (env.getDialog()) {
					case START_DIALOG:
						return sendQuestDialog(env, 2375);
					case CHECK_COLLECTED_ITEMS:
						if (QuestService.collectItemCheck(env, true)) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						else
							return sendQuestDialog(env, 2716);
				}

			}
			else if (targetId == 204837 && var == 0)// Hresvelgr
			{
				switch (env.getDialog()) {
					case START_DIALOG:
						return sendQuestDialog(env, 1352);
					case STEP_TO_1:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return true;
				}
			}

			return false;
		}
		return false;
	}
}