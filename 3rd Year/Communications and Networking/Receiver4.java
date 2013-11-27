/* Scott Hofman s0943941
 * 
 * This is the Receiver for Question 4. As with the previous receivers,
 * this receives an image over UDP. This implementation uses Selective Repeat,
 * where this Receiver has a window size. Whenever we receive a packet within
 * our window, we send an ACK back for that packet. Otherwise, we ignore it. 
 * Note that the window size needs to be the same as Sender4 for Selective 
 * Repeat to be implemented properly.
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
import java.util.HashMap;

public class Receiver4 {
	private static DatagramSocket receiverSocket;

	public static void main(String args[]) throws Exception {
		if (args.length != 3) {
			throw new Exception(
					"Needs <Port> <Filename> <WindowSize> to work properly.");
		}

		//Set up the file in the current directory
		boolean last = false;
		boolean done = false;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String workingDir = new File("").getAbsolutePath();
		File cwk = new File(workingDir + "/" + args[1]);
		FileOutputStream fos = new FileOutputStream(cwk);

		try {
			//Set up the socket to receive data and return Acks
			int port = Integer.parseInt(args[0]);
			receiverSocket = new DatagramSocket(port);
			short lookingfor = 0;
			byte[] receiveData;
			
			//Set up the window of available ACKs
			int base = 0;
			short greatest = 0;
			int window = Integer.parseInt(args[2]);
			
			//Set up two hash maps that function as a buffer for when we receive packets out of order
			HashMap<Short, byte[]> buffer = new HashMap<Short, byte[]>(window);
			HashMap<Short, Integer> length = new HashMap<Short, Integer>(window);
			
			//While we still need to receive packets
			while (!done) {
				
				//Receive Data
				receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				receiverSocket.receive(receivePacket);
				receiveData = receivePacket.getData();
				
				//Find the return port and header
				int returnport = receivePacket.getPort();
				short headernum = toShort(receiveData[0], receiveData[1]);

				//If the header sequence is within our window, then we acknowledge it
				if (headernum < base + window) {
					sendACK(headernum, returnport);

					//If it has been received in order, write it to the file
					if (headernum == lookingfor) {
						bos.write(receiveData, 3, receivePacket.getLength() - 3);
						lookingfor++;
					} else {
						//If not, place it in the buffer
						if (headernum > base && headernum < base + window) {
							buffer.put(headernum, receiveData);
							length.put(headernum, receivePacket.getLength());
						}
					}

					/*
					 * If we have incremented lookingfor, and that is within our buffer
					 * then we can write it to file. Increment lookingfor again and repeat
					 * until our buffer does not contain what we want
					 */
					while (buffer.containsKey(lookingfor)) {
						bos.write(buffer.get(lookingfor), 3,
								length.get(lookingfor) - 3);
						buffer.remove(lookingfor);
						length.remove(lookingfor);
						lookingfor++;
					}
					
					//Since buffer doesn't have what we want, our base is whatever lookingfor is
					base = lookingfor;

					//Check if we've received the last packet
					if (receiveData[2] == 1) {
						last = true;
						greatest = headernum;
					}

					//Error checking to finish
					if (base - 1 == greatest && greatest != 0 && last) {
						done = true;
					}
				}
			}
			
			//Write to file and close
			bos.flush();
			bos.writeTo(fos);
			bos.close();
			receiverSocket.close();
			fos.close();

		} catch (SocketException ex) {
			//This will never run because the port is being occuped
			System.out.println("UDP Port " + args[0] + " is occupied. Choose another or restart.");
			System.exit(1);
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

	/**
	 * Function to send acknowledgements back to the Sender
	 * @param s - the value received (either what we expected or what we received previously)
	 * @param port - the port we want to send this back to
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
