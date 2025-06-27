package ai;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

@AIName("coinfountain")
public class CoinFountainAI2 extends ActionItemNpcAI2 {

	int quest;
	
	@Override
	protected void handleUseItemFinish(Player player) {
  	quest = player.getRace() == Race.ASMODIANS ? 2717 : 1717;
  	if (player.getCommonData().getLevel() >= 25)
      PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, quest));
  	else
  		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000) {
			if (hasItem(player, 186000030)) {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 5, quest));
				return true;
			}
			else {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
				return true;
			}
		}
		else if (dialogId == 27) {
			if (hasItem(player, 186000030)) {
				player.getInventory().decreaseByItemId(186000030, 1);
				addCoins(player);
				player.getCommonData().addExp(1000, RewardType.QUEST);
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1008, quest));
				return true;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
	
	private boolean hasItem(Player player, int itemId) {
    return player.getInventory().getItemCountByItemId(itemId) > 0;
	}
	
	 private void addCoins(Player player) {
     int rnd = Rnd.get(0, 100);
     if (rnd < 10)
    	 ItemService.addItem(player, 186000096, 1);
     else
    	 ItemService.addItem(player, 182005205, 1);
	}
}