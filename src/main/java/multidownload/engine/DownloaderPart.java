package main.java.multidownload.engine;

import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public class DownloaderPart extends Thread implements Downloader {

	private HttpClient httpClient;
	private long from, to;
	private String url;
	private RandomAccessFile file;
	private long downloaded = 0;
	private boolean cancel = false;
	private boolean done = false;

	public DownloaderPart(HttpClient httpClient, long from, long to, String url, RandomAccessFile file) {
		super();
		this.httpClient = httpClient;
		this.from = from;
		this.to = to;
		this.url = url;
		this.file = file;
	}

	public void run() {
		try {
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("Range", "bytes=" + from + "-" + to);
			HttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null && file != null) {
				InputStream inputStream = entity.getContent();
				byte buf[] = new byte[1024];
				int len;
				int pos = (int) from;
				while ((len = inputStream.read(buf)) > 0 && !cancel) {
					synchronized (file) {
						file.seek(pos);
						file.write(buf, 0, len);
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
			}
		} catch (Exception e) {
			cancel = true;
			e.printStackTrace();
		}

	}

	public boolean isDone() {
		return done;
	}

	public void cancel() {
		cancel = true;
	}

	public boolean isCancel() {
		return cancel;
	}

	public long getFrom() {
		return from;
	}

	public long getTo() {
		return to;
	}

	public long getDownloaded() {
		return downloaded;
	}

}
