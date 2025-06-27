package com.aionemu.gameserver.model.templates.quest;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author vlog
 */
@XmlType(name = "QuestRepeatCycle")
@XmlEnum
public enum QuestRepeatCycle {

	ALL,
	MON,
	TUE,
	WED,
	THU,
	FRI,
	SAT,
	SUN,
}