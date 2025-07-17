package apis;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import domain.NewsResponse;

public class MarketAuxApiFetcher {

	private static final String apiToken = "FwBLXf749FClyD5X8oObXww6UjDhxxf6vM3SmQEc";
	private static final Gson gson = new Gson();

	public static NewsResponse request(String symbol) {
		StringBuilder content = new StringBuilder();
		try {

			String urlString = "https://api.marketaux.com/v1/news/all?symbols=" + symbol
					+ "&filter_entities=true&language=en&sort_by=published_desc" + "&api_token=" + apiToken;

			URL url = new URL(urlString);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");

			int status = connection.getResponseCode();
			System.out.println("Response code: " + status);

			BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String inputLine;

			while ((inputLine = input.readLine()) != null) {
				content.append(inputLine);
			}

			input.close();

			connection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return gson.fromJson(content.toString(), NewsResponse.class);
	}

}
