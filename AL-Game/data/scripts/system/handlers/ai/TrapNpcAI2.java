package ai;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 */
@AIName("trap")
public class TrapNpcAI2 extends NpcAI2 {
	public static int EVENT_SET_TRAP_RANGE = 1;
	
	private int trapRange = 0;
	private Future<?> despawnTask;
	
	@Override
	protected void handleCreatureMoved(Creature creature) {
		tryActivateTrap(creature);
	}

	@Override
	protected void handleSpawned() {
		getKnownList().doUpdate();
		getKnownList().doOnAllObjects(new Visitor<VisibleObject>() {

			@Override
			public void visit(VisibleObject object) {
				if (!(object instanceof Creature))
					return;
				Creature creature = (Creature) object;
				tryActivateTrap(creature);
			}
		});
		super.handleSpawned();
	}

	private void tryActivateTrap(Creature creature) {
		if (despawnTask != null)
			return;
		
		if (!creature.getLifeStats().isAlreadyDead() && isInRange(creature, trapRange)) {

			Creature creator = (Creature) getCreator();
			if (!creator.isEnemy(creature))
				return;
			
			if (setStateIfNot(AIState.FIGHT)) {
				AI2Actions.targetCreature(this, creature);
				AI2Actions.useSkill(this, getSkillList().getRandomSkill().getSkillId());
				despawnTask = ThreadPoolManager.getInstance().schedule(new TrapDelete(this), 5000);
			}
		}
	}
	
	@Override
	protected void handleCustomEvent(int eventId, Object... args) {
		if(eventId == EVENT_SET_TRAP_RANGE)
			trapRange = (Integer) args[0];
	}

	@Override
	public boolean isMoveSupported() {
		return false;
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}

	private static final class TrapDelete implements Runnable {

		private TrapNpcAI2 ai;

		TrapDelete(TrapNpcAI2 ai) {
			this.ai = ai;
		}

		@Override
		public void run() {
			AI2Actions.deleteOwner(ai);
			ai = null;
		}
	}
}