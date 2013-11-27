/* Scott Hofman s0943941
 * 
 * This is the Receiver for Question 3. As with the previous Receivers, this one receives an image
 * over packets and displays it. This is an implementation of the receiver for Go Back N, though
 * its functionality differs little from Stop and Wait. The function needs a port number and
 * a filename to write the output to (NOTE: If the port selected is too low, root privilege may be
 * required. 
 *  
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Receiver3 {
	private static DatagramSocket receiverSocket;

	public static void main(String args[]) throws Exception {
		if (args.length != 2) {
			throw new Exception("Needs <Port> <Filename> to work properly.");
		}

		// Set the image to appear within the current directory
		boolean last = false;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String workingDir = new File("").getAbsolutePath();
		File cwk = new File(workingDir + "/" + args[1]);
		FileOutputStream fos = new FileOutputStream(cwk);

		try {
			// set up the socket to receive packets
			int port = Integer.parseInt(args[0]);
			receiverSocket = new DatagramSocket(port);
			short lookingfor = 0;
			short highestack = 0;

			byte[] receiveData;
			System.out.println("Receving...");

			// While we haven't received the last packet, continue to wait
			while (!last) {
				// Receive the data and get return port
				receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				receiverSocket.receive(receivePacket);
				receiveData = receivePacket.getData();
				int returnport = receivePacket.getPort();

				// Identify which packet we are dealing with
				short headernum = toShort(receiveData[0], receiveData[1]);

				// If this packet is what we are looking for, send ack,
				// increment what are looking for, and write to file
				if (lookingfor == headernum) {
					highestack = lookingfor;
					lookingfor++;
					sendACK(headernum, returnport);
					bos.write(receiveData, 3, receivePacket.getLength() - 3);

					// If last, terminate
					if (receiveData[2] == 1) {
						last = true;
					}
				} else {
					/* If the received data was not what we wanted, then we send
					 *  an ACK for what we previously received (this assumes we are
					 *  receiving in order)
					 */
					sendACK(highestack, returnport);
				}

			}

			// Write to file and close
			bos.flush();
			bos.writeTo(fos);
			bos.close();
			receiverSocket.close();
			fos.close();

		} catch (SocketException ex) {
			// Socket is busy - function won't work
			System.out.println("UDP Port " + args[0] + " is occupied.");
			System.exit(1);
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

	/**
	 * Function to send acknowledgements back to the Sender
	 * 
	 * @param s
	 *            - the value received (either what we expected or what we
	 *            received previously)
	 * @param port
	 *            - the port we want to send this back to
	 */
	public static void sendACK(short s, int port) {
		byte send[] = toBytes(s);
		try {
			DatagramPacket sendPacket = new DatagramPacket(send, send.length,
					InetAddress.getByName("localhost"), port);
			receiverSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
