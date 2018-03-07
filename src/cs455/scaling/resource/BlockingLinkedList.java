package cs455.scaling.resource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription Class name is more or less missleading doesn't explicitly block
 *              with wait and notify
 * 
 * @param <E>
 */
public class BlockingLinkedList<E> {

	/**
	 * The underlying list for the class
	 */
	private List<E> list = new LinkedList<E>();

	/**
	 * @Discription
	 */
	public BlockingLinkedList() {

	}

	/**
	 * @Discription Adds an element to the LinkedList
	 * @param obj
	 */
	public synchronized void add(E obj) {
		list.add(obj);
	}

	/**
	 * @Discription Checks if the LinkedList contains and element, if so it removes
	 * @param obj
	 * @return
	 */
	public synchronized boolean contains(E obj) {
		if (list.contains(obj)) {
			list.remove(obj);
			return true;
		}
		return false;
	}

	/**
	 * @Discription Removes Element from the LinkedList
	 * @param obj
	 */
	public synchronized void remove(E obj) {
		list.remove(obj);
	}

	/**
	 * @Discription Checks if element is in the list, if it is a list of byte[]
	 * @param b
	 * @return
	 * @deprecated method serves no purpose in the context of this Assignment as i
	 *             switch to saving the hash as a string
	 */
	public synchronized boolean contains(byte[] b) {
		for (E e : list) {
			if (Arrays.equals((byte[]) e, b)) {
				list.remove(e);
				return true;
			}
		}
		return false;
	}

}
