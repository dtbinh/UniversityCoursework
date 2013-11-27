

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import vmap.main.Tools;

import junit.framework.TestCase;

public class Task6 extends TestCase {
	
	
	@Test
	public void testGetExtension() {
		File temp = null;
		Tools.getExtension(temp);
	}
	

	@Test
	public void testCopyDirectory() throws FileNotFoundException, IOException {

		String temp = createRandomFile("/tmp/tmpfile1");
		
		File from = new File(temp);
		File to = new File(temp);
		

		try {
			Tools.copyDirectory(from, to);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			deleteRandomFile(temp);
		}
		deleteRandomFile(temp);
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

}
