package quest.theobomos;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.item.ItemService;

/**
 * @author Wakizashi
 * @modified vlog
 */
public class _3074DangerousProbability extends QuestHandler {

	private final static int questId = 3074;
	private int reward = -1;

	public _3074DangerousProbability() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798193).addOnQuestStart(questId);
		qe.registerQuestNpc(798193).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 798193) { // Nagrunerk
				if (dialog == QuestDialog.EXCHANGE_COIN) {
					if (QuestService.startQuest(env)) {
						return sendQuestDialog(env, 1011);
					}
					else {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798193) { // Nagrunerk
				long kinahAmount = player.getInventory().getKinah();
				long angelsEye = player.getInventory().getItemCountByItemId(186000037);
				switch (dialog) {
					case EXCHANGE_COIN: {
						return sendQuestDialog(env, 1011);
					}
					case SELECT_ACTION_1011: {
						if (kinahAmount >= 1000 && angelsEye >= 1) {
							changeQuestStep(env, 0, 0, true);
							reward = 0;
							return sendQuestDialog(env, 5);
						}
						else {
							return sendQuestDialog(env, 1009);
						}
					}
					case SELECT_ACTION_1352: {
						if (kinahAmount >= 5000 && angelsEye >= 1) {
							changeQuestStep(env, 0, 0, true);
							reward = 1;
							return sendQuestDialog(env, 6);
						}
						else {
							return sendQuestDialog(env, 1009);
						}
					}
					case SELECT_ACTION_1693: {
						if (kinahAmount >= 25000 && angelsEye >= 1) {
							changeQuestStep(env, 0, 0, true);
							reward = 2;
							return sendQuestDialog(env, 7);
						}
						else {
							return sendQuestDialog(env, 1009);
						}
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798193) { // Nagrunerk
				if (dialog == QuestDialog.SELECT_NO_REWARD) {
					switch (reward) {
						case 0: {
							if (QuestService.finishQuest(env, 0)) {
								player.getInventory().decreaseKinah(1000);
								removeQuestItem(env, 186000037, 1);
								ItemService.addItem(player, 186000005, 1);
								reward = -1;
								break;
							}
						}
						case 1: {
							if (QuestService.finishQuest(env, 1)) {
								player.getInventory().decreaseKinah(5000);
								removeQuestItem(env, 186000037, 1);
								ItemService.addItem(player, 186000005, Rnd.get(1, 3));
								reward = -1;
								break;
							}
						}
						case 2: {
							if (QuestService.finishQuest(env, 2)) {
								ItemService.addItem(player, 186000005, Rnd.get(1, 6));
								player.getInventory().decreaseKinah(25000);
								removeQuestItem(env, 186000037, 1);
								reward = -1;
								break;
							}
						}
					}
					return closeDialogWindow(env);
				}
				else {
					QuestService.abandonQuest(player, questId);
				}
			}
		}
		return false;
	}
}