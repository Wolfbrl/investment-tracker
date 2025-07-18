package apis;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import domain.NewsResponse;

public class MarketAuxApiFetcher {

	private static final String apiToken1 = "hide";
	private static final String apiToken2 = "hide";
	private static final String apiToken3 = "hide";
	private static final String apiToken4 = "hide";

	private static final Gson gson = new Gson();

	public static NewsResponse request(String symbol) {
		StringBuilder content = makeHTTPrequest(symbol);

		return gson.fromJson(content.toString(), NewsResponse.class);
	}

	private static StringBuilder makeHTTPrequest(String symbol) {
		StringBuilder content = new StringBuilder();
		int tries = 0;
		boolean responseok = false;

		try {
			do {
				tries++;
				String token = "";

				if (tries <= 4) {
					switch (tries) {
					case 1:
						token = apiToken1;
						break;

					case 2:
						token = apiToken2;
						break;

					case 3:
						token = apiToken3;
						break;

					case 4:
						token = apiToken4;
						break;
					}
				}

				String urlString = "https://api.marketaux.com/v1/news/all?symbols=" + symbol
						+ "&filter_entities=true&language=en&sort_by=published_desc" + "&api_token=" + token;

				URL url = new URL(urlString);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("GET");

				int status = connection.getResponseCode();

				System.out.println("Response code: " + status);

				if (status == HttpURLConnection.HTTP_OK) {
					responseok = true;
					BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

					String inputLine;

					while ((inputLine = input.readLine()) != null) {
						content.append(inputLine);
					}

					input.close();
				}

				connection.disconnect();
			} while (responseok == false);

		} catch (IOException e) {

		} catch (Exception i) {
			i.printStackTrace();
		}
		return content;
	}

}
