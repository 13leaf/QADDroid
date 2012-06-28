package com.qad.test;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import android.os.Environment;

import com.qad.lang.Files;
import com.qad.lang.util.Texts;

public class FilesTest extends TestCase {
	
	File testDir=new File(Environment.getExternalStorageDirectory(),"testDir");
	
	@Override
	protected void setUp() throws Exception {
		testDir.mkdir();
	}
	
	@Override
	protected void tearDown() throws Exception {
		Files.deleteDir(testDir);
	}
	
	
	private void createFile(File dir,int length) throws IOException
	{
		for(int i=1;i<=length;i++)
			new File(dir,"file"+i).createNewFile();
	}
	
	/**
	 * constructor
	 * Root
	 * dir1 dir2 file1 file2 file3 file4
	 * 
	 * dir1=file1 file2 file3
	 * dir2=dir21 file1 file2 file3 file4
	 * dir21=file1
	 * @throws Exception
	 */
	public void testScanFiles() throws Exception
	{
		File subDir1=new File(testDir,"subDir");subDir1.mkdir();
		
		final int rootFileCount=4;
		final int subDir1FileCount=3;
		
		createFile(testDir, rootFileCount);
		createFile(subDir1, subDir1FileCount);
		
		final File subDir2=new File(testDir,"subDir2");subDir2.mkdir();
		final int subDir2FileCount=4;
		createFile(subDir2, subDir2FileCount);
		
		final File subDir21=new File(subDir2,"subDir1");subDir21.mkdir();
		final int subDir21FileCount=1;
		createFile(subDir21, subDir21FileCount);
		
		assertEquals(rootFileCount+subDir1FileCount+subDir2FileCount+subDir21FileCount, Files.scanFiles(testDir).length);
	}
	
	
	public void testFileSize()
	{
		final int aFileSize=1024*100;//100KB
		final int fileCount=10;//10个文件
		final int totalSize=aFileSize*fileCount;
		
		for(int i=0;i<fileCount;i++)
		{
			File file=new File(testDir,""+i);
			Files.write(file, new byte[aFileSize]);
			assertEquals(aFileSize, file.length());
		}
		
		assertEquals(totalSize,Files.size(testDir));
		assertEquals("0.98MB", Texts.formatSize(Files.size(testDir)));
	}

}
