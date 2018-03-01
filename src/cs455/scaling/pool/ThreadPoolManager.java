package cs455.scaling.pool;

import cs455.scaling.resource.BlockingQueue;
import cs455.scaling.resource.Task;

/**
 * 
 * @author Adam Bellendir
 * Manages the ThreadPool and distributes task to threads in the thread pool
 */
public class ThreadPoolManager implements Runnable {
	
	private final ThreadPool pool;
	private final BlockingQueue<Task> queue;
	
	public ThreadPoolManager(BlockingQueue<Task> queue, ThreadPool pool) {
		this.queue = queue;
		this.pool  = pool;
	}
	
	@Override
	public void run(){
		while(true) {
			try {
				Task task = queue.dequeue();
				ThreadPoolWorker worker = (ThreadPoolWorker) pool.get();
				worker.executeTask(task);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
