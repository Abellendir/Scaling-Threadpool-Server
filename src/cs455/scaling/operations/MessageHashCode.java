package cs455.scaling.operations;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Description Static method to calculate hash code for sent message and
 *              received message returns a byte[] of length 20;
 *
 */
public class MessageHashCode {
	public static byte[] SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		return digest.digest(data);
	}
}
