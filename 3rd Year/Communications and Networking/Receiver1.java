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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Receiver1 {
	public static void main(String args[]) throws Exception {
		// Ensure the proper arguments are given
		if (args.length != 2) {
			throw new Exception("Needs <Port> <Filename> to work properly.");
		}

		// Set up variables - set the image to appear within the current
		// directory (bin) NOTE: This operation may require root privileges to run
		boolean last = false;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String workingDir = new File("").getAbsolutePath();
		File cwk = new File(workingDir + "/" + args[1]);
		FileOutputStream fos = new FileOutputStream(cwk);

		try {
			// Get the port and start a DatagramSocket to receive the Data
			int port = Integer.parseInt(args[0]);
			DatagramSocket receiverSocket = new DatagramSocket(port);

			byte[] receiveData;
			System.out.println("Receiving packets...");

			// We loop until we have received the final packet (indicated by its
			// header)
			while (!last) {
				// Receive the data
				receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				receiverSocket.receive(receivePacket);

				// If the header indicates we are at the end of the file, we set
				// the loop to terminate
				if (receiveData[2] == 1) {
					last = true;
				}

				// Write the received data (aside from the header) to a
				// ByteOutputStream
				bos.write(receiveData, 3, receivePacket.getLength() - 3);
			}

			// Finish writing the file and close everything
			bos.flush();
			bos.writeTo(fos);
			bos.close();
			receiverSocket.close();
			fos.close();

		} catch (SocketException ex) {
			// Error checking if the port is busy and won't run
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
}
