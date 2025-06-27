package com.aionemu.gameserver.model.templates.ingameshop;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IGCategory")
public class IGCategory {

    @XmlElement(name = "sub_category")
	protected List<IGSubCategory> subCategories;
    @XmlAttribute(required = true)
    protected int id;
    @XmlAttribute(required = true)
    protected String name;

    public List<IGSubCategory> getSubCategories() {
        if (subCategories == null) {
            subCategories = new ArrayList<IGSubCategory>();
        }
        return this.subCategories;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}