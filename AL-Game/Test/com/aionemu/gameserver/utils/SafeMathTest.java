package com.aionemu.gameserver.utils;

import org.junit.Test;

/**
 * @author MrPoke
 *
 */
public class SafeMathTest {
	@Test
	public void intMultiTest(){
		try {
			SafeMath.multSafe(1000000, 10000000);
		}
		catch (OverfowException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void longMultiTest(){
		try {
			SafeMath.multSafe(100000000000L, 1000000000000L);
		}
		catch (OverfowException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void intAddTest(){
		try {
			SafeMath.addSafe(Integer.MAX_VALUE-10, 11);
		}
		catch (OverfowException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void longAddTest(){
		try {
			SafeMath.addSafe(Long.MAX_VALUE-10, 11);
		}
		catch (OverfowException e) {
			System.out.println(e.getMessage());
		}
	}
}