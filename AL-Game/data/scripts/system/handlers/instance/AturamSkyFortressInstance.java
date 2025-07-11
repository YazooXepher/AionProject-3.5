package instance;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import java.util.Map;

/**
 * @author xTz
 */
@InstanceID(300240000)
public class AturamSkyFortressInstance extends GeneralInstanceHandler {

	private Map<Integer, StaticDoor> doors;
	private boolean isInstanceDestroyed;
	private boolean msgIsSended;
	private int officerKilled;
	private int chiefKilled;
	private int generators;

	@Override
	public void onDie(Npc npc) {
		if (isInstanceDestroyed) {
			return;
		}

		switch (npc.getNpcId()) {
			case 700981:
				spawn(282281, 524.1896f, 489.7742f, 649.916f, (byte) 34);
				break;
			case 700982:
				spawn(282279, 467.7094f, 465.6622f, 647.93896f, (byte) 40);
				break;
			case 700983:
				spawn(282278, 449.5576f, 420.7812f, 652.9143f, (byte) 89);
				doors.get(68).setOpen(true);
				break;
			case 700984:
				spawn(282280, 581.1f, 401.3544f, 648.6401f, (byte) 9);
				break;
			case 700985:
				spawn(282277, 572.8088f, 459.4094f, 647.93896f, (byte) 15);
				break;
			case 217373:
				doors.get(26).setOpen(true);
				spawn(730375, 374.85f, 424.32f, 653.52f, (byte) 0);
				break;
			case 701043:
				despawnNpc(npc);
				despawnNpc(instance.getNpc(701030));
				sendMsg(1400909);
				break;
			case 217371:
				spawn(730374, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
				break;
			case 217370:
				officerKilled++;
				if (officerKilled == 4) {
					doors.get(174).setOpen(true);
					sendMsg(1401050);
					startOfficerWalkerEvent();
				}
				else if (officerKilled == 8) {
					doors.get(175).setOpen(true);
					startMarbataWalkerEvent();
				}
				despawnNpc(npc);
				break;
			case 217656:
				chiefKilled++;
				if (chiefKilled == 1) {
					startOfficerWalkerEvent();
				}
				else if (chiefKilled == 2) {
					doors.get(178).setOpen(true);
				}
				despawnNpc(npc);
				break;
			case 217382:
				doors.get(230).setOpen(true);
				Player player = npc.getAggroList().getMostPlayerDamage();
				if (player != null) {
					AbyssPointsService.addAp(player, 540);
				}
				sendMsg(1401048);
				break;
			case 218577:
				spawn(217382, 258.3894f, 796.7554f, 901.6453f, (byte) 80);
				break;
			case 701029:
				Npc boss = instance.getNpc(217371);
				generators++;
				if (boss != null) {
					if (generators == 1) {
						sendMsg(1400910);
					}
					else if (generators == 2) {
						boss.getEffectController().removeEffect(19406);
						SkillEngine.getInstance().getSkill(boss, 19407, 1, boss).useNoAnimationSkill();
						sendMsg(1400911);
					}
					else if (generators == 3) {
						boss.getEffectController().removeEffect(19407);
						SkillEngine.getInstance().getSkill(boss, 19408, 1, boss).useNoAnimationSkill();
						sendMsg(1400912);
					}
					else if (generators == 4) {
						boss.getEffectController().removeEffect(19408);
						SkillEngine.getInstance().getSkill(boss, 18117, 1, boss).useNoAnimationSkill();
						sendMsg(1400913);
					}
				}
				despawnNpc(npc);
				break;
			case 217369:
			case 217368:
			case 217655:
				despawnNpc(npc);
				break;
		}
	}

	private void startMarbataWalkerEvent() {
		sendMsg(1401050);
		startWalk((Npc) spawn(218577, 193.45583f, 802.1455f, 900.7575f, (byte) 103), "3002400009");
		startWalk((Npc) spawn(217655, 198.34431f, 801.4107f, 900.66125f, (byte) 110), "30024000010");
		startWalk((Npc) spawn(217655, 197.13315f, 798.7863f, 900.6499f, (byte) 110), "30024000011");
	}

	private void startWalk(final Npc npc, final String walkId) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					npc.getSpawn().setWalkerId(walkId);
					WalkManager.startWalking((NpcAI2) npc.getAi2());
					npc.setState(1);
					PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
				}
			}

		}, 2000);
	}

	private void startOfficerWalkerEvent() {
		startWalk((Npc) spawn(217655, 146.53816f, 713.5974f, 901.0108f, (byte) 111), "3002400003");
		startWalk((Npc) spawn(217655, 144.84991f, 720.9318f, 901.0604f, (byte) 96), "3002400004");
		startWalk((Npc) spawn(217655, 146.19899f, 709.60455f, 901.0078f, (byte) 110), "3002400005");
		startWalk((Npc) spawn(217656, 144.11845f, 716.8327f, 901.046f, (byte) 100), "3002400006");
		startWalk((Npc) spawn(217369, 144.96825f, 712.83344f, 901.0133f, (byte) 110), "3002400007");
		startWalk((Npc) spawn(217369, 144.75804f, 718.4293f, 901.05493f, (byte) 80), "3002400008");
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(234).setOpen(true);
		doors.get(177).setOpen(true);
		doors.get(17).setOpen(true);
		Npc npc = instance.getNpc(217371);
		if (npc != null) {
			SkillEngine.getInstance().getSkill(npc, 19406, 1, npc).useNoAnimationSkill();
		}
	}

	@Override
	public void onEnterInstance(Player player) {
		final QuestState qs = player.getQuestStateList().getQuestState(player.getRace().equals(Race.ELYOS) ? 18302 : 28302);
		if (qs != null && qs.getStatus() == QuestStatus.COMPLETE) {
			doors.get(26).setOpen(true);
		}
	}

	@Override
	public void onLeaveInstance(Player player) {
		player.getEffectController().removeEffect(19407);
		player.getEffectController().removeEffect(19408);
		player.getEffectController().removeEffect(19520);
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
			player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
		return true;
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 730398:
				player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, 5205);
				player.getLifeStats().increaseMp(SM_ATTACK_STATUS.TYPE.MP, 5205);
				sendMsg(1400927);
				despawnNpc(npc);
				break;
			case 730397:
				SkillEngine.getInstance().getSkill(npc, 19520, 51, player).useNoAnimationSkill();
				sendMsg(1400926);
				break;
			case 730410:
				doors.get(90).setOpen(true);
				break;
		}
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("SKY_FORTRESS_WAREHOUSE_ZONE_300240000")) {
			if (!msgIsSended) {
				msgIsSended = true;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401023));
			}
		}
	}

	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
}