package cs455.scaling.tasks;

import cs455.scaling.server.Server;

public class Shutdown implements Runnable {

	private Server server;

	public Shutdown(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		this.server.close();
	}

}
