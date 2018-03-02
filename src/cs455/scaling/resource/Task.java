package cs455.scaling.resource;

import java.io.IOException;
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
public class Task implements Runnable{
	
	SelectionKey key;
	private byte[] data;
	private int buffSize;
	private String hash;
	
	public Task(SelectionKey key, int buffSize) {
		this.key = key;
		this.buffSize = buffSize;
	}

	@Override
	public void run() {
		try {
			read(this.key);
			hash = MessageHashCode.SHA1FromBytes(data);
			write(this.key);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
	
	private void read(SelectionKey key) throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(buffSize);
		int read = 0;
		try {
			while(buffer.hasRemaining() && read != -1) {
				read = channel.read(buffer);
			}
		}catch (IOException e) {
			//Cancel the key and close the socket channel
			key.cancel();
			channel.close();
		}
		
		// You may want to flip the buffer here
		buffer.flip();
		if(read == -1) {
			/* Connection was terminated by the client. */
			key.cancel();
			channel.close();
			return;
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
	private void write(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		channel.write(buffer);
		key.interestOps(SelectionKey.OP_READ);
	}
}
