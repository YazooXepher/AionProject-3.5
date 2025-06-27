package ai.instance.tiamatStrongHold;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;

/**
 * @author Cheatkiller
 *
 */
@AIName("distortedspace")
public class DistortedSpaceAI2 extends NpcAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        useskill();
    }

    private void useskill() {
  	    ThreadPoolManager.getInstance().schedule(new Runnable() {
  		    @Override
  		    public void run() {
  			    AI2Actions.useSkill(DistortedSpaceAI2.this, 20742);
  			    getOwner().getController().die();
  		    }
  	    },10000);
    }
}