package cs455.scaling.resource;

import cs455.scaling.task.Task;

public class ThreadPoolWorker implements Runnable {
	BlockingQueue<Task> queue;
	
	public ThreadPoolWorker() {
		
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				Task task = queue.dequeue();
			}
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
