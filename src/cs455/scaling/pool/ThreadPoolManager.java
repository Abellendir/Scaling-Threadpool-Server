package cs455.scaling.pool;

import cs455.scaling.resource.BlockingQueue;
import cs455.scaling.tasks.Shutdown;

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

	private volatile boolean kill = false;
	private final ThreadPool pool;
	private final BlockingQueue<Runnable> queue;

	public ThreadPoolManager(BlockingQueue<Runnable> queue, ThreadPool pool) {
		this.queue = queue;
		this.pool = pool;
	}

	@Override
	public void run() {
		while (!shutdown()) {
			try {
				Runnable task = queue.dequeue();
				if (task instanceof Shutdown) {
					Thread.sleep(2000);
					for(int i = 0; i < 10; i++) {
						queue.clear();
						ThreadPoolWorker worker = pool.get();
						worker.close();
						worker.executeTask(new Runnable() {
							@Override
							public void run() {
							}
						});
					}
					task.run();
					kill();
				} else {
					ThreadPoolWorker worker = pool.get();
					worker.executeTask(task);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread().getName() + " has closed");
	}

	private void kill() {
		System.out.println("Threadpool manager closing");
		this.kill = !kill;
	}

	public boolean shutdown() {
		return kill;
	}

}
