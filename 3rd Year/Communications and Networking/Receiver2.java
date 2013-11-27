/* Scott Hofman s0943941
 * 
 * This is the Receiver for Question 2. As with Receiver1, this function will receive an image
 * from Sender1 via packets. After each received packet, it either sends an acknowledgement
 * that it received what it expected (the next sequential packet) or an ACK with what it is expecting
 * and wants back. The function needs a port number and a filename to write the output to 
 * (NOTE: If the port selected is too low, root privilege may be required. 
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

public class Receiver2 {
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

			// The value of what we are expecting
			short lookingfor = 0;
			byte[] receiveData;
			System.out.println("Receiving data...");

			// While we haven't received the last packet, continue to wait
			while (!last) {

				// Receive the data via socket and find the port that we will
				// return to
				receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				receiverSocket.receive(receivePacket);
				receiveData = receivePacket.getData();
				int returnport = receivePacket.getPort();

				// Process the data - strip the header to find which number of
				// packet this is
				short headernum = toShort(receiveData[0], receiveData[1]);

				// If this packet is what we are looking for, send an ACK,
				// increment what we are looking for, and write to the
				// ByteOutputStream
				if (lookingfor == headernum) {
					lookingfor++;
					sendACK(headernum, returnport);
					bos.write(receiveData, 3, receivePacket.getLength() - 3);

					// If its the last one, finish it off
					if (receiveData[2] == 1) {
						last = true;
					}

				} else {
					// If the received data was not what we wanted, then we send
					// an ACK for
					// what we previously received (this assumes we are
					// receiving in order)
					sendACK((short) (lookingfor - 1), returnport);
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
			e.printStackTrace();
		}
	}

}
