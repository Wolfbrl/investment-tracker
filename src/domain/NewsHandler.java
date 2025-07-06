package domain;

import java.util.*;

import apis.MarketAuxApiFetcher;

public class NewsHandler {

	public static List<String> requestNews(List<String> symbols) {

//		private String title;
//		private String description;
//		private String keywords;
//		private String snippet;
//		private String url;
//		private String image_url;
//		private String published_at;
//		private String source;
//		private List<Entity> entities;

		List<String> responselijst = new ArrayList<>();

		NewsResponse response = MarketAuxApiFetcher.request(symbols);

		if (response == null || response.getData() == null) {
			responselijst.add("No news available");
			return responselijst;
		}

		for (NewsArticle article : response.getData()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Title: ").append(article.getTitle()).append("\n");
			sb.append("Description: ").append(article.getDescription()).append("\n");
			sb.append("Source: ").append(article.getSource()).append("\n");
			sb.append("Date: ").append(article.getPublished_at()).append("\n");
			sb.append("URL: ").append(article.getUrl()).append("\n");
			sb.append("Image_URL: ").append(article.getImage_url()).append("\n");
			sb.append("----------------------------------");
			responselijst.add(sb.toString());
		}

	}

}
