package cs455.scaling.util;

import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import cs455.scaling.resource.IndividualClientThroughPut;

/**
 * 
 * @author Adam Bellendir
 * @Date 2018-02-28
 * @Class CS 455
 * @Assignment 2
 * @Discription 
 */
public class StatisticsPrinterServer implements Runnable {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	private static final StatisticsPrinterServer stats = new StatisticsPrinterServer();
	private HashMap<SocketChannel, IndividualClientThroughPut> clients;
	private boolean kill = false;
	private int totalMessages;
	private double[] values;
	private double mean;

	private StatisticsPrinterServer() {
	}

	public static StatisticsPrinterServer getInstance() {
		return stats;
	}

	public void giveHashMap(HashMap<SocketChannel, IndividualClientThroughPut> clients) {
		this.clients = clients;
	}

	@Override
	public void run() {
		while (!killed()) {
			try {

				Thread.sleep(20000);
				synchronized (clients) {
					System.out.printf(
							"[%s] Server Throughput: %f messages/s, " + "Active Client Connections: %d , "
									+ "Mean Per-\nclient Throughput: %f messages/s, "
									+ "Std. Dev. Of Per-client Throughput: %f messages/s\n",
							sdf.format(new Timestamp(System.currentTimeMillis())), totalMessages / 20.0, clients.size(),
							getMean(), currentStdDev());
				}
				totalMessages = 0;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread().getName() + " has closed");
	}

	private double currentStdDev() {
		double sum = 0;
		for(int i = 0; i < values.length;i++) {
			sum += Math.pow((values[i]-mean),2);
		}
		return Math.sqrt(sum/values.length);
	}

	private double getMean() {
		int total = 0;
		values = new double[clients.size()];
		int i = 0;
		for (IndividualClientThroughPut client : clients.values()) {
			total += client.getValue();
			values[i] = client.getValue()/20.0;
			i++;
			client.clearValue();
		}
		if (clients.size() == 0) {
			return 0;
		}
		this.mean = total/clients.size()/20.0;
		return this.mean;
	}

	public synchronized void incrementProcessed() {
		totalMessages++;
	}

	private synchronized boolean killed() {
		return kill;
	}

	public synchronized void kill() {
		System.out.println("Closing Statistics Printer");
		this.kill = !kill;
	}

}
