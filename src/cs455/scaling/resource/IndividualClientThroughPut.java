package cs455.scaling.resource;

public class IndividualClientThroughPut {
	private int messagesProcessed = 0;
	
	public IndividualClientThroughPut() {
	}
	
	public synchronized void incrementValue() {
		messagesProcessed++;
	}
	
	public synchronized int getValue() {
		return messagesProcessed;
	}
	
	public synchronized void clearValue() {
		messagesProcessed = 0;
	}
}
