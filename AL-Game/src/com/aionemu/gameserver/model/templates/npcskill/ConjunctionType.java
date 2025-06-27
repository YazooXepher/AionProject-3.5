package com.aionemu.gameserver.model.templates.npcskill;

import javax.xml.bind.annotation.*;

/**
 * @author nrg
 */

@XmlType(name = "ConjunctionType")
@XmlEnum
public enum ConjunctionType {

	AND,
	OR,
	XOR;

	public String value() {
		return name();
	}

	public static ConjunctionType fromValue(String v) {
		return valueOf(v);
	}
}