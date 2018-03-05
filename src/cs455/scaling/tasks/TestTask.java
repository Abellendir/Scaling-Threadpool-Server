package cs455.scaling.tasks;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;

import cs455.scaling.operations.MessageHashCode;
import cs455.scaling.server.Server;

/**
 * This class is for testing a switch where methods reside, specifically the residing in the server
 */
public class TestTask implements Runnable {

	private SelectionKey key;
	private Server server;
	
	public TestTask(SelectionKey key, Server server) {
		this.key = key;
		this.server = server;
	}
	
	@Override
	public void run() {
		try {
			byte[] data = server.read(key);
			byte[] hash = MessageHashCode.SHA1FromBytes(data);
			server.write(hash,key);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
