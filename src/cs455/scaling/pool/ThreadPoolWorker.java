package cs455.scaling.pool;

import cs455.scaling.resource.BlockingQueue;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription
 * 
 */
public class ThreadPoolWorker implements Runnable {

	private volatile boolean kill = false;
	private final ThreadPool pool;

	BlockingQueue<Runnable> queue = new BlockingQueue<Runnable>(1);

	public ThreadPoolWorker(ThreadPool pool) {
		this.pool = pool;
	}

	@Override
	public void run() {
		while (!kill) {
			try {
				Runnable task = queue.dequeue();
				task.run();
				pool.add(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @param task
	 * @throws InterruptedException
	 */
	public synchronized void executeTask(Runnable task) throws InterruptedException {
		queue.enqueue(task);
	}
	
	public void kill() {
		this.kill = !kill;
	}
}
