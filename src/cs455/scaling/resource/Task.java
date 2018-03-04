package cs455.scaling.resource;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

import cs455.scaling.operations.MessageHashCode;

/**
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 *
 */
public class Task implements Runnable {

	SelectionKey key;
	private SocketChannel channel;
	private byte[] received = new byte[8000];
	private byte[] data;
	private int buffSize;
	private boolean debug;

	public Task(SelectionKey key, int buffSize, boolean debug) {
		this.key = key;
		this.buffSize = buffSize;
		this.channel = (SocketChannel) key.channel();
		this.debug = debug;
	}

	@Override
	public void run() {
		try {
			if (debug)
				System.out.println(Thread.currentThread().getName() + ": Reading message from client");
			ByteBuffer buffer = ByteBuffer.allocate(8000);
			int read = 0;
			try {
				while (buffer.hasRemaining() && read != -1) {
					read = this.channel.read(buffer);
				}
			} catch (IOException e) {
				this.key.cancel();
				this.channel.close();
			}
			buffer.flip();
			if (read == -1) {
				/* Connection was terminated by the client. */
				this.key.cancel();
				this.channel.close();
				return;
			}
			buffer.get(received);
			data = MessageHashCode.SHA1FromBytes(received);
			if (debug)
				System.out.println(Thread.currentThread().getName() + ": Hash generated by server: "
						+ new BigInteger(1, data).toString(16));
			if (debug)
				System.out.println(Thread.currentThread().getName() + ": Sending message to client");

			ByteBuffer sendBuffer = ByteBuffer.wrap(data);
			// while (buffer.hasRemaining()) {
			this.channel.write(sendBuffer);
			// }
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	private void read() throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(8000);
		int read = 0;
		try {
			while (buffer.hasRemaining() && read != -1) {
				read = this.channel.read(buffer);
			}
		} catch (IOException e) {
			this.key.cancel();
			this.channel.close();
		}
		buffer.flip();
		if (read == -1) {
			/* Connection was terminated by the client. */
			this.key.cancel();
			this.channel.close();
			return;
		}
		buffer.get(received);
		// this.key.interestOps(SelectionKey.OP_WRITE);
	}

	private void write(byte[] data) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		// while (buffer.hasRemaining()) {
		this.channel.write(buffer);
		// }
		this.key.interestOps(SelectionKey.OP_READ);
	}
}
