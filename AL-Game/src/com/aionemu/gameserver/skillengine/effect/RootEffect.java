package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.commons.utils.Rnd;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RootEffect")
public class RootEffect extends EffectTemplate {

	@XmlAttribute
	protected int resistchance = 100;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.ROOT_RESISTANCE, null);
	}

	@Override
	public void startEffect(final Effect effect) {
		final Creature effected = effect.getEffected();
		effected.getMoveController().abortMove();
		effected.getEffectController().setAbnormal(AbnormalState.ROOT.getId());
		effect.setAbnormal(AbnormalState.ROOT.getId());
		//PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));

		ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {

			@Override
			public void attacked(Creature creature) {
				if (Rnd.get(0, 100) > resistchance)
					effected.getEffectController().removeEffect(effect.getSkillId());
			}
		};
		effected.getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);

	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.ROOT.getId());
		ActionObserver observer = effect.getActionObserver(position);
		if (observer != null)
			effect.getEffected().getObserveController().removeObserver(observer);
	}
}