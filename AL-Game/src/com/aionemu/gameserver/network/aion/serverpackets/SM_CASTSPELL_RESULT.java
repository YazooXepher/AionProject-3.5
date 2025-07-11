package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This packet show cast spell result (including hit time).
 *
 * @author alexa026, Sweetkr
 */
public class SM_CASTSPELL_RESULT extends AionServerPacket {

	private Creature effector;
	private Creature target;
	private Skill skill;
	private int cooldown;
	private int hitTime;
	private List<Effect> effects;
	private int spellStatus;
	private int dashStatus;
	private int targetType;
	private boolean chainSuccess;

	public SM_CASTSPELL_RESULT(Skill skill, List<Effect> effects, int hitTime, boolean chainSuccess, int spellStatus,
			int dashStatus) {
		this.skill = skill;
		this.effector = skill.getEffector();
		this.target = skill.getFirstTarget();
		this.effects = effects;
		this.cooldown = effector.getSkillCooldown(skill.getSkillTemplate());
		this.spellStatus = spellStatus;
		this.chainSuccess = chainSuccess;
		this.targetType = 0;
		this.hitTime = hitTime;
		this.dashStatus = dashStatus;
	}

	public SM_CASTSPELL_RESULT(Skill skill, List<Effect> effects, int hitTime, boolean chainSuccess, int spellStatus,
			int dashStatus, int targetType) {
		this(skill, effects, hitTime, chainSuccess, spellStatus, dashStatus);
		this.targetType = targetType;
	}
	
	
	

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(effector.getObjectId());
		writeC(targetType);
		switch (targetType) {
			case 0:
			case 3:
			case 4:
				writeD(target.getObjectId());
				break;
			case 1:
				writeF(skill.getX());
				writeF(skill.getY());
				writeF(skill.getZ());
				break;
			case 2:
				writeF(skill.getX());
				writeF(skill.getY());
				writeF(skill.getZ());
				writeF(0);//unk1
				writeF(0);//unk2
				writeF(0);//unk3
				writeF(0);//unk4
				writeF(0);//unk5
				writeF(0);//unk6
				writeF(0);//unk7
				writeF(0);//unk8
				break;
		}
		writeH(skill.getSkillTemplate().getSkillId());
		writeC(skill.getSkillTemplate().getLvl());
		writeD(cooldown);
		writeH(hitTime);
		writeC(0); // unk

		/**
		 * 0 : chain skill (counter too) 
		 * 16 : no damage to all target like dodge, resist or effect size is 0 
		 * 32 : regular
		 */
		if (effects.isEmpty())
			writeH(16);
		else if (chainSuccess)
			writeH(32);
		else
			writeH(0);

		// Get dash status
		writeC(this.dashStatus);
		switch (this.dashStatus) {
			case 1:
			case 2:
			case 3:
			case 4:
				writeC(skill.getH());
				writeF(skill.getX());
				writeF(skill.getY());
				writeF(skill.getZ());
				break;
			default:
				break;
		}

		writeH(effects.size());
		for (Effect effect : effects) {
			Creature effected = effect.getEffected();

			if (effected != null) {
				writeD(effected.getObjectId());
				writeC(effect.getEffectResult().getId());// 0 - NORMAL, 1 - ABSORBED, 2 - CONFLICT
				writeC((int) (100f * effected.getLifeStats().getCurrentHp() / target.getLifeStats().getMaxHp())); // target %hp
			}
			else { // point point skills
				writeD(0);
				writeC(0);
				writeC(0);
			}

			writeC((int) (100f * effector.getLifeStats().getCurrentHp() / effector.getLifeStats().getMaxHp())); // attacker %hp

			/**
			 * Spell Status
			 * 1 : stumble
			 * 2 : knockback
			 * 4 : open aerial
			 * 8 : close aerial
			 * 16 : spin
			 * 32 : block
			 * 64 : parry
			 * 128 :dodge
			 * 256 : resist
			 */
			writeC(this.spellStatus);
			writeC(effect.getSkillMoveType().getId());
			writeH(0);
			writeC(effect.getCarvedSignet()); // current carve signet count

			switch (this.spellStatus) {
				case 1:
				case 2:
				case 4:
				case 8:
					writeF(effect.getTargetX());
					writeF(effect.getTargetY());
					writeF(effect.getTargetZ());
					break;
				case 16:
					writeC(effect.getEffector().getHeading());
					break;
				default:
					switch (effect.getSkillMoveType()) {
						case PULL:
						case KNOCKBACK:
							writeF(effect.getTargetX());
							writeF(effect.getTargetY());
							writeF(effect.getTargetZ());
						default:
							break;
					}
					break;
			}

			writeC(1); // loops size - always 1?
			// TODO for(lop size)
			{
				writeC(effect.isMphealInstant() ? 1 : 0);
				if (effect.isDelayedDamage())
					writeD(0);
				else
					writeD(effect.getReserved1()); //TODO value from specific effect using loop
				writeC(effect.getAttackStatus().getId());

				//setting counter skill from packet to have the best synchronization of time with client
				if (effect.getEffected() instanceof Player) {
					if (effect.getAttackStatus().isCounterSkill())
						((Player) effect.getEffected()).setLastCounterSkill(effect.getAttackStatus());
				}

				writeC(effect.getShieldDefense());

				/**
				 * shield Type:
				 * 1: reflector
				 * 2: normal shield
				 * 8: protect effect (ex. skillId: 417 Bodyguard)
				 * TODO find out 4
				 */
				switch (effect.getShieldDefense()) {
					case 0:
					case 2:
						break;
					case 8:
					case 10:
						writeD(effect.getProtectorId()); // protectorId
						writeD(effect.getProtectedDamage()); // protected damage
						writeD(effect.getProtectedSkillId()); // skillId
						break;
					default:
						writeD(effect.getProtectorId()); // protectorId
						writeD(effect.getProtectedDamage()); // protected damage
						writeD(effect.getProtectedSkillId()); // skillId
						writeD(effect.getReflectedDamage()); // reflect damage
						writeD(effect.getReflectedSkillId()); // skill id
						break;
				}
			}
		}
	}
}