package ai.instance.tiamatStrongHold;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author Cheatkiller
 *
 */
@AIName("timeaccelerator")
public class TimeAcceleratorAI2 extends NpcAI2 {

    @Override
    protected void handleCreatureSee(Creature creature) {
        checkDistance(this, creature);
    }
  
    @Override
    protected void handleCreatureMoved(Creature creature) {
        checkDistance(this, creature);
    }

    private void checkDistance(NpcAI2 ai, Creature creature) {
    	if (MathUtil.isIn3dRange(getOwner(), creature, 5) && !creature.getEffectController().hasAbnormalEffect(20727)) {
    		AI2Actions.useSkill(this, 20727);
        }
    }
  
    @Override
    protected void handleSpawned() {
  	    super.handleSpawned();
  	    despawn();
    }
  
    private void despawn() {
  	    ThreadPoolManager.getInstance().schedule(new Runnable() {
		
  		    @Override
  		    public void run() {
  			    getOwner().getController().onDelete();
  		    }
  	    }, 20000);
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
}