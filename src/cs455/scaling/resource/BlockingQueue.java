package cs455.scaling.resource;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<Task> {
	private Queue<Task> queue = new LinkedList<Task>();
	private final int EMPTY = 0;
	private final int MAXSIZE;
	
	public BlockingQueue(int size) {
		this.MAXSIZE = size;
	}
	
	public synchronized void enqueue(Task task) throws InterruptedException{
		while(this.queue.size()==this.MAXSIZE) {
			wait();
		}
		if(this.queue.size() == EMPTY) {
			notifyAll();
		}
		this.queue.offer(task);
	}
	
	public synchronized Task dequeue() throws InterruptedException{
		while(this.queue.size()==EMPTY) {
			wait();
		}
		if(this.queue.size() == this.MAXSIZE) {
			notifyAll();
		}
		return this.queue.poll();
	}
}
