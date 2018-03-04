package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 */
public class Client implements Runnable {

	private Selector selector;
	private final String hostAddress;
	private final int port;
	private final int r;
	private final List<String> list = new LinkedList<>();
	private boolean debug;

	public Client(String hostName, int port, int r, boolean debug) throws IOException {
		System.out.print("Initializing Client...");
		this.hostAddress = hostName;
		this.selector = Selector.open();
		this.port = port;
		this.r = r;
		this.debug = debug;

	}

	private void startClient() throws IOException, InterruptedException {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_CONNECT);
		channel.connect(new InetSocketAddress(this.hostAddress, this.port));
		System.out.print("Client started\n");
		while (true) {
			this.selector.select();
			Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
			SelectionKey key = keys.next();
			if (key.isConnectable()) {
				System.out.println("Client connecting to server");
				this.connect(key);
				System.out.println("Client connected to server");
				new Thread(new Writer(key,this.r,this.list,this.debug),"Thread-Writer").start();
			} else if (key.isReadable()) {
				ByteBuffer buffer = ByteBuffer.allocate(20);
				int read = 0;
				while (buffer.hasRemaining() && read != -1) {
					read = channel.read(buffer);
				}
				buffer.flip();
				byte[] bytes = new byte[20];
				buffer.get(bytes);
				boolean contains = list.contains(new BigInteger(1,bytes).toString(16));
				if(debug) System.out.println("Hash received from server " + new BigInteger(1, bytes).toString(16) + " matches = " + contains);
			}
		}
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
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		String serverHost = args[0];
		int serverPort = Integer.parseInt(args[1]);
		int messageRate = Integer.parseInt(args[2]);
		boolean debug = false;
		if(args.length == 4) {
			if(args[3].equals("true")) {
				debug = true;
			}
		}
		Client client = new Client(serverHost, serverPort, messageRate,debug);
		new Thread(client, "Thread-Client").start();

	}

}
