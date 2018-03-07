package cs455.scaling.tasks;

import cs455.scaling.server.Server;

/**
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription Shutdown poison job, when it is sent to the job queue the
 *              ThreadPoolManager does an extra check for the Class type and
 *              starts closing the thread pool and then executes the
 *              server.close() command.
 * 
 */
public class Shutdown implements Runnable {

	private Server server;

	/**
	 * @Discription Constructor Takes a Server Object, Might make this generic so
	 *              that it can handle and class that has a .close method
	 * @param server
	 */
	public Shutdown(Server server) {
		this.server = server;
	}

	/**
	 * @Discription Runs the close command.
	 */
	@Override
	public void run() {
		this.server.close();
	}

}
