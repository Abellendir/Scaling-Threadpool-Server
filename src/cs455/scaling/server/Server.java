package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import cs455.scaling.pool.ThreadPool;
import cs455.scaling.pool.ThreadPoolManager;
import cs455.scaling.pool.ThreadPoolWorker;
import cs455.scaling.resource.BlockingQueue;
import cs455.scaling.resource.ChangeOps;
import cs455.scaling.resource.IndividualClientThroughPut;
import cs455.scaling.tasks.Task;
import cs455.scaling.tasks.Shutdown;
import cs455.scaling.util.StatisticsPrinterServer;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Description Server class that monitors for incoming connections and read
 *              operations. Passes read operations to the job queue to be
 *              handled by the ThreadPoolManager
 * 
 */
public class Server implements Runnable {

	/**
	 * 
	 */
	private boolean debug;
	private Selector selector;
	private final int portNumber;
	private final BlockingQueue<Runnable> queue;
	private final List<ChangeOps> changeOps = new LinkedList<>();
	private HashMap<SocketChannel, IndividualClientThroughPut> clients;
	private boolean closed = false;
	/**
	 * @Discription Server Constructor
	 * @param portNumber
	 *            this server is to be listing on
	 * @param queue
	 *            for the jobs that the server needs to have process for the
	 *            socketChannel
	 * @param clients
	 *            HashMap to store channels and a stat collector to be used by
	 *            StatisticsPrinterServer
	 * @param debug
	 *            For print statements to verify messages are being sent and hash
	 *            correctly
	 * @throws IOException
	 */
	public Server(int portNumber, BlockingQueue<Runnable> queue,
			HashMap<SocketChannel, IndividualClientThroughPut> clients, boolean debug) throws IOException {
		System.out.print("Initializing Server...");
		this.portNumber = portNumber;
		this.queue = queue;
		this.selector = Selector.open();
		this.clients = clients;
		this.debug = debug;
	}

	/**
	 * @Discription Primary Loop for the server where it listens for incoming
	 *              communication from clients and hands them to the queue to be
	 *              processed
	 * @throws IOException
	 */
	private void startServer() throws IOException, InterruptedException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(portNumber));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.print("Started\n");
		while (!shutdown()) {
			synchronized (this.changeOps) {
				Iterator<ChangeOps> changes = this.changeOps.iterator();
				while (changes.hasNext()) {
					ChangeOps request = (ChangeOps) changes.next();
					changes.remove();
					SelectionKey key = request.getChannel().keyFor(selector);
					key.interestOps(SelectionKey.OP_READ);
				}
			}
			int readyKeys = this.selector.select();
			if (debug)
				System.out.println("Keys ready for event: " + readyKeys);
			Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();
				if (!key.isValid()) {
					continue;
				}
				if (key.isAcceptable()) {
					this.accept(key);
				} else if (key.isReadable()) {
					key.interestOps(0);
					queue.enqueue(new Task(key, changeOps, selector, clients, debug));
				}
				keys.remove();
			}
		}
		Iterator<SocketChannel> channels = clients.keySet().iterator();
		while (channels.hasNext()) {
			SocketChannel channel = (SocketChannel) channels.next();
			channel.close();
		}
		selector.close();
		serverSocketChannel.close();

	}

	/**
	 * @Discription CHeck if the server is suppose to shutdown: ! not properly
	 *              implemented yet
	 * 
	 * @return
	 */
	private synchronized boolean shutdown() {
		return this.closed;
	}

	/**
	 * @Discription shutdown command to be called by an entity who has scope of the
	 *              server
	 */
	public synchronized void close() {
		System.out.println("Closing Server...");
		this.closed = !closed;
		this.selector.wakeup();
	}

	/**
	 * @Discription Accepts incoming connections from clients who are requesting a
	 *              connection
	 * @param key
	 * @throws IOException
	 */
	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel servSocket = (ServerSocketChannel) key.channel();
		SocketChannel channel = servSocket.accept();
		System.out.println("Accepting incoming connection...");
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
		synchronized (clients) {
			clients.put(channel, new IndividualClientThroughPut());
		}
	}

	/**
	 * @Discription Testing keeping the read method in the server instead of the
	 *              server
	 * @param key
	 * @return
	 * @throws IOException
	 * @deprecated
	 */
	public byte[] read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(8192);
		int read = 0;
		try {
			while (buffer.hasRemaining() && read != -1) {
				read = channel.read(buffer);
			}
		} catch (IOException e) {
			key.cancel();
			channel.close();
		}
		buffer.flip();
		if (read == -1) {
			/* Connection was terminated by the client. */
			key.cancel();
			channel.close();
		}
		byte[] received = new byte[8192];
		buffer.get(received);
		return received;
		// this.key.interestOps(SelectionKey.OP_WRITE); 
	}

	/**
	 * Testing keeping the write operation method in the server and have the task
	 * call it
	 * 
	 * @deprecated
	 * @param data
	 * @param key
	 * @throws IOException
	 */
	public void write(byte[] data, SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		channel.write(buffer);
		key.interestOps(SelectionKey.OP_READ);
		this.selector.wakeup();
	}

	/**
	 * @Discription Run method to initiate server thread
	 */
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
		System.out.println(Thread.currentThread().getName() + " has closed");
	}

	/**
	 * @Discription driver for initiating the server
	 * @param args
	 */
	public static void main(String[] args) {

		// Cmd line args to initialize Port number and Number of server threads
		int portNumber = Integer.parseInt(args[0]);
		int threadPoolSize = Integer.parseInt(args[1]);
		boolean debug = false;

		// Checks to see if the cmd line args include a debug command
		if (args.length == 3) {
			if (args[2].equals("true")) {
				debug = true;
			}
		}

		// Initializing the Threadpool and adding them to a thread pool
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

		// Initializing the ThreadPoolManager and starting it's thread
		System.out.print("Starting ThreadPoolManager...");
		new Thread(new ThreadPoolManager(queue, pool), "Thread-Manager").start();
		System.out.print("Started\n");

		/*
		 * Client HashMap to track number of connections, number of messages sent, for
		 * throughput of server overall and individual mean throughput of clients
		 */
		HashMap<SocketChannel, IndividualClientThroughPut> clients = new HashMap<>();

		// initialization of stats collector
		StatisticsPrinterServer stats = StatisticsPrinterServer.getInstance();
		stats.giveHashMap(clients);

		// Initialization of the Server thread and stats Thread
		Server server = null;
		try {
			server = new Server(portNumber, queue, clients, debug);
			new Thread(server, "Thread-Server").start();
			new Thread(StatisticsPrinterServer.getInstance(), "Thread-Stats Collector").start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Scanner to wait for the kill cmd to be entered from cmd line
		Scanner scan = new Scanner(System.in);
		String cmd = "";
		while (!cmd.equals("kill")) {
			cmd = scan.nextLine();
			if (cmd.equals("kill")) {
				try {
					if (server != null)
						queue.enqueue(new Shutdown(server));
					stats.kill();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		scan.close();
	}
}
