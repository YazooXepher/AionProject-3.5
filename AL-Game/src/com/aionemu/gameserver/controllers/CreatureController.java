package com.aionemu.gameserver.controllers;

import java.util.List;
import java.util.concurrent.Future;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.ShoutEventHandler;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Homing;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.HealType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.Skill.SkillMethod;
import com.aionemu.gameserver.taskmanager.tasks.MovementNotifyTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneUpdateService;

/**
 * This class is for controlling Creatures [npc's, players etc]
 *
 * @author -Nemesiss-, ATracer(2009-09-29), Sarynth
 * @modified by Wakizashi
 */
public abstract class CreatureController<T extends Creature> extends VisibleObjectController<Creature> {

	private static final Logger log = LoggerFactory.getLogger(CreatureController.class);
	private FastMap<Integer, Future<?>> tasks = new FastMap<Integer, Future<?>>().shared();
	private float healingSkillBoost = 1.0f;

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object == getOwner().getTarget()) {
			getOwner().setTarget(null);
		}
	}

	/**
	 * Perform tasks on Creature starting to move
	 */
	public void onStartMove() {
		getOwner().getObserveController().notifyMoveObservers();
		notifyAIOnMove();
	}

	/**
	 * Perform tasks on Creature move in progress
	 */
	public void onMove() {
		notifyAIOnMove();
		updateZone();
	}

	/**
	 * Perform tasks on Creature stop move
	 */
	public void onStopMove() {
		notifyAIOnMove();
	}

	/**
	 * Perform tasks on Creature return at home
	 */
	public void onReturnHome() {
	}

	/**
	 * Notify everyone in knownlist about move event
	 */
	protected void notifyAIOnMove() {
		MovementNotifyTask.getInstance().add(getOwner());
	}

	/**
	 * Refresh completely zone irrespective of the current zone
	 */
	public void refreshZoneImpl() {
		getOwner().revalidateZones();
	}

	/**
	 * Zone update mask management
	 *
	 * @param mode
	 */
	public final void updateZone() {
		ZoneUpdateService.getInstance().add(getOwner());
	}

	/**
	 * Will be called by ZoneManager when creature enters specific zone
	 *
	 * @param zoneInstance
	 */
	public void onEnterZone(ZoneInstance zoneInstance) {
	}

	/**
	 * Will be called by ZoneManager when player leaves specific zone
	 *
	 * @param zoneInstance
	 */
	public void onLeaveZone(ZoneInstance zoneInstance) {
	}

	/**
	 * Perform tasks on Creature death
	 *
	 * @param lastAttacker
	 */
	public void onDie(Creature lastAttacker) {
		this.getOwner().getMoveController().abortMove();
		this.getOwner().setCasting(null);
		this.getOwner().getEffectController().removeAllEffects();
		// exception for player
		if (getOwner() instanceof Player && ((Player) getOwner()).getIsFlyingBeforeDeath()) {
			getOwner().unsetState(CreatureState.ACTIVE);
			getOwner().setState(CreatureState.FLOATING_CORPSE);
		}
		else
			this.getOwner().setState(CreatureState.DEAD);
		this.getOwner().getObserveController().notifyDeathObservers(lastAttacker);
	}

	/**
	 * Perform tasks when Creature was attacked //TODO may be pass only Skill
	 * object - but need to add properties in it
	 */
	public void onAttack(final Creature attacker, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (damage != 0 && !((getOwner() instanceof Npc) && ((Npc) getOwner()).isBoss())) {
			Skill skill = getOwner().getCastingSkill();
			if (skill != null && log != LOG.BLEED && log != LOG.SPELLATK && log != LOG.POISON) {
				if (skill.getSkillMethod() == SkillMethod.ITEM)
					cancelCurrentSkill();
				else {
					int cancelRate = skill.getSkillTemplate().getCancelRate();
					if (cancelRate == 100000)
						cancelCurrentSkill();
					else if (cancelRate > 0) {
						int conc = getOwner().getGameStats().getStat(StatEnum.CONCENTRATION, 0).getCurrent();
						float maxHp = getOwner().getGameStats().getMaxHp().getCurrent();

						float cancel = ((7f * (damage / maxHp) * 100f) - conc / 2f) * (cancelRate / 100f);
						if (Rnd.get(100) < cancel)
							cancelCurrentSkill();
					}
				}
			}
		}

		// Do NOT notify attacked observers if the damage is 0 and shield is up (means the attack has been absorbed)
		if (damage == 0 && getOwner().getEffectController().isUnderShield()) {
			notifyAttack = false;
		}
		if (notifyAttack) {
			getOwner().getObserveController().notifyAttackedObservers(attacker);
		}
		
		// Reduce the damage to exactly what is required to ensure death.
		// - Important that we don't include 7k worth of damage when the
		// creature only has 100 hp remaining. (For AggroList dmg count.)
		if (damage > getOwner().getLifeStats().getCurrentHp())
			damage = getOwner().getLifeStats().getCurrentHp() + 1;
		
		getOwner().getAggroList().addDamage(attacker, damage);
		getOwner().getLifeStats().reduceHp(damage, attacker);

		
		if (getOwner() instanceof Npc) {
			AI2 ai = getOwner().getAi2();
			if (ai.poll(AIQuestion.CAN_SHOUT)) {
				if (attacker instanceof Player)
					ShoutEventHandler.onHelp((NpcAI2) ai, attacker);
				else
					ShoutEventHandler.onEnemyAttack((NpcAI2) ai, attacker);
			}
		}
		else if (getOwner() instanceof Player && attacker instanceof Npc) {
			AI2 ai = attacker.getAi2();
			if (ai.poll(AIQuestion.CAN_SHOUT))
				ShoutEventHandler.onAttack((NpcAI2) ai, getOwner());
		}
		getOwner().incrementAttackedCount();

		// notify all NPC's around that creature is attacking me
		getOwner().getKnownList().doOnAllNpcs(new Visitor<Npc>() {
			@Override
			public void visit(Npc object) {
				object.getAi2().onCreatureEvent(AIEventType.CREATURE_NEEDS_SUPPORT, getOwner());
			}

		});
	}

	/**
	 * Perform tasks when Creature was attacked
	 */
	public final void onAttack(Creature creature, int skillId, final int damage, boolean notifyAttack) {
		this.onAttack(creature, skillId, TYPE.REGULAR, damage, notifyAttack, LOG.REGULAR);
	}

	public final void onAttack(Creature creature, final int damage, boolean notifyAttack) {
		this.onAttack(creature, 0, TYPE.REGULAR, damage, notifyAttack, LOG.REGULAR);
	}

	/**
	 * @param hopType
	 * @param value
	 */
	public void onRestore(HealType hopType, int value) {
		switch (hopType) {
			case HP:
				getOwner().getLifeStats().increaseHp(TYPE.HP, value);
				break;
			case MP:
				getOwner().getLifeStats().increaseMp(TYPE.MP, value);
				break;
			case FP:
				getOwner().getLifeStats().increaseFp(TYPE.FP, value);
				break;
		}
	}

	/**
	 * Perform drop operation
	 *
	 * @param player
	 */
	public void doDrop(Player player) {
	}

	/**
	 * Perform reward operation
	 */
	public void doReward() {
	}

	/**
	 * This method should be overriden in more specific controllers
	 *
	 * @param player
	 */
	public void onDialogRequest(Player player) {
	}

	/**
	 * @param target
	 * @param time
	 */
	public void attackTarget(final Creature target, int time) {
		boolean addAttackObservers = true;
		/**
		 * Check all prerequisites
		 */
		if (target == null || !getOwner().canAttack() || getOwner().getLifeStats().isAlreadyDead()
				|| !getOwner().isSpawned()) {
			return;
		}

		/**
		 * Calculate and apply damage
		 */
		int attackType = 0;
		List<AttackResult> attackResult;
		if (getOwner() instanceof Homing) {
			attackResult = AttackUtil.calculateHomingAttackResult(getOwner(), target, getOwner().getAttackType() .getMagicalElement());
			attackType = 1;
		}
		else {
			if (getOwner().getAttackType() == ItemAttackType.PHYSICAL)
				attackResult = AttackUtil.calculatePhysicalAttackResult(getOwner(), target);
		  else {
		  	attackResult = AttackUtil.calculateMagicalAttackResult(getOwner(), target, getOwner().getAttackType().getMagicalElement());
				attackType = 1;
		  }
		}
			
		 
		

		int damage = 0;
		for (AttackResult result : attackResult) {
			if (result.getAttackStatus() == AttackStatus.RESIST || result.getAttackStatus() == AttackStatus.DODGE)
				addAttackObservers = false;
			damage += result.getDamage();
		}

		PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_ATTACK(getOwner(), target, getOwner().getGameStats()
				.getAttackCounter(), time, attackType, attackResult));

		getOwner().getGameStats().increaseAttackCounter();
		if (addAttackObservers)
			getOwner().getObserveController().notifyAttackObservers(target);
		
		final Creature creature = getOwner();
		if (time == 0) {
			target.getController().onAttack(getOwner(), damage, true);
		}
		else {
			ThreadPoolManager.getInstance().schedule(new DelayedOnAttack(target, creature, damage), time);
		}
	}

	/**
	 * Stops movements
	 */
	public void stopMoving() {
		Creature owner = getOwner();
		World.getInstance().updatePosition(owner, owner.getX(), owner.getY(), owner.getZ(), owner.getHeading());
		PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
	}

	/**
	 * Handle Dialog_Select
	 *
	 * @param dialogId
	 * @param player
	 * @param questId
	 */
	public void onDialogSelect(int dialogId, Player player, int questId, int extendedRewardIndex) {
		// TODO Auto-generated method stub
	}

	/**
	 * @param taskId
	 * @return
	 */
	public Future<?> getTask(TaskId taskId) {
		return tasks.get(taskId.ordinal());
	}

	/**
	 * @param taskId
	 * @return
	 */
	public boolean hasTask(TaskId taskId) {
		return tasks.containsKey(taskId.ordinal());
	}

	/**
	 * @param taskId
	 * @return
	 */
	public boolean hasScheduledTask(TaskId taskId) {
		Future<?> task = tasks.get(taskId.ordinal());
		return task != null ? !task.isDone() : false;
	}

	/**
	 * @param taskId
	 */
	public Future<?> cancelTask(TaskId taskId) {
		Future<?> task = tasks.remove(taskId.ordinal());
		if (task != null) {
			task.cancel(false);
		}
		return task;
	}

	/**
	 * If task already exist - it will be canceled
	 *
	 * @param taskId
	 * @param task
	 */
	public void addTask(TaskId taskId, Future<?> task) {
		cancelTask(taskId);
		tasks.put(taskId.ordinal(), task);
	}

	/**
	 * Cancel all tasks associated with this controller (when deleting object)
	 */
	public void cancelAllTasks() {
		for (int i : tasks.keySet()) {
			Future<?> task = tasks.get(i);
			if (task != null && i != TaskId.RESPAWN.ordinal()) {
				task.cancel(false);
			}
		}
		tasks.clear();
	}

	@Override
	public void delete() {
		cancelAllTasks();
		super.delete();
	}

	/**
	 * Die by reducing HP to 0
	 */
	public void die() {
		getOwner().getLifeStats().reduceHp(getOwner().getLifeStats().getCurrentHp() + 1, getOwner());
	}

	/**
	 * Use skill with default level 1
	 */
	public final boolean useSkill(int skillId) {
		return useSkill(skillId, 1);
	}

	/**
	 * @param skillId
	 * @param skillLevel
	 * @return true if successful usage
	 */
	public boolean useSkill(int skillId, int skillLevel) {
		try {
			Creature creature = getOwner();
			Skill skill = SkillEngine.getInstance().getSkill(creature, skillId, skillLevel, creature.getTarget());
			if (skill != null) {
				return skill.useSkill();
			}
		}
		catch (Exception ex) {
			log.error("Exception during skill use: " + skillId, ex);
		}
		return false;
	}

	/**
	 * Notify hate value to all visible creatures
	 *
	 * @param value
	 */
	public void broadcastHate(int value) {
		for (VisibleObject visibleObject : getOwner().getKnownList().getKnownObjects().values())
			if (visibleObject instanceof Creature)
				((Creature) visibleObject).getAggroList().notifyHate(getOwner(), value);
	}

	public void abortCast() {
		Creature creature = getOwner();
		Skill skill = creature.getCastingSkill();
		if (skill == null)
			return;
		creature.setCasting(null);
		if (creature.getSkillNumber() > 0)
			creature.setSkillNumber(creature.getSkillNumber() - 1);
	}

	/**
	 * Cancel current skill and remove cooldown
	 */
	public void cancelCurrentSkill() {
		if (getOwner().getCastingSkill() == null) {
			return;
		}

		Creature creature = getOwner();
		Skill castingSkill = creature.getCastingSkill();
		castingSkill.cancelCast();
		creature.removeSkillCoolDown(castingSkill.getSkillTemplate().getCooldownId());
		creature.setCasting(null);
		PacketSendUtility.broadcastPacketAndReceive(creature, new SM_SKILL_CANCEL(creature, castingSkill.getSkillTemplate()
				.getSkillId()));
		if (getOwner().getAi2() instanceof NpcAI2) {
			NpcAI2 npcAI = (NpcAI2) getOwner().getAi2();
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.onGeneralEvent(AIEventType.ATTACK_COMPLETE);
			if (creature.getSkillNumber() > 0)
				creature.setSkillNumber(creature.getSkillNumber() - 1);
		}
	}

	/**
	 * Cancel use Item
	 */
	public void cancelUseItem() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDespawn() {
		cancelTask(TaskId.DECAY);

		Creature owner = getOwner();
		if (owner == null || !owner.isSpawned()) {
			return;
		}
		owner.getAggroList().clear();
		owner.getObserveController().clear();
	}

	private static final class DelayedOnAttack implements Runnable {

		private Creature target;
		private Creature creature;
		private int finalDamage;

		public DelayedOnAttack(Creature target, Creature creature, int finalDamage) {
			this.target = target;
			this.creature = creature;
			this.finalDamage = finalDamage;
		}

		@Override
		public void run() {
			target.getController().onAttack(creature, finalDamage, true);
			target = null;
			creature = null;
		}

	}

	public float getHealingSkillsBoost() {
		return healingSkillBoost;
	}

	public void setHealingSkillsBoost(float value) {
		this.healingSkillBoost = value;
	}

	@Override
	public void onAfterSpawn() {
		super.onAfterSpawn();
		getOwner().revalidateZones();
	}
}