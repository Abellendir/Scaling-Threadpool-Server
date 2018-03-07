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

	/**
	 * 
	 */
	private volatile boolean kill = false;
	private final ThreadPool pool;
	private final BlockingQueue<Runnable> queue;

	/**
	 * @Discription Constructor takes the queue of jobs to be completed and the
	 *              ThreadPool
	 * @param queue
	 * @param pool
	 */
	public ThreadPoolManager(BlockingQueue<Runnable> queue, ThreadPool pool) {
		this.queue = queue;
		this.pool = pool;
	}

	/**
	 * @Discription Threads run will wait for incoming tasks, and wait for a worker
	 *              thread to become available to execute the task
	 */
	@Override
	public void run() {
		while (!isShutdown()) {
			try {

				/*
				 * Waits for an available task from the queue, waiting if necessary in a FIFO
				 * pattern
				 */
				Runnable task = queue.dequeue();

				/*
				 * Looks for the poison task to be given to the Manager to begin the shutdown
				 * process else carry on as usual and pass a job to a thread to be processed
				 */
				if (task instanceof Shutdown) {
					Thread.sleep(2000);
					for (int i = 0; i < 10; i++) {
						queue.clear();
						ThreadPoolWorker worker = pool.get();
						worker.terminate();
						worker.executeTask(new Runnable() {
							@Override
							public void run() {
								/*
								 * Left blank, forces the thread to wake up and exit it's loop
								 */
							}
						});
					}
					task.run();
					shutdown();
				} else {

					/*
					 * Thread retrieves and available worker to execute the next task, or waits tell
					 * one is available
					 */
					ThreadPoolWorker worker = pool.get();
					worker.executeTask(task);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*
		 * Console output to notify the user the Thread is closed
		 */
		System.out.println(Thread.currentThread().getName() + " has closed");
	}

	/**
	 * @Discription
	 */
	private void shutdown() {
		System.out.println("Threadpool Manager - closing");
		this.kill = !kill;
	}

	/**
	 * @Discription
	 * @return
	 */
	public boolean isShutdown() {
		if (kill) {
			System.out.println("ThreadPool Manager - Shutdown has been initiated");
		}
		return kill;
	}

}
