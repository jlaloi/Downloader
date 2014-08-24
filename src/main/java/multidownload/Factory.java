package main.java.multidownload;

import java.awt.Font;
import java.text.NumberFormat;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

public class Factory {

	public static final String fontName = "Calibri";

	public static final int fontSize = 12;

	public static final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Chrome/7.0.517.44 Safari/534.7";

	public static final Font font = new Font(fontName, Font.PLAIN, fontSize);

	public static double d1024 = new Double(1024);

	public static String encodeUrl(String url) {
		String result = url;
		result = result.replace("[", "%5B");
		result = result.replace("]", "%5D");
		result = result.replace(" ", "%20");
		return result;
	}

	public static String formatTime(long elapsedTime) {
		String format = String.format("%%0%dd", 2);
		String ref = String.format(format, 0);
		elapsedTime = elapsedTime / 1000;
		String seconds = String.format(format, elapsedTime % 60);
		String minutes = String.format(format, (elapsedTime % 3600) / 60);
		if (minutes.equals(ref))
			return seconds + "s";
		String hours = String.format(format, elapsedTime / 3600);
		if (hours.equals(ref))
			return minutes + "m" + seconds + "s";
		String time = hours + "h" + minutes;
		return time;
	}

	public static String getName(String name) {
		String[] tmp = name.replace("%20", " ").split("/");
		if (tmp.length == 0)
			return name;
		return tmp[tmp.length - 1];
	}

	public static String formatSize(double size, NumberFormat myformat) {
		if (size < d1024) {
			return myformat.format(size) + " Byte";
		}
		size /= d1024;
		if (size < 1024) {
			return myformat.format(size) + " Ko";
		}
		size /= d1024;
		if (size < 1024) {
			return myformat.format(size) + " Mo";
		}
		size /= d1024;
		return myformat.format(size) + " Go";
	}

	public static String formatSize(double size) {
		NumberFormat myformat = NumberFormat.getInstance();
		myformat.setMaximumFractionDigits(2);
		myformat.setMinimumFractionDigits(2);
		return formatSize(size, myformat);
	}

	public static HttpClient getNewHttpClient() {
		BasicCookieStore cookieStore = new BasicCookieStore();
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setDefaultCookieStore(cookieStore);
		httpClientBuilder.setUserAgent(userAgent);
		return httpClientBuilder.build();
	}
}
