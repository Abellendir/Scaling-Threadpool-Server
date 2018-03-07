package cs455.scaling.resource;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription
 * TODO remove and replace instance of this with HashMap<SocketChannel,Integer> instead of HashMap<SocketChannel,this>
 *
 */
public class IndividualClientThroughPut {

	/**
	 * 
	 */
	private int messagesProcessed = 0;

	/**
	 * @Discription
	 */
	public IndividualClientThroughPut() {
	}

	/**
	 * @Discription
	 */
	public synchronized void incrementValue() {
		messagesProcessed++;
	}

	/**
	 * @Discription
	 * @return
	 */
	public synchronized int getValue() {
		return messagesProcessed;
	}

	/**
	 * @Discription
	 */
	public synchronized void clearValue() {
		messagesProcessed = 0;
	}
}
