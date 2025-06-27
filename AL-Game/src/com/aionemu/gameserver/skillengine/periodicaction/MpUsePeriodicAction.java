package com.aionemu.gameserver.skillengine.periodicaction;

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author antness
 */
public class MpUsePeriodicAction extends PeriodicAction {

	@XmlAttribute(name = "value")
	protected int value;

	@Override
	public void act(Effect effect) {
		Creature effected = effect.getEffected();
		int maxMp = effected.getGameStats().getMaxMp().getCurrent();
		int requiredMp = (int) (maxMp * (value / 100f));
		if (effected.getLifeStats().getCurrentMp() < requiredMp) {
			effect.endEffect();
			return;
		}
		effected.getLifeStats().reduceMp(requiredMp);
	}
}