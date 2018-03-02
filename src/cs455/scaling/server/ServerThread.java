package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import cs455.scaling.resource.BlockingQueue;
import cs455.scaling.resource.Task;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 */
public class ServerThread implements Runnable {
	
	private Selector selector;
	private int buffSize;
	private int portNumber;
	private final BlockingQueue<Runnable> queue;
	
	public ServerThread(int portNumber, int buffSize, BlockingQueue<Runnable> queue) throws IOException {
		this.portNumber = portNumber;
		this.buffSize = buffSize;
		this.queue = queue;
		this.selector = Selector.open();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void startServer() throws IOException, InterruptedException{
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(portNumber));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		while(true) {
			//wait for events
			this.selector.select();
			
			Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
			
			while(keys.hasNext()) {
				SelectionKey key = keys.next();
				if(key.isAcceptable()) {
					this.accept(key);
				}
				else if(key.isReadable()) {
					queue.enqueue(new Task(key,this.buffSize));
				}else if(key.isWritable()) {
					
				}

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
	

	@Override
	public void run() {
		try {
			this.startServer();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
