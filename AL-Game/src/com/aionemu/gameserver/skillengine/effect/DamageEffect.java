package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DamageEffect")
public abstract class DamageEffect extends EffectTemplate {

	@XmlAttribute
	protected Func mode = Func.ADD;
	@XmlAttribute
	protected boolean shared;
	
	@Override
	public void applyEffect(Effect effect) {
	  effect.getEffected().getController() .onAttack(effect.getEffector(), effect.getSkillId(), effect.getReserved1(), true);
	  effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
	}

	public boolean calculate(Effect effect, DamageType damageType) {
		if (!super.calculate(effect, null, null))
			return false;
		
		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		ActionModifier modifier = getActionModifiers(effect);
		int accMod = this.accMod2 + this.accMod1 * skillLvl;
		int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * skillLvl;
		switch (damageType) {
			case PHYSICAL:
				boolean cannotMiss = false;
				if (this instanceof SkillAttackInstantEffect) {
					cannotMiss = ((SkillAttackInstantEffect)this).isCannotmiss();
				}
				int rndDmg = (this instanceof SkillAttackInstantEffect ? ((SkillAttackInstantEffect)this).getRnddmg() : 0);
				AttackUtil.calculateSkillResult(effect, valueWithDelta, modifier, this.getMode(), rndDmg, accMod, this.critProbMod2, critAddDmg, cannotMiss, shared, false);
				break;
			case MAGICAL:
				boolean useKnowledge = true;
				if (this instanceof ProcAtkInstantEffect) {
					useKnowledge = false;
				}
				AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, useKnowledge, false, this.getMode(), this.critProbMod2, critAddDmg, shared, false);
				break;
			default:
				AttackUtil.calculateSkillResult(effect, 0, null, this.getMode(), 0, accMod, 100, 0, false, shared, false);
		}
		
		return true;
	}

	public Func getMode() {
		return mode;
	}
}