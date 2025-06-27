package playercommands;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;


/**
 * @author Maestross
 */
 
public class Medal extends PlayerCommand {

    public Medal() {
        super("medal");
    }

    @Override
	public void execute(Player player, String... params) {
		if (params.length < 1) {
			PacketSendUtility.sendMessage(player, "Syntax: .medal <silver | gold | platinum | mithril>"
			+ "\nSyntax: .medal silver - Silber zu Gold eintauschen."
			+ "\nSyntax: .medal gold - Gold zu Platin eintauschen."
			+ "\nSyntax: .medal platinum - Platin zu Mithril eintauschen."
           + "\nSyntax: .medal mithril - Mithril zu Heldenhafter Mithril eintauschen.");
			return;
		}
                
				if (params[0].equalsIgnoreCase("silver")) {
                   silver_medal(player);
                }
                if (params[0].equalsIgnoreCase("gold")) {
                   gold_medal(player);
                }
                if (params[0].equalsIgnoreCase("platinum")) {
                   platinum_medal(player);
                }
                if (params[0].equalsIgnoreCase("mithril")) {
                   mithril_medal(player);
                }
    }

	private void silver_medal(Player player) {
        Storage bag = player.getInventory();
                                
		long itemsInBag = bag.getItemCountByItemId(186000031);
                int ss = player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3;
                if (itemsInBag <= 0)
                	return;
                if (itemsInBag >= 1000)
                	return;
                if (itemsInBag < ss) {
			PacketSendUtility.sendYellowMessageOnCenter(player, "Du besitzt nicht genug Silber Medaillen");
			return;
		}

		Item item = bag.getFirstItemByItemId(186000031);
		bag.decreaseByObjectId(item.getObjectId(), player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3);
                ItemService.addItem(player, 186000030, 5);
		PacketSendUtility.sendMessage(player, "Dein Silber wurde in Gold umgewandelt");
    }
 
    private void gold_medal(Player player) {
        Storage bag = player.getInventory();
                                
		long itemsInBag = bag.getItemCountByItemId(186000030);
               int ss = player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3;
               if (itemsInBag <= 0)
               	return;
               if (itemsInBag >= 1000)
               	return;
    if (itemsInBag < ss) {
			PacketSendUtility.sendYellowMessageOnCenter(player, "Du besitzt nicht genug Gold Medaillen");
			return;
		}

		Item item = bag.getFirstItemByItemId(186000030);
		bag.decreaseByObjectId(item.getObjectId(), player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3);
                ItemService.addItem(player, 186000096, 5);
		PacketSendUtility.sendMessage(player, "Dein Gold wurde in Platin umgewandelt");
    }

    private void platinum_medal(Player player) {
        Storage bag = player.getInventory();
                                
		long itemsInBag = bag.getItemCountByItemId(186000096);
                int ss = player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3;
             if (itemsInBag <= 0)
                	return;
             if (itemsInBag >= 1000)
             	return;
		if (itemsInBag < ss) {
			PacketSendUtility.sendYellowMessageOnCenter(player, "Du besitzt nicht genug Platin Medaillen");
			return;
		}

		Item item = bag.getFirstItemByItemId(186000096);
		bag.decreaseByObjectId(item.getObjectId(), player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3);
                ItemService.addItem(player, 186000147, 3);
		PacketSendUtility.sendMessage(player, "Dein Platin wurde in Mithril umgewandelt");
    }

    private void mithril_medal(Player player) {
        Storage bag = player.getInventory();
                                
		long itemsInBag = bag.getItemCountByItemId(186000147);
                int ss = player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3;
    if (itemsInBag <= 0)
    	return;
    if (itemsInBag >= 1000)
    	return;
		if (itemsInBag < ss) {
			PacketSendUtility.sendYellowMessageOnCenter(player, "Du besitzt nicht genug Mithril Medaillen");
			return;
		}

		Item item = bag.getFirstItemByItemId(186000147);
		bag.decreaseByObjectId(item.getObjectId(), player.getClientConnection().getAccount().getMembership() < 2 ? 5 : 3);
                ItemService.addItem(player, 186000223, 2);
		PacketSendUtility.sendMessage(player, "Dein Mithril wurde in Heldenhaftes Mithril umgewandelt");
    }
}