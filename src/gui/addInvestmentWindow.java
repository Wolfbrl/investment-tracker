package gui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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

		Label symbol = new Label("Enter stock symbol");
		TextField stockSymbol = new TextField("symbol");

		LocalDate date;

		Label amount = new Label("Enter amount");
		TextField initialValue = new TextField("initial value");
		TextField currentValue = new TextField("current value");

		Label notelabel = new Label("Add a note");
		TextField note = new TextField("note");

		HBox buttonbox = new HBox();

		Button addButton = new Button("Add Investment");
		Button removeButton = new Button("Clear All");

		buttonbox.getChildren().addAll(addButton, removeButton);

		this.getChildren().addAll(symbol, stockSymbol, amount, initialValue, currentValue, notelabel, note, buttonbox);

		addButton.setOnAction(e -> {
			BigDecimal initialamount = BigDecimal.valueOf(Double.parseDouble(initialValue.getText()));

			BigDecimal currentamount = BigDecimal.valueOf(Double.parseDouble(currentValue.getText()));
			String id = UUID.randomUUID().toString();
			InvestmentType investmenttype = InvestmentType.STOCK;
			Currencies currency = Currencies.USD;
			String invnote = note.getText();
			String stocksymbol = stockSymbol.getText();

			investmenthandler.addInvestment(id, stocksymbol, LocalDate.now(), initialamount, currentamount, currency,
					investmenttype, user.getUsername(), invnote);

			dashboard.refresh();

			popupStage.close();
		});

	}

}
