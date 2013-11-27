/* Scott Hofman s0943941
 * 
 * This is the Receiver for Question 1. Assuming we are transferring on a perfect connection,
 * this function will receive packets containing a byte array with size of 1024 and
 * write the data received (values 3-1023) to a created image (NOTE: This may require root privilege
 * to run. This image should be the same as provided by Sender1. The function needs a port number and
 * a filename to write the output to (NOTE: If the port selected is too low, root privilege may be
 * required. 
 *  
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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender3 {
	public static void main(String args[]) throws Exception {
		try {
			if (args.length != 5) {
				throw new Exception(
						"Needs <localhost> <Port> <Filename> <RetryTimeout> <WindowSize> as arguments");
			}

			//Set up the socket
			DatagramSocket senderSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(args[0]);
			System.out.println("Attemping to connect to " + IPAddress
					+ ") via UDP port " + args[1]);

			//Set up file
			String workingDir = new File("").getAbsolutePath();
			File cwk = new File(workingDir + "/" + args[2]);
			FileInputStream fis = new FileInputStream(cwk);

			//Convert fis into packets
			byte[] buf; 
			byte[] send;
			short count = 0;
			short last = 0;
			double bytecount = fis.available();
			LinkedList<Integer> readNumList = new LinkedList<Integer>();
			LinkedList<byte[]> sendList = new LinkedList<byte[]>();
			int twogenerals = 0;
			
			//Read the file into memory and shift byte array into send array 
			while(fis.available() > 0) {
				buf = new byte[1021];
				send = new byte[1024];	
				int readNum = fis.read(buf); 
				readNumList.add(readNum);
				send[0] = toBytes(count)[0];
				send[1] = toBytes(count)[1];
				if (readNum < 1021 || fis.available()==0) {
					last = 1;
				} else {
					last = 0;
				}
				send[2] = toBytes(last)[0];
				for (int i = 0; i < buf.length; i++) {
					send[i + 3] = buf[i];
				}
				sendList.add(send);
				count++;
			}
			
			//Set up window
			int base = 0;
			int nextSeq = 0;
			int window = Integer.parseInt(args[4]);
			byte[] receiveData;
			boolean finished = false;
			boolean secondlast = false;
			
			try {
				final long startTime = System.currentTimeMillis();
				long endTime = 0;
				System.out.println("Sending file...");
				breaker:
					while(!finished) {
					//Send each packet in the window
					while(nextSeq < base+window && nextSeq < count) {
						DatagramPacket sendPacket = new DatagramPacket(sendList.get(nextSeq), readNumList.get(nextSeq) + 3, IPAddress,Integer.parseInt(args[1]));
						senderSocket.send(sendPacket);
						nextSeq++;
					}
					try {
						//If we are at the end, and we've received everything but the last packet
						if(nextSeq == count && secondlast == true) {
							//Two Generals problem 
							twogenerals++;
							DatagramPacket sendPacket = new DatagramPacket(sendList.get(nextSeq-1), readNumList.get(nextSeq-1) + 3, IPAddress,Integer.parseInt(args[1]));
							senderSocket.send(sendPacket);
							if(twogenerals>30) {
								//Probability is low that the we haven't received
								endTime = System.currentTimeMillis();
								break breaker;
							}
						}
						
						//Receive Data
						senderSocket.setSoTimeout(Integer.parseInt(args[3]));
						receiveData = new byte[2];
						DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
						senderSocket.receive(receivePacket);
						short received = toShort(receiveData[0], receiveData[1]); 
						
						//If it is greater than our base (since we assume the Receiver can only receive in order
						if (received >= base) {
							//If this is the last one, finish
							if(received==count-1) {
								endTime = System.currentTimeMillis();
								finished = true;
							}
							//Error checking to keep the window in range and to make sure that first packet (0) is Acked before shifting the base
							if(base+window < count) {
								base = received+1;
							}

							//Needed for two generals problem
							if(received==count-2) {
								secondlast = true;
							}
						} 

					} catch (SocketTimeoutException ste) {
						//Resend everything in the window
						for(int i=base; i<nextSeq; i++) {
							DatagramPacket sendPacket = new DatagramPacket(
							sendList.get(i), readNumList.get(i) + 3, IPAddress,
							Integer.parseInt(args[1]));
							
							senderSocket.send(sendPacket);
						}
					}
				}
			
				//Print out everything
				final long duration = endTime - startTime;
				double time = (double) duration/1000;
				double kilo = bytecount/1024;
				System.out.println("Duration: " + time);
				System.out.println("Kilobytes: " + kilo);
				System.out.println("Throughput: " + kilo/time);
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
