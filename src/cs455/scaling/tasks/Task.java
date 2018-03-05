package cs455.scaling.tasks;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import cs455.scaling.operations.MessageHashCode;
import cs455.scaling.resource.ChangeOps;

/**
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 *
 */
public class Task implements Runnable {

	SelectionKey key;
	private byte[] received = new byte[8000];
	private boolean debug;
	private List<ChangeOps> list;
	private Selector selector;

	public Task(SelectionKey key, List<ChangeOps> changeOps, Selector selector, boolean debug) {
		this.key = key;
		this.list = changeOps;
		this.debug = debug;
		this.selector = selector;
	}

	@Override
	public void run() {
		try {
			if(debug) System.out.println("Server processing message: ");
			read(key);
			if(debug) System.out.println("Server processed Message...hashing");
			byte[] data = MessageHashCode.SHA1FromBytes(received);
			if(debug) System.out.println(Thread.currentThread().getName() + ": Hashed Message To: " + new BigInteger(1,data).toString(16));
			write(data,key);
			if(debug) System.out.println("Server sent hashcode");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(8000);
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
		synchronized(this.list) {
			list.add(new ChangeOps(channel,SelectionKey.OP_READ));
		}
		this.selector.wakeup();
		// this.key.interestOps(SelectionKey.OP_WRITE);
	}

	private void write(byte[] data, SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		channel.write(buffer);
		key.interestOps(SelectionKey.OP_READ);
	}
}
