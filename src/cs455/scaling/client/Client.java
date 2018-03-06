package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cs455.scaling.operations.MessageHashCode;
import cs455.scaling.util.StatisticsPrinterClient;

/**
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Description
 * 
 */
public class Client implements Runnable {

	private Selector selector;
	private final String hostAddress;
	private final int port;
	private final int r;
	private final List<String> list = new LinkedList<>();
	private boolean debug;
	private StatisticsPrinterClient stats = StatisticsPrinterClient.getInstance();
	private boolean kill = false;
	private boolean closed = false;

	public Client(String hostName, int port, int r, boolean debug) throws IOException {
		System.out.print("Initializing Client...");
		this.hostAddress = hostName;
		this.selector = Selector.open();
		this.port = port;
		this.r = r;
		this.debug = debug;

	}

	private void startClient() throws IOException, InterruptedException, NoSuchAlgorithmException {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_CONNECT);
		channel.connect(new InetSocketAddress(this.hostAddress, this.port));
		System.out.print("Client started\n");
		while (!closed()) {
			this.selector.select(4000);
			if (!selector.isOpen())
				break;
			Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
			SelectionKey key = keys.next();
			if (key.isConnectable()) {
				System.out.println("Client connecting to server");
				this.connect(key);
				System.out.println("Client connected to server");
				//new Thread(new Writer(key, this.r, this.list, this.debug, this.selector, this), "Thread-Writer").start();
			} if (key.isWritable()) {
				write(key);
			} if (key.isReadable()) {
				read(key);
			} 
			if (closed())
				break;
			Thread.sleep(1000/(this.r));
		}
		System.out.println("Selector Timed out, Server Disconnected");
		System.out.println(Thread.currentThread().getName() + " has closed");
		selector.close();
		channel.close();
		stats.kill();
	}

	/**
	 * @param SelectionKey
	 * @throws IOException
	 */
	private void read(SelectionKey key) throws IOException {
		
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(20);
		int read = 0;
		while (buffer.hasRemaining() && read != -1) {
			read = channel.read(buffer);
		}
		buffer.flip();
		byte[] bytes = new byte[20];
		buffer.get(bytes);
		boolean contains = list.contains(new BigInteger(1, bytes).toString(16));
		stats.incrementReceived();
		if (debug)
			System.out.println("Hash received from server " + new BigInteger(1, bytes).toString(16)
					+ " matches = " + contains);
	}

	private void write(SelectionKey key) throws NoSuchAlgorithmException, IOException, InterruptedException {
		SocketChannel channel = (SocketChannel) key.channel();
		Random random = new Random();
		byte[] data = new byte[8192];
		random.nextBytes(data);

		// Hashes the message to be sent
		byte[] hash = MessageHashCode.SHA1FromBytes(data);

		// Debug statement
		if (debug)
			System.out.println("Client Sending message hashed to: " + new BigInteger(1, hash).toString(16));

		// Adds the message as a string to be later verified
		list.add(new BigInteger(1, hash).toString(16));

		// Prepares the data to be sent
		ByteBuffer buffer = ByteBuffer.wrap(data);

		// Writes the data to the channel
		channel.write(buffer);

		// Clears the buffer for clean up
		buffer.clear();

		// Increment stats in the stat collector to be printed every 20 seconds
		stats.incrementSent();

		// Number of messages to send every second
		//Thread.sleep(1000 / this.r);

	}

	public synchronized void close() {
		this.kill = true;
	}

	public synchronized boolean closed() {
		// System.out.println(kill);
		return this.kill;
	}

	private void connect(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		channel.finishConnect();
		key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
	}

	@Override
	public void run() {
		System.out.print("Starting client...");
		try {
			startClient();
		} catch (IOException | InterruptedException e) {
			stats.kill();
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void shutdown() {
		this.closed = !closed;
	}

	private boolean isClosed() {
		return closed;
	}

	public static void main(String[] args) throws IOException {
		String serverHost = args[0];
		int serverPort = Integer.parseInt(args[1]);
		int messageRate = Integer.parseInt(args[2]);
		boolean debug = false;
		if (args.length == 4) {
			if (args[3].equals("true")) {
				debug = true;
			}
		}
		Client client = new Client(serverHost, serverPort, messageRate, debug);
		new Thread(client, "Thread-Client").start();
		new Thread(StatisticsPrinterClient.getInstance(), "Thread-Output").start();

	}

}
