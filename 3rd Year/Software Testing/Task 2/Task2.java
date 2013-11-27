import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.net.*;



public class Task2 {
	
	@Before
	public void setUp() throws Exception {
		
		@SuppressWarnings("unused")
		RelativeURL rl = new RelativeURL();
		
	}
	
	//Different Protocol
	@Test
	public void test1() {
		
		assertEquals("http://host/",runTest("ftp://host/","http://host/"));
		
	}
	
	//Different Host
	@Test
	public void test2() {
		
		assertEquals("http://differenthost/",runTest("http://host/","http://differenthost/"));
		
	}
	
	//Base has File
	@Test
	public void test3() {
		
		assertEquals("",runTest("http://host/file.ext","http://host/"));
		
	}
	
	//Target has File
	@Test 
	public void test4() {
		
		assertEquals("file.ext",runTest("http://host/","http://host/file.ext"));
		
	}
	
	//Base has Query
	@Test
	public void test5() {
		
		assertEquals("",runTest("http://host/?query","http://host/"));
		
	}
	
	//Target has Query
	@Test 
	public void test6() {
		
		assertEquals("?query",runTest("http://host/","http://host/?query"));
		
	}
	
	//Base has Fragment
	@Test
	public void test7() {
		
		assertEquals("",runTest("http://host/#fragment","http://host/"));
		
	}
	
	//Target has Fragment
	@Test 
	public void test8() {
		
		assertEquals("#fragment",runTest("http://host/","http://host/#fragment"));
		
	}
	
	/* folder structure: folder1/folder2/folder3 ... */
	
	/* folder structures the same */
	// Zero Folders
	@Test
	public void test9() {
		
		assertEquals("",runTest("http://host/","http://host/"));
		
	}
	
	//Zero Folders for base, One for target
	@Test 
	public void test10() {
		
		assertEquals("folder1/",runTest("http://host/","http://host/folder1/"));
		
	}
	
	//Zero Folders for base, Two folders for target
	@Test
	public void test11() {
		
		assertEquals("folder1/folder2/",runTest("http://host/","http://host/folder1/folder2/"));
		
	}
	
	//One Folder for base, Zero folders for Target
	@Test
	public void test12() {
		
		assertEquals("../",runTest("http://host/folder1/","http://host/"));
		
	}
	
	//One Folder for Base, Two Folders for Target
	@Test
	public void test13() {
		
		assertEquals("folder2/",runTest("http://host/folder1/","http://host/folder1/folder2/"));
		
	}
	
	//Two Folders for Base, Zero Folders for Target
	@Test 
	public void test14() {
		
		assertEquals("../../",runTest("http://host/folder1/folder2/","http://host/"));
		
	}
	
	//Two Folders for Base, One Folder for Target
	@Test
	public void test15() {
		
		assertEquals("../",runTest("http://host/folder1/folder2/","http://host/folder1/"));
		
	}
	
	/* end folder structures the same */
	
	/* folder structure (base): basefolder1/basefolder2/basefolder3 ... */
	/* folder structure (target): targetfolder1/targetfolder2/targetfolder3 ... */
	
	/* folder structures different - names of folders*/
	// Number of Folders Same
	@Test
	public void test16() {
		
		assertEquals("../targetfolder1/",runTest("http://host/basefolder1/","http://host/targetfolder1/"));
		
	}
	
	// Number of Folders Different - Base has one, Target has two
	@Test
	public void test17() {
		
		assertEquals("../targetfolder1/targetfolder2/",runTest("http://host/basefolder1/","http://host/targetfolder1/targetfolder2/"));
		
	}
	
	// Number of Folders Different - Base has two, Target has one
	@Test
	public void test18() {
		
		assertEquals("../../targetfolder1/",runTest("http://host/basefolder1/basefolder2/","http://host/targetfolder1/"));
		
	}
	
	// Number of Folders Different - Base has two, Target has two
	@Test
	public void test19() {
		
		assertEquals("../../targetfolder1/targetfolder2/",runTest("http://host/basefolder1/basefolder2/","http://host/targetfolder1/targetfolder2/"));
		
	}
	/* end folder structures the same */
	
	
	
	
	/* task 2 test */
	@Test
	public void task2Test1()
	{
		
		assertEquals("",runTest("http://host/folder1/folder2/folder3/folder4/folder5/","http://host/folder1/folder2/folder3/folder4/folder5/"));
		
	}
	
	
	
	private String runTest(String baseStr, String targetStr)
	{
		
		URL base = null, target = null;
		
		try
		{
			base = new URL(baseStr);
			target = new URL(targetStr);
			
			System.out.println("\n\nbase: " + baseStr + " target:" + targetStr);
			
			System.out.println("target file: " + target.toString());
			
			
		}
		catch (Exception e)
		{
			return null;
		}
		
		return RelativeURL.toRelativeURL(base, target);
		
	}
	

}