package ntou.cs.sose.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class MyHttpURLConnection {
	public String connection(String URL) {
		String errorMessage = "{\"error\":\"Unable to connect.\"}";
		try {
			URL url = new URL(URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json; utf-8");

			BufferedReader br = null;
			if (200 <= con.getResponseCode() && con.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String responseBody = br.lines().collect(Collectors.joining());
				return responseBody;
			} else {
				System.out.println("Error: " + con.getResponseCode());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return errorMessage;

	}
}
