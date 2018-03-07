package cs455.scaling.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 
 * @author Adam Bellendir
 *
 */
public class StatisticsPrinterClient implements Runnable {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	private boolean kill = false;
	private int sent;
	private int received;
	private static final StatisticsPrinterClient stats = new StatisticsPrinterClient();
	
	private StatisticsPrinterClient() {
	}
	
	public static StatisticsPrinterClient getInstance() {
		return stats;
	}
	
	@Override
	public void run() {
		while (!killed()) {
			try {
				Thread.sleep(20000);
				System.out.printf("[%s] Total Sent Count: %d, Total Received Count: %d\n",
						sdf.format(new Timestamp(System.currentTimeMillis())), sent, received);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread().getName() + " has closed");
	}
	
	public synchronized void incrementSent() {
		sent++;
	}
	
	public synchronized void incrementReceived() {
		received++;
	}
	
	private synchronized boolean killed() {
		return kill;
	}

	public synchronized void kill() {
		System.out.println("Closing Statistics Printer");
		this.kill = !kill;
	}
}
