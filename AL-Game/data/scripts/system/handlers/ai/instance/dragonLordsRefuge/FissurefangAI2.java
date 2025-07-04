package ai.instance.dragonLordsRefuge;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;


/**
 * @author Cheatkiller
 *
 */
@AIName("fissurefang")
public class FissurefangAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> skillTask;

	@Override
	protected void handleAttack(Creature creature){
		super.handleAttack(creature);
		if(isHome.compareAndSet(true, false))
			startSkillTask();
		isDeadGod();
	}

	private void startSkillTask()	{
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run()	{
				if (isAlreadyDead())
					cancelTask();
				else {
					sinkEvent();
				}
			}
		}, 5000, 30000);
	}
	
	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}
	
	private void sinkEvent() {
  	int rand = Rnd.get(0, 1);
  	int npc = rand == 0 ? 282735 : 282737;
  	int skill = npc == 282735 ? 20718 : 20172;
  	if(skill == 20172)
  		SkillEngine.getInstance().getSkill(getOwner(), 20476, 55, getOwner()).useNoAnimationSkill();
  	SkillEngine.getInstance().getSkill(getOwner(), skill, 55, getOwner()).useNoAnimationSkill();
  	for (Player player : getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 30)) {
				spawn(npc, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
	}
  
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
	}
	
	private boolean isDeadGod() {
		Npc marcutan = getNpc(219567);
		Npc kaisinel = getNpc(219564);
		if (isDead(marcutan) || isDead(kaisinel)) {
			AI2Actions.useSkill(this, 20983);
			return true;
		}
		return false;
	}
	
	private boolean isDead(Npc npc) {
		return (npc != null && npc.getLifeStats().isAlreadyDead());
	}
	
	private Npc getNpc(int npcId) {
		return getPosition().getWorldMapInstance().getNpc(npcId);
	}
}