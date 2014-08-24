package main.java.multidownload.engine;

public interface Downloader {

	public long getDownloaded();

	public boolean isDone();

	public boolean isCancel();

	public void cancel();

}
