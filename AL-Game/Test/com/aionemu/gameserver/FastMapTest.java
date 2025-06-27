package com.aionemu.gameserver;

import javolution.util.FastMap;
import javolution.util.FastMap.Entry;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author VladimirZ
 */
public class FastMapTest {

	@Test
	public void removeTestShared() {
		FastMap<Integer, String> map = new FastMap<Integer, String>().shared();
		map.put(1, "test1");
		map.put(2, "test2");
		map.put(3, "test3");
		map.put(4, "test4");
		map.put(5, "test5");
		map.remove(5);
		Entry<Integer, String> entry = map.tail();

		// check for memory leak in shared fastmap
		Assert.assertEquals(entry.getNext().getPrevious().getValue(), null);
	}

	@Test
	public void removeTest() {
		FastMap<Integer, String> map = new FastMap<Integer, String>();
		map.put(1, "test1");
		map.put(2, "test2");
		map.put(3, "test3");
		map.put(4, "test4");
		map.put(5, "test5");
		map.remove(5);
		Entry<Integer, String> entry = map.tail();

		// check for memory leak in non-shared fastmap
		Assert.assertEquals(entry.getNext().getPrevious().getValue(), null);
	}
}