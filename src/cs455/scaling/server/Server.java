package cs455.scaling.server;

import java.io.IOException;
import java.nio.channels.Selector;

import cs455.scaling.pool.ThreadPool;
import cs455.scaling.pool.ThreadPoolManager;
import cs455.scaling.pool.ThreadPoolWorker;
import cs455.scaling.resource.BlockingQueue;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 */
public class Server {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int portNumber = Integer.parseInt(args[0]);
		int threadPoolSize = Integer.parseInt(args[1]);
		ThreadPool pool = new ThreadPool(threadPoolSize);
		BlockingQueue<Runnable> queue = new BlockingQueue<>(1000);
		ThreadPoolWorker worker = null;
		for(int i = 0; i < threadPoolSize; i++) {
			worker = new ThreadPoolWorker(pool);
			new Thread(worker, ("Thread-" + i)).start();
			try {
				pool.add(worker);
			} catch (InterruptedException e) {
				System.err.print("Interrupted Exception when adding worker to ThreadPool");
				e.printStackTrace();
			}
		}
		ThreadPoolManager manager = new ThreadPoolManager(queue,pool);
		new Thread(manager).start();
		ServerThread server;
		try {
			server = new ServerThread(portNumber,8000,queue);
			new Thread(server, "Thread-Server").start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
