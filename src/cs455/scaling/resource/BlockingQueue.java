package cs455.scaling.resource;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription Implementation of a blocking queue to hold both workerthreads
 *              and tasks
 * 
 * @param <E>
 */
public class BlockingQueue<E> {

	private Queue<E> queue = new LinkedList<E>();
	private final int EMPTY = 0;
	private final int MAXSIZE;

	public BlockingQueue(int size) {
		this.MAXSIZE = size;
	}

	public synchronized void enqueue(E obj) throws InterruptedException {
		while (this.queue.size() == this.MAXSIZE) {
			wait();
		}
		if (this.queue.size() == EMPTY) {
			notifyAll();
		}
		this.queue.offer(obj);
	}

	public synchronized E dequeue() throws InterruptedException {
		while (this.queue.size() == EMPTY) {
			wait();
		}
		if (this.queue.size() == this.MAXSIZE) {
			notifyAll();
		}
		return this.queue.poll();
	}
	
	public synchronized void clear() {
		queue.clear();
	}
}
