package quest.reshanta;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Go to Brusthonin and meet Phyper (798300). See if the prophecy of Phyper is realized (go out from Brusthonin). A
 * summons has arrived! Go to Khrudgelmir (204253). Talk with the Arena Master, Garm (204089). Enter Underground Arena
 * Entrance (700368) and find a Shadow Judge (700963). Find Underground Arena Exit (730067) and escape from the Shadow
 * Court Dungeon. Talk with Khrudgelmir. Go to Ishalgen and talk with Munin (203550).
 * 
 * @author vlog
 */
public class _2076TheShadowSummons extends QuestHandler {

	private final static int questId = 2076;
	private final static int[] npcs = { 798300, 204253, 700369, 204089, 203550 };

	public _2076TheShadowSummons() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerOnLeaveZone(ZoneName.get("BALTASAR_HILL_VILLAGE_220050000"), questId);
		qe.registerOnDie(questId);
		qe.registerOnEnterWorld(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2701, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
		if (qs == null)
			return false;
		Npc target = (Npc) env.getVisibleObject();
		int targetId = target.getNpcId();
		int var = qs.getQuestVarById(0);
		QuestDialog dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798300: { // Phyper
					if (dialog == QuestDialog.START_DIALOG && var == 0) {
						return sendQuestDialog(env, 1011);
					}
					if (dialog == QuestDialog.STEP_TO_1) {
						return defaultCloseDialog(env, 0, 1); // 1
					}
					break;
				}
				case 204253: { // Khrudgelmir
					if (dialog == QuestDialog.START_DIALOG && var == 2) {
						return sendQuestDialog(env, 1693);
					}
					if (dialog == QuestDialog.START_DIALOG && var == 6) {
						return sendQuestDialog(env, 3057);
					}
					if (dialog == QuestDialog.STEP_TO_3) {
						removeQuestItem(env, 182205502, 1);
						return defaultCloseDialog(env, 2, 3); // 3
					}
					if (dialog == QuestDialog.SET_REWARD) {
						return defaultCloseDialog(env, 6, 6, true, false); // reward
					}
					break;
				}
				case 700369: { // Underground Arena Exit
					if (dialog == QuestDialog.USE_OBJECT && var == 5) {
						TeleportService2.teleportTo(player, 120010000, 981.6009f, 1552.97f, 210.46f);
						changeQuestStep(env, 5, 6, false); // 6
						return true;
					}
					break;
				}
				case 204089: { // Garm
					if (dialog == QuestDialog.START_DIALOG && var == 3) {
						return sendQuestDialog(env, 2034);
					}
					if (dialog == QuestDialog.STEP_TO_4) {
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(320120000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						TeleportService2.teleportTo(player, 320120000, newInstance.getInstanceId(), 591.47894f, 420.20865f,
							202.97754f);
						playQuestMovie(env, 423);
						changeQuestStep(env, 3, 5, false); // 5
						return closeDialogWindow(env);
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203550) { // Munin
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					int[] questItems = { 182205502 };
					return sendQuestEndDialog(env, questItems);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLeaveZoneEvent(QuestEnv env, ZoneName zoneName) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("BALTASAR_HILL_VILLAGE_220050000") && var == 1) {
				giveQuestItem(env, 182205502, 1);
				changeQuestStep(env, 1, 2, false); // 2
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getWorldId() == 320120000 || qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int var = qs.getQuestVarById(0);
		if (var == 5) {
			qs.setQuestVarById(0, 3); // 3
			updateQuestStatus(env);
			return true;
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int var = qs.getQuestVarById(0);
		if (var == 5) {
			qs.setQuestVarById(0, 3); // 3
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
}