package com.aionemu.gameserver.model;

/**
 * @author Rolandas
 */
public enum DialogPage {

	NULL(DialogAction.NULL, 0),
	STIGMA(DialogAction.OPEN_STIGMA_WINDOW, 1),
	CREATE_LEGION(DialogAction.CREATE_LEGION, 2),
	VENDOR(DialogAction.OPEN_VENDOR, 13),
	RETRIEVE_CHAR_WAREHOUSE(DialogAction.RETRIEVE_CHAR_WAREHOUSE, 14),
	DEPOSIT_CHAR_WAREHOUSE(DialogAction.DEPOSIT_CHAR_WAREHOUSE, 15),
	RETRIEVE_ACCOUNT_WAREHOUSE(DialogAction.RETRIEVE_ACCOUNT_WAREHOUSE, 16),
	DEPOSIT_ACCOUNT_WAREHOUSE(DialogAction.DEPOSIT_ACCOUNT_WAREHOUSE, 17),
	MAIL(DialogAction.OPEN_POSTBOX, 18),
	CHANGE_ITEM_SKIN(DialogAction.CHANGE_ITEM_SKIN, 19),
	REMOVE_MANASTONE(DialogAction.REMOVE_MANASTONE, 20),
	GIVE_ITEM_PROC(DialogAction.GIVE_ITEM_PROC, 21),
	GATHER_SKILL_LEVELUP(DialogAction.GATHER_SKILL_LEVELUP, 23),
	LOOT(DialogAction.NULL, 24),
	LEGION_WAREHOUSE(DialogAction.OPEN_LEGION_WAREHOUSE, 25),
	PERSONAL_WAREHOUSE(DialogAction.OPEN_PERSONAL_WAREHOUSE, 26),
	COMPOUND_WEAPON(DialogAction.COMPOUND_WEAPON, 29),
	DECOMPOUND_WEAPON(DialogAction.DECOMPOUND_WEAPON, 30),
	HOUSING_MARKER(DialogAction.NULL, 32), // Unknown
	HOUSING_LIFETIME(DialogAction.NULL, 33), // Unknown
	CHARGE_ITEM(DialogAction.NULL, 35), // Actually, two choices
	HOUSING_FRIENDLIST(DialogAction.HOUSING_FRIENDLIST, 36),
	HOUSING_POST(DialogAction.NULL, 37), // Unknown
	HOUSING_AUCTION(DialogAction.HOUSING_PERSONAL_AUCTION, 38),
	HOUSING_PAY_RENT(DialogAction.HOUSING_PAY_RENT, 39),
	HOUSING_KICK(DialogAction.HOUSING_KICK, 40),
	HOUSING_CONFIG(DialogAction.HOUSING_CONFIG, 41);

	private int id;
	private DialogAction action;

	private DialogPage(DialogAction action, int id) {
		this.id = id;
		this.action = action;
	}

	public int id() {
		return id;
	}
	
	public int actionId() {
		return action.id();
	}
	
	public static DialogPage getPageByAction(int dialogId) {
		for (DialogPage page : DialogPage.values()) {
			if (page.actionId() == dialogId)
				return page;
		}
		return DialogPage.NULL;
	}
}