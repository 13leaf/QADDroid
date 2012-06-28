package com.qad.test;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.qad.lang.Files;

public class FilesTest2 extends TestCase {

	File file1;
	
	File file2;

	protected void setUp() throws Exception {
		
	}

	protected void tearDown() throws Exception {
		Files.deleteDir(file1);
		Files.deleteDir(file2);
	}
	
	public void testGetParentDir() throws IOException
	{
		file1=new File("D:\\test\\abc\\..\\abc");file1.mkdirs();
		file2=new File("D:\\test");file2.mkdirs();
		assertEquals("D:\\test", Files.getParentDir(file1.getAbsolutePath(), file2.getAbsolutePath()));
	}
	
	public void testGetParentDir2() throws IOException
	{
		file1=new File("D:\\test");file1.mkdirs();
		file2=new File("C:\\test2");file2.mkdirs();
		assertNull(Files.getParentDir(file1.getAbsolutePath(),file2.getAbsolutePath()));
	}
	
	
	private void assertRelativeComplete(File original,File target) throws IOException {
		String relativePath=Files.toRelativePath(original, target);
		File relativeFile=new File(original,relativePath);
		assertEquals(relativeFile.getCanonicalPath(),target.getCanonicalPath());
	}
	
	/*
	 * 测试情景1:1是2的父文件夹
	 */
	public void testRelativePath1() throws IOException
	{
		file1=new File("D:\\test\\abc\\def");file1.mkdirs();
		file2=new File("D:\\test\\abc");
		
		assertRelativeComplete(file1,file2);
	}
	
	/*
	 * 测试情景2:1是2的子文件夹
	 */
	public void testRelativePath2() throws Exception
	{
		file1=new File("D:\\test\\abc");
		file2=new File("D:\\test\\abc\\def");file1.mkdirs();
		
		assertRelativeComplete(file1,file2);
	}
	
	/*
	 * 测试情景3:1和2有公共的父亲(至少是驱动器)
	 */
	public void testRelativePath3() throws Exception
	{
		file1=new File("D:\\test\\abc\\ilm\\nku");file1.mkdirs();
		file2=new File("D:\\test\\def\\ghk");file2.mkdirs();
		
		assertRelativeComplete(file1,file2);
	}
	
	/*
	 * 测试情景4:1和2没有公共的父亲
	 */
	public void testRelativePath4()
	{
		file1=new File("D:\\test\\abc");file1.mkdirs();
		file2=new File("E:\\test\\abc");file2.mkdirs();
		
		try {
			assertRelativeComplete(file1, file2);
			fail("Expect make Exception cause file1 and file2 are in different path");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
