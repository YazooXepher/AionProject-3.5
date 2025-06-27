package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.drop.NpcDrop;

/**
 * @author MrPoke
 */
@XmlRootElement(name = "npc_drops")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "npcDropData", propOrder = { "npcDrop" })
public class NpcDropData {

    @XmlElement(name = "npc_drop")
    protected List<NpcDrop> npcDrop;

    /**
     * @return the npcDrop
     */
    public List<NpcDrop> getNpcDrop() {
        return npcDrop;
    }

    /**
     * @param npcDrop
     *          the npcDrop to set
     */
    public void setNpcDrop(List<NpcDrop> npcDrop) {
        this.npcDrop = npcDrop;
    }

    public int size() {
        return npcDrop.size();
    }
}