package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.cosmeticitems.CosmeticItemTemplate;
import java.util.List;
import java.util.Map;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javolution.util.FastMap;
/**
 *
 * @author xTz
 */
@XmlRootElement(name = "cosmetic_items")
@XmlAccessorType(XmlAccessType.FIELD)
public class CosmeticItemsData {
	@XmlElement(name = "cosmetic_item", type = CosmeticItemTemplate.class)
	private List<CosmeticItemTemplate> templates;
	private final Map<String, CosmeticItemTemplate> cosmeticItemTemplates = new FastMap<String, CosmeticItemTemplate>().shared();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (CosmeticItemTemplate template : templates) {
			cosmeticItemTemplates.put(template.getCosmeticName(), template);
		}
		templates.clear();
		templates = null;
	}

	public int size() {
		return cosmeticItemTemplates.size();
	}

	public CosmeticItemTemplate getCosmeticItemsTemplate(String str) {
		return cosmeticItemTemplates.get(str);
	}
}