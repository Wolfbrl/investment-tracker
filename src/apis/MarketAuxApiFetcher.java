package apis;

import java.io.*;
import java.net.*;
import java.util.List;

import com.google.gson.Gson;

import domain.NewsResponse;

public class MarketAuxApiFetcher {

	private static final String apiToken = "";
	private static final Gson gson = new Gson();

	public static NewsResponse request(List<String> symbols) {
		StringBuilder content = new StringBuilder();
		try {

			String joinedSymbols = String.join(",", symbols);
			String urlString = "https://api.marketaux.com/v1/news/all?symbols=" + joinedSymbols
					+ "&filter_entities=true&language=en&api_token=" + apiToken;

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
