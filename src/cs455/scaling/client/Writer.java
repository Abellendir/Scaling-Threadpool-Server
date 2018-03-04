package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import cs455.scaling.operations.MessageHashCode;

public class Writer implements Runnable{
	
	private volatile boolean kill = false;
	private final List<String> list;
	private final SocketChannel channel;
	private final int r;
	private boolean debug;
	
	public Writer(SelectionKey key,int r, List<String> list, boolean debug) {
		System.out.print("Writer Initializing...");
		this.r = r;
		this.list = list;
		this.channel = (SocketChannel) key.channel();
		this.debug = debug;
	}
	
	@Override
	public void run() {
		System.out.print("Starting to send messages\n");
		Random random = new Random();
		while(!kill) {
			byte[] data = new byte[8000];
			random.nextBytes(data);
			try {
				byte[] hash = MessageHashCode.SHA1FromBytes(data);
				if(debug)System.out.println("Client Sending message hashed to: " + new BigInteger(1,hash).toString(16));
				list.add(new BigInteger(1,hash).toString(16));
				ByteBuffer buffer = ByteBuffer.wrap(data);
				this.channel.write(buffer);
				buffer.clear();
				Thread.sleep(1000 / this.r);
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void kill() {
		this.kill = !kill;
	}
	
}
