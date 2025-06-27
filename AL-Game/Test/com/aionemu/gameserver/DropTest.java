package com.aionemu.gameserver;

import org.junit.Test;

import com.aionemu.gameserver.dataholders.NpcDropData;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;

/**
 * @author MrPoke
 *
 */
public class DropTest {

	@Test
	public void test() {
/**		NpcDropData dropData = NpcDropData.load();
		for (NpcDrop npcDrop : dropData.getNpcDrop()){
			if (npcDrop.getNpcId() == 210186){
				for (DropGroup dropGroup : npcDrop.getDropGroup()){
					System.out.println("DropGroup"+dropGroup.getGroupName()+", "+ dropGroup.getRace());
					for (Drop drop : dropGroup.getDrop()){
						System.out.println(drop.toString());
					}
				}
			}
		}
*/	}
}