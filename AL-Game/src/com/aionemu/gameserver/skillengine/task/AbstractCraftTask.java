package com.aionemu.gameserver.skillengine.task;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer, synchro2
 */
public abstract class AbstractCraftTask extends AbstractInteractionTask {

	protected int completeValue = 100;
	protected int currentSuccessValue;
	protected int currentFailureValue;
	protected int skillLvlDiff;

	/**
	 * @param requestor
	 * @param responder
	 * @param successValue
	 * @param failureValue
	 */
	public AbstractCraftTask(Player requestor, VisibleObject responder, int skillLvlDiff) {
		super(requestor, responder);
		this.skillLvlDiff = skillLvlDiff;
	}

	@Override
	protected boolean onInteraction() {
		if (currentSuccessValue == completeValue) {
			return onSuccessFinish();
		}
		if (currentFailureValue == completeValue) {
			onFailureFinish();
			return true;
		}

		analyzeInteraction();

		sendInteractionUpdate();
		return false;
	}

	/**
	 * Perform interaction calculation
	 */
	private void analyzeInteraction() {
		// TODO better random
		// if(Rnd.nextBoolean())
		int multi = Math.max(0, 33 - skillLvlDiff * 5);
		if (Rnd.get(100) > multi) {
			currentSuccessValue += Rnd.get(completeValue / (multi + 1) / 2, completeValue);
		}
		else {
			currentFailureValue += Rnd.get(completeValue / (multi + 1) / 2, completeValue);
		}

		if (currentSuccessValue >= completeValue) {
			currentSuccessValue = completeValue;
		}
		else if (currentFailureValue >= completeValue) {
			currentFailureValue = completeValue;
		}
	}

	protected abstract void sendInteractionUpdate();

	protected abstract boolean onSuccessFinish();

	protected abstract void onFailureFinish();
}