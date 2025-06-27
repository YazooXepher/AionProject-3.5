package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
public class MoveEventHandler {

	/**
	 * @param npcAI
	 */
	public static final void onMoveValidate(NpcAI2 npcAI) {
		npcAI.getOwner().getController().onMove();
		TargetEventHandler.onTargetTooFar(npcAI);
		if(npcAI.getOwner().getTarget() != null && npcAI.getOwner().getObjectTemplate().getStatsTemplate().getRunSpeed() != 0) { //delete check geo when the Path Finding
		  if(!GeoService.getInstance().canSee(npcAI.getOwner(), npcAI.getOwner().getTarget()) && !MathUtil.isInRange(npcAI.getOwner(), npcAI.getOwner().getTarget(), 15))
			  npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		}
	}

	/**
	 * @param npcAI
	 */
	public static final void onMoveArrived(NpcAI2 npcAI) {
		npcAI.getOwner().getController().onMove();
		TargetEventHandler.onTargetReached(npcAI);
		if(npcAI.getOwner().getTarget() != null && npcAI.getOwner().getObjectTemplate().getStatsTemplate().getRunSpeed() != 0) { //delete check geo when the Path Finding
		  if(!GeoService.getInstance().canSee(npcAI.getOwner(), npcAI.getOwner().getTarget()) && !MathUtil.isInRange(npcAI.getOwner(), npcAI.getOwner().getTarget(), 15))
			  npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		}
	}
}