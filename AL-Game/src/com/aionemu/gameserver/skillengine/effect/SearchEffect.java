package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.services.player.PlayerVisualStateService;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sweetkr
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchEffect")
public class SearchEffect extends EffectTemplate {

	@XmlAttribute
	protected CreatureSeeState state;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void endEffect(Effect effect) {
		Creature effected = effect.getEffected();

		effected.unsetSeeState(state);

		if (SecurityConfig.INVIS && effected instanceof Player)
			PlayerVisualStateService.seeValidate((Player) effected);

		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_PLAYER_STATE(effected));
	}

	@Override
	public void startEffect(final Effect effect) {
		final Creature effected = effect.getEffected();

		effected.setSeeState(state);
		
		if (SecurityConfig.INVIS && effected instanceof Player)
			PlayerVisualStateService.seeValidate((Player) effected);

		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_PLAYER_STATE(effected));
	}
}