package instance;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Bobobear
 * @modified Luzien
 */
@InstanceID(300520000)
public class DragonLordsRefuge extends GeneralInstanceHandler {

    private final AtomicInteger specNpcKilled = new AtomicInteger();
    private boolean isInstanceDestroyed;
    private Race instanceRace;
    private int killedCount;
    private Future<?> failTask;

    @Override
    public void onDie(Npc npc) {
	    if (isInstanceDestroyed) {
		    return;
	    }

	    int npcId = npc.getNpcId();

	    switch (npcId) {
		    case 219413: //fissurefang
			    despawnNpc(219413); //despawn fissurefang corpse
			    performSkillToTarget(219409, 219409, 20979); //remove Fissure Buff
			    sendMsg(1401533);
			    checkIncarnationKills();
			    break;
		    case 219414: //graviwing
			    despawnNpc(219414); //despawn graviwing corpse
			    performSkillToTarget(219409, 219409, 20981); //remove Gravity Buff
			    sendMsg(1401535);
			    checkIncarnationKills();
			    break;
		    case 219415: //wrathclaw
			    despawnNpc(219415); //despawn wrathclaw corpse
			    performSkillToTarget(219409, 219409, 20980); //remove Wrath Buff
			    sendMsg(1401534);
			    checkIncarnationKills();
			    break;
		    case 219416: //petriscale
			    despawnNpc(219416); //despawn petriscale corpse
			    performSkillToTarget(219409, 219409, 20982); //remove Petrification Buff 
			    sendMsg(1401536);
			    checkIncarnationKills();
			    break;
		    case 730695:
			instance.getNpc(219407).getEffectController().removeEffect(20590);
			    break;
		    case 730696:
			    instance.getNpc(219407).getEffectController().removeEffect(20591);
			    break;
		    case 219407: //Calindi Flamelord
			    despawnNpc(730694); //despawn tiamat aetheric field
			    despawnNpc(730695); //despawn Surkanas if spawned
			    despawnNpc(730696); //despawn Surkanas if spawned
			    performSkillToTarget(219408, 219408, 20919); //Transformation
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
			        @Override
			        public void run() {
				        despawnNpc(219408); //despawn tiamat woman (1st spawn)
				        spawn(219409, 466.7468f, 514.5500f, 417.4044f, (byte) 0);//tiamat dragon 2nd Spawn
				        performSkillToTarget(219409, 219409, 20975); //Fissure Buff
				        performSkillToTarget(219409, 219409, 20976); //Wrath Buff
				        performSkillToTarget(219409, 219409, 20977); //Gravity Buff
				        performSkillToTarget(219409, 219409, 20978); //Petrification Buff
				        performSkillToTarget(219409, 219409, 20984); //Unbreakable Wing (reflect)
			        }
			    }, 5000);

			    //schedule dragon lords roar skill to block all players before spawn empyrean lords
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
			        @Override
			        public void run() {
				        performSkillToTarget(219409, 219409, 20920);
			        }
			    }, 8000);

			    //spawn Kaisinel or Marchutan Gods (depends of group race)
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
			        @Override
			        public void run() {
				        spawn((instanceRace == Race.ELYOS ? 219564 : 219567), 504f, 515f, 417.405f, (byte) 60);
			        }
			    }, 15000);

			    //schedule spawn of balaur spiritualists and broadcast messages
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
			        @Override
			        public void run() {
				        sendMsg(instanceRace == Race.ELYOS ? 1401531 : 1401532);
				        //spawn balaur spritualists (will defend Internal Passages)
				        spawn(283698, 463f, 568f, 417.405f, (byte) 105);
				        spawn(283699, 545f, 568f, 417.405f, (byte) 78);
				        spawn(283700, 545f, 461f, 417.405f, (byte) 46);
				        spawn(283701, 463f, 461f, 417.405f, (byte) 17);
			        }
			    }, 40000);
			    break;
		    case 219410: //Tiamat Dragon (3rd spawn)
			    if (failTask != null && !failTask.isDone())
			    failTask.cancel(true);
			    spawn(701542, 480f, 514f, 417.405f, (byte) 0);//tiamat treasure chest reward
			    spawn(730630, 548.18683f, 514.54523f, 420f, (byte) 0, 23);
			    spawn(800430, 502.426f, 510.462f, 417.405f, (byte) 0);
			    spawn(800431, 482.872f, 514.705f, 417.405f, (byte) 0);
			    spawn(800464, 544.964f, 517.898f, 417.405f, (byte) 113);
			    spawn(800465, 545.605f, 510.325f, 417.405f, (byte) 17);
			    break;
		    case 283698: //balaur spiritualist (spawn Portal after die)
			    healEmpyreanLord(0); //heal Empyrean Lord
			    spawn(730675, 460.082f, 571.978f, 417.405f, (byte) 43); //spawn portal to tiamat incarnation
			    break;
		    case 283699:
			    healEmpyreanLord(1);
			    spawn(730676, 547.822f, 571.876f, 417.405f, (byte) 18);
			    break;
		    case 283700:
			    healEmpyreanLord(2);
			    spawn(730674, 547.909f, 456.568f, 417.405f, (byte) 103);
			    break;
		    case 283701:
			    healEmpyreanLord(3);
			    spawn(730673, 459.548f, 456.849f, 417.405f, (byte) 78);
			    break;
		    case 219409: // Tiamat Dragon (1st spawn) - Players cannot kill tiamat, they must kill 4 incanation before
			    //TODO: what to do?
			    break;
		    case 219564: // Kaisinel Gods (1st Spawn)
		    case 219567: // Marchutan Gods (1st Spawn)
			    sendMsg(1401542);
			    Npc tiamat = getNpc(219409);
			    tiamat.getController().useSkill(20983);
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
			        @Override
			        public void run() {
				        despawnNpc(219409);//despawn tiamat dragon
				        spawn(730694, 436.7526f, 513.8103f, 420.6662f, (byte) 0, 14); //re-spawn tiamat aetheric field
				        spawn(219408, 451.9700f, 514.5500f, 417.4044f, (byte) 0); //re-spawn tiamat woman to initial position
				        sendMsg(1401563);//broadcast message of instance failed
				        spawn(730630, 548.18683f, 514.54523f, 420f, (byte) 0, 23); //spawn exit
			        }
			    }, 5000);
			    //TODO: check on retail
			    break;
	    }
    }

    private void performSkillToTarget(int npcId, int targetId, int skillId) {
	    if (isSpawned(npcId) && isSpawned(targetId)) {
		    final Npc npc = getNpc(npcId);
		    final Npc target = getNpc(targetId);
		    SkillEngine.getInstance().getSkill(npc, skillId, 100, target).useSkill();
	    }
    }

    private void despawnNpc(int npcId) {
	    Npc npc = getNpc(npcId);
	    if (npc != null) {
		    npc.getController().onDelete();
	    }
    }

    private boolean isSpawned(int npcId) {
	    Npc npc = getNpc(npcId);
	    if (!isInstanceDestroyed && npc != null && !NpcActions.isAlreadyDead(npc))
		    return true;
	    return false;
    }

    private void startFinalTimer() {
	    sendMsg(1401547);//broadcast message for start time

	    failTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
		    @Override
		    public void run() {
			    if (isSpawned(219410)) {
			        despawnNpc(219410); //despawn tiamat dragon
			        spawn(730694, 436.7526f, 513.8103f, 420.6662f, (byte) 0, 14); //re-spawn tiamat aetheric field
			        spawn(219408, 451.9700f, 514.5500f, 417.4044f, (byte) 0); //re-spawn tiamat woman to initial position
			        sendMsg(1401563); //broadcast message of instance failed
			        spawn(730630, 548.18683f, 514.54523f, 420f, (byte) 0, 23); //spawn exit
			    }
		    }
	    }, 1800000);
    }

    private void healEmpyreanLord(int id) {
	    int npcId = instanceRace == Race.ELYOS ? 219564 : 219567;
	    int skill = 20993 + id;
	    Npc npc = instance.getNpc(npcId);
	    if (npc != null && !NpcActions.isAlreadyDead(npc)) {
		    SkillEngine.getInstance().getSkill(npc, skill, 60, npc).useNoAnimationSkill(); //heal 7% + def buff
		    sendMsg(1401551);
	    }
    }

    private void checkIncarnationKills() {
	    killedCount = specNpcKilled.incrementAndGet();
	    if (killedCount == 4) {
		    if (!isSpawned(219409)) {
			    return;
		    }
		    Npc npc = getNpc(219409);
		    final int npcId = instanceRace == Race.ELYOS ? 219564 : 219567;
		    final int msg = instanceRace == Race.ELYOS ? 1401540 : 1401541;
		    npc.getEffectController().removeEffect(20984);// dispel Unbreakable Wing (reflect)
		    sendMsg(1401537);
		    //schedule spawn of empyrean lords for final attack to tiamat before became exausted
		    ThreadPoolManager.getInstance().schedule(new Runnable() {
			    @Override
			    public void run() {
			        if (isSpawned(npcId)) {
				        despawnNpc(npcId);
				        spawn(npcId + 1, 528f, 514f, 417.405f, (byte) 60);
			        }
			    }
		    }, 30000);
		    //schedule spawn of Tiamat 3rd Spawn
		    ThreadPoolManager.getInstance().schedule(new Runnable() {
			    @Override
			    public void run() {
				    if (isSpawned(npcId + 1)) {
					    spawn(283331, 461f, 514f, 417.405f, (byte) 0);
					    spawn(283328, 461f, 514f, 417.405f, (byte) 0);
					    spawn(219410, 461f, 514f, 417.405f, (byte) 0);
					    ThreadPoolManager.getInstance().schedule(new Runnable() {
						    @Override
						    public void run() {
						        despawnNpc(283328);
						        despawnNpc(283331);
						        despawnNpc(219409);
						        sendMsg(msg);
						        ThreadPoolManager.getInstance().schedule(new Runnable() {
							        @Override
							        public void run() {
								        startFinalTimer();
							        }
						        }, 10000);
						    }
					    }, 2000);
				    }
			    }
		    }, 40000);
	    }
    }

    @Override
    public void onInstanceDestroy() {
	    isInstanceDestroyed = true;
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
	    super.onInstanceCreate(instance);
	    killedCount = 0;
    }

    @Override
    public boolean onDie(final Player player, Creature lastAttacker) {
	    PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
			    : lastAttacker.getObjectId()), true);

	    PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
	    return true;
    }

    @Override
    public void onEnterInstance(Player player) {
	    if (instanceRace == null) {
		    instanceRace = player.getRace();
	    }
    }

    @Override
    public void onExitInstance(Player player) {
	    TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
    }
}