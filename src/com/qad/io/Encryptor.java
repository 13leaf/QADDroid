package com.qad.io;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class Encryptor {

	public static byte[] encrypt(byte[] content, byte[] key) {
		if (key.length == 0)
			throw new IllegalArgumentException("key can not be empty!");
		byte[] copy = copyOf(content, content.length);
		encrypt1(copy, key, 0);
		return copy;
	}

	private static void encrypt1(byte[] content, byte[] key, int start) {
		for(int i=start,j=0;i<content.length;i++,j=(j+1==key.length?0:j+1))
		{
			content[i]^=key[j];
		}
	}
	
	/**
	 * 获取指定参数后密钥的偏移量
	 * @param contentLength
	 * @param keyLength
	 * @param start
	 * @param keyOffset
	 * @return
	 */
	public static int getKeyOffset(int contentLength,int keyLength,int start,int keyOffset)
	{
		return (contentLength+keyLength-keyOffset-start)%keyLength;
	}
	
	private static void encrypt2(byte[] content,byte[] key,int start,int keyOffset)
	{
		for(int i=keyOffset;i<key.length;i++)
			content[i-keyOffset]^=key[i];
		encrypt1(content, key, key.length-keyOffset+start);
	}

	public static byte[] decrypt(byte[] content, byte[] key) {
		// 异或的解密等于加密
		return encrypt(content, key);
	}

	/**
	 * @param file
	 * @param privateKey
	 * @throws IOException
	 */
	public static void encryptFile(File file, byte[] privateKey)
			throws IOException {
//		////////////  d    r    a     g   o    n    f    l    y
//		byte[] head={0x64,0x72,0x61,0x67,0x6f,0x6e,0x66,0x6c,0x79};
		if (privateKey.length == 0)
			throw new IllegalArgumentException("key can not be empty!");
		RandomAccessFile randomFile = null;
		
		try {
			randomFile = new RandomAccessFile(file, "rw");
			byte[] buffer = new byte[getFitBufferSize(privateKey.length, 8192)];
			int cusor = 0;
			int latestKeyOffset=0;
			while (true) {
				int currentLength = randomFile.read(buffer);
				if (currentLength == -1)
					break;
				byte[] content = buffer;
				if (currentLength != buffer.length)// truncat more
				{
					content = copyOf(buffer, currentLength);
				}
				encrypt2(content, privateKey, 0,latestKeyOffset);// encrypt content
				latestKeyOffset=getKeyOffset(currentLength, privateKey.length, 0, latestKeyOffset);
				randomFile.seek(cusor);
				randomFile.write(content);
				cusor += currentLength;
			}
		} finally {
			randomFile.close();
		}
	}
	
	private static byte[] copyOf(byte[] buffer,int length)
	{
		byte[] copy=new byte[length];
		for(int i=0;i<length;i++)
			copy[i]=buffer[i];
		return copy;
	}
	
	/**
	 * 依据总密钥长度和参考size来获取fitSize
	 * @param keyLength
	 * @param size
	 * @return
	 */
	private static int getFitBufferSize(int keyLength,long size)
	{
		if(keyLength>size){
			if(keyLength>20*1024)
				throw new RuntimeException("keyLength should less than 20KB!");
			return keyLength;
		}else {
			return (int) (Math.floor(size/keyLength)*keyLength);
		}
		
	}

	public static void decryptFile(File file, byte[] privateKey)
			throws IOException {
		encryptFile(file, privateKey);
	}
	
	public static class DecryptInputStream extends FileInputStream
	{
		final byte[] privateKey;
		int pos=-1;
		
		public DecryptInputStream(File file,byte[] privateKey) throws FileNotFoundException {
			super(file);
			if(privateKey==null || privateKey.length==0) throw new IllegalArgumentException("privateKey can not be empty!");
			this.privateKey=privateKey;
		}
		
		private int encryptByOffset(int offset,int val)
		{
			int keyOffset=offset%privateKey.length;
			val^=privateKey[keyOffset];
			return val;
		}
		
		@Override
		public int read() throws IOException {
			int val=super.read();
			pos++;
			return encryptByOffset(pos, val);
		}
		
		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int readedLength=super.read(b, off, len);
			for(int i=off;i<off+readedLength;i++)
			{
				pos++;//此处可以做优化
				b[i]=(byte) encryptByOffset(pos, b[i]);
			}
			return readedLength;
		}
	}

	public static void main(String[] args) throws Exception {
		byte[] key = "kiss".getBytes();
		byte[] content = "1234567890".getBytes();
		encrypt2(content, key,0,2);
		System.out.println(new String(content));
		
		DecryptInputStream is=new DecryptInputStream(new File("我的母亲丁玲.txt"), key);
		InputStreamReader reader=new InputStreamReader(is);
		BufferedReader br=new BufferedReader(reader);
		for(int i=0;i<100;i++)
			System.out.println(br.readLine());
		br.close();
	}
}