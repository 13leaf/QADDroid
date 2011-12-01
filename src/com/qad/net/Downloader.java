package com.qad.net;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpResponse;

import com.qad.lang.Files;
import com.qad.lang.Streams;

public class Downloader {

	/**
	 * 通知下载完成情况
	 * 
	 * @author 13leaf
	 * 
	 */
	public interface PublishCallBack {
		/**
		 * @param downSize
		 *            已下载字节大小
		 * @param fullSize
		 *            总下载字节大小
		 * @param percent
		 *            下载百分比。公式如下
		 *            Math.floor(downSize/fullSize*100)。注意不是百分比例,如返回50,则表示完成百分之50
		 * @return 返回false表示继续下载,若返回true将终止下载            
		 */
		boolean publish(long downSize, long fullSize, int percent);
	}

	public final static int DEFAULT_BLOCK_SIZE = 1024;

	public static void downLoad(String url, String targetPath, int blockSize,
			PublishCallBack publisher) throws IOException {
		File target = new File(targetPath);
		Files.createNewFile(target);
		OutputStream targetOutputStream = Streams.fileOut(target);
		byte[] block = new byte[blockSize];

		HttpResponse response = HttpUtils.executeGet(url);

		BufferedInputStream is=new BufferedInputStream(response.getEntity().getContent(), blockSize);

		long downSize = 0;
		long fullSize = response.getEntity().getContentLength();

		try {
			int count;
			while ((count = is.read(block)) != -1) {
				// write block and publish task
				targetOutputStream.write(block, 0, count);
				downSize += count;
				if (publisher != null) {
					if(!publisher.publish(downSize, fullSize, (int) (Math
							.floor(downSize / (double) fullSize * 100)))){
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			Streams.safeClose(targetOutputStream);
			Streams.safeClose(is);
		}
	}

	public static void downLoad(String url, String targetPath)
			throws IOException {
		downLoad(url, targetPath, DEFAULT_BLOCK_SIZE, null);
	}
}
