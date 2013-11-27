/* Scott Hofman s0943941
 * 
 * This is the Sender for Question 1. Assuming we are transferring on a perfect connection,
 * this function will send packets containing a byte array with size of 1024 with a delay 
 * to ensure that the Receiver has enough time to deal with the packets. The first 3 
 * of these will provide information to the Receiver with regard to the packet. The first 
 * two contain information about which number of packet we are sending and the second 
 * indicates if this is the last packet or not, determined by the packet size. Three
 * arguments need to be provided to run - IPAddress (localhost in this case), Port (a number
 * - NOTE: If the port selected is too low, root privilege may be required), and Filename
 * (the file to be sent over UDP).  
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender1 {

	public static void main(String args[]) throws Exception {
		try {
			if (args.length != 3) 
				throw new Exception("Needs <localhost> <Port> <Filename> as arguments");
			
			//Set up a DatagramSocket to send the packets using the IPAddress provided
			DatagramSocket senderSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(args[0]);
			System.out.println("Attemping to connect to " + IPAddress
					+ ") via UDP port " + args[1]);

			//Find the file we are sending and use a FileInputStream
			String workingDir = new File("").getAbsolutePath();
			File cwk = new File(workingDir + "/" + args[2]);
			FileInputStream fis = new FileInputStream(cwk);

			//Two byte arrays - one to hold the data, the other to send as a packet
			byte[] buf = new byte[1021];
			byte[] send = new byte[1024];
			short count = 0;
			short last = 0;
			System.out.println("Sending packet...");
			
			try {
				//Read in the file's bytes, store the amount in buf, and repeat until no more bytes
				for (int readNum; (readNum = fis.read(buf)) != -1;) {
					//Allocate the first two bytes of the array to what packet we are
					send[0] = toBytes(count)[0];
					send[1] = toBytes(count)[1];
					
					//Set the third byte to a last indicator (1 if last, determined by size)
					if (readNum < 1021) {
						last = 1;
					} else {
						last = 0;
					}
					send[2] = toBytes(last)[0];
					
					//Copy the buf array (the bytes from the file) into send byte array
					for (int i = 0; i < buf.length; i++) {
						send[i + 3] = buf[i];
					}
					
					//Send the filled byte array along the specified port for Receiver1 to handle
					DatagramPacket sendPacket = new DatagramPacket(send,
							readNum + 3, IPAddress, Integer.parseInt(args[1]));
					senderSocket.send(sendPacket);
					
					//Give Receiver1 enough time to handle the packets 
					//Removing Thread.sleep will break the file
					Thread.sleep(1);
					
					//Increment the sent packet number
					count++;
				}
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
