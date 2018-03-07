package cs455.scaling.pool;

import cs455.scaling.resource.BlockingQueue;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription A container for the threads utilizing the BlockingQueue in the
 *              resource package
 */
public class ThreadPool {

	/**
	 * BlockingQueue to hold the worker threads, and facilitate which worker thread
	 * to be given a task
	 */
	private final BlockingQueue<ThreadPoolWorker> pool;

	/**
	 * @Discription Constructor to initialize the size of the thread pool
	 * @param numberThreads
	 */
	public ThreadPool(int numberThreads) {
		pool = new BlockingQueue<>(numberThreads);
	}

	/**
	 * @Discription Adds a thread to the thread pool and get blocked by the queue
	 * @param thread
	 * @throws InterruptedException
	 */
	public void add(ThreadPoolWorker thread) throws InterruptedException {
		pool.enqueue(thread);
	}

	/**
	 * @Discription Returns a free thread from the pool to process a job/event
	 * @return
	 * @throws InterruptedException
	 */
	public ThreadPoolWorker get() throws InterruptedException {
		return pool.dequeue();
	}
}
