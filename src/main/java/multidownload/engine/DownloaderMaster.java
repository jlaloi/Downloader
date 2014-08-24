package main.java.multidownload.engine;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import main.java.multidownload.Factory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public class DownloaderMaster extends Thread implements Downloader {

	private File file;

	private RandomAccessFile randomAccessFile;

	private String url;

	private HttpClient httpClient;

	private int nbConnection;

	private long downloaded = 0, fileSize = 0;

	private ArrayList<Downloader> downloaders;

	private boolean done = false;

	private boolean cancel = false;

	public DownloaderMaster(File file, String url, int nbConnection) {
		super();
		this.url = url;
		this.file = file;
		this.httpClient = Factory.getNewHttpClient();
		this.nbConnection = nbConnection;
		this.downloaders = new ArrayList<Downloader>();
	}

	public void run() {
		try {
			HttpGet httpget = new HttpGet(url);

			// Get File Size to create the file
			HttpResponse response = httpClient.execute(httpget);
			fileSize = new Long(response.getFirstHeader("Content-Length").getValue());
			if (fileSize < 0) {
				throw new Exception("File size is 0");
			}

			// Create file
			randomAccessFile = new RandomAccessFile(file, "rw");
			randomAccessFile.setLength(fileSize);

			// Create Part
			long partsize = fileSize / nbConnection;
			long from = partsize;
			long to = 0;
			for (int i = 1; i < nbConnection; i++) {
				if (i + 1 != nbConnection) {
					to = from + partsize;
				} else {
					to = fileSize - 1;
				}
				DownloaderPart downloader = new DownloaderPart(httpClient, from, to, url, randomAccessFile);
				downloaders.add(downloader);
				downloader.start();
				from += partsize + 1;
			}

			// Downloading
			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();
			byte buf[] = new byte[1024];
			int len;
			int pos = 0;
			while ((len = inputStream.read(buf)) > 0 && !cancel && downloaded < partsize) {
				synchronized (file) {
					randomAccessFile.seek(pos);
					randomAccessFile.write(buf, 0, len);
				}
				downloaded += len;
				pos += len;
			}
			if (cancel) {
				entity = null;
				inputStream = null;
			} else {
				done = true;
				inputStream.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				while (!isCancel() && !isAllDone()) {
					sleep(100);
				}
				randomAccessFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public int getNbConnection() {
		int result = (done || cancel) ? 0 : 1;
		for (Downloader downloader : downloaders) {
			if (!downloader.isDone() && !downloader.isCancel()) {
				result++;
			}
		}
		return result;
	}

	public long getDownloaded() {
		return Math.min(getRealDownloaded(), fileSize);
	}

	public long getRealDownloaded() {
		long result = downloaded;
		for (Downloader downloader : downloaders) {
			result += downloader.getDownloaded();
		}
		return result;
	}

	public long getFileSize() {
		return fileSize;
	}

	public boolean isAllDone() {
		boolean result = done;
		for (Downloader downloader : downloaders) {
			result = downloader.isDone() && result;
		}
		return result;
	}

	public boolean isDone() {
		return done;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void cancel() {
		cancel = true;
		for (Downloader downloader : downloaders) {
			downloader.cancel();
		}
	}

	public RandomAccessFile getRandomAccessFile() {
		return randomAccessFile;
	}

	public File getFile() {
		return file;
	}

}
