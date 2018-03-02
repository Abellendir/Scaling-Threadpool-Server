package cs455.scaling.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 */
public class Client {
	
	private Selector selector;
	private InetAddress hostAddress;
	private int port;
	private SelectionKey key;
	
	private void startClient() throws IOException{
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_CONNECT);
		channel.connect(new InetSocketAddress(this.hostAddress, this.port));
		while(true) {
			//other operations
			if(key.isConnectable()) {
				this.connect(key);
			}
		}
	}
	
	private void connect(SelectionKey key) throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();
		channel.finishConnect();
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
	public static void main(String[] args) {
		String serverHost = args[0];
		int ServerPort = Integer.parseInt(args[1]);
		int messageRate = Integer.parseInt(args[2]);

	}

}
