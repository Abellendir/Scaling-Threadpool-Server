package cs455.scaling.server;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
/**
 * 
 * @author Adam Bellendir
 *
 */
public class Server {
	
	private Selector selector;
	private int buffSize;
	private int portNumber;
	
	public Server(int portNumber, int buffSize) {
		this.portNumber = portNumber;
		this.buffSize = buffSize;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void startServer() throws IOException{
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetAddress(/**/));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		while(true) {
			this.selector.select();
			Iterator keys = this.selector.selectedKeys().iterator();
			while(keys.hasNext()) {
				if(keys.isAcceptable()) {
					this.accept(keys);
				}
				/*
				 * other classes
				 */
			}
		}
		
	}
	
	/**
	 * 
	 * @param key
	 * @throws IOException
	 */
	private void accept(SelectionKey key) throws IOException{
		ServerSocketChannel servSocket = (ServerSocketChannel) key.channel();
		SocketChannel channel = servSocket.accept();
		System.out.println("Accepting incoming connection...");
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
	}
	
	/**
	 * 
	 * @param key
	 * @throws IOException
	 */
	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel)key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(buffSize);
		int read = 0;
		try {
			while(buffer.hasRemaining() && read != -1) {
				read = channel.read(buffer);
			}
		}catch(IOException e) {
			//Cancel the key and close the socket channel
		}
		if(read == -1) {
			//Cancel the key and close the socket channel
			return;
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
	/**
	 * 
	 * @param key
	 * @throws IOException
	 */
	private void write(SelectionKey key)throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		channel.write(buffer);
		key.interestOps(SelectionKey.OP_READ);
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int portNumber = Integer.parseInt(args[0]);
		int threadPoolSize = Integer.parseInt(args[1]);

	}

}
