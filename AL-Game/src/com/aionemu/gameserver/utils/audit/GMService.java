package com.aionemu.gameserver.utils.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author MrPoke
 *
 */
public class GMService {
	public static final GMService getInstance() {
		return SingletonHolder.instance;
	}

	private Map<Integer, Player> gms = new FastMap<Integer, Player>();
	private boolean announceAny = false;
	private List<Byte> announceList;
	private GMService() {
		announceList = new ArrayList<Byte>();
		announceAny = AdminConfig.ANNOUNCE_LEVEL_LIST.equals("*");
		if (!announceAny) {
			try {
				for (String level : AdminConfig.ANNOUNCE_LEVEL_LIST.split(","))
					announceList.add(Byte.parseByte(level));
			} catch (Exception e) {
				announceAny = true;
			}
		}
	}
	public void onAddGMSkills(Player player)
    {
    // Special skill for gm
    if (AdminConfig.COMMAND_SPECIAL_SKILL && player.getAccessLevel() >= AdminConfig.COMMAND_SPECIAL_SKILL_LVL)
    {
			FastList<Integer> gmSkill = FastList.newInstance();
            //gmSkill.add(174); // GM's Armor
            //gmSkill.add(175); // GM's Tempest
            //gmSkill.add(1904); // Wrath of Developer
            gmSkill.add(1911); // Frustration of Developer
            //gmSkill.add(3224); // GM Power!
            //gmSkill.add(3226); // GM Armor!
            //gmSkill.add(3227); // GM Cleanse!
            //gmSkill.add(3232); // GM Wings!
            //gmSkill.add(3233); // GM Haste!
            //gmSkill.add(3234); // GM Fortitude!
            //gmSkill.add(3235); // GM Fortune!
            //gmSkill.add(3236); // GM Defenses I
            //gmSkill.add(3237); // GM Defenses II
            //gmSkill.add(3238); // GM Defenses III
            //gmSkill.add(3239); // GM Sleight of Hand I
            //gmSkill.add(3240); // GM Mental Alacrity I
            //gmSkill.add(3241); // GM Hustle I
            for (FastList.Node<Integer> n = gmSkill.head(), end = gmSkill.tail(); (n = n.getNext()) != end; )
            {
					PlayerSkillEntry skill = new PlayerSkillEntry(n.getValue(), true, 1, PersistentState.NOACTION);
                    player.getSkillList().addStigmaSkill(player, skill.getSkillId(), skill.getSkillLevel());
            }
	}
	}
	public Collection<Player> getGMs(){
		return gms.values();
	}
	public void onPlayerLogin(Player player){
		if (player.isGM()){
			gms.put(player.getObjectId(), player);
			
			if(announceAny) 
				broadcastMesage("GM: "+player.getName()+ " logged in.");
			else if (announceList.contains(player.getAccessLevel())) 
				broadcastMesage("GM: "+player.getName()+ " logged in.");
		}
	}
	
	public void onPlayerLogedOut(Player player){
		gms.remove(player.getObjectId());
	}

	public void broadcastMesage(String message){
		SM_MESSAGE packet = new SM_MESSAGE(0, null, message, ChatType.YELLOW);
		for (Player player : gms.values()){
			PacketSendUtility.sendPacket(player, packet);
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final GMService instance = new GMService();
	}
}