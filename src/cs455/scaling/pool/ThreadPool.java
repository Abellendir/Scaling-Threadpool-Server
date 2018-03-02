package cs455.scaling.pool;

import cs455.scaling.resource.BlockingQueue;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription Container for the threads
 */
public class ThreadPool {

	private final BlockingQueue<ThreadPoolWorker> pool;

	public ThreadPool(int numberThreads) {
		pool = new BlockingQueue<>(numberThreads);
	}

	public void add(ThreadPoolWorker thread) throws InterruptedException {
		pool.enqueue(thread);
	}

	public ThreadPoolWorker get() throws InterruptedException {
		return pool.dequeue();
	}
}
