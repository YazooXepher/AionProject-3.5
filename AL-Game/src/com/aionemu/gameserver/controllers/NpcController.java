package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.common.service.PlayerTeamDistributionService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.DialogService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import java.util.Collection;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is for controlling Npc's
 *
 * @author -Nemesiss-, ATracer (2009-09-29), Sarynth modified by Wakizashi
 */
public class NpcController extends CreatureController<Npc> {

	private static final Logger log = LoggerFactory.getLogger(NpcController.class);

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object instanceof Creature) {
			getOwner().getAi2().onCreatureEvent(AIEventType.CREATURE_NOT_SEE, (Creature) object);
			getOwner().getAggroList().remove((Creature) object);
		}
		// TODO not see player ai event
	}

	@Override
	public void see(VisibleObject object) {
		super.see(object);
		Npc owner = getOwner();
		if (object instanceof Creature) {
			owner.getAi2().onCreatureEvent(AIEventType.CREATURE_SEE, (Creature) object);
		}
		if (object instanceof Player) {
			// TODO see player ai event
			if (owner.getLifeStats().isAlreadyDead())
				DropService.getInstance().see((Player) object, owner);
		}
		else if (object instanceof Summon) {
			// TODO see summon ai event
		}
	}

	@Override
	public void onBeforeSpawn() {
		super.onBeforeSpawn();
		Npc owner = getOwner();

		// set state from npc templates
		if (owner.getObjectTemplate().getState() != 0)
			owner.setState(owner.getObjectTemplate().getState());
		else
			owner.setState(CreatureState.NPC_IDLE);

		owner.getLifeStats().setCurrentHpPercent(100);
		owner.getAi2().onGeneralEvent(AIEventType.RESPAWNED);

		if (owner.getSpawn().canFly()) {
			owner.setState(CreatureState.FLYING);
		}
		if (owner.getSpawn().getState() != 0) {
			owner.setState(owner.getSpawn().getState());
		}
	}

	@Override
	public void onAfterSpawn() {
		super.onAfterSpawn();
		getOwner().getAi2().onGeneralEvent(AIEventType.SPAWNED);
	}

	@Override
	public void onDespawn() {
		Npc owner = getOwner();
		DropService.getInstance().unregisterDrop(getOwner());
		owner.getAi2().onGeneralEvent(AIEventType.DESPAWNED);
		super.onDespawn();
	}

	@Override
	public void onDie(Creature lastAttacker) {
		Npc owner = getOwner();
		if (owner.getSpawn().hasPool()) {
			owner.getSpawn().setUse(false);
		}
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.DIE, 0,
				owner.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()));

		try {
			if (owner.getAi2().poll(AIQuestion.SHOULD_REWARD))
				this.doReward();
			owner.getPosition().getWorldMapInstance().getInstanceHandler().onDie(owner);
			owner.getAi2().onGeneralEvent(AIEventType.DIED);
		}
		finally { // always make sure npc is schedulled to respawn
			if (owner.getAi2().poll(AIQuestion.SHOULD_DECAY)) {
				addTask(TaskId.DECAY, RespawnService.scheduleDecayTask(owner));
			}
			if (owner.getAi2().poll(AIQuestion.SHOULD_RESPAWN) && !owner.isDeleteDelayed()
					&& !SiegeService.getInstance().isSiegeNpcInActiveSiege(owner)) {
				Future<?> task = scheduleRespawn();
				if (task != null) {
					addTask(TaskId.RESPAWN, task);
				}
			}
			else if (!hasScheduledTask(TaskId.DECAY)) {
				onDelete();
			}
		}
		super.onDie(lastAttacker);
	}

	@Override
	public void doReward() {
		super.doReward();
		AggroList list = getOwner().getAggroList();
		Collection<AggroInfo> finalList = list.getFinalDamageList(true);
		AionObject winner = list.getMostDamage();

		if (winner == null) {
			return;
		}

		float totalDmg = 0;
		for (AggroInfo info : finalList) {
			totalDmg += info.getDamage();
		}

		if (totalDmg <= 0) {
			log.warn("WARN total damage to " + getOwner().getName() + " is " + totalDmg + " reward process was skiped!");
			return;
		}

		for (AggroInfo info : finalList) {
			AionObject attacker = info.getAttacker();

			// We are not reward Npc's
			if (attacker instanceof Npc) {
				continue;
			}

			float percentage = info.getDamage() / totalDmg;
			if (percentage > 1) {
				log.warn("WARN BIG REWARD PERCENTAGE: " + percentage + " damage: " + info.getDamage() + " total damage: "
						+ totalDmg + " name: " + info.getAttacker().getName() + " obj: " + info.getAttacker().getObjectId()
						+ " owner: " + getOwner().getName() + " player was skiped");
				continue;
			}
			if (attacker instanceof TemporaryPlayerTeam<?>) {
				PlayerTeamDistributionService.doReward((TemporaryPlayerTeam<?>) attacker, percentage, getOwner(), winner);
			}
			else if (attacker instanceof Player && ((Player) attacker).isInGroup2()) {
				PlayerTeamDistributionService.doReward(((Player) attacker).getPlayerGroup2(), percentage, getOwner(), winner);
			}
			else if (attacker instanceof Player) {
				Player player = (Player) attacker;
				if (!player.getLifeStats().isAlreadyDead()) {
					// Reward init
					long rewardXp = StatFunctions.calculateSoloExperienceReward(player, getOwner());
					int rewardDp = StatFunctions.calculateSoloDPReward(player, getOwner());
					float rewardAp = 1;

					// Dmg percent correction
					rewardXp *= percentage;
					rewardDp *= percentage;
					rewardAp *= percentage;

					QuestEngine.getInstance().onKill(new QuestEnv(getOwner(), player, 0, 0));
					player.getCommonData().addExp(rewardXp, RewardType.HUNTING, this.getOwner().getObjectTemplate().getNameId());
					player.getCommonData().addDp(rewardDp);
					if (getOwner().isRewardAP()) {
						int calculatedAp = StatFunctions.calculatePvEApGained(player, getOwner());
						rewardAp *= calculatedAp;
						if (rewardAp >= 1) {
							AbyssPointsService.addAp(player, getOwner(), (int) rewardAp);
						}
					}

					if (attacker.equals(winner)) {
						DropRegistrationService.getInstance().registerDrop(getOwner(), player, player.getLevel(), null);
					}
				}
			}
		}
	}

	@Override
	public Npc getOwner() {
		return (Npc) super.getOwner();
	}

	@Override
	public void onDialogRequest(Player player) {
		// notify npc dialog request observer
		if (!getOwner().getObjectTemplate().canInteract()) {
			return;
		}
		player.getObserveController().notifyRequestDialogObservers(getOwner());

		getOwner().getAi2().onCreatureEvent(AIEventType.DIALOG_START, player);
	}

	@Override
	public void onDialogSelect(int dialogId, final Player player, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		if (!MathUtil.isInRange(getOwner(), player, getOwner().getObjectTemplate().getTalkDistance() + 2) && !QuestEngine.getInstance().onDialog(env)) {
			return;
		}
		if (!getOwner().getAi2().onDialogSelect(player, dialogId, questId, extendedRewardIndex)) {
			DialogService.onDialogSelect(dialogId, player, getOwner(), questId, extendedRewardIndex);
		}
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead())
			return;
		final Creature actingCreature;

		// summon should gain its own aggro
		if (creature instanceof Summon)
			actingCreature = creature;
		else
			actingCreature = creature.getActingCreature();

		super.onAttack(actingCreature, skillId, type, damage, notifyAttack, log);

		Npc npc = getOwner();

		if (actingCreature instanceof Player) {
			QuestEngine.getInstance().onAttack(new QuestEnv(npc, (Player) actingCreature, 0, 0));
		}

		PacketSendUtility.broadcastPacket(npc, new SM_ATTACK_STATUS(npc, type, skillId, damage, log));
	}

	@Override
	public void onStopMove() {
		getOwner().getMoveController().setInMove(false);
		super.onStopMove();
	}

	@Override
	public void onStartMove() {
		getOwner().getMoveController().setInMove(true);
		super.onStartMove();
	}

	@Override
	public void onReturnHome() {
		if (getOwner().isDeleteDelayed()) {
			onDelete();
		}
		super.onReturnHome();
	}

	@Override
	public void onEnterZone(ZoneInstance zoneInstance) {
		if (zoneInstance.getAreaTemplate().getZoneName() == null) {
			log.error("No name found for a Zone in the map " + zoneInstance.getAreaTemplate().getWorldId());
		}
	}

	/**
	 * Schedule respawn of npc In instances - no npc respawn
	 */
	public Future<?> scheduleRespawn() {
		if (!getOwner().getSpawn().isNoRespawn()) {
			return RespawnService.scheduleRespawnTask(getOwner());
		}
		return null;
	}

	public final float getAttackDistanceToTarget() {
		return getOwner().getGameStats().getAttackRange().getCurrent() / 1000f;
	}

	@Override
	public boolean useSkill(int skillId, int skillLevel) {
		SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (!getOwner().isSkillDisabled(skillTemplate)) {
			getOwner().getGameStats().renewLastSkillTime();
			return super.useSkill(skillId, skillLevel);
		}
		return false;
	}
}