package quest.tiamaranta;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Luzien
 */
public class _10061HeaviestOfHearts extends QuestHandler {

	private final static int questId = 10061;
	
	private final static int[] mobs = { 218826, 218827, 218828};

	public _10061HeaviestOfHearts() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerQuestNpc(205886).addOnTalkEvent(questId);
		qe.registerQuestNpc(800019).addOnTalkEvent(questId);
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10060);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env){
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();
		
		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 205886) {
				switch (dialog) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1);
					}
				}
			}
			else if (targetId == 800019) { 
				switch (dialog) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					}
					case STEP_TO_3: {
						//TODO Protect Garnon
						QuestService.questTimerStart(env, 120);
						return defaultCloseDialog(env, 2, 3); 
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 800019) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
		int var = qs.getQuestVarById(0);
		if (var == 1) {
			checkAndUpdateVar(qs, env, targetId);
		}
		}
		return false;
	}
		
	private void checkAndUpdateVar(QuestState qs, QuestEnv env, int targetId){
		switch(targetId){
			case 218826:
				qs.setQuestVarById(1, 1);
				updateQuestStatus(env);
				isAllKilledMobs(qs, env);
				break;
			case 218827:
				qs.setQuestVarById(2, 1);
				updateQuestStatus(env);
				isAllKilledMobs(qs, env);
				break;
			case 218828:
				qs.setQuestVarById(3, 1);
				updateQuestStatus(env);
				isAllKilledMobs(qs, env);
				break;
		}
	}
	private void isAllKilledMobs(QuestState qs, QuestEnv env){
		if(qs.getQuestVarById(1) == 1 && qs.getQuestVarById(2) == 1 && qs.getQuestVarById(3) == 1){
			qs.setQuestVarById(1, 0);
			qs.setQuestVarById(2, 0);
			qs.setQuestVarById(3, 0);
			changeQuestStep(env, 1, 2, false);
		}
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				changeQuestStep(env, 3, 4, true);
				return true;
			}
		}
		return false;
	}
}