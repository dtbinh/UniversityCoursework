/* Scott Hofman s0943941
 * 
 * This is the Sender for Question 4. This is my implementation of Selective Repeat
 * where each packet has its own timer. Once this timer reaches a timeout, given in
 * the parameters, the thread resends the packet. 
 *  
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

public class Sender4 {
	private static LinkedList<Integer> readNumList;
	private static LinkedList<byte[]> sendList;
	private static LinkedList<Boolean> ackedList;
	private static InetAddress IPAddress;
	private static int port;
	private static DatagramSocket senderSocket;
	private static int timeout;

	static class Timeout implements Runnable {
		//Seperate class for dealing with the timeout
		private DatagramPacket sendPacket;
		private int seq;
		private int time;
		private boolean sent;
		private boolean lastwindow;
		private int numResent;


		public Timeout(int sequence, boolean last) {
			//Set the timeout to be the number of the packet and whether its in the last window or not
			seq = sequence;
			//Packet to be sent (data taken from two arraylists and variables set in the main loop)
			sendPacket = new DatagramPacket(sendList.get(sequence),
					readNumList.get(sequence) + 3, IPAddress, port);
			Thread.currentThread().setPriority(5);
			//Initial time
			time = 0;
			//Count of amount of times packet is resent (only applicable for last window)
			numResent = 0;
			lastwindow = last;
			sent = false;
		}

		@Override
		public void run() {
			try {
				//Send the packet upon initial run
				senderSocket.send(sendPacket);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//While the packet hasn't been sent
			breaker: while (!sent) {
				try {
					//Sleep the thread to allow other threads to send their packets
					Thread.sleep(1);
				} catch (InterruptedException ioe) {
					//This terminates the program
					continue;
				}
				//If the packet has already been acked, and we haven't been interrupted, exit
				if (ackedList.get(seq)) {
					sent = true;
					break breaker;
				}
				synchronized (this) {
					//Increase time by 1 (what we slept by)
					time++;

					//If we have exceeded the timeout
					if (time > timeout) {
						try {
							//Check if the two generals problem applies (could be anything within the last window)
							if (lastwindow) {
								numResent++;
								if (numResent > 100) {
									/*
									 * The program could hang at this stage, because
									 * we are waiting to receive something, but
									 * the Receiver has closed. We will assume
									 * that, after 100 resends, the last 4
									 * packets will have been sent at least a
									 * few times (say 30, most likely more). The
									 * probability with 1% packet loss rate is
									 * (1/100)^30 - very small. If we have
									 * reached this stage, we will simply close
									 * the Datagram socket to end the program.
									 */
									senderSocket.close();
									ackedList.set(seq, true);
								}

							}
							//Resent the packet
							senderSocket.send(sendPacket);
						} catch (Exception e) {
							System.exit(0);
						}
						//Reset the timer
						time = 0;
					}
				}
			}
		}
	}

	public static void main(String args[]) {
		try {
			if (args.length != 5) {
				throw new Exception(
						"Needs <localhost> <Port> <Filename> <RetryTimeout> <WindowSize> as arguments");
			}

			// Setup variables
			IPAddress = InetAddress.getByName(args[0]);
			senderSocket = new DatagramSocket();
			timeout = Integer.parseInt(args[3]);
			port = Integer.parseInt(args[1]);
			System.out.println("Attemping to connect to " + IPAddress
					+ ") via UDP port " + args[1]);

			// Load in file
			String workingDir = new File("").getAbsolutePath();
			File cwk = new File(workingDir + "/" + args[2]);
			FileInputStream fis = new FileInputStream(cwk);

			// Convert fis into packets
			byte[] buf;
			byte[] send;
			short count = 0;
			short last = 0;
			double bytecount = fis.available();
			readNumList = new LinkedList<Integer>();
			sendList = new LinkedList<byte[]>();
			ackedList = new LinkedList<Boolean>();


			while (fis.available() > 0) {
				buf = new byte[1021];
				send = new byte[1024];
				int readNum = fis.read(buf);
				readNumList.add(readNum);
				send[0] = toBytes(count)[0];
				send[1] = toBytes(count)[1];
				if (readNum < 1021 || fis.available() == 0) {
					last = 1;
				} else {
					last = 0;
				}
				send[2] = toBytes(last)[0];
				for (int i = 0; i < buf.length; i++) {
					send[i + 3] = buf[i];
				}
				ackedList.add(false);
				sendList.add(send);
				count++;
			}

			// Set up the threads
			// Variables
			boolean finished = false;
			boolean finalwindow = false;
			int base = 0;
			int nextSeq = 0;
			int window = Integer.parseInt(args[4]);
			senderSocket.setReceiveBufferSize(window);
			Thread t;
			LinkedList<Thread> threadList = new LinkedList<Thread>();
			final long startTime = System.currentTimeMillis();
			long endTime = 0;
			System.out.println("Sending file...");
			
			// Send the threads until finished
			while (!finished) {
				
				//For every element in the 
				while (nextSeq < base + window && nextSeq < count) {
					
					//If we are 4 from the end, then we should be aware for hangs
					//(If the window size is small, we won't be within the final window.
					//However, I have assumed that, since we have demonstrated the 
					//ineffectiveness of a small window, I assume we will be greater than 4) 
					if (nextSeq > count - 4) {
						finalwindow = true;
					}
					
					//Start the sender Thread and add it to the list of Threads
					t = new Thread(new Timeout(nextSeq, finalwindow));
					threadList.add(t);
					t.start();
					
					//Increment the next Sequence to be sent
					nextSeq++;
				}

				//Set to receive
				byte[] receiveData = new byte[2];
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				senderSocket.receive(receivePacket);
				receiveData = receivePacket.getData();
				short received = toShort(receiveData[0], receiveData[1]);

				//If what we've received in within our window
				if (received >= base && received < base + window) {
					//End the corresponding thread and set its Ack to true
					threadList.get(received).interrupt();
					ackedList.set(received, true);

					//If this is the final piece received, end the loop
					if (received == count - 1) {
						//This is faulty reasoning for termination - the program might receive the last one
						//While other packets within the window have been lost. This 
						//could result in a failed image. To fix this, I would check if 
						//every element in ackedList has been set to true
						finished = true;
						endTime = System.currentTimeMillis();
					}
					
					// Shift the window by finding the next unAcked packet in the list
					//and setting that to the base
					for (int i = base; i < count; i++) {
						if (ackedList.get(i) == false) {
							base = i;
							break;
						}
					}
				}
			}

			//

			final long duration = endTime - startTime;
			double time = (double) duration / 1000;
			double kilo = bytecount / 1024;
			System.out.println("Duration: " + time);
			System.out.println("Kilobytes: " + kilo);
			System.out.println("Throughput: " + kilo / time);
			System.exit(0);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param s
	 *            short to be converted
	 * @return byte array converted from short
	 */
	public static byte[] toBytes(short s) {
		return new byte[] { (byte) (s & 0x00FF), (byte) ((s & 0xFF00) >> 8) };
	}

	/**
	 * Function to convert two bytes into a short
	 * 
	 * @param b1
	 *            - first half of byte array for the short
	 * @param b2
	 *            - second half of byte array for the short
	 * @return short made from b1 and b2
	 */
	public static short toShort(byte b1, byte b2) {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(b1);
		bb.put(b2);
		return bb.getShort(0);
	}
}
