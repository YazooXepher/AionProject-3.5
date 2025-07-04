package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StumbleEffect")
public class StumbleEffect extends EffectTemplate {

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
		final Creature effected = effect.getEffected();
		effected.getEffectController().removeParalyzeEffects();
		effected.getMoveController().abortMove();
		World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(),
			effected.getHeading());
		PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), 
			new SM_FORCED_MOVE(effect.getEffector(), effect.getEffected().getObjectId(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
	}

	@Override
	public void startEffect(Effect effect) {
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.STUMBLE.getId());
		effect.setAbnormal(AbnormalState.STUMBLE.getId());
	}

	@Override
	public void calculate(Effect effect) {
		if (!super.calculate(effect, StatEnum.STUMBLE_RESISTANCE, SpellStatus.STUMBLE))
			return;
		effect.setSkillMoveType(SkillMoveType.STUMBLE);
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		float direction = effected instanceof Player ? 1.5f : 0.5f;
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		float x1 = (float) (Math.cos(radian) * direction);
		float y1 = (float) (Math.sin(radian) * direction);
		float z = effected.getZ();
		byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
		Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, effected.getX() + x1,
			effected.getY() + y1, effected.getZ() - 0.4f, false, intentions);
		x1 = closestCollision.x;
		y1 = closestCollision.y;
		z = closestCollision.z;
		effect.setTargetLoc(x1, y1, z);
	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.STUMBLE.getId());
	}
}