package quest.greater_stigma;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author JIEgOKOJI
 * @modified kecimis
 */
public class _4934TheShulacksStigma extends QuestHandler {

	private final static int questId = 4934;

	public _4934TheShulacksStigma() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204051).addOnQuestStart(questId); // Vergelmir start
		qe.registerQuestNpc(204211).addOnTalkEvent(questId); // Moreinen
		qe.registerQuestNpc(204285).addOnTalkEvent(questId); // Teirunerk
		qe.registerQuestNpc(700562).addOnTalkEvent(questId); //
		qe.registerQuestNpc(204051).addOnTalkEvent(questId); // Vergelmir
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		// Instanceof
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		// ------------------------------------------------------------
		// NPC Quest :
		// 0 - Vergelmir start
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204051) {
				// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
				if (env.getDialog() == QuestDialog.START_DIALOG)
					// Send HTML_PAGE_SELECT_NONE to eddit-HtmlPages.xml
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);

			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {

			switch (targetId) {
				case 204211: // Moreinen
					if (var == 0) {
						switch (env.getDialog()) {
							// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
							case START_DIALOG:
								// Send select1 to eddit-HtmlPages.xml
								return sendQuestDialog(env, 1011);
								// Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
							case STEP_TO_1:
								qs.setQuestVar(1);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
						}
					}
					// 2 / 4- Talk with Teirunerk
				case 204285:
					if (var == 1) {
						switch (env.getDialog()) {
							// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
							case START_DIALOG:
								// Send select1 to eddit-HtmlPages.xml
								return sendQuestDialog(env, 1352);
								// Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
							case STEP_TO_2:
								qs.setQuestVar(2);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
						}
					}
					else if (var == 2) {
						switch (env.getDialog()) {
							// Get HACTION_QUEST_SELECT in the eddit-HyperLinks.xml
							case START_DIALOG:
								// Send select1 to eddit-HtmlPages.xml
								return sendQuestDialog(env, 1693);
								// Get HACTION_SETPRO1 in the eddit-HyperLinks.xml
							case CHECK_COLLECTED_ITEMS:
								if (player.getInventory().getItemCountByItemId(182207102) < 1) {
									// player doesn't own required item
									return sendQuestDialog(env, 10001);
								}
								removeQuestItem(env, 182207102, 1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 10000);
						}
					}
					return false;
				case 700562:
					if (var == 2) {
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								updateQuestStatus(env);
							}
						}, 3000);
						return true;
					}
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204051) {
				if (env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == 1009)
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}