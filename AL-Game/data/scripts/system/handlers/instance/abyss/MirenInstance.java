package instance.abyss;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author keqi, xTz
 * @reworked Luzien
 */
@InstanceID(300130000)
public class MirenInstance extends GeneralInstanceHandler {

	private boolean rewarded = false;

	@Override
	public void onDie(Npc npc) {
		switch(npc.getNpcId()) {
			case 215222: // bosses
			case 215221:
				spawnChests(npc);
				break;
			case 215415: // artifact spawns weak boss
				Npc boss = getNpc(215222);
				if (boss != null && !boss.getLifeStats().isAlreadyDead()) {
					spawn(215221, boss.getX(), boss.getY(), boss.getZ(), boss.getHeading());
					boss.getController().onDelete();
				}	
		}
	}

	private void spawnChests(Npc npc) {
		if (!rewarded) {
			rewarded = true; //safety mechanism
			if (npc.getAi2().getRemainigTime() != 0) {
				long rtime = (600000 - npc.getAi2().getRemainigTime()) / 30000;
					spawn(700543, 478.7917f, 815.5538f, 199.70894f, (byte) 8);
					if (rtime > 1)
						spawn(700543, 471, 853, 199f, (byte) 115);
					if (rtime > 2)
						spawn(700543, 477, 873, 199.7f, (byte) 109);
					if (rtime > 3)
						spawn(700543, 507, 899, 199.7f, (byte) 96);
					if (rtime > 4)
						spawn(700543, 548, 889, 199.7f, (byte) 83);
					if (rtime > 5)
						spawn(700543, 565, 889, 199.7f, (byte) 76);
					if (rtime > 6)
						spawn(700543, 585, 855, 199.7f, (byte) 63);
					if (rtime > 7)
						spawn(700543, 578, 874, 199.7f, (byte) 11);
					if (rtime > 8)
						spawn(700543, 528, 903, 199.7f, (byte) 30);
					if (rtime > 9)
						spawn(700543, 490, 899, 199.7f, (byte) 44);
					if (rtime > 10)
						spawn(700561, 470, 834, 199.7f, (byte) 63);
					if (rtime > 11 && npc.getNpcId() == 215222)
						spawn(700544, 576.8508f, 836.40424f, 199.7f, (byte) 44);
			}
		}
	}
}