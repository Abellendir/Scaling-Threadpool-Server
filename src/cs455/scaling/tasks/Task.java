package cs455.scaling.tasks;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import cs455.scaling.operations.MessageHashCode;
import cs455.scaling.resource.ChangeOps;
import cs455.scaling.resource.IndividualClientThroughPut;
import cs455.scaling.util.StatisticsPrinterServer;

/**
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription Primary task that handles the read, hash and write operation for
 *              the current channel. ALso updates the stats of the statistics
 *              printer for the 20s Server Message.
 */
public class Task implements Runnable {

	/**
	 * Variables for the task SelectionKey key -
	 */
	SelectionKey key;
	private byte[] received = new byte[8192];
	private boolean debug;
	private List<ChangeOps> list;
	private Selector selector;
	private HashMap<SocketChannel, IndividualClientThroughPut> clients;
	private StatisticsPrinterServer stats = StatisticsPrinterServer.getInstance();

	/**
	 * @Discription Constructor that initializes the task before it is ready for
	 *              execution
	 * @param key
	 * @param changeOps
	 * @param selector
	 * @param clients
	 * @param debug
	 */
	public Task(SelectionKey key, List<ChangeOps> changeOps, Selector selector,
			HashMap<SocketChannel, IndividualClientThroughPut> clients, boolean debug) {
		this.key = key;
		this.list = changeOps;
		this.debug = debug;
		this.selector = selector;
		this.clients = clients;
	}

	/**
	 * @Discription TODO
	 */
	@Override
	public void run() {
		try {
			if (debug)
				System.out.println("Server processing message: ");
			read(key);
			if (debug)
				System.out.println("Server processed Message...hashing");
			byte[] data = MessageHashCode.SHA1FromBytes(received);
			if (debug)
				System.out.println(Thread.currentThread().getName() + ": Hashed Message To: "
						+ new BigInteger(1, data).toString(16));
			write(data, key);
			synchronized (clients) {
				clients.get((SocketChannel) key.channel()).incrementValue();
			}
			stats.incrementProcessed();
			if (debug)
				System.out.println("Server sent hashcode");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @Discription Reads the channel and saves the data in a field variable to be
	 *              hashed
	 * @param key
	 * @throws IOException
	 */
	private void read(SelectionKey key) throws IOException {
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
			return;
		}
		buffer.get(received);
		synchronized (this.list) {
			list.add(new ChangeOps(channel, SelectionKey.OP_READ));
		}
		/* Testing some design implementations */
		// this.selector.wakeup();
		// this.key.interestOps(SelectionKey.OP_WRITE);
	}

	/**
	 * @Discription Writes the hash of the data sent from the channel back to the
	 *              channel to confirm receipt
	 * @param data
	 * @param key
	 * @throws IOException
	 */
	private void write(byte[] data, SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		channel.write(buffer);
		key.interestOps(SelectionKey.OP_READ);
		this.selector.wakeup();
	}
}
