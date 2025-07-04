package ai.instance.tiamatStrongHold;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author Cheatkiller
 *
 */
@AIName("muragan")
public class MuraganAI2 extends GeneralNpcAI2 {

	private boolean isMove;
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if(getOwner().getNpcId() == 800438) {
			NpcShoutsService.getInstance().sendMsg(getOwner(), 390852, getOwner().getObjectId(), 0, 1000);
		}
	}
	
    @Override
    protected void handleCreatureSee(Creature creature) {
        checkDistance(this, creature);
    }
  
    @Override
    protected void handleCreatureMoved(Creature creature) {
        checkDistance(this, creature);
    }

    private void checkDistance(NpcAI2 ai, Creature creature) {
  	    if (creature instanceof Player) {
  		    if (MathUtil.isIn3dRange(getOwner(), creature, 15) && !isMove) {
  			    isMove = true;
  			    openSuramaDoor();
  			    startWalk((Player) creature);
  		    }
  	    }
    }
  
    private void startWalk(final Player player) {
  	    int owner = getOwner().getNpcId();
  	    if (owner == 800436 || owner == 800438)
  		    return;
  	    switch(owner) {
  		    case 800435:
  			    NpcShoutsService.getInstance().sendMsg(getOwner(), 390837, getOwner().getObjectId(), 0, 0);
  			    NpcShoutsService.getInstance().sendMsg(getOwner(), 390838, getOwner().getObjectId(), 0, 4000);
  			    killGuardCaptain();
  			   break;
  	    }
  	    setStateIfNot(AIState.WALKING);
  	    getOwner().setState(1);
		getMoveController().moveToPoint(838, 1317, 396);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
		ThreadPoolManager.getInstance().schedule(new Runnable() {

		    @Override
		    public void run() {
		  	    forQuest(player);
		  	    AI2Actions.deleteOwner(MuraganAI2.this);
		    }
	    }, 10000);
	}
  
    private void openSuramaDoor() {
  	    if (getOwner().getNpcId() == 800436) {
  		    NpcShoutsService.getInstance().sendMsg(getOwner(), 390835, getOwner().getObjectId(), 0, 0);
			getPosition().getWorldMapInstance().getDoors().get(56).setOpen(true);
			AI2Actions.deleteOwner(this);
  	    }
    }
  
    private void killGuardCaptain() {
  	    WorldMapInstance instance = getOwner().getPosition().getWorldMapInstance();
  	    for (Npc npc : instance.getNpcs()) {
  		    if (npc.getNpcId() == 219440) {
  			    spawn(283448, npc.getX(), npc.getY(), npc.getZ(), (byte) npc.getHeading());
  			    npc.getController().onDelete();
  		    }
  	    }
    }
  
    private void forQuest(Player player) {
  	    int quest = player.getRace().equals(Race.ELYOS) ? 30708 : 30758;
  	    final QuestState qs = player.getQuestStateList().getQuestState(quest);
  	    if (qs != null && qs.getQuestVarById(0) != 5) {
  		    qs.setQuestVar(qs.getQuestVarById(0) + 1);
  		    PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(quest, qs.getStatus(), qs.getQuestVars().getQuestVars()));
  	    }
    }
}