package admincommands;

import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author ginho1, Damon
 * 
 */
public class AddEmotion extends AdminCommand {

	public AddEmotion() {
		super("addemotion");
	}

	@Override
	public void execute(Player admin, String... params) {
		
		long expireMinutes = 0;
		int emotionId = 0;
		VisibleObject target = null;
		Player finalTarget = null;
			
		if((params.length < 1) || (params.length > 2)) {
			PacketSendUtility.sendMessage(admin, "syntax: //addemotion <emotion id [expire time] || html>\nhtml to show html with names.");
			return;
		}
		
		try {
			emotionId = Integer.parseInt(params[0]);
			if(params.length == 2)
				expireMinutes = Long.parseLong(params[1]);
		}
		catch (NumberFormatException ex) {
			if(params[0].equalsIgnoreCase("html"))
				HTMLService.showHTML(admin, HTMLCache.getInstance().getHTML("emote.xhtml"));
				return;
		}

		if(emotionId < 1 || (emotionId > 35 && emotionId < 64) || emotionId > 129) {
			PacketSendUtility.sendMessage(admin, "Invalid <emotion id>, must be in intervals : [1-35]U[64-129]");
			return;
		}
		
		target = admin.getTarget();
		
		if (target == null) {
			finalTarget = admin;
		} 
		else if (target instanceof Player) {
			finalTarget = (Player) target;
		}
		
		if(finalTarget.getEmotions().contains(emotionId)) {
			PacketSendUtility.sendMessage(admin, "Target has aldready this emotion !");
			return;
		}
			
		if(params.length == 2) {
			finalTarget.getEmotions().add(emotionId, (int)((System.currentTimeMillis()/1000)+expireMinutes*60), true);
		}
		else {
			finalTarget.getEmotions().add(emotionId, 0, true);
		}
	}
}