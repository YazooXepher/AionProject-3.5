package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeCondition")
public class ChargeCondition extends Condition {

	@XmlAttribute(name = "level")
	private int level;
	
	@Override
	public boolean validate(Stat2 env, IStatFunction statFunction) {
		StatOwner owner = statFunction.getOwner();
		if (owner instanceof Item) {
			Item item = (Item) owner;
			return item.getChargeLevel() >= level;
		}
		return false;
	}

	@Override
	public boolean validate(Skill env) {
		return false;
	}
}