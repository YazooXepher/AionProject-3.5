package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * @author ATracer
 */
public class StatAddFunction extends StatFunction {

	public StatAddFunction() {
	}

	public StatAddFunction(StatEnum name, int value, boolean bonus) {
		super(name, value, bonus);
	}

	@Override
	public void apply(Stat2 stat) {
		if (isBonus()) {
			stat.addToBonus(getValue());
		}
		else {
			stat.addToBase(getValue());
		}
	}

	@Override
	public int getPriority() {
		return isBonus() ? 50 : 30;
	}

	@Override
	public String toString() {
		return "StatAddFunction [" + super.toString() + "]";
	}
}