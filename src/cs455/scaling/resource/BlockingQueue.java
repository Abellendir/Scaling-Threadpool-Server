package cs455.scaling.resource;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription Implementation of a blocking queue to hold both workers and
 *              tasks
 * 
 * @param <E>
 */
public class BlockingQueue<E> {

	/**
	 * Underlying queue the class is holding
	 * 
	 */
	private Queue<E> queue = new LinkedList<E>();
	private final int EMPTY = 0; //Redundant TODO remove
	private final int MAXSIZE;

	/**
	 * @Discription Constructor enforces a max side on the queue;
	 */
	public BlockingQueue(int size) {
		this.MAXSIZE = size;
	}

	/**
	 * @Discription Adds an element to the end of a queue, blocking if the max size
	 *              is reached
	 * @param obj
	 * @throws InterruptedException
	 */
	public synchronized void enqueue(E obj) throws InterruptedException {
		while (this.queue.size() == this.MAXSIZE) {
			wait();
		}
		if (this.queue.size() == EMPTY) {
			notifyAll();
		}
		this.queue.offer(obj);
	}

	/**
	 * @Discription removes the first element in a queue, blocking if the queue is
	 *              empty
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized E dequeue() throws InterruptedException {
		while (this.queue.size() == EMPTY) {
			wait();
		}
		if (this.queue.size() == this.MAXSIZE) {
			notifyAll();
		}
		return this.queue.poll();
	}

	/**
	 * @Discription Clears the queue of all entries 
	 */
	public synchronized void clear() {
		queue.clear();
	}
}
