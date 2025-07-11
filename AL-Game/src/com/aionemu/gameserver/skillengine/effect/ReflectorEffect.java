package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackShieldObserver;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ginho1 modified by Wakizashi, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReflectorEffect")
public class ReflectorEffect extends ShieldEffect {

	@Override
	public void startEffect(final Effect effect) {
		int hit = hitvalue + hitdelta * effect.getSkillLevel();

		AttackShieldObserver asObserver = new AttackShieldObserver(hit, value, percent, false, effect, hitType,
			this.getType(), this.hitTypeProb, minradius, radius, null, 0);

		effect.getEffected().getObserveController().addAttackCalcObserver(asObserver);
		effect.setAttackShieldObserver(asObserver, position);
	}

	@Override
	public void endEffect(Effect effect) {
		AttackCalcObserver acObserver = effect.getAttackShieldObserver(position);
		if (acObserver != null)
			effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
	}
	
	/**
	 * shieldType
	 * 0: convertHeal
	 * 1: reflector
	 * 2: normal shield
	 * 8: protect
	 * @return
	 */
	@Override
	public int getType() {
		return 1;
	}
}