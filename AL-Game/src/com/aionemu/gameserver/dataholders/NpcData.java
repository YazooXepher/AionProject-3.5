package com.aionemu.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This is a container holding and serving all {@link NpcTemplate} instances.<br>
 * Briefly: Every {@link Npc} instance represents some class of NPCs among which each have the same id, name, items,
 * statistics. Data for such NPC class is defined in {@link NpcTemplate} and is uniquely identified by npc id.
 * 
 * @author Luno
 */
@XmlRootElement(name = "npc_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class NpcData extends ReloadableData {

	@XmlElement(name = "npc_template")
	private List<NpcTemplate> npcs;

	/** A map containing all npc templates */
	private TIntObjectHashMap<NpcTemplate> npcData = new TIntObjectHashMap<NpcTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (NpcTemplate npc : npcs) {
			npcData.put(npc.getTemplateId(), npc);
		}
		npcs.clear();
		npcs = null;
	}

	public int size() {
		return npcData.size();
	}

	/**
	 * /** Returns an {@link NpcTemplate} object with given id.
	 * 
	 * @param id
	 *          id of NPC
	 * @return NpcTemplate object containing data about NPC with that id.
	 */
	public NpcTemplate getNpcTemplate(int id) {
		return npcData.get(id);
	}
	
	/**
	 * @return the npcData
	 */
	public TIntObjectHashMap<NpcTemplate> getNpcData() {
		return npcData;
	}

	@Override
	public void reload(Player admin) {
		File dir = new File("./data/static_data/npcs");
		try {
			JAXBContext jc = JAXBContext.newInstance(StaticData.class);
			Unmarshaller un = jc.createUnmarshaller();
			un.setSchema(getSchema("./data/static_data/static_data.xsd"));
			List<NpcTemplate> newTemplates = new ArrayList<NpcTemplate>();
			for (File file : listFiles(dir, true)) {
				NpcData data = (NpcData) un.unmarshal(file);
				if (data != null && data.getData() != null)
					newTemplates.addAll(data.getData());
			}
			DataManager.NPC_DATA.setData(newTemplates);
		}
		catch (Exception e) {
			PacketSendUtility.sendMessage(admin, "Npc reload failed!");
			log.error("Npc reload failed!", e);
		}
		finally {
			PacketSendUtility.sendMessage(admin, "Npc reload Success! Total loaded: "+DataManager.NPC_DATA.size());
		}
	}
	
	protected List<NpcTemplate> getData() {
		return npcs;
	}
	
	@SuppressWarnings("unchecked")
	protected void setData(List<?> templates) {
		this.npcs = (List<NpcTemplate>) templates;
		afterUnmarshal(null, null);
	}
}