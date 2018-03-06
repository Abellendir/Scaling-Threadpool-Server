package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import cs455.scaling.operations.MessageHashCode;
import cs455.scaling.util.StatisticsPrinterClient;

/**
 * 
 * @author Adam Bellendir
 *
 */
public class Writer implements Runnable {

	private boolean kill = false;
	private final List<String> list;
	private final SocketChannel channel;
	private final int r;
	private boolean debug;
	private StatisticsPrinterClient stats = StatisticsPrinterClient.getInstance();
	private Client client;
	private Selector selector;

	/**
	 * 
	 * @param key
	 * @param r
	 * @param list
	 * @param debug
	 * @param client
	 * @param selector
	 */
	public Writer(SelectionKey key, int r, List<String> list, boolean debug, Selector selector, Client client) {
		System.out.print("Writer Initializing...");
		this.r = r;
		this.list = list;
		this.channel = (SocketChannel) key.channel();
		this.debug = debug;
		this.selector = selector;
		this.client = client;
	}

	@Override
	public void run() {
		System.out.print("Starting to send messages\n");
		Random random = new Random();
		while (channel != null) {
			try {
				write(random);
			} catch (NoSuchAlgorithmException | IOException | InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		selector.wakeup();
		try {
			selector.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + " closed");
		try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * 
	 */
	private void write(Random random) throws NoSuchAlgorithmException, IOException, InterruptedException {
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
			this.channel.write(buffer);

			// Clears the buffer for clean up
			buffer.clear();

			// Increment stats in the stat collector to be printed every 20 seconds
			stats.incrementSent();

			// Number of messages to send every second
			Thread.sleep(1000 / this.r);

	}

	private synchronized boolean closed() {
		return kill;
	}

	public synchronized void close() {
		this.kill = !kill;
	}

}
