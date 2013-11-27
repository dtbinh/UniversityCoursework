

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import vmap.main.Vmap;

import java.util.Random;
import java.util.zip.*;

public class Task3 {

	Vmap v;
	Vmap v2;

	@Test
	public void testVmap() throws SecurityException {

		deleteDir("/afs/inf.ed.ac.uk/user/s09/s0943941/vmap/");

		v = new Vmap();
		//v2 = new Vmap();
	}

	private void deleteDir(String dir) {

		File root = new File(dir);
		File[] files = root.listFiles();
		for (int i = 0; i < files.length; i++) {

			if (files[i].isDirectory()) {
				deleteDir(files[i].toString());
			} else {
				files[i].delete();
			}

		}

		root.delete();

	}

	@Test
	public void testIsApplet() {
		v = new Vmap();
		assertFalse(v.isApplet());
	}

	@Test
	public void testGetVmapVersion() {
		v = new Vmap();
		assertTrue(v.getVmapVersion().equals(Vmap.version));
	}

	@Test
	public void testGetWinHeight() {
		v = new Vmap();
		System.out.println(v.getWinHeight());
	}
	
	@Test
	public void testExtractZip()
	{
		
		try
		{
			//Create temporary folder to hold the zip
			String tmpFolder = "/tmp/vmaptest/";
			if (new File(tmpFolder).exists())
				deleteDir(tmpFolder);
			
			//Create and fill a random file
			String tmpFile = "/tmp/vmapziptest";
			createRandomFile(tmpFile);
			
			//Zip the file with a function
			String zipFile = "/tmp/vmaptestzip.zip";
			zipFile(tmpFile,zipFile);
			
			//Create output folder
			File outputFolder = new File(tmpFolder);
			outputFolder.mkdir();
			
			//Extract the zip file 
			v = new Vmap();
			v.extractZip(new FileInputStream(zipFile), outputFolder);
			
			//Check that the extracted zip file is equivalent to the initial file
			assertTrue(compareFiles(tmpFile, tmpFolder + "vmapziptest"));
			
			deleteRandomFile(tmpFile);
			deleteDir(tmpFolder);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception generated in extract zip test");
		}
		
	}
	
	@Test
	public void testCompareFiles()
	{
		//Test that compare files works as intended
		try
		{
			
			String tmpFile = createRandomFile("/tmp/tmpfile1");
			String tmpFile2 = createRandomFile("/tmp/tmpfile2");
			
			//Compared the file against itself (true)
			assertTrue(compareFiles(tmpFile,tmpFile));
			//Compared the file against another random file (false)
			assertFalse(compareFiles(tmpFile,tmpFile2));
			
			deleteRandomFile(tmpFile);
			deleteRandomFile(tmpFile2);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception generated in compare files test");
		}
		
	}
	
	/* taken from: http://www.exampledepot.com/egs/java.util.zip/CreateZip.html */
	private void zipFile(String fileIn, String fileOut) {
		
		
		// These are the files to include in the ZIP file
		String[] filenames = new String[] { fileIn };
		
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		try {
			// Create the ZIP file
			String outFilename = fileOut;
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					outFilename));

			// Compress the files
			for (int i = 0; i < filenames.length; i++) {
				FileInputStream in = new FileInputStream(filenames[i]);

				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(filenames[i]));

				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				// Complete the entry
				out.closeEntry();
				in.close();
			}

			// Complete the ZIP file
			out.close();
		} catch (IOException e) {
			// no catch stuff here
		}
	}
	
	private boolean compareFiles(String f1, String f2) throws FileNotFoundException, IOException
	{
		//Create two 
		FileInputStream fin1 = new FileInputStream(f1);
		FileInputStream fin2 = new FileInputStream(f2);
		
		int nextByte;
		
		while ((nextByte = fin1.read()) != -1)
		{
			
			if (nextByte != fin2.read())
			{
				return false;
			}
			
		}
		
		if (fin2.read() != -1)
			return false;
		
		return true;

	}
	
	private String createRandomFile(String filename) throws FileNotFoundException, IOException
	{
		
		FileOutputStream fout = new FileOutputStream(filename);
		
		Random r = new Random();
		
		for (int i = 0; i < 1000; i++)
		{
			
			fout.write(r.nextInt(255));
			
		}
		
		return filename;
		
	}
	
	private void deleteRandomFile(String filename)
	{
		File f = new File(filename);
		f.delete();
	}
	
	@Test
	public void testGetProperty() {
		v = new Vmap();
		assertEquals(v.getProperty("language"), "en");
	}

	@Test
	public void testSetProperty() {
		v = new Vmap();
		v.setProperty("test", "tested");
		assertEquals(v.getProperty("test"), "tested");
	}



	@Test
	public void testSaveProperties() {
		v = new Vmap();
		v.saveProperties();
	}




	@Test
	public void testOut() {
		v = new Vmap();
		v.out("Test");
		assertEquals(v.getStatus(), "Test");
	}

	@Test
	public void testErr() {
		v = new Vmap();
		v.err("Test");
		assertEquals(v.getStatus(), "Test");
	}

	@Test
	public void testOpenDocument() {
		v = new Vmap();
		URL test;
		File testfile = new File("/tmp/test.doc");
		try {
			if(!testfile.exists()) 
				testfile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("Could not create test document");
		}
		try {
			// This file exists on the Desktop - should pass
			test = new URL("file://"+testfile.toString());
			v.openDocument(test);
		} catch (Exception e) {
			fail("Failed to open test URL");
		}
		try {
			// This file does not exist - should pass
			test = new URL("mailto:s0943941@sms.ed.ac.uk");
			v.openDocument(test);
		} catch (Exception e) {
			fail("Failed to open test URL");
		}
		try {
			// This file does not exist - should pass
			test = new URL("http://www.google.com/");
			v.openDocument(test);
		} catch (Exception e) {
			fail("Failed to open test URL");
		}
	}

	@Test
	public void testSetWaitingCursor() {
		v = new Vmap();
		v.setWaitingCursor(false);
		assertEquals(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR), v.getCursor());
		v.setWaitingCursor(true);
		assertEquals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR), v.getCursor());
	}


	@Test
	public void testTranspose() {
		v = new Vmap();
		assertEquals("ab  defgh", v.transpose("abcdefgh", 'c', "  "));
	}

	@Test
	public void testGetLogger() {
		v = new Vmap();
		assertEquals(java.util.logging.Logger.getLogger("Vmap"),
				v.getLogger("Vmap"));
	}

	@Test
	public void testMain() {
		// Vmap.main(null);
		String[] test = new String[1];
		test[0] = "";
		Vmap.main(test);
		test[0] = "patterns.xml";
		Vmap.main(test);
	}

}
