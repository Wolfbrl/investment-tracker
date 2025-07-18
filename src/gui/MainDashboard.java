package gui;

import java.awt.Desktop;
import java.awt.image.RenderedImage;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import domain.*;
import javafx.collections.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainDashboard extends BorderPane {

	private final InvestmentHandler investmenthandler;
	private final Stage primaryStage;
	private final User user;
	private final ObservableList<Investment> investmentObservableList = FXCollections.observableArrayList();

	public MainDashboard(InvestmentHandler investmenthandler, Stage primaryStage, User user) {
		this.primaryStage = primaryStage;
		primaryStage.setMaximized(true);
		this.investmenthandler = investmenthandler;
		this.user = user;
		build();
	}

	private void build() {

		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Menu");
		MenuItem dashboard = new MenuItem("Dashboard");
		MenuItem news = new MenuItem("News");
		MenuItem settings = new MenuItem("Settings");
		MenuItem signout = new MenuItem("Sign out");
		signout.setOnAction(e -> {
			this.getScene().setRoot(new RegisterLoginScreen(this.investmenthandler, this.primaryStage));
		});
		dashboard.setDisable(true);
		menu.getItems().addAll(dashboard, news, settings, signout);

		menuBar.getMenus().add(menu);

		BigDecimal totalValue = investmenthandler.giveAllInvestments().stream()
				.filter(inv -> inv.getUser().equals(user.getUsername())).map(Investment::getCurrentValue)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Label title = new Label(String.format(" Total portfolio value: €%.2f", totalValue));
		title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
		title.setPadding(new Insets(10, 0, 0, 290));

		VBox topBox = new VBox(menuBar, title);
		this.setTop(topBox);

		// Grafiek en tabel
		LineChart<String, Number> chart = buildPortfolioChart();
		TableView<Investment> table = buildInvestmentTable();

		chart.setPrefSize(400, 250);
		table.setPrefSize(400, 300);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(20));
		grid.setHgap(20);
		grid.setVgap(20);

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(50);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(50);

		grid.getColumnConstraints().addAll(col1, col2);
		grid.getRowConstraints().addAll(row1, row2);

		StackPane chartPane = new StackPane(chart);
		chartPane.setAlignment(Pos.CENTER);
		grid.add(chartPane, 0, 0);

		VBox tableBox = new VBox(table);
		tableBox.setAlignment(Pos.BOTTOM_LEFT);
		tableBox.setPadding(new Insets(0, 0, 35, 10)); // extra ruimte links
		tableBox.setPadding(new Insets(0, 0, 35, 0));

		Button sellInvestmentButton = new Button("Remove selected investment");
		sellInvestmentButton.setDisable(true);

		table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			sellInvestmentButton.setDisable(newSelection == null);
		});

		sellInvestmentButton.setOnAction(e -> {
			Investment selectedInvestment = table.getSelectionModel().getSelectedItem();
			if (selectedInvestment != null && investmenthandler.doesInvestmentExist(selectedInvestment.getId())) {
				investmenthandler.removeInvestment(selectedInvestment.getId());

				// Verwijder uit de ObservableList, zodat de tabel update
				investmentObservableList.remove(selectedInvestment);
			}
		});

		VBox.setMargin(sellInvestmentButton, new Insets(10, 0, 0, 0)); // 20 px ruimte boven de button

		tableBox.getChildren().add(sellInvestmentButton);

		grid.add(tableBox, 1, 0);

		grid.setTranslateY(-40);

		this.setCenter(grid);

		// quick actions

		VBox newsandquickactionsbox = displayNewsArticles();

		grid.add(newsandquickactionsbox, 0, 1);

		Label quickactions = new Label("Quick Actions");
		quickactions.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 0 0 0 4;");

		newsandquickactionsbox.getChildren().add(quickactions);

		HBox quickactionsbuttonsbox = new HBox();
		quickactionsbuttonsbox.setSpacing(10);

		Button addInvestment = new Button("Add a new investment");
		Button screenshotPortfolio = new Button("Save your portfolio as a screenshot");

		screenshotPortfolio.setOnAction(e -> {

			try {

				WritableImage snapshot = primaryStage.getScene().snapshot(null);

				int cropX = 0;
				int cropY = 25;
				int cropWidth = (int) snapshot.getWidth();

				int cropHeight = 375;

				PixelReader reader = snapshot.getPixelReader();

				WritableImage croppedSnapshot = new WritableImage(reader, cropX, cropY, cropWidth, cropHeight);

				String userHome = System.getProperty("user.home");

				File downloadFolder = new File(userHome, "Downloads");

				File file = new File(downloadFolder, "portfolio.png");

				RenderedImage renderedImage = SwingFXUtils.fromFXImage(croppedSnapshot, null);

				boolean success = ImageIO.write(renderedImage, "png", file);

				if (success) {
					System.out.println(String.format("Screenshot saved to: %s", file.getAbsolutePath()));
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}

		});

		Button exportPortfolio = new Button("Export your portfolio as a CSV file (Excel)");

		quickactionsbuttonsbox.getChildren().addAll(addInvestment, screenshotPortfolio, exportPortfolio);

		newsandquickactionsbox.getChildren().add(quickactionsbuttonsbox);

	}

	private VBox displayNewsArticles() {

		List<Investment> userInvestments = investmenthandler.giveAllInvestments().stream()
				.filter(inv -> inv.getUser().equals(user.getUsername())).toList();

		List<String> symbols = userInvestments.stream()
				.sorted(Comparator.comparing(Investment::getCurrentValue).reversed()).map(Investment::getName)
				.distinct().collect(Collectors.toList());

		List<Map<String, String>> lijstje = NewsHandler.requestNews(symbols, 3);

		VBox newsbox = new VBox(10);
		newsbox.setPadding(new Insets(10));

		Label newsboxlabel = new Label("Relevant News Articles Regarding Your Holdings");
		newsboxlabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 0 0 0 4;");

		newsbox.getChildren().add(newsboxlabel);

		for (Map<String, String> map : lijstje) {

			Hyperlink newsurl = new Hyperlink(String.format("%s | %s", map.get("Symbols"), map.get("Title")));

			newsurl.setOnAction(e -> {
				try {
					Desktop desktop = Desktop.getDesktop();
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
						desktop.browse(java.net.URI.create(map.get("URL")));
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			});

			newsurl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
			newsbox.getChildren().add(newsurl);

		}
		newsbox.setAlignment(Pos.TOP_LEFT);
		GridPane.setMargin(newsbox, new Insets(0, 0, 0, +20));

		return newsbox;
	}

	private TableView<Investment> buildInvestmentTable() {
		TableView<Investment> table = new TableView<>();

		investmentObservableList.setAll(investmenthandler.giveAllInvestments().stream()
				.filter(inv -> inv.getUser().equals(user.getUsername())).toList());

		table.setItems(investmentObservableList);

		TableColumn<Investment, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

		TableColumn<Investment, String> dateCol = new TableColumn<>("Start Date");
		dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				cellData.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));

		TableColumn<Investment, String> typeCol = new TableColumn<>("Type");
		typeCol.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType().toString()));

		TableColumn<Investment, String> initialvalueCol = new TableColumn<>("initialValue");
		initialvalueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				"€" + cellData.getValue().getInitialValue().toString()));

		TableColumn<Investment, String> currentvalueCol = new TableColumn<>("currentValue");
		currentvalueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				"€" + cellData.getValue().getCurrentValue().toPlainString()));

		TableColumn<Investment, String> pnlCol = new TableColumn<>("PNL");
		pnlCol.setCellValueFactory(cellData -> {
			BigDecimal pnl = cellData.getValue().getProfitOrLoss();
			String formatted = (pnl.signum() >= 0 ? "+" : "") + "€" + pnl.setScale(2, BigDecimal.ROUND_HALF_UP);
			return new javafx.beans.property.SimpleStringProperty(formatted);
		});

		table.getColumns().addAll(nameCol, dateCol, typeCol, initialvalueCol, currentvalueCol, pnlCol);

		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		return table;
	}

	private LineChart<String, Number> buildPortfolioChart() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

		lineChart.setLegendVisible(false);
		lineChart.setHorizontalGridLinesVisible(false);
		lineChart.setVerticalGridLinesVisible(false);
		lineChart.setCreateSymbols(false);
		lineChart.setTitle("");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		Map<LocalDate, BigDecimal> dailyTotals = new TreeMap<>();
		Set<LocalDate> relevanteDatums = new TreeSet<>();

		for (Investment inv : investmenthandler.giveAllInvestments()) {
			if (!inv.getUser().equals(user.getUsername()))
				continue;

			for (ValuePoint vp : inv.getSimulatedHistory()) {
				LocalDate date = vp.getDate();
				BigDecimal value = vp.getValue();
				dailyTotals.put(date, dailyTotals.getOrDefault(date, BigDecimal.ZERO).add(value));
			}
			relevanteDatums.add(inv.getStartDate());
		}
		relevanteDatums.add(LocalDate.now());

		xAxis.setAutoRanging(false);
		xAxis.setCategories(
				FXCollections.observableArrayList(relevanteDatums.stream().map(d -> d.format(formatter)).toList()));

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		for (Map.Entry<LocalDate, BigDecimal> entry : dailyTotals.entrySet()) {
			if (relevanteDatums.contains(entry.getKey())) {
				String formattedDate = entry.getKey().format(formatter);
				series.getData().add(new XYChart.Data<>(formattedDate, entry.getValue()));
			}
		}

		lineChart.getData().add(series);
		return lineChart;
	}

}
