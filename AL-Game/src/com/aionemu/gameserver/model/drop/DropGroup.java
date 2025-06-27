package com.aionemu.gameserver.model.drop;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dropGroup", propOrder = { "drop" })
public class DropGroup implements DropCalculator {

    protected List<Drop> drop;
    @XmlAttribute
    protected Race race = Race.PC_ALL;
    @XmlAttribute(name = "use_category")
    protected Boolean useCategory = true;
    @XmlAttribute(name = "name")
    protected String group_name;

    public List<Drop> getDrop() {
        return this.drop;
    }

    public Race getRace() {
        return race;
    }

    public Boolean isUseCategory() {
        return useCategory;
    }

    /**
     * @return the name
     */
    public String getGroupName() {
        if (group_name == null)
            return "";
        return group_name;
    }

    @Override
    public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, Collection<Player> groupMembers) {
        if (useCategory) {
            Drop d = drop.get(Rnd.get(0, drop.size() - 1));
            return d.dropCalculator(result, index, dropModifier, race, groupMembers);
        }

        for (int i = 0; i < drop.size(); i++) {
            Drop d = drop.get(i);
            index = d.dropCalculator(result, index, dropModifier, race, groupMembers);
        }
        return index;
    }
}