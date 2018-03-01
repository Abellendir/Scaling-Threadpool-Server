package cs455.scaling.pool;

import cs455.scaling.resource.BlockingQueue;

/**
 * 
 * @author Adam Bellendir
 * Container for the threads
 */
public class ThreadPool{
	
	private final BlockingQueue<Runnable> pool;
	
	public ThreadPool(int numberThreads) {
		pool = new BlockingQueue<>(numberThreads);
	}
	
	public void add(Runnable thread) throws InterruptedException{
		pool.enqueue(thread);
	}
	
	public Runnable get() throws InterruptedException{
		return pool.dequeue();
	}
}
