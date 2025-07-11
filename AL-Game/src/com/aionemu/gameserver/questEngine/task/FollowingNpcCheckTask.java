package com.aionemu.gameserver.questEngine.task;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author ATracer
 */
public class FollowingNpcCheckTask implements Runnable {

	private final QuestEnv env;
	private final DestinationChecker destinationChecker;

	/**
	 * @param player
	 * @param npc
	 * @param destinationChecker
	 */
	FollowingNpcCheckTask(QuestEnv env, DestinationChecker destinationChecker) {
		this.env = env;
		this.destinationChecker = destinationChecker;
	}

	@Override
	public void run() {
		final Player player = env.getPlayer();
		Npc npc = (Npc) destinationChecker.follower;
		if (player.getLifeStats().isAlreadyDead() || npc.getLifeStats().isAlreadyDead()) {
			onFail(env);
		}
		if (!MathUtil.isIn3dRange(player, npc, 50)) {
			onFail(env);
		}
		
		if (destinationChecker.check()) {
			onSuccess(env);
		}
	}

	/**
	 * Following task succeeded, proceed with quest
	 */
	private final void onSuccess(QuestEnv env) {
		stopFollowing(env);
		QuestEngine.getInstance().onNpcReachTarget(env);
	}

	/**
	 * Following task failed, abort further progress
	 */
	protected void onFail(QuestEnv env) {
		stopFollowing(env);
		QuestEngine.getInstance().onNpcLostTarget(env);
	}

	private final void stopFollowing(QuestEnv env) {
		Player player = env.getPlayer();
		Npc npc = (Npc) destinationChecker.follower;
		player.getController().cancelTask(TaskId.QUEST_FOLLOW);
		npc.getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, player);
		if(!npc.getAi2().getName().equals("following"))
			npc.getController().onDelete();
	}
}

abstract class DestinationChecker {
	protected Creature follower;
	abstract boolean check();
}

final class TargetDestinationChecker extends DestinationChecker {

	private final Creature target;

	/**
	 * @param follower
	 * @param target
	 */
	TargetDestinationChecker(Creature follower, Creature target) {
		this.follower = follower;
		this.target = target;
	}

	@Override
	boolean check() {
		return MathUtil.isIn3dRange(target, follower, 3);
	}

}

final class CoordinateDestinationChecker extends DestinationChecker {

	private final float x;
	private final float y;
	private final float z;

	/**
	 * @param follower
	 * @param x
	 * @param y
	 * @param z
	 */
	CoordinateDestinationChecker(Creature follower, float x, float y, float z) {
		this.follower = follower;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	boolean check() {
		return MathUtil.isNearCoordinates(follower, x, y, z, 3);
	}
}

final class ZoneChecker extends DestinationChecker {
	
	private final ZoneName zoneName;

	ZoneChecker(Creature follower, ZoneName zoneName) {
		this.follower = follower;
		this.zoneName = zoneName;
	}

	@Override
	boolean check() {
		return follower.isInsideZone(zoneName);
	}
}