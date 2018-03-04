package cs455.scaling.resource;

import java.nio.channels.SocketChannel;

public class ChangeOps {
	private SocketChannel channel;
	private int ops;
	
	public ChangeOps(SocketChannel channel, int ops){
		this.channel = channel ;
		this.ops = ops;
	}
	
	public SocketChannel getChannel() {
		return this.channel;
	}
	
	public int ops() {
		return this.ops;
	}
	
}
