package com.aionemu.gameserver.model;

import java.util.ArrayList;
import com.aionemu.gameserver.dataholders.WorldMapsData;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;

/**
 * @author Rolandas
 */
public class DummyWorldMapData extends WorldMapsData {

	public static final int DEFAULT_MAP = 10010000;

	public DummyWorldMapData() {
		super.worldMaps = new ArrayList<WorldMapTemplate>();
		worldMaps.add(new DummyWorldTemplate(DummyZoneData.DEFAULT_SIZE));
		super.afterUnmarshal(null, null);
	}

	public static class DummyWorldTemplate extends WorldMapTemplate {

		public DummyWorldTemplate(int size) {
			super.mapId = DEFAULT_MAP;
			super.worldSize = size;
		}
	}
}