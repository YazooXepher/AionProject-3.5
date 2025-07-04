package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * @author kecimis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FPHealEffect")
public class FPHealEffect extends HealOverTimeEffect {
	
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, HealType.FP);
	}

	@Override
	public void onPeriodicAction(Effect effect) {
		super.onPeriodicAction(effect, HealType.FP);
	}
	
	@Override
	protected int getCurrentStatValue(Effect effect) {
		return effect.getEffected().getLifeStats().getCurrentFp();
	}

	@Override
	protected int getMaxStatValue(Effect effect) {
		return effect.getEffected().getLifeStats().getMaxFp();
	}
}