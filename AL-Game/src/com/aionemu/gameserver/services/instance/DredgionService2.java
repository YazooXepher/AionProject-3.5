package com.aionemu.gameserver.services.instance;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import java.util.Iterator;
import javolution.util.FastList;

/**
 * 
 * @author xTz
 */
public class DredgionService2 {
	private static final Logger log = LoggerFactory.getLogger(DredgionService2.class);
	private boolean registerAvailable;
	private FastList<Integer> playersWithCooldown = new FastList<Integer>();
	private SM_AUTO_GROUP[] autoGroupUnreg, autoGroupReg;

	private final byte maskLvlGradeC = 1, maskLvlGradeB = 2, maskLvlGradeA = 3;
	private byte minGradeLvl = 45;

	public DredgionService2() {
		this.autoGroupUnreg = new SM_AUTO_GROUP[this.maskLvlGradeA +1];
		this.autoGroupReg = new SM_AUTO_GROUP[this.autoGroupUnreg.length];
		for (byte i = this.maskLvlGradeC; i <= this.maskLvlGradeA; i++) {
			this.autoGroupUnreg[i] = new SM_AUTO_GROUP(i, SM_AUTO_GROUP.wnd_EntryIcon, true);
			this.autoGroupReg[i] = new SM_AUTO_GROUP(i, SM_AUTO_GROUP.wnd_EntryIcon);
		}
	}

	public void start() {
		String[] times = AutoGroupConfig.DREDGION_TIMES.split("\\|");
		for (String cron : times) {
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startDredgionRegistration();
				}
			}, cron);
			log.info("Scheduled Dredgion: based on cron expression: " + cron + " Duration: " + AutoGroupConfig.DREDGION_TIMER + " in minutes");
		}
	}

	private void startUregisterDredgionTask() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				registerAvailable = false;
				playersWithCooldown.clear();
				AutoGroupService.getInstance().unRegisterInstance(maskLvlGradeA);
				AutoGroupService.getInstance().unRegisterInstance(maskLvlGradeB);
				AutoGroupService.getInstance().unRegisterInstance(maskLvlGradeC);
				Iterator<Player> iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext()) {
					Player player = iter.next();
					if (player.getLevel() > DredgionService2.this.minGradeLvl) {
						byte instanceMaskId = getInstanceMaskId(player);
						if (instanceMaskId > 0) {
							PacketSendUtility.sendPacket(player, DredgionService2.this.autoGroupUnreg[instanceMaskId]);
						}
					}
				}
			}
		}, AutoGroupConfig.DREDGION_TIMER * 60 * 1000);
	}

	private void startDredgionRegistration() {
		this.registerAvailable = true;
		startUregisterDredgionTask();
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext()) {
			Player player = iter.next();
			if (player.getLevel() > this.minGradeLvl) {
				byte instanceMaskId = getInstanceMaskId(player);
				if (instanceMaskId > 0) {
					PacketSendUtility.sendPacket(player, this.autoGroupReg[instanceMaskId]);
					switch (instanceMaskId) {
						case maskLvlGradeC:
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDAB1_DREADGION);
							break;
						case maskLvlGradeB:
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDDREADGION_02);				
							break;
						case maskLvlGradeA:
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDDREADGION_03);
							break;
					}
				}
			}
		}
	}

	public boolean isDredgionAvialable() {
		return this.registerAvailable;
	}

	public byte getInstanceMaskId(Player player) {
		int level = player.getLevel();
		if(level < this.minGradeLvl)
			return 0;
		
		if (level < 51) {
			return this.maskLvlGradeC;
		} else if (level < 56) {
			return this.maskLvlGradeB;
		} else {
			return this.maskLvlGradeA;
		}
	}

	public void addCoolDown(Player player) {
		this.playersWithCooldown.add(player.getObjectId());
	}

	public boolean hasCoolDown(Player player) {
		return this.playersWithCooldown.contains(player.getObjectId());
	}

	public void showWindow(Player player, byte instanceMaskId) {
		if (getInstanceMaskId(player) != instanceMaskId) {
			return;
		}

		if (!this.playersWithCooldown.contains(player.getObjectId())) {
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId));
		}
	}

	private static class SingletonHolder {
		protected static final DredgionService2 instance = new DredgionService2();
	}

	public static DredgionService2 getInstance() {
		return SingletonHolder.instance;
	}
}