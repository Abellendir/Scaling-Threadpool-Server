package cs455.scaling.pool;

import cs455.scaling.resource.BlockingQueue;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription Worker thread for the ThreadPool, responsible for executing
 *              tasks it is given.
 * 
 */
public class ThreadPoolWorker implements Runnable {

	/**
	 * 
	 */
	private volatile boolean kill = false;
	private final ThreadPool pool;
	private Runnable job;
	BlockingQueue<Runnable> queue = new BlockingQueue<Runnable>(1);

	/**
	 * @Discription Constructor for the work, is given the pool that it is contained
	 *              in. This facilitates it being able to re-add it's self to the
	 *              the thread pool for the ThreadPoolManager to be able to select
	 *              this thread again.
	 * @param pool
	 */
	public ThreadPoolWorker(ThreadPool pool) {
		this.pool = pool;
	}

	/**
	 * @Discription Executes the given job
	 */
	@Override
	public void run() {
		while (!isTerminate()) {
			try {
				// Runnable task = getJob();
				Runnable task = queue.dequeue();
				task.run();
				pool.add(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread().getName() + " has closed");
	}

	/**
	 * @Discription Gives the worker a task, waking it up so it begins executing.
	 * @param task
	 * @throws InterruptedException
	 */
	public synchronized void executeTask(Runnable job) throws InterruptedException {
		queue.enqueue(job);
		/*
		 * if(this.job != null) { wait(); } this.job = job; notifyAll();
		 * 
		 */
	}

	/**
	 * @Discription Working a new potential implementation to eliminate the need for
	 *              the BlockingQueue, so the worker is self contained
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized Runnable nextJob() throws InterruptedException {
		while (job == null) {
			wait();
		}
		return this.job;
	}

	/**
	 * @Discription Sets the worker to be terminated at the end of the current loop,
	 *              need to send a runnable to wake the thread up to terminate it.
	 */
	public synchronized void terminate() {
		this.kill = !kill;
	}

	/**
	 * @Discription Returns if the worker is marked for termination
	 * @return
	 */
	public boolean isTerminate() {
		return kill;
	}
}
