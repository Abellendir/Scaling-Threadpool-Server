package cs455.scaling.pool;

import cs455.scaling.resource.BlockingQueue;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Description Manages the ThreadPool and distributes task to threads in the
 *              thread pool
 */
public class ThreadPoolManager implements Runnable {

	private final ThreadPool pool;
	private final BlockingQueue<Runnable> queue;

	public ThreadPoolManager(BlockingQueue<Runnable> queue, ThreadPool pool) {
		this.queue = queue;
		this.pool = pool;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Runnable task = queue.dequeue();
				ThreadPoolWorker worker = (ThreadPoolWorker) pool.get();
				worker.executeTask(task);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
