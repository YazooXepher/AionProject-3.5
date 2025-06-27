package com.aionemu.gameserver.network.factories;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionPacketHandler;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.clientpackets.*;

/**
 * This factory is responsible for creating {@link AionPacketHandler} object. It also initializes created handler with a
 * set of packet prototypes.<br>
 * Object of this classes uses <tt>Injector</tt> for injecting dependencies into prototype objects.<br>
 * <br>
 * 
 * @author Luno
 */
public class AionPacketHandlerFactory {

	private AionPacketHandler handler;

	public static AionPacketHandlerFactory getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Creates new instance of <tt>AionPacketHandlerFactory</tt><br>
	 */
	public AionPacketHandlerFactory() {
		handler = new AionPacketHandler();
		// ////////////////2.6/////////////////////
		/*
		 * This packets possible is removed 4-ever 
		 * addPacket(new CM_PETITION(0x89, State.IN_GAME)); // 2.6
		 */
		// ////////////////////// 3.5 ///////////////////////
		addPacket(new CM_VERSION_CHECK(0xCA, State.CONNECTED));
		addPacket(new CM_TIME_CHECK(0xD8, State.CONNECTED, State.AUTHED, State.IN_GAME));
		addPacket(new CM_L2AUTH_LOGIN_CHECK(0x12F, State.CONNECTED));
		addPacket(new CM_CHARACTER_LIST(0x12C, State.AUTHED));
		addPacket(new CM_PING(0xC6, State.AUTHED, State.IN_GAME));
		addPacket(new CM_MAY_LOGIN_INTO_GAME(0x140, State.AUTHED));
		addPacket(new CM_ENTER_WORLD(0xD2, State.AUTHED));
		addPacket(new CM_CHARACTER_PASSKEY(0x19B, State.AUTHED));
		addPacket(new CM_LEVEL_READY(0xD3, State.IN_GAME));
		addPacket(new CM_MAC_ADDRESS(0x197, State.CONNECTED, State.AUTHED, State.IN_GAME));
		addPacket(new CM_MOVE(0xFA, State.IN_GAME));
		addPacket(new CM_MAY_QUIT(0xDE, State.AUTHED, State.IN_GAME));
		addPacket(new CM_UI_SETTINGS(0xD0, State.IN_GAME));
		addPacket(new CM_QUIT(0xC9, State.AUTHED, State.IN_GAME));
		addPacket(new CM_CASTSPELL(0xEB, State.IN_GAME));
		addPacket(new CM_CHAT_MESSAGE_PUBLIC(0xA1, State.IN_GAME));
		addPacket(new CM_EMOTION(0xF1, State.IN_GAME));
		addPacket(new CM_TITLE_SET(0x151, State.IN_GAME));
		addPacket(new CM_MOTION(0x11D, State.IN_GAME));
		addPacket(new CM_SPLIT_ITEM(0x177, State.IN_GAME));
		addPacket(new CM_DELETE_ITEM(0x10E, State.IN_GAME));
		addPacket(new CM_SHOW_MAP(0x19E, State.IN_GAME));
		addPacket(new CM_CHAT_MESSAGE_WHISPER(0xF6, State.IN_GAME));
		addPacket(new CM_PING_REQUEST(0x13D, State.IN_GAME));
		addPacket(new CM_QUEST_SHARE(0x17E, State.IN_GAME));
		addPacket(new CM_PLAYER_SEARCH(0x175, State.IN_GAME));
		addPacket(new CM_DELETE_QUEST(0x11A, State.IN_GAME));
		addPacket(new CM_ABYSS_RANKING_PLAYERS(0x196, State.IN_GAME));
		addPacket(new CM_ABYSS_RANKING_LEGIONS(0x10C, State.IN_GAME));
		addPacket(new CM_PRIVATE_STORE(0x10D, State.IN_GAME));
		addPacket(new CM_USE_ITEM(0xFF, State.IN_GAME));
		addPacket(new CM_TARGET_SELECT(0xF5, State.IN_GAME));
		addPacket(new CM_SHOW_DIALOG(0xCE, State.IN_GAME));
		addPacket(new CM_CHECK_NICKNAME(0x17B, State.AUTHED));
		addPacket(new CM_REMOVE_ALTERED_STATE(0xE9, State.IN_GAME));
		addPacket(new CM_PRIVATE_STORE_NAME(0x102, State.IN_GAME));
		addPacket(new CM_CREATE_CHARACTER(0x12D, State.AUTHED));
		addPacket(new CM_DELETE_CHARACTER(0x122, State.AUTHED));
		addPacket(new CM_RESTORE_CHARACTER(0x123, State.AUTHED));
		addPacket(new CM_MOVE_ITEM(0x176, State.IN_GAME));
		addPacket(new CM_MACRO_CREATE(0x145, State.IN_GAME));
		addPacket(new CM_MACRO_DELETE(0x17A, State.IN_GAME));
		addPacket(new CM_FRIEND_ADD(0x105, State.IN_GAME));
		addPacket(new CM_GATHER(0xD9, State.IN_GAME));
		addPacket(new CM_INSTANCE_INFO(0x18A, State.IN_GAME));
		addPacket(new CM_CLIENT_COMMAND_ROLL(0x131, State.IN_GAME));
		addPacket(new CM_START_LOOT(0x120, State.IN_GAME));
		addPacket(new CM_DIALOG_SELECT(0xCC, State.IN_GAME));
		addPacket(new CM_CLOSE_DIALOG(0xCF, State.IN_GAME));
		addPacket(new CM_SET_NOTE(0xC0, State.IN_GAME));
		addPacket(new CM_FIND_GROUP(0x2E7, State.IN_GAME)); // TODO its sent also on AUTHED state
		addPacket(new CM_BUY_ITEM(0xF9, State.IN_GAME));
		addPacket(new CM_REPLACE_ITEM(0x178, State.IN_GAME));
		addPacket(new CM_IN_GAME_SHOP_INFO(0x18B, State.IN_GAME));
		addPacket(new CM_SEND_MAIL(0x15E, State.IN_GAME));
		addPacket(new CM_READ_MAIL(0x15C, State.IN_GAME));
		addPacket(new CM_DELETE_MAIL(0x153, State.IN_GAME));
		addPacket(new CM_READ_EXPRESS_MAIL(0x168, State.IN_GAME));
		addPacket(new CM_GET_MAIL_ATTACHMENT(0x152, State.IN_GAME));
		addPacket(new CM_EQUIP_ITEM(0xFC, State.IN_GAME));
		addPacket(new CM_TELEPORT_SELECT(0x12E, State.IN_GAME));
		addPacket(new CM_INVITE_TO_GROUP(0x12B, State.IN_GAME));
		addPacket(new CM_VIEW_PLAYER_DETAILS(0x13E, State.IN_GAME));
		addPacket(new CM_EXCHANGE_REQUEST(0x115, State.IN_GAME));
		addPacket(new CM_DUEL_REQUEST(0x138, State.IN_GAME));
		addPacket(new CM_LOOT_ITEM(0x121, State.IN_GAME));
		addPacket(new CM_QUESTIONNAIRE(0x15B, State.IN_GAME));
		addPacket(new CM_REVIVE(0xDF, State.IN_GAME));
		addPacket(new CM_ATTACK(0xEA, State.IN_GAME));
		addPacket(new CM_AUTO_GROUP(0x192, State.IN_GAME));
		addPacket(new CM_PLAY_MOVIE_END(0x11B, State.IN_GAME));
		addPacket(new CM_WINDSTREAM(0x11C, State.IN_GAME));
		addPacket(new CM_OPEN_STATICDOOR(0xAD, State.IN_GAME));
		addPacket(new CM_APPEARANCE(0x19F, State.IN_GAME));
		addPacket(new CM_STOP_TRAINING(0x2EE, State.IN_GAME));
		addPacket(new CM_QUESTION_RESPONSE(0xF8, State.IN_GAME));
		addPacket(new CM_PET(0xAC, State.IN_GAME));
		addPacket(new CM_CRAFT(0x127, State.IN_GAME));
		addPacket(new CM_GROUP_DATA_EXCHANGE(0x2E5, State.IN_GAME));
		addPacket(new CM_SHOW_BRAND(0x14F, State.IN_GAME));
		addPacket(new CM_DISTRIBUTION_SETTINGS(0x143, State.IN_GAME));
		addPacket(new CM_MANASTONE(0x110, State.IN_GAME));
		addPacket(new CM_MOVE_IN_AIR(0xFB, State.IN_GAME));
		addPacket(new CM_FRIEND_STATUS(0x170, State.IN_GAME));
		addPacket(new CM_BLOCK_ADD(0x17C, State.IN_GAME));
		addPacket(new CM_INSTANCE_LEAVE(0xC4, State.IN_GAME));
		addPacket(new CM_FUSION_WEAPONS(0x164, State.IN_GAME));
		addPacket(new CM_LEGION_WH_KINAH(0x2E6, State.IN_GAME));
		addPacket(new CM_OBJECT_SEARCH(0xD1, State.IN_GAME));
		addPacket(new CM_GODSTONE_SOCKET(0x2E1, State.IN_GAME));
		addPacket(new CM_ITEM_REMODEL(0x2E0, State.IN_GAME));
		addPacket(new CM_RECIPE_DELETE(0x2E3, State.IN_GAME));
		addPacket(new CM_EXCHANGE_ADD_ITEM(0x10A, State.IN_GAME));
		addPacket(new CM_EXCHANGE_ADD_KINAH(0x108, State.IN_GAME));
		addPacket(new CM_EXCHANGE_LOCK(0x109, State.IN_GAME));
		addPacket(new CM_CUSTOM_SETTINGS(0xA6, State.IN_GAME));
		addPacket(new CM_EXCHANGE_OK(0x11E, State.IN_GAME));
		addPacket(new CM_EXCHANGE_CANCEL(0x11F, State.IN_GAME));
		addPacket(new CM_BROKER_LIST(0x101, State.IN_GAME));
		addPacket(new CM_BROKER_SEARCH(0x156, State.IN_GAME));
		addPacket(new CM_REGISTER_BROKER_ITEM(0x155, State.IN_GAME));
		addPacket(new CM_BROKER_CANCEL_REGISTERED(0x14A, State.IN_GAME));
		addPacket(new CM_BUY_BROKER_ITEM(0x154, State.IN_GAME));
		addPacket(new CM_BROKER_SETTLE_LIST(0x14B, State.IN_GAME));
		addPacket(new CM_BROKER_REGISTERED(0x157, State.IN_GAME));
		addPacket(new CM_BROKER_SETTLE_ACCOUNT(0x148, State.IN_GAME));
		addPacket(new CM_LEGION_SEND_EMBLEM_INFO(0xDA, State.IN_GAME));
		addPacket(new CM_LEGION_TABS(0xCD, State.IN_GAME));
		addPacket(new CM_SHOW_FRIENDLIST(0x104, State.IN_GAME));
		addPacket(new CM_SHOW_BLOCKLIST(0x172, State.IN_GAME));
		addPacket(new CM_FRIEND_DEL(0x13A, State.IN_GAME));
		addPacket(new CM_PET_EMOTE(0xAF, State.IN_GAME));
		addPacket(new CM_LEGION(0xC7, State.IN_GAME));
		addPacket(new CM_CHAT_AUTH(0x144, State.IN_GAME));
		addPacket(new CM_PLAYER_STATUS_INFO(0x12A, State.IN_GAME));
		addPacket(new CM_MARK_FRIENDLIST(0x1BF, State.IN_GAME));
		addPacket(new CM_BREAK_WEAPONS(0x165, State.IN_GAME));
		addPacket(new CM_BUY_TRADE_IN_TRADE(0x2E2, State.IN_GAME));
		addPacket(new CM_BLOCK_SET_REASON(0x179, State.IN_GAME));
		addPacket(new CM_BLOCK_DEL(0x17D, State.IN_GAME));
		addPacket(new CM_CHARGE_ITEM(0x2E4, State.IN_GAME));
		addPacket(new CM_LEGION_MODIFY_EMBLEM(0xC1, State.IN_GAME));
		addPacket(new CM_LEGION_UPLOAD_INFO(0x16A, State.IN_GAME));
		addPacket(new CM_LEGION_UPLOAD_EMBLEM(0x16B, State.IN_GAME));
		addPacket(new CM_CHARACTER_EDIT(0xDD, State.AUTHED));
		addPacket(new CM_SUMMON_MOVE(0x193, State.IN_GAME));
		addPacket(new CM_SUMMON_COMMAND(0x103, State.IN_GAME));
		addPacket(new CM_SUMMON_EMOTION(0x190, State.IN_GAME));
		addPacket(new CM_SUMMON_ATTACK(0x191, State.IN_GAME));
		addPacket(new CM_SUMMON_CASTSPELL(0x167, State.IN_GAME));
		addPacket(new CM_GROUP_LOOT(0x142, State.IN_GAME));
		addPacket(new CM_CHANGE_CHANNEL(0x146, State.IN_GAME));
		addPacket(new CM_HOUSE_OPEN_DOOR(0x1AB, State.IN_GAME));
		addPacket(new CM_HOUSE_EDIT(0x118, State.IN_GAME));
		addPacket(new CM_HOUSE_SETTINGS(0x113, State.IN_GAME));
		addPacket(new CM_GET_HOUSE_BIDS(0x163, State.IN_GAME));
		addPacket(new CM_GROUP_DISTRIBUTION(0x106, State.IN_GAME));
		addPacket(new CM_RECONNECT_AUTH(0x14D, State.AUTHED));
		addPacket(new CM_TOGGLE_SKILL_DEACTIVATE(0xE8, State.IN_GAME));
		addPacket(new CM_HOUSE_TELEPORT(0x1B7, State.IN_GAME));
		addPacket(new CM_HOUSE_TELEPORT_BACK(0x135, State.IN_GAME));
		addPacket(new CM_HOUSE_DECORATE(0x111, State.IN_GAME));
		addPacket(new CM_HOUSE_KICK(0x112, State.IN_GAME));
		addPacket(new CM_CHAT_GROUP_INFO(0x117, State.IN_GAME));
		addPacket(new CM_CHAT_PLAYER_INFO(0xFD, State.IN_GAME));
		addPacket(new CM_USE_HOUSE_OBJECT(0x1B5, State.IN_GAME));
		addPacket(new CM_RELEASE_OBJECT(0x1AA, State.IN_GAME));
		addPacket(new CM_HOUSE_SCRIPT(0xF4, State.IN_GAME));
		addPacket(new CM_PLACE_BID(0x1B6, State.IN_GAME));
		addPacket(new CM_TELEPORT_DONE(0xA5, State.IN_GAME));
		addPacket(new CM_HOUSE_PAY_RENT(0x1B4, State.IN_GAME));
		addPacket(new CM_REGISTER_HOUSE(0x160, State.IN_GAME));
		addPacket(new CM_CAPTCHA(0xA4, State.IN_GAME));
		addPacket(new CM_PLAYER_LISTENER(0xF2, State.IN_GAME));
		addPacket(new CM_LEGION_SEND_EMBLEM(0xC5, State.IN_GAME));
		addPacket(new CM_REPORT_PLAYER(0x195, State.IN_GAME));
		addPacket(new CM_CHALLENGE_LIST(0x1bd, State.IN_GAME));
		addPacket(new CM_GM_BOOKMARK(0xF3, State.IN_GAME));
		// ////////////////////// 3.0 ///////////////////////
		/*
		 * addPacket(new CM_UNK(0x136, State.IN_GAME)); 
		 */
	}

	public AionPacketHandler getPacketHandler() {
		return handler;
	}

	private void addPacket(AionClientPacket prototype) {
		handler.addPacketPrototype(prototype);
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final AionPacketHandlerFactory instance = new AionPacketHandlerFactory();
	}
}