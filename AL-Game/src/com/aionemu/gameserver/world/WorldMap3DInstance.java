package com.aionemu.gameserver.world;

import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * @author ATracer
 */
public class WorldMap3DInstance extends WorldMapInstance {

	/**
	 * @param parent
	 * @param instanceId
	 */
	public WorldMap3DInstance(WorldMap parent, int instanceId) {
		super(parent, instanceId);
	}

	@Override
	public MapRegion getRegion(float x, float y, float z) {
		int regionId = RegionUtil.get3dRegionId(x, y, z);
		return regions.get(regionId);
	}

	protected void initMapRegions() {
		int size = this.getParent().getWorldSize();
		float maxZ = Math.round((float) size / regionSize) * regionSize;

		// Create all mapRegion
		for (int x = 0; x <= size; x = x + regionSize) {
			for (int y = 0; y <= size; y = y + regionSize) {
				for (int z = 0; z < maxZ; z = z + regionSize) {
					int regionId = RegionUtil.get3dRegionId(x, y, z);
					regions.put(regionId, createMapRegion(regionId));
				}
			}
		}

		// Add Neighbour
		for (int x = 0; x <= size; x = x + regionSize) {
			for (int y = 0; y <= size; y = y + regionSize) {
				for (int z = 0; z < maxZ; z = z + regionSize) {
					int regionId = RegionUtil.get3dRegionId(x, y, z);
					MapRegion mapRegion = regions.get(regionId);
					for (int x2 = x - regionSize; x2 <= x + regionSize; x2 += regionSize) {
						for (int y2 = y - regionSize; y2 <= y + regionSize; y2 += regionSize) {
							for (int z2 = z - regionSize; z2 < z + regionSize; z2 += regionSize) {
								if (x2 == x && y2 == y && z2 == z)
									continue;
								int neighbourId = RegionUtil.get3dRegionId(x2, y2, z2);
								MapRegion neighbour = regions.get(neighbourId);
								if (neighbour != null)
									mapRegion.addNeighbourRegion(neighbour);
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected MapRegion createMapRegion(int regionId) {
		float startX = RegionUtil.getXFrom3dRegionId(regionId);
		float startY = RegionUtil.getYFrom3dRegionId(regionId);
		float startZ = RegionUtil.getZFrom3dRegionId(regionId);
		ZoneInstance[] zones = filterZones(this.getMapId(), regionId, startX, startY, startZ, startZ + regionSize);
		return new MapRegion(regionId, this, zones);
	}

	@Override
	public boolean isPersonal() {
		return false;
	}

	@Override
	public int getOwnerId() {
		return 0;
	}
}