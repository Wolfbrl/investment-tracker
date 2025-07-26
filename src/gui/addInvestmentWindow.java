package gui;

import java.math.*;
import java.time.LocalDate;
import java.util.*;

import apis.MarketDataApiFetcher;
import domain.*;
import enums.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class addInvestmentWindow extends VBox {

	private InvestmentHandler investmenthandler;
	private User user;
	private MainDashboard dashboard;
	private Stage primaryStage;
	private Stage popupStage;
	private TextField stockSymbol;
	private TextField initialValue;
	private TextField note;

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
		stockSymbol = new TextField("");
		stockSymbol.setPromptText("AMZN");

		LocalDate date;

		Label amount = new Label("Enter amount");
		initialValue = new TextField("");
		initialValue.setPromptText("150");

		Label notelabel = new Label("Add a note");
		note = new TextField("");
		note.setPromptText("Long term investment.");

		HBox buttonbox = new HBox();

		Button addButton = new Button("Add Investment");
		Button removeButton = new Button("Clear All");

		removeButton.setOnAction(e -> {
			clearAll();
		});

		buttonbox.getChildren().addAll(addButton, removeButton);

		this.getChildren().addAll(symbol, stockSymbol, amount, initialValue, notelabel, note, buttonbox);

		addButton.setOnAction(e -> {
			try {
				BigDecimal initialamount = BigDecimal.valueOf(Double.parseDouble(initialValue.getText()));
				String id = UUID.randomUUID().toString();
				InvestmentType investmenttype = InvestmentType.STOCK;
				Currencies currency = Currencies.USD;
				String invnote = note.getText();
				String stocksymbol = stockSymbol.getText().toUpperCase();

				BigDecimal startPrice = MarketDataApiFetcher.requestPrice(stocksymbol).getMid().get(0);

				BigDecimal actualCurrentAmount = calculateCurrentAmount(stocksymbol, initialamount, startPrice);

				investmenthandler.addInvestment(id, stocksymbol, LocalDate.now(), startPrice, initialamount,
						actualCurrentAmount, currency, investmenttype, user.getUsername(), invnote);

				dashboard.refresh();

				popupStage.close();
			} catch (NullPointerException i) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("An error has occured");
				alert.setContentText(
						"Couldn't retrieve data for this stock symbol. Please check if the symbol is valid.");
				alert.show();

			} catch (IllegalArgumentException z) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("An error has occured");
				alert.setContentText(z.getMessage());
				alert.show();
			} catch (InputMismatchException a) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("An error has occured");
				alert.setContentText("Please fill in a valid positive number");
				alert.show();
			}
		});

	}

	private void clearAll() {
		stockSymbol.setText("");
		initialValue.setText("");
		note.setText("");
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
