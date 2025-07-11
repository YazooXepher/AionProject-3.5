package com.aionemu.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "quests")
public class QuestsData {

	@XmlElement(name = "quest", required = true)
	protected List<QuestTemplate> questsData;
	private TIntObjectHashMap<QuestTemplate> questData = new TIntObjectHashMap<QuestTemplate>();
	private TIntObjectHashMap<List<QuestTemplate>> sortedByFactionId = new TIntObjectHashMap<List<QuestTemplate>>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		questData.clear();
		sortedByFactionId.clear();
		for (QuestTemplate quest : questsData) {
			questData.put(quest.getId(), quest);
			int npcFactionId = quest.getNpcFactionId();
			if (npcFactionId == 0 || quest.isTimeBased())
				continue;
			if (!sortedByFactionId.containsKey(npcFactionId)) {
				List<QuestTemplate> factionQuests = new ArrayList<QuestTemplate>();
				factionQuests.add(quest);
				sortedByFactionId.put(npcFactionId, factionQuests);
			}
			else {
				sortedByFactionId.get(npcFactionId).add(quest);
			}
		}
	}

	public QuestTemplate getQuestById(int id) {
		return questData.get(id);
	}

	public List<QuestTemplate> getQuestsByNpcFaction(int npcFactionId, Player player) {
		List<QuestTemplate> factionQuests = sortedByFactionId.get(npcFactionId);
		List<QuestTemplate> quests = new ArrayList<QuestTemplate>();
		QuestEnv questEnv = new QuestEnv(null, player, 0, 0);
		for (QuestTemplate questTemplate : factionQuests){
			if (!QuestEngine.getInstance().isHaveHandler(questTemplate.getId()))
				continue;
			if (questTemplate.getMinlevelPermitted() != 0 && player.getLevel() < questTemplate.getMinlevelPermitted())
				continue;
			questEnv.setQuestId(questTemplate.getId());
			if (QuestService.checkStartConditions(questEnv, false))
				quests.add(questTemplate);
		}
		return quests;
	}

	public int size() {
		return questData.size();
	}

	/**
	 * @return the questsData
	 */
	public List<QuestTemplate> getQuestsData() {
		return questsData;
	}

	/**
	 * @param questsData
	 *          the questsData to set
	 */
	public void setQuestsData(List<QuestTemplate> questsData) {
		this.questsData = questsData;
		afterUnmarshal(null, null);
	}
}