package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author kecimis
 */

public class _4200ASuspiciousCall extends QuestHandler {

	private final static int questId = 4200;
	private final static int[] npc_ids = { 204839, 798332, 700522, 279006, 204286 };

	/*
	 * 204839 - Uikinerk 798332 - Haorunerk 700522 - Haorunerks Bag 279006 - Garkbinerk 204286 - Payrinrinerk
	 */

	public _4200ASuspiciousCall() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204839).addOnQuestStart(questId); // Uikinerk
		qe.registerQuestItem(182209097, questId); // Teleport Scroll
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204839) // Uikinerk
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
			return false;
		}

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204286)// Payrinrinerk
			{
				if (env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == 1009)
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204839)// Uikinerk
			{
				switch (env.getDialog()) {
					case START_DIALOG:
						return sendQuestDialog(env, 1003);
					case SELECT_ACTION_1011:
						return sendQuestDialog(env, 1011);
					case STEP_TO_1:
						// Create instance
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300100000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						// teleport to cell in steel rake: 300100000 403.55 508.11 885.77 0
						TeleportService2.teleportTo(player, 300100000, newInstance.getInstanceId(), 403.55f, 508.11f, 885.77f);
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return true;
				}
			}
			else if (targetId == 798332 && var == 1) // Haorunerk
			{
				switch (env.getDialog()) {
					case START_DIALOG:
						return sendQuestDialog(env, 1352);
					case SELECT_ACTION_1353:
						playQuestMovie(env, 431);
						break;
					case STEP_TO_2:
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
				}
			}
			else if (targetId == 700522 && var == 2) // Haorunerks Bag, loc: 401.24 503.19 885.76 119
			{
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						updateQuestStatus(env);
					}
				}, 3000);
				return true;
			}
			else if (targetId == 279006 && var == 3)// Garkbinerk
			{
				switch (env.getDialog()) {
					case START_DIALOG:
						return sendQuestDialog(env, 2034);
					case SET_REWARD:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (id != 182209097 || qs == null)
			return HandlerResult.UNKNOWN;
		
		if (qs.getQuestVarById(0) != 2)
			return HandlerResult.FAILED;

		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);
				removeQuestItem(env, 182209097, 1);
				// teleport location(BlackCloudIsland): 400010000 3419.16 2445.43 2766.54 57
				TeleportService2.teleportTo(player, 400010000, 3419.16f, 2445.43f, 2766.54f, (byte) 57);
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}