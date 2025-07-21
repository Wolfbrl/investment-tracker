package apis;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import domain.StockResponse;
import io.github.cdimascio.dotenv.Dotenv;

public class MarketDataApiFetcher {

	static Dotenv dotenv = Dotenv.load();

	private static final String apiToken = dotenv.get("MARKETDATA_API_TOKEN_1");

	private static final Gson gson = new Gson();

	public static StockResponse requestPrice(String symbols) {

		StringBuilder content = new StringBuilder();

		String urlString = "https://api.marketdata.app/v1/stocks/prices/?format=json&symbols=" + symbols
				+ "&dateformat=timestamp&human=true&token=" + apiToken;

		try {
			URL url = new URI(urlString).toURL();

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");

			int status = connection.getResponseCode();

			System.out.println("MarketData Response code: " + status);

			if (status == HttpURLConnection.HTTP_OK) {
				BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				String inputLine;

				while ((inputLine = input.readLine()) != null) {
					content.append(inputLine);
				}

//				System.out.println(content.toString());

				input.close();

				connection.disconnect();
			} else {
				System.out.println("Response not ok");
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return gson.fromJson(content.toString(), StockResponse.class);

	}

}
