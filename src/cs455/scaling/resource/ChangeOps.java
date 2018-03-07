package cs455.scaling.resource;

import java.nio.channels.SocketChannel;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription
 */
public class ChangeOps {

	/**
	 * 
	 */
	private SocketChannel channel;
	private int ops;

	/**
	 * @Discription
	 * @param channel
	 * @param ops
	 */
	public ChangeOps(SocketChannel channel, int ops) {
		this.channel = channel;
		this.ops = ops;
	}

	/**
	 * @Discription
	 * @return
	 */
	public SocketChannel getChannel() {
		return this.channel;
	}

	/**
	 * @Discription
	 * @return
	 */
	public int ops() {
		return this.ops;
	}

}
