package cs455.scaling.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 */
public class StatisticsPrinterServer implements Runnable {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	private static final StatisticsPrinterServer stats = new StatisticsPrinterServer();
	private boolean kill = false;
	private int totalMessages;
	private int totalClients;
	private double currentMean;
	private double currentStdDev;

	private StatisticsPrinterServer() {
	}

	public StatisticsPrinterServer getInstance() {
		return stats;
	}
	
	@Override
	public void run() {
		while (!killed()) {
			try {
				Thread.sleep(20000);
				System.out.printf(
						"[%s] Server Throughput: %f messages/s, " + "Active Client Connections: %d , "
								+ "Mean Per-client Throughput: %f messages/s, "
								+ "Std. Dev. Of Per-client Throughput: %f messages/s",
						sdf.format(new Timestamp(System.currentTimeMillis())), totalMessages / 20, totalClients,
						currentMean, currentStdDev);
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
