package com.aionemu.gameserver.skillengine.properties;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class FirstTargetProperty {

	/**
	 * @param skill
	 * @param properties
	 * @return
	 */
	public static final boolean set(Skill skill, Properties properties) {

		FirstTargetAttribute value = properties.getFirstTarget();
		skill.setFirstTargetAttribute(value);
		switch (value) {
			case ME:
				skill.setFirstTargetRangeCheck(false);
				skill.setFirstTarget(skill.getEffector());
				break;
			case TARGETORME:
				boolean changeTargetToMe = false;
				if (skill.getFirstTarget() == null) {
					skill.setFirstTarget(skill.getEffector());
				}
				else if (skill.getFirstTarget().isAttackableNpc()) {
					Player playerEffector = (Player) skill.getEffector();
					if (skill.getFirstTarget().isEnemy(playerEffector)) {
						changeTargetToMe = true;
					}
				}
				else if ((skill.getFirstTarget() instanceof Player) && (skill.getEffector() instanceof Player)) {
					Player playerEffected = (Player) skill.getFirstTarget();
					Player playerEffector = (Player) skill.getEffector();
					if (playerEffected.isEnemy(playerEffector)) {
						changeTargetToMe = true;
					}
				}
				else if (skill.getFirstTarget() instanceof Npc) {
					Npc npcEffected = (Npc) skill.getFirstTarget();
					Player playerEffector = (Player) skill.getEffector();
					if (npcEffected.isEnemy(playerEffector)) {
						changeTargetToMe = true;
					}
				}
				else if ((skill.getFirstTarget() instanceof Summon) && (skill.getEffector() instanceof Player)) {
					Summon summon = (Summon) skill.getFirstTarget();
					Player playerEffected = summon.getMaster();
					Player playerEffector = (Player) skill.getEffector();
					if (playerEffected.isEnemy(playerEffector)) {
						changeTargetToMe = true;
					}
				}
				if (changeTargetToMe) {
					if (skill.getEffector() instanceof Player)
						PacketSendUtility.sendPacket((Player) skill.getEffector(),
							SM_SYSTEM_MESSAGE.STR_SKILL_AUTO_CHANGE_TARGET_TO_MY);
					skill.setFirstTarget(skill.getEffector());
				}
				break;
			case TARGET:
				// Exception for effect skills which are not used directly
				if (skill.getSkillId() > 8000 && skill.getSkillId() < 9000)
					break;
				// Exception for NPC skills which applied on players
				if (skill.getSkillTemplate().getDispelCategory() == DispelCategoryType.NPC_BUFF
					|| skill.getSkillTemplate().getDispelCategory() == DispelCategoryType.NPC_DEBUFF_PHYSICAL)
					break;

				if (skill.getFirstTarget() == null || skill.getFirstTarget().equals(skill.getEffector())) {
					if (skill.getEffector() instanceof Player) {
						if (skill.getSkillTemplate().getProperties().getTargetType() == TargetRangeAttribute.AREA)
							return skill.getFirstTarget() != null;

						TargetRelationAttribute relation = skill.getSkillTemplate().getProperties().getTargetRelation();
						TargetRangeAttribute type = skill.getSkillTemplate().getProperties().getTargetType();
						if ((relation != TargetRelationAttribute.ALL && relation != TargetRelationAttribute.MYPARTY) 
										|| type == TargetRangeAttribute.PARTY || skill.getSkillId() == 2353) { //TODO: Remove ID, find logic!
							PacketSendUtility.sendPacket((Player) skill.getEffector(),
								SM_SYSTEM_MESSAGE.STR_SKILL_TARGET_IS_NOT_VALID);
							return false;
						}
					}
				}
				break;
			case MYPET:
				Creature effector = skill.getEffector();
				if (effector instanceof Player) {
					Summon summon = ((Player) effector).getSummon();
					if (summon != null)
						skill.setFirstTarget(summon);
					else
						return false;
				}
				else {
					return false;
				}
				break;
			case MYMASTER:
				Creature peteffector = skill.getEffector();
				if (peteffector instanceof Summon) {
					Player player = ((Summon) peteffector).getMaster();
					if (player != null)
						skill.setFirstTarget(player);
					else
						return false;
				}
				else {
					return false;
				}
				break;
			case PASSIVE:
				skill.setFirstTarget(skill.getEffector());
				break;
			case TARGET_MYPARTY_NONVISIBLE:
				Creature effected = skill.getFirstTarget();
				if (effected == null || skill.getEffector() == null)
					return false;
				if (!(effected instanceof Player) || !(skill.getEffector() instanceof Player) || !((Player) skill.getEffector()).isInGroup2())
					return false;
				boolean myParty = false;
				for (Player member : ((Player)skill.getEffector()).getPlayerGroup2().getMembers()) {
					if (member == skill.getEffector())
						continue;
					if (member == effected) {
						myParty = true;
						break;
					}
				}
				if (!myParty)
					return false;

				skill.setFirstTargetRangeCheck(false);
				break;
			case POINT:
				skill.setFirstTarget(skill.getEffector());
				skill.setFirstTargetRangeCheck(false);
				return true;
		}

		if (skill.getFirstTarget() != null)
			skill.getEffectedList().add(skill.getFirstTarget());
		return true;
	}
}