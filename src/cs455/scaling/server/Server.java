package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cs455.scaling.pool.ThreadPool;
import cs455.scaling.pool.ThreadPoolManager;
import cs455.scaling.pool.ThreadPoolWorker;
import cs455.scaling.resource.BlockingQueue;
import cs455.scaling.resource.ChangeOps;
import cs455.scaling.tasks.Task;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 */
public class Server implements Runnable {

	private boolean debug;
	private Selector selector;
	private final int portNumber;
	private final BlockingQueue<Runnable> queue;
	private final List<ChangeOps> changeOps = new LinkedList<>();

	public Server(int portNumber, BlockingQueue<Runnable> queue, boolean debug) throws IOException {
		System.out.print("Initializing Server...");
		this.portNumber = portNumber;
		this.queue = queue;
		this.selector = Selector.open();
		this.debug = debug;
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void startServer() throws IOException, InterruptedException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(portNumber));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.print("Started\n");
		while (true) {
			synchronized(this.changeOps) {
				if(debug) System.out.println("Checking ChangeOps for key interest updates");
				Iterator changes = this.changeOps.iterator();
				if(debug) System.out.println("Number ready: " + changeOps.size());
				while(changes.hasNext()) {
					ChangeOps request = (ChangeOps) changes.next();
					changes.remove();
					SelectionKey key = request.getChannel().keyFor(selector);
					key.interestOps(SelectionKey.OP_READ);
				}
			}
			if(debug) System.out.println("this.selector.select()");
			int readyKeys = this.selector.select();
			if(debug) System.out.println("Keys ready for event: " + readyKeys);
			Iterator keys = this.selector.selectedKeys().iterator();
			while(keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();
				if(key.isAcceptable()) {
					this.accept(key);
				}
				else if(key.isReadable()){
					key.interestOps(0);
					queue.enqueue(new Task(key, changeOps, selector, debug));
				}
				keys.remove();
			}
		}

	}

	/**
	 * 
	 * @param key
	 * @throws IOException
	 */
	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel servSocket = (ServerSocketChannel) key.channel();
		SocketChannel channel = servSocket.accept();
		System.out.println("Accepting incoming connection...");
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
	}

	@Override
	public void run() {
		System.out.print("Starting Server...");
		try {
			this.startServer();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int portNumber = Integer.parseInt(args[0]);
		int threadPoolSize = Integer.parseInt(args[1]);
		boolean debug = false;
		if (args.length == 3) {
			if (args[2].equals("true")) {
				debug = true;
			}
		}
		System.out.println("Initializing ThreadPool...");
		ThreadPool pool = new ThreadPool(threadPoolSize);
		BlockingQueue<Runnable> queue = new BlockingQueue<>(1000);
		ThreadPoolWorker worker = null;
		for (int i = 0; i < threadPoolSize; i++) {
			worker = new ThreadPoolWorker(pool);
			new Thread(worker, ("Thread-" + (i + 1))).start();
			System.out.println("\tStarting worker Thread-" + (i + 1));
			try {
				pool.add(worker);
			} catch (InterruptedException e) {
				System.out.println("\tInterrupted Exception when adding worker to ThreadPool");
				e.printStackTrace();
			}
		}
		System.out.print("Starting ThreadPoolManager...");
		new Thread(new ThreadPoolManager(queue, pool)).start();
		System.out.print("Started\n");
		try {
			new Thread(new Server(portNumber, queue, debug), "Thread-Server").start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
