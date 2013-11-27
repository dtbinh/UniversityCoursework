/* Scott Hofman s0943941
 * 
 *  This is the Sender for Question 2. After sending the packet as in Sender1, we wait to
 *  receive an ACK. This indicates the Receiver has received the packet, and Sender2 will 
 *  send the next packet. This is a more realistic scenario than Sender1, as we assume packets
 *  will be lost in the data transfer. As with Sender1, we have the arguments for localhost, 
 *  port, and filename, but we have also introduced RetryTimeout. This value indicates how 
 *  long the socket waits to receive acknowledgement of a packet before it resends. 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender2 {
	public static void main(String args[]) throws Exception {
		try {
			if (args.length != 4) {
				throw new Exception(
						"Needs <localhost> <Port> <Filename> <RetryTimeout> as arguments");
			}

			// Create a new DatagramSocket to send and receive packets with
			// (from localhost)
			DatagramSocket senderSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(args[0]);
			System.out.println("Attemping to connect to " + IPAddress
					+ ") via UDP port " + args[1]);

			// Find the file to transfer and set up a FileInputStream
			String workingDir = new File("").getAbsolutePath();
			File cwk = new File(workingDir + "/" + args[2]);
			FileInputStream fis = new FileInputStream(cwk);

			// Two byte arrays - one to hold the data, the other to send as a
			// packet
			byte[] buf = new byte[1021];
			byte[] send = new byte[1024];

			// Another byte array to receive an ACK
			byte[] receiveData;

			/*
			 * Two Generals Problem - we cannot guarantee that the last packet
			 * has been received due to lossy connection. This typically results
			 * in the Sender not closing. To prevent this, when we are sending
			 * the last packet, we continue to send the packet and increment a
			 * counter (twogenerals). Once this is past a certain threshold
			 * (ideally correlated with the packet loss rate), we terminate the
			 * Sender. If we operate within the assumption that the packet loss
			 * rate will be low (~1%) then the probability that all the resent
			 * packets have failed is (1/100)^(number of resends). This value is
			 * very small, so we can assume that at least one will have
			 * succeeded.
			 */

			int twogenerals = 0;
			int numReTrans = 0;

			// Packet number and indicator of last packet
			short count = 0;
			short last = 0;

			// Start time for the program
			final long startTime = System.currentTimeMillis();
			long endTime = 0;

			// Amount of bytes within the file
			double bytecount = fis.available();

			try {
				//While there are still bytes within the file, continue to send
				breaker: while (fis.available() > 0) {
					// Amount of bytes read in - 1021
					int readNum = fis.read(buf);

					// Set this packet to be false
					boolean acked = false;

					//Set the first two bytes to be a 
					send[0] = toBytes(count)[0];
					send[1] = toBytes(count)[1];
					
					//Set the third byte to be an indicator of the last file
					if (readNum < 1021) {
						last = 1;
					} else {
						last = 0;
					}
					send[2] = toBytes(last)[0];
					
					//Fill the rest of send with the image bytes
					for (int i = 0; i < buf.length; i++) {
						send[i + 3] = buf[i];
					}

					//Send the packet
					DatagramPacket sendPacket = new DatagramPacket(send,
							readNum + 3, IPAddress, Integer.parseInt(args[1]));
					senderSocket.send(sendPacket);

					//We stop and wait until we receive the ack we want
					while (!acked) {
						try {
							// Two Generals problem
							if (last == 1) {
								twogenerals++;
								if (twogenerals > 50) {
									// Probability is low that the we haven't received
									endTime = System.currentTimeMillis();
									break breaker;
								}
							}
							
							//Set the timeout to the passed argument
							senderSocket.setSoTimeout(Integer.parseInt(args[3]));

							//Receive an ACK - this waits until something is received or timeout
							receiveData = new byte[2];
							DatagramPacket receivePacket = new DatagramPacket(
									receiveData, receiveData.length);
							senderSocket.receive(receivePacket);
							
							//Once we've received something, we check if it equals what we sent
							if (toShort(receiveData[0], receiveData[1]) == count) {
								acked = true;
								if (last == 1) {
									//Last one, get ready to exit
									endTime = System.currentTimeMillis();
								}
								//Shift the count to the next packet
								count++;
							}

						} catch (SocketTimeoutException ste) {
							// Timeout has occured, retransmit the package
							senderSocket.send(sendPacket);
							numReTrans++;
						}
					}
				}
				//Calculate time, kilobytes, throughput, and print out everything
				final long duration = endTime - startTime;
				double time = (double) duration / 1000;

				double kilo = bytecount / 1024;
				System.out.println("Duration: " + time);
				System.out.println("Kilobytes: " + kilo);
				System.out.println("Throughput: " + kilo / time);
				System.out.println("Retransmissions: " + numReTrans);
			} catch (IOException ex) {
				Logger.getLogger(Sender1.class.getName()).log(Level.SEVERE,
						null, ex);
			}
			senderSocket.close();
		} catch (UnknownHostException ex) {
			System.err.println(ex);
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	/**
	 * @param s short to be converted
	 * @return byte array converted from short
	 */
	public static byte[] toBytes(short s) {
		return new byte[] { (byte) (s & 0x00FF), (byte) ((s & 0xFF00) >> 8) };
	}

	/**
	 * Function to convert two bytes into a short
	 *  @param b1 - first half of byte array for the short
	 *  @param b2 - second half of byte array for the short
	 *  @return short made from b1 and b2
	 */
	public static short toShort(byte b1, byte b2) {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(b1);
		bb.put(b2);
		return bb.getShort(0);
	}
}
