package com.aionemu.gameserver.model.drop;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

@XmlRootElement(name = "npc_drop")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "npcDrop", propOrder = { "dropGroup" })
public class NpcDrop implements DropCalculator {

    @XmlElement(name = "drop_group")
    protected List<DropGroup> dropGroup;
    @XmlAttribute(name = "npc_id", required = true)
    protected int npcId;

    public List<DropGroup> getDropGroup() {
        if (dropGroup == null)
            return Collections.emptyList();
        return this.dropGroup;
    }

    /**
     * Gets the value of the npcId property.
     */
    public int getNpcId() {
        return npcId;
    }

    @Override
    public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, Collection<Player> groupMembers) {
        if (dropGroup == null || dropGroup.isEmpty())
            return index;
        for (DropGroup dg : dropGroup) {
            if (dg.getRace() == Race.PC_ALL || dg.getRace() == race) {
                index = dg.dropCalculator(result, index, dropModifier, race, groupMembers);
            }
        }
        return index;
    }
}