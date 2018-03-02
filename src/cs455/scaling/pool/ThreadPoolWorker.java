package cs455.scaling.pool;

import cs455.scaling.resource.BlockingQueue;
import cs455.scaling.resource.Task;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription Worker thread that accepts a task, executes the task and returns
 *              itself back to the thread pool
 */
public class ThreadPoolWorker implements Runnable {

	private final ThreadPool pool;

	BlockingQueue<Task> queue = new BlockingQueue<Task>(1);

	public ThreadPoolWorker(ThreadPool pool) {
		this.pool = pool;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Task task = queue.dequeue();
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
	public synchronized void executeTask(Task task) throws InterruptedException {
		queue.enqueue(task);
	}
}
