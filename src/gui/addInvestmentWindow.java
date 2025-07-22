package gui;

import java.math.*;
import java.time.LocalDate;
import java.util.UUID;

import apis.MarketDataApiFetcher;
import domain.*;
import enums.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class addInvestmentWindow extends VBox {

	private InvestmentHandler investmenthandler;
	private User user;
	private MainDashboard dashboard;
	private Stage primaryStage;
	private Stage popupStage;

	public addInvestmentWindow(InvestmentHandler investmenthandler, User user, MainDashboard dashboard,
			Stage primaryStage, Stage popupstage) {

		this.investmenthandler = investmenthandler;
		this.primaryStage = primaryStage;
		this.dashboard = dashboard;
		this.user = user;
		this.dashboard = dashboard;
		this.popupStage = popupstage;
		buildGUI();

	}

	private void buildGUI() {

//		StockResponse response = MarketDataApiFetcher.requestPrice("AAPL,TSLA");

		Label symbol = new Label("Enter stock symbol (example: AAPL)");
		TextField stockSymbol = new TextField("");
		stockSymbol.setPromptText("AMZN");

		LocalDate date;

		Label amount = new Label("Enter amount");
		TextField initialValue = new TextField("");
		initialValue.setPromptText("150");

		Label notelabel = new Label("Add a note");
		TextField note = new TextField("");
		note.setPromptText("Long term investment.");

		HBox buttonbox = new HBox();

		Button addButton = new Button("Add Investment");
		Button removeButton = new Button("Clear All");

		removeButton.setOnAction(e -> {
			stockSymbol.setText("");
			initialValue.setText("");
			note.setText("");
		});

		buttonbox.getChildren().addAll(addButton, removeButton);

		this.getChildren().addAll(symbol, stockSymbol, amount, initialValue, notelabel, note, buttonbox);

		addButton.setOnAction(e -> {
			BigDecimal initialamount = BigDecimal.valueOf(Double.parseDouble(initialValue.getText()));
			String id = UUID.randomUUID().toString();
			InvestmentType investmenttype = InvestmentType.STOCK;
			Currencies currency = Currencies.USD;
			String invnote = note.getText();
			String stocksymbol = stockSymbol.getText();

			BigDecimal startPrice = MarketDataApiFetcher.requestPrice(stocksymbol).getMid().get(0);

			BigDecimal actualCurrentAmount = calculateCurrentAmount(stocksymbol, initialamount, startPrice);

			investmenthandler.addInvestment(id, stocksymbol, LocalDate.now(), startPrice, initialamount,
					actualCurrentAmount, currency, investmenttype, user.getUsername(), invnote);

			dashboard.refresh();

			popupStage.close();
		});

	}

	public static BigDecimal calculateCurrentAmount(String symbol, BigDecimal initialAmount, BigDecimal startPrice) {

		BigDecimal newPrice = MarketDataApiFetcher.requestPrice(symbol).getMid().getFirst();

		BigDecimal percentageChange = newPrice.subtract(startPrice).divide(startPrice, 10, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100));

		BigDecimal changeAmount = initialAmount.multiply(percentageChange).divide(BigDecimal.valueOf(100), 10,
				RoundingMode.HALF_UP);

		return initialAmount.add(changeAmount).setScale(2, RoundingMode.HALF_UP);

	}

}
