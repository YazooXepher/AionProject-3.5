package com.aionemu.gameserver.model.stats.container;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javolution.util.FastMap;
import javolution.util.FastMap.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.ReverseStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.StatCapUtil;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunctionProxy;

/**
 * @author xavier
 */
public abstract class CreatureGameStats<T extends Creature> {

	protected static final Logger log = LoggerFactory.getLogger(CreatureGameStats.class);

	private static final int ATTACK_MAX_COUNTER = Integer.MAX_VALUE;
	private long lastGeoUpdate = 0;

	protected FastMap<StatEnum, TreeSet<IStatFunction>> stats;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private int attackCounter = 0;
	protected T owner = null;

	private Stat2 cachedHPStat;
	private Stat2 cachedMPStat;

	protected CreatureGameStats(T owner) {
		this.owner = owner;
		this.stats = new FastMap<StatEnum, TreeSet<IStatFunction>>();
	}

	/**
	 * @return the atcount
	 */
	public int getAttackCounter() {
		return attackCounter;
	}

	/**
	 * @param atcount
	 *          the atcount to set
	 */
	protected void setAttackCounter(int attackCounter) {
		if (attackCounter <= 0) {
			this.attackCounter = 1;
		}
		else {
			this.attackCounter = attackCounter;
		}
	}

	public void increaseAttackCounter() {
		if (attackCounter == ATTACK_MAX_COUNTER) {
			this.attackCounter = 1;
		}
		else {
			this.attackCounter++;
		}
	}

	public final void addEffectOnly(StatOwner statOwner, List<? extends IStatFunction> functions) {
		lock.writeLock().lock();
		try {
			for (IStatFunction function : functions) {
				if (!stats.containsKey(function.getName()))
					stats.put(function.getName(), new TreeSet<IStatFunction>());
				IStatFunction func = function;
				TreeSet<IStatFunction> mods = stats.get(function.getName());
				if (function instanceof StatFunction) {
					func = new StatFunctionProxy(statOwner, function);
				}
				if (mods.contains(func)) {
					log.warn("Effect " + statOwner + " already active" + func);
				}
				mods.add(func);
			}
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	public final void addEffect(StatOwner statOwner, List<? extends IStatFunction> functions) {
		addEffectOnly(statOwner, functions);
		onStatsChange();
	}

	public final void endEffect(StatOwner statOwner) {
		lock.writeLock().lock();
		try {
			for (Entry<StatEnum, TreeSet<IStatFunction>> e = stats.head(), end = stats.tail(); (e = e.getNext()) != end;) {
				TreeSet<IStatFunction> value = e.getValue();
				for (Iterator<IStatFunction> iter = value.iterator(); iter.hasNext();) {
					IStatFunction ownedMod = iter.next();
					if (ownedMod.getOwner() != null && ownedMod.getOwner().equals(statOwner)) {
						iter.remove();
					}
				}
			}
		}
		finally {
			lock.writeLock().unlock();
		}
		onStatsChange();
	}

	public int getPositiveStat(StatEnum statEnum, int base) {
		Stat2 stat = getStat(statEnum, base);
		int value = stat.getCurrent();
		return value > 0 ? value : 0;
	}

	public int getPositiveReverseStat(StatEnum statEnum, int base) {
		Stat2 stat = getReverseStat(statEnum, base);
		int value = stat.getCurrent();
		return value > 0 ? value : 0;
	}

	public Stat2 getStat(StatEnum statEnum, int base) {
		Stat2 stat = new AdditionStat(statEnum, base, (Creature) owner);
		return getStat(statEnum, stat);
	}

	public Stat2 getStat(StatEnum statEnum, int base, float bonusRate) {
		Stat2 stat = new AdditionStat(statEnum, base, (Creature) owner, bonusRate);
		return getStat(statEnum, stat);
	}

	public Stat2 getReverseStat(StatEnum statEnum, int base) {
		Stat2 stat = new ReverseStat(statEnum, base, (Creature) owner);
		return getStat(statEnum, stat);
	}

	public Stat2 getReverseStat(StatEnum statEnum, int base, float bonusRate) {
		Stat2 stat = new ReverseStat(statEnum, base, (Creature) owner, bonusRate);
		return getStat(statEnum, stat);
	}

	public Stat2 getStat(StatEnum statEnum, Stat2 stat) {
		lock.readLock().lock();
		try {
			TreeSet<IStatFunction> functions = stats.get(statEnum);
			if (functions == null) {
				return stat;
			}
			for (IStatFunction func : functions) {
				if (func.validate(stat, func)) {
					func.apply(stat);
				}
			}
			StatCapUtil.calculateBaseValue(stat, ((Creature)owner).isPlayer());
			return stat;
		}
		finally {
			lock.readLock().unlock();
		}
	}

	public Stat2 getItemStatBoost(StatEnum statEnum, Stat2 stat) {
		lock.readLock().lock();
		try {
			TreeSet<IStatFunction> functions = stats.get(statEnum);
			if (functions == null || functions.isEmpty())
				return stat;
			for (IStatFunction func : functions) {
				if (func.validate(stat, func) && (func.getOwner() instanceof Item || func.getOwner() instanceof ManaStone))
					func.apply(stat);
			}
		}
		finally {
			lock.readLock().unlock();
		}
		return stat;
	}

	public abstract Stat2 getMaxHp();

	public abstract Stat2 getMaxMp();

	public abstract Stat2 getAttackSpeed();

	public abstract Stat2 getMovementSpeed();

	public abstract Stat2 getAttackRange();

	public abstract Stat2 getPDef();

	public abstract Stat2 getMDef();

	public abstract Stat2 getMResist();

	public abstract Stat2 getPower();

	public abstract Stat2 getHealth();

	public abstract Stat2 getAccuracy();

	public abstract Stat2 getAgility();

	public abstract Stat2 getKnowledge();

	public abstract Stat2 getWill();

	public abstract Stat2 getEvasion();

	public abstract Stat2 getParry();

	public abstract Stat2 getBlock();

	public abstract Stat2 getMainHandPAttack();

	public abstract Stat2 getMainHandPCritical();

	public abstract Stat2 getMainHandPAccuracy();

	public abstract Stat2 getMAttack();

	public abstract Stat2 getMBoost();
	
	public abstract Stat2 getMBResist();

	public abstract Stat2 getMAccuracy();

	public abstract Stat2 getMCritical();

	public abstract Stat2 getHpRegenRate();

	public abstract Stat2 getMpRegenRate();
	
  public abstract Stat2 getPCR();
	
	public abstract Stat2 getMCR();
	
	public abstract Stat2 getAllSpeed();

	public int getMagicalDefenseFor(SkillElement element) {
		switch (element) {
			case EARTH:
				return getStat(StatEnum.EARTH_RESISTANCE, 0).getCurrent();
			case FIRE:
				return getStat(StatEnum.FIRE_RESISTANCE, 0).getCurrent();
			case WATER:
				return getStat(StatEnum.WATER_RESISTANCE, 0).getCurrent();
			case WIND:
				return getStat(StatEnum.WIND_RESISTANCE, 0).getCurrent();
			case LIGHT:
				return getStat(StatEnum.ELEMENTAL_RESISTANCE_LIGHT, 0).getCurrent();
			case DARK:
				return getStat(StatEnum.ELEMENTAL_RESISTANCE_DARK, 0).getCurrent();
			default:
				return 0;
		}
	}

	public float getMovementSpeedFloat() {
		return getMovementSpeed().getCurrent() / 1000f;
	}

	/**
	 * Send packet about stats info
	 */
	public void updateStatInfo() {
	}

	/**
	 * Send packet about speed info
	 */
	public void updateSpeedInfo() {
	}

	public TreeSet<IStatFunction> getStatsByStatEnum(StatEnum stat) {
		return stats.get(stat);
	}

	/**
	 * @return
	 */
	public boolean checkGeoNeedUpdate() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastGeoUpdate > 600) {
			lastGeoUpdate = currentTime;
			return true;
		}
		return false;
	}

	/**
	 * Perform additional calculations after effects added/removed<br>
	 * This method will be called outside of stats lock.
	 */
	protected void onStatsChange() {
		checkHPStats();
		checkMPStats();
	}

	private void checkHPStats(){
		Stat2 oldHP = cachedHPStat;
		cachedHPStat = null;
		Stat2 newHP = this.getMaxHp();
		cachedHPStat = newHP;
		if(oldHP == null)
			return;
		if (oldHP.getCurrent() != newHP.getCurrent()) {
			float percent = 1f*newHP.getCurrent()/oldHP.getCurrent();
			owner.getLifeStats().setCurrentHp(Math.round(owner.getLifeStats().getCurrentHp()*percent));
		}
	}
	
	private void checkMPStats(){
		Stat2 oldMP = cachedMPStat;
		cachedMPStat = null;
		Stat2 newMP = this.getMaxMp();
		cachedMPStat = newMP;
		if(oldMP == null)
			return;
		if (oldMP.getCurrent() != newMP.getCurrent()) {
			float percent = 1f*newMP.getCurrent()/oldMP.getCurrent();
			owner.getLifeStats().setCurrentMp(Math.round(owner.getLifeStats().getCurrentMp()*percent));
		}
	}
}