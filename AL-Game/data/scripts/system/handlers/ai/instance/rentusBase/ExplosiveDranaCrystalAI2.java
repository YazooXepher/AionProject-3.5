package ai.instance.rentusBase;

import ai.ActionItemNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.world.WorldPosition;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("explosive_drana_crystal")
public class ExplosiveDranaCrystalAI2 extends ActionItemNpcAI2 {

	private AtomicBoolean isUsed = new AtomicBoolean(false);
	private Future<?> lifeTask;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		startLifeTask();
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		if (isUsed.compareAndSet(false, true)) {
			WorldPosition p = getPosition();
			Npc boss = p.getWorldMapInstance().getNpc(217308);
			if (boss != null && !NpcActions.isAlreadyDead(boss)) {
				EffectController ef = boss.getEffectController();
				if (ef.hasAbnormalEffect(19370)) {
					ef.removeEffect(19370);
				}
				else if (ef.hasAbnormalEffect(19371)) {
					ef.removeEffect(19371);
				}
				else if (ef.hasAbnormalEffect(19372)) {
					ef.removeEffect(19372);
				}
			}
			Npc npc = (Npc) spawn(282530, p.getX(), p.getY(), p.getZ(), p.getHeading());
			Npc invisibleNpc = (Npc) spawn(282529, p.getX(), p.getY(), p.getZ(), p.getHeading());
			SkillEngine.getInstance().getSkill(npc, 19373, 60, npc).useNoAnimationSkill();
			SkillEngine.getInstance().getSkill(invisibleNpc, 19654, 60, invisibleNpc).useNoAnimationSkill();
			NpcActions.delete(invisibleNpc);
			AI2Actions.deleteOwner(this);
		}
	}

	private void cancelLifeTask() {
		if (lifeTask != null && !lifeTask.isDone()) {
			lifeTask.cancel(true);
		}
	}

	private void startLifeTask() {
		lifeTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					AI2Actions.deleteOwner(null);
				}
			}
		}, 60000);
	}

	@Override
	protected void handleDied() {
		cancelLifeTask();
		super.handleDied();
	}

	@Override
	protected void handleDespawned() {
		cancelLifeTask();
		super.handleDespawned();
	}
}