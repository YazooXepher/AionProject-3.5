package com.aionemu.gameserver.model.limiteditems;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author xTz
 */
public class LimitedItem {

	private int itemId;
	private int sellLimit;
	private int buyLimit;
	private int defaultSellLimit;
	private String salesTime;

	private TIntObjectHashMap<Integer> buyCounts = new TIntObjectHashMap<Integer>();

	public LimitedItem() {
	}

	public LimitedItem(int itemId, int sellLimit, int buyLimit, String salesTime) {
		this.itemId = itemId;
		this.sellLimit = sellLimit;
		this.buyLimit = buyLimit;
		this.defaultSellLimit = sellLimit;
		this.salesTime = salesTime;
	}

	/**
	 * return itemId.
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @param set
	 *          playerObjectId.
	 * @param set
	 *          count.
	 */
	public void setBuyCount(int playerObjectId, int count) {
		buyCounts.putIfAbsent(playerObjectId, count);
	}

	/**
	 * return playerListByObject.
	 */
	public TIntObjectHashMap<Integer> getBuyCount() {
		return buyCounts;
	}

	/**
	 * @param set
	 *          itemId.
	 */
	public void setItem(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * return sellLimit.
	 */
	public int getSellLimit() {
		return sellLimit;
	}

	/**
	 * return buyLimit.
	 */
	public int getBuyLimit() {
		return buyLimit;
	}

	public void setToDefault() {
		sellLimit = defaultSellLimit;
		buyCounts.clear();
	}

	/** 
	 * @param set
	 *          sellLimit.
	 */
	public void setSellLimit(int sellLimit) {
		this.sellLimit = sellLimit;
	}

	/**
	 * return defaultSellLimit.
	 */
	public int getDefaultSellLimit() {
		return defaultSellLimit;
	}

	public String getSalesTime() {
		return salesTime;
	}
}