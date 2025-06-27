package playercommands;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * @author Maestross
 */
 
public class Start extends PlayerCommand {

	public Start() {
		super("start");
	}

	@Override
	public void execute(final Player player, String... params) {
			if (player.getPlayerClass() == PlayerClass.ASSASSIN) {
				ItemService.addItem(player, 110300795, 1); //
				ItemService.addItem(player, 113300778, 1); //
				ItemService.addItem(player, 114300793, 1); // 
				ItemService.addItem(player, 112300712, 1); // 
				ItemService.addItem(player, 111300753, 1); // 
				ItemService.addItem(player, 122000851, 2); // Archon Tribunus's Ruby Ring
				ItemService.addItem(player, 120000802, 2); // Archon Tribunus's Ruby Earrings
				ItemService.addItem(player, 121000731, 1); // Archon Tribunus's Ruby Necklace
				ItemService.addItem(player, 125001715, 1); // Archon Tribunus's Hat
				ItemService.addItem(player, 123000844, 1); // Archon Tribunus's Leather Belt
				ItemService.addItem(player, 100200807, 2); // Stormwing's Dagger
				ItemService.addItem(player, 100001080, 1); // Stormwing Scale Sword
				ItemService.addItem(player, 100000618, 1); // Archon Tribunus's Sword
				ItemService.addItem(player, 100200589, 2); // Archon Tribunus's Dagger
			}
			else if (player.getPlayerClass() == PlayerClass.CHANTER) {
				ItemService.addItem(player, 114500759, 1); // 
				ItemService.addItem(player, 110500771, 1); // 
				ItemService.addItem(player, 111500740, 1); // 
				ItemService.addItem(player, 113500751, 1); // 
				ItemService.addItem(player, 112500700, 1); // 
				ItemService.addItem(player, 125001716, 1); // Archon Tribunus's Chain Helm
				ItemService.addItem(player, 123000844, 1); // Archon Tribunus's Leather Belt
				ItemService.addItem(player, 120000802, 2); // Archon Tribunus's Ruby Earrings
				ItemService.addItem(player, 122000851, 2); // Archon Tribunus's Ruby Ring
				ItemService.addItem(player, 121000731, 1); // Archon Tribunus's Ruby Necklace
				ItemService.addItem(player, 101500698, 1); // Stormwing's Staff
				ItemService.addItem(player, 115000967, 1); // Stormwing Scale Shield
				ItemService.addItem(player, 100100676, 1); // Stormwing's Warhammer
				ItemService.addItem(player, 100100482, 1); // Archon Tribunus's Warhammer
				ItemService.addItem(player, 115000765, 1); // Archon Tribunus's Shield
				ItemService.addItem(player, 101500481, 1); // Archon Tribunus's Staff
			}
			else if (player.getPlayerClass() == PlayerClass.CLERIC) {
				ItemService.addItem(player, 114500759, 1); // 
				ItemService.addItem(player, 110500771, 1); // 
				ItemService.addItem(player, 111500740, 1); // 
				ItemService.addItem(player, 113500751, 1); //
				ItemService.addItem(player, 112500700, 1); // 
				ItemService.addItem(player, 125001716, 1); // Archon Tribunus's Chain Helm
				ItemService.addItem(player, 122000852, 2); // Archon Tribunus's Sapphire Ring
				ItemService.addItem(player, 120000803, 2); // Archon Tribunus's Sapphire Earrings
				ItemService.addItem(player, 121000732, 1); // Archon Tribunus's Sapphire Necklace
				ItemService.addItem(player, 123000845, 1); // Archon Tribunus's Belt
				ItemService.addItem(player, 101500698, 1); // Stormwing's Staff
				ItemService.addItem(player, 115000967, 1); // Stormwing Scale Shield
				ItemService.addItem(player, 100100676, 1); // Stormwing's Warhammer
				ItemService.addItem(player, 100100482, 1); // Archon Tribunus's Warhammer
				ItemService.addItem(player, 115000765, 1); // Archon Tribunus's Shield
				ItemService.addItem(player, 101500481, 1); // Archon Tribunus's Staff
			}
			else if (player.getPlayerClass() == PlayerClass.GLADIATOR) {
				ItemService.addItem(player, 114600719, 1); // 
				ItemService.addItem(player, 111600732, 1); // 
				ItemService.addItem(player, 113600725, 1); // 
				ItemService.addItem(player, 110600759, 1); // 
				ItemService.addItem(player, 112600708, 1); // 
				ItemService.addItem(player, 125001717, 1); // Archon Tribunus's Helm
				ItemService.addItem(player, 123000844, 1); // Archon Tribunus's Leather Belt
				ItemService.addItem(player, 122000851, 2); // Archon Tribunus's Ruby Ring
				ItemService.addItem(player, 120000802, 2); // Archon Tribunus's Ruby Earrings
				ItemService.addItem(player, 121000731, 1); // Archon Tribunus's Ruby Necklace
				ItemService.addItem(player, 115000966, 1); // Stormwing's Shield
				ItemService.addItem(player, 100000893, 2); // Stormwing's Azure Scale Sword
				ItemService.addItem(player, 101300655, 1); // Stormwing's Spear
				ItemService.addItem(player, 100900684, 1); // Stormwing's Greatsword
				ItemService.addItem(player, 115000765, 1); // Archon Tribunus's Shield
				ItemService.addItem(player, 100000618, 2); // Archon Tribunus's Sword
				ItemService.addItem(player, 100900475, 1); // Archon Tribunus's Greatsword
				ItemService.addItem(player, 101300464, 1); // Archon Tribunus's Spear
			}
			else if (player.getPlayerClass() == PlayerClass.RANGER) {
				ItemService.addItem(player, 110300795, 1); // 
				ItemService.addItem(player, 113300778, 1); // 
				ItemService.addItem(player, 114300793, 1); // 
				ItemService.addItem(player, 112300712, 1); // 
				ItemService.addItem(player, 111300753, 1); // 
				ItemService.addItem(player, 122000851, 2); // Archon Tribunus's Ruby Ring
				ItemService.addItem(player, 120000802, 2); // Archon Tribunus's Ruby Earrings
				ItemService.addItem(player, 121000731, 1); // Archon Tribunus's Ruby Necklace
				ItemService.addItem(player, 125001715, 1); // Archon Tribunus's Hat
				ItemService.addItem(player, 123000844, 1); // Archon Tribunus's Leather Belt
				ItemService.addItem(player, 100200807, 2); // Stormwing's Dagger
				ItemService.addItem(player, 101700722, 1); // Stormwing's Longbow
				ItemService.addItem(player, 101700494, 1); // Archon Tribunus's Longbow
				ItemService.addItem(player, 100200589, 2); // Archon Tribunus's Dagger
			}
			else if (player.getPlayerClass() == PlayerClass.SORCERER) {
				ItemService.addItem(player, 114100780, 1); // 
				ItemService.addItem(player, 111100751, 1); // 
				ItemService.addItem(player, 113100761, 1); // 
				ItemService.addItem(player, 112100712, 1); // 
				ItemService.addItem(player, 110100843, 1); // 
				ItemService.addItem(player, 125001714, 1); // Archon Tribunus's Headband
				ItemService.addItem(player, 122000852, 2); // Archon Tribunus's Sapphire Ring
				ItemService.addItem(player, 120000803, 2); // Archon Tribunus's Sapphire Earrings
				ItemService.addItem(player, 121000732, 1); // Archon Tribunus's Sapphire Necklace
				ItemService.addItem(player, 123000845, 1); // Archon Tribunus's Belt
				ItemService.addItem(player, 100500698, 1); // Stormwing's Jewel
				ItemService.addItem(player, 100600755, 1); // Stormwing's Tome
				ItemService.addItem(player, 100600512, 1); // Archon Tribunus's Tome
				ItemService.addItem(player, 100500479, 1); // Archon Tribunus's Jewel
			}
			else if (player.getPlayerClass() == PlayerClass.SPIRIT_MASTER) {
				ItemService.addItem(player, 114100780, 1); // 
				ItemService.addItem(player, 111100751, 1); // 
				ItemService.addItem(player, 113100761, 1); // 
				ItemService.addItem(player, 112100712, 1); // 
				ItemService.addItem(player, 110100843, 1); // 
				ItemService.addItem(player, 125001714, 1); // Archon Tribunus's Headband
				ItemService.addItem(player, 122000852, 2); // Archon Tribunus's Sapphire Ring
				ItemService.addItem(player, 120000803, 2); // Archon Tribunus's Sapphire Earrings
				ItemService.addItem(player, 121000732, 1); // Archon Tribunus's Sapphire Necklace
				ItemService.addItem(player, 123000845, 1); // Archon Tribunus's Belt
				ItemService.addItem(player, 100500698, 1); // Stormwing's Jewel
				ItemService.addItem(player, 100600755, 1); // Stormwing's Tome
				ItemService.addItem(player, 100600512, 1); // Archon Tribunus's Tome
				ItemService.addItem(player, 100500479, 1); // Archon Tribunus's Jewel
			}
			else if (player.getPlayerClass() == PlayerClass.TEMPLAR) {
				ItemService.addItem(player, 114600719, 1); // 
				ItemService.addItem(player, 111600732, 1); // 
				ItemService.addItem(player, 113600725, 1); // 
				ItemService.addItem(player, 110600759, 1); // 
				ItemService.addItem(player, 112600708, 1); // 
				ItemService.addItem(player, 125001717, 1); // Archon Tribunus's Helm
				ItemService.addItem(player, 123000844, 1); // Archon Tribunus's Leather Belt
				ItemService.addItem(player, 122000851, 2); // Archon Tribunus's Ruby Ring
				ItemService.addItem(player, 120000802, 2); // Archon Tribunus's Ruby Earrings
				ItemService.addItem(player, 121000731, 1); // Archon Tribunus's Ruby Necklace
				ItemService.addItem(player, 115000967, 1); // Stormwing Scale Shield
				ItemService.addItem(player, 100000893, 1); // Stormwing's Azure Scale Sword
				ItemService.addItem(player, 100900684, 1); // Stormwing's Greatsword
				ItemService.addItem(player, 115000765, 1); // Archon Tribunus's Shield
				ItemService.addItem(player, 100000618, 1); // Archon Tribunus's Sword
				ItemService.addItem(player, 100900475, 1); // Archon Tribunus's Greatsword
			}
		player.setCommandUsed(true);

			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					player.setCommandUsed(false);
				}
			}, 500 * 500 * 100000);
		PacketSendUtility.sendMessage(player, "Du hast dein Starter Paket erhalten!");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: .start ");
	}
}