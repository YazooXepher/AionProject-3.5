package com.aionemu.gameserver.world;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.aionemu.gameserver.configs.main.WorldConfig;

/**
 * @author ATracer
 */
public class RegionUtilTest {

	@Before
	public void setup() {
		WorldConfig.WORLD_REGION_SIZE = 500;
	}

	@Test
	public void testGenerateReverse() {
		testRegionReverse(100, 100, 0f, 0f);
		testRegionReverse(600f, 600f, 500f, 500f);
		testRegionReverse(1000f, 1000f, 1000f, 1000f);
	}

	private void testRegionReverse(float x, float y, float expectedX, float expectedY) {
		int regionId = RegionUtil.get2dRegionId(x, y);
		float reversedX = RegionUtil.getXFrom2dRegionId(regionId);
		float reversedY = RegionUtil.getYFrom2dRegionId(regionId);
		Assert.assertEquals(expectedX, reversedX);
		Assert.assertEquals(expectedY, reversedY);
	}
}