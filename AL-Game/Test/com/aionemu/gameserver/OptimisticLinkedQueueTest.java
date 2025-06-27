package com.aionemu.gameserver;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.utils.collections.OptimisticLinkedQueue;

/**
 * @author Rolandas
 */
public class OptimisticLinkedQueueTest {

	static OptimisticLinkedQueue<Integer> queue = new OptimisticLinkedQueue<Integer>();
	// ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
	static AtomicInteger totalAdded = new AtomicInteger();

	@Test
	public void test() {

		Thread[] threads = new Thread[1000];
		for (int i = 0; i < 1000; i++) {
			threads[i] = new Thread() {

				@Override
				public void run() {
					if (Rnd.get(100) < 50) {
						int addCount = Rnd.get(200);
						int addedCount = 0;
						for (int i = 0; i < addCount; i++) {
							Integer val = Integer.valueOf(Rnd.get(100));
							synchronized (queue) {
								if (queue.offer(val))
									addedCount++;
							}
						}
						totalAdded.addAndGet(addedCount);
						assertTrue(addCount == addedCount);
					}
					else {
						int removeCount = Rnd.get(200);
						int removed = 0;
						for (int i = 0; i < removeCount; i++) {
							synchronized (queue) {
								if (queue.poll() == null)
									continue;
								removed++;
							}
						}
						totalAdded.addAndGet(-removed);
					}
				}
			};
			threads[i].start();
		}

		for (int i = 0; i < 100; i++) {
			try {
				threads[i].join();
			}
			catch (InterruptedException e) {
			}
		}

		queue.leaveTail();
		assertTrue(queue.size() == 1);

		queue.clear();
		assertTrue(queue.size() == 0);
	}
}