package ai.instance.argentManor;

import ai.GeneralNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("elemental_iron_prison")
public class ElementalIronPrisonAI2 extends GeneralNpcAI2 {

	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private AtomicBoolean isStartEvent = new AtomicBoolean(false);
	private Future<?> phaseTask;
	private Future<?> aggroTask;

	@Override
	public boolean canThink() {
		return false;
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 25) {
				if (isStartEvent.compareAndSet(false, true)) {
					Npc npc = getPosition().getWorldMapInstance().getNpc(205498);
					if (npc != null) {
						NpcShoutsService.getInstance().sendMsg(npc, 1500465, npc.getObjectId(), 0, 0);
						NpcShoutsService.getInstance().sendMsg(npc, 1500464, npc.getObjectId(), 0, 10000);
					}
				}
			}
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		if (isAggred.compareAndSet(false, true)) {
			getPosition().getWorldMapInstance().getDoors().get(76).setOpen(false);
			startPhaseTask();
			aggroTask();
		}
		super.handleAttack(creature);
	}

	private void aggroTask() {
		aggroTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelAggroTask();
				}
				else {
					if (!isInRangePlayer()) {
						handleBackHome();
					}
				}
			}

		}, 2000, 2000);
	}

	private boolean isInRangePlayer() {
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 40) && !PlayerActions.isAlreadyDead(player) && getOwner().canSee(player)) {
				return true;
			}
		}
		return false;
	}

	private void startPhaseTask() {
		phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelPhaseTask();
				}
				else {
					int skill = 0;
					switch (Rnd.get(1, 4)) {
						case 1:
							skill = 19312;
							break;
						case 2:
							skill = 19313;
							break;
						case 3:
							skill = 19314;
							break;
						case 4:
							skill = 19315;
							break;
					}
					SkillEngine.getInstance().getSkill(getOwner(), skill, 60, getOwner()).useNoAnimationSkill();
				}
			}

		}, 0, 30000);
	}

	private void cancelPhaseTask() {
		if (phaseTask != null && !phaseTask.isDone()) {
			phaseTask.cancel(true);
		}
	}

	private void cancelAggroTask() {
		if (aggroTask != null && !aggroTask.isDone()) {
			aggroTask.cancel(true);
		}
	}

	@Override
	protected void handleBackHome() {
		handleFinishAttack();
		cancelAggroTask();
		cancelPhaseTask();
		getPosition().getWorldMapInstance().getDoors().get(76).setOpen(true);
		getEffectController().removeEffect(19312);
		getEffectController().removeEffect(19313);
		getEffectController().removeEffect(19314);
		getEffectController().removeEffect(19315);
		isAggred.set(false);
		super.handleBackHome();
	}

	@Override
	protected void handleDied() {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		if (instance != null) {
			instance.getDoors().get(76).setOpen(true);
			instance.getDoors().get(26).setOpen(true);
			Npc npc = instance.getNpc(701000);
			NpcActions.delete(npc);
		}
		cancelAggroTask();
		cancelPhaseTask();
		super.handleDied();
	}
	
	@Override
	public int modifyHealValue(int value) {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		int healValue = instance.getPlayersInside().size() == 12 ? 1 : 10;
		return healValue;
	}
}