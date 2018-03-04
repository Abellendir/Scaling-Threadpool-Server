package cs455.scaling.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class StatisticsPrinterClient implements Runnable {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	private boolean kill = false;
	private int sent;
	private int received;
	private static final StatisticsPrinterClient stats = new StatisticsPrinterClient();
	
	private StatisticsPrinterClient() {
	}
	
	public StatisticsPrinterClient getInstance() {
		return stats;
	}
	
	@Override
	public void run() {
		while (!killed()) {
			try {
				Thread.sleep(20000);
				System.out.printf("[%s] Total Sent Count: %d, Total Received Count: %d",
						sdf.format(new Timestamp(System.currentTimeMillis())), sent, received);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized boolean killed() {
		return kill;
	}

	public synchronized void kill() {
		this.kill = !kill;
	}
}
