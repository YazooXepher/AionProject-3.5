package quest.convent_of_marchutan;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Gigi
 */
public class _20001TravelingtoGelkmaros extends QuestHandler {

	private final static int questId = 20001;

	public _20001TravelingtoGelkmaros() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnMovieEndQuest(551, questId);
		qe.registerQuestNpc(798800).addOnTalkEvent(questId);
		qe.registerQuestNpc(798409).addOnTalkEvent(questId); // eremitia
		qe.registerQuestNpc(204202).addOnTalkEvent(questId); // machiah
		qe.registerQuestNpc(204073).addOnTalkEvent(questId); // bellia
		qe.registerQuestNpc(204283).addOnTalkEvent(questId); // jhaelas
		qe.registerQuestNpc(799225).addOnTalkEvent(questId); // sibylle
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20000, true);
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId != 551)
			return false;
		Player player = env.getPlayer();
		if (player.getCommonData().getRace() != Race.ASMODIANS)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		qs.setStatus(QuestStatus.REWARD);
		updateQuestStatus(env);
		return true;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 5) {
				if (player.getWorldId() == 220070000)
					return playQuestMovie(env, 551);
			}
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798800 && var == 0) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else if (env.getDialog() == QuestDialog.STEP_TO_1) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
			}
			if (targetId == 798409) {
				if (var == 1) {
					if (env.getDialog() == QuestDialog.START_DIALOG)
						return sendQuestDialog(env, 1352);
					else if (env.getDialog() == QuestDialog.STEP_TO_2) {
						qs.setQuestVar(++var);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					else
						return sendQuestStartDialog(env);
				}
				else if (var == 5) {
					if (env.getDialog() == QuestDialog.START_DIALOG)
						return sendQuestDialog(env, 2716);
					else if (env.getDialogId() == 10255) {
						TeleportService2.teleportTo(player, 220070000, 1, 1868, 2746, 531, (byte) 20);
						return true;
					}
					else
						return sendQuestStartDialog(env);
				}
			}
			else if (targetId == 204202 && var == 2) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1693);
				else if (env.getDialog() == QuestDialog.STEP_TO_3) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (targetId == 204073 && var == 3) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2034);
				else if (env.getDialog() == QuestDialog.STEP_TO_4) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (targetId == 204283 && var == 4) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2375);
				else if (env.getDialogId() == 10004) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799225) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2802);
				else {
					return sendQuestEndDialog(env);
				}
			}
			else if(targetId == 798409){
				if(env.getDialogId() == 10009){
					TeleportService2.teleportTo(player, 220070000, 1, 1868, 2746, 531, (byte) 20);
					return true;
				}else{
					return sendQuestDialog(env, 2802);
				}
			}
		}
		return false;
	}
}