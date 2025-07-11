package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * @author ATracer
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealOverTimeEffect")
public abstract class HealOverTimeEffect extends AbstractOverTimeEffect {

	public void calculate(Effect effect, HealType healType) {
		if (!super.calculate(effect, null, null))
			return;
		
		Creature effector = effect.getEffector();
		if (effect.getEffected() instanceof Npc) {
			value = effector.getAi2().modifyHealValue(value);
		}
		int valueWithDelta = value + delta * effect.getSkillLevel();
		int maxCurValue = getMaxStatValue(effect);
		int possibleHealValue = 0;
		if (percent)
			possibleHealValue = maxCurValue * valueWithDelta / 100;
		else
			possibleHealValue = valueWithDelta;
		
		int finalHeal = possibleHealValue;
		
		if (healType == HealType.HP && effect.getItemTemplate() == null) {
			int baseHeal = possibleHealValue;
			int boostHealAdd = effector.getGameStats().getStat(StatEnum.HEAL_BOOST, 0).getCurrent();
			// Apply percent Heal Boost bonus (ex. Passive skills)
			int boostHeal = (effector.getGameStats().getStat(StatEnum.HEAL_BOOST, baseHeal).getCurrent() - boostHealAdd);
			// Apply Add Heal Boost bonus (ex. Skills like Benevolence)
			if (boostHealAdd > 0)
				boostHeal += boostHeal * boostHealAdd / 1000;
			finalHeal = effect.getEffected().getGameStats().getStat(StatEnum.HEAL_SKILL_BOOST, boostHeal).getCurrent();
		}
		effect.setReservedInt(position, finalHeal);
		effect.addSucessEffect(this);
	}

	public void onPeriodicAction(Effect effect, HealType healType) {
		Creature effected = effect.getEffected();

		int currentValue = getCurrentStatValue(effect);
		int maxCurValue = getMaxStatValue(effect);
		int possibleHealValue = effect.getReservedInt(position);
		
		int healValue = maxCurValue - currentValue < possibleHealValue ? (maxCurValue - currentValue) : possibleHealValue;
		
		if (healValue <= 0)
			return;
		
		switch (healType) {
			case HP:
				effected.getLifeStats().increaseHp(TYPE.HP, healValue, effect.getSkillId(), LOG.HEAL);
				break;
			case MP:
				effected.getLifeStats().increaseMp(TYPE.MP, healValue, effect.getSkillId(), LOG.MPHEAL);
				break;
			case FP:
				((Player)effected).getLifeStats().increaseFp(TYPE.FP, healValue, effect.getSkillId(), LOG.FPHEAL);
				break;
			case DP:
				((Player)effected).getCommonData().addDp(healValue);
				break;
		}

	}
	
	protected abstract int getCurrentStatValue(Effect effect);
	protected abstract int getMaxStatValue(Effect effect);
}