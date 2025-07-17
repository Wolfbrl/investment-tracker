package domain;

import java.util.*;
import java.util.stream.Collectors;

import apis.MarketAuxApiFetcher;

public class NewsHandler {

	public static List<Map<String, String>> requestNews(List<String> symbols) {

//		private String title;
//		private String description;
//		private String keywords;
//		private String snippet;
//		private String url;
//		private String image_url;
//		private String published_at;
//		private String source;
//		private List<Entity> entities;

		List<Map<String, String>> responsemaplijst = new ArrayList<>();

		for (int i = 0; i < symbols.size(); i++) {
			NewsResponse response = MarketAuxApiFetcher.request(symbols.get(i));

			for (NewsArticle article : response.getData()) {
				Map<String, String> responsemap = new HashMap<>();
				responsemap.put("Title", article.getTitle());
				responsemap.put("Description", article.getDescription());
				responsemap.put("Source", article.getSource());
				responsemap.put("Date", article.getPublished_at());
				responsemap.put("URL", article.getUrl());
				responsemap.put("Image_URL", article.getImage_url());
				responsemap.put("Symbols",
						article.getEntities().stream().map(x -> x.getSymbol()).collect(Collectors.joining("-")));
				responsemaplijst.add(responsemap);
			}
		}

		return responsemaplijst;

	}

}
