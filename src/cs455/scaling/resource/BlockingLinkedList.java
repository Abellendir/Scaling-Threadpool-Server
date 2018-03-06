package cs455.scaling.resource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BlockingLinkedList<E> {
	
	private List<E> list = new LinkedList<E>();
	
	public BlockingLinkedList(){
		
	}
	
	public synchronized void add(E obj) {
		list.add(obj);
	}
	
	public synchronized boolean contains(E obj) {
		if(list.contains(obj)) {
			list.remove(obj);
			return true;
		}
		return false;
	}
	
	public synchronized void remove(E obj) {
		list.remove(obj);
	}
	
	public synchronized boolean contains(byte[] b) {
		for(E e: list) {
			if(Arrays.equals((byte[]) e, b)) {
				list.remove(e);
				return true;
			}
		}
		return false;
	}
	
}
