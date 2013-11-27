import static org.junit.Assert.*;

import org.junit.Test;

import java.net.*;

public class Task4 {

	@Test
	public void testLDFU1() {
		
		// All tests are done assuming the osName = 'Windows XP' & osNameStart = "Win"

		// Tests the file protocol branches
		try
		{
			
			URL url = new URL("file://afs/inf.ed.ac.uk/user/s09/s0914007/hello.txt");
			Vmap vmap = new Vmap();
			
			vmap.openDocument(url);
			
		}
		catch (Exception e)
		{
			fail("File branch not accessed");
		}
		
	}
	
	@Test
	public void testLDFU2() {
		
		// Tests the mailto protocol branches
		try
		{
			
			URL url = new URL("mailto:abc123@gmail.com");
			Vmap vmap = new Vmap();
			
			vmap.openDocument(url);
			
		}
		catch (Exception e)
		{
			fail("Mailto branch not accessed");
		}
		
	}

}


