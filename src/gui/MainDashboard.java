package gui;

import java.awt.Desktop;
import java.awt.image.RenderedImage;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import domain.*;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainDashboard extends BorderPane {

	private final InvestmentHandler investmenthandler;
	private final Stage primaryStage;
	private final User user;
	private final ObservableList<Investment> investmentObservableList = FXCollections.observableArrayList();

	private TableView<Investment> table;
	private LineChart<String, Number> chart;
	Label title;

	private PieChart pieChart;

	private final Font titleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Black.ttf"), 24);
	private final Font buttonFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Medium.ttf"), 12);
	private final Font labelFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Black.ttf"), 12);
	private final Font smallLabelFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Light.ttf"), 12);

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private VBox newsandquickactionsbox;
	private VBox newsbox;

	public MainDashboard(InvestmentHandler investmenthandler, Stage primaryStage, User user) {
		this.primaryStage = primaryStage;
		primaryStage.setMaximized(true);
		this.investmenthandler = investmenthandler;
		this.user = user;
		build();
	}

	private void build() {

		this.setStyle("-fx-background-color: white;");

		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Menu");
		MenuItem dashboard = new MenuItem("Dashboard");
		MenuItem settings = new MenuItem("Settings");
		MenuItem signout = new MenuItem("Sign out");
		signout.setOnAction(e -> {
			this.getScene().setRoot(new RegisterLoginScreen(this.investmenthandler, this.primaryStage));
		});
		dashboard.setDisable(true);
		menu.getItems().addAll(dashboard, settings, signout);

		menuBar.getMenus().add(menu);

		investmenthandler.updateCurrentValues();

		BigDecimal totalValue = investmenthandler.giveAllInvestments().stream()
				.filter(inv -> inv.getUser().equals(user.getUsername())).map(Investment::getCurrentValue)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		title = new Label(String.format(" Total portfolio value: €%.2f", totalValue));
		title.setFont(labelFont);

		title.setPadding(new Insets(10, 0, 0, 290));

		VBox topBox = new VBox(menuBar, title);
		this.setTop(topBox);

		// Grafiek en tabel
		chart = buildPortfolioChart();
		table = buildInvestmentTable();

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

		Button removeInvestmentButton = new Button("Remove selected investment");
		removeInvestmentButton.setFont(buttonFont);

		removeInvestmentButton.setDisable(true);

		table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			removeInvestmentButton.setDisable(newSelection == null);
		});

		removeInvestmentButton.setOnAction(e -> {
			Investment selectedInvestment = table.getSelectionModel().getSelectedItem();
			if (selectedInvestment != null && investmenthandler.doesInvestmentExist(selectedInvestment.getId())) {
				investmenthandler.removeInvestment(selectedInvestment.getId());

				refresh();
			}
		});

		VBox.setMargin(removeInvestmentButton, new Insets(10, 0, 0, 0)); // 20 px ruimte boven de button

		tableBox.getChildren().add(removeInvestmentButton);

		grid.add(tableBox, 1, 0);

		grid.setTranslateY(-40);

		this.setCenter(grid);

//		Button refreshButton 

		// quick actions

		newsandquickactionsbox = displayNewsArticles();

		grid.add(newsandquickactionsbox, 0, 1);

		pieChart = buildPortfolioPieChart();
		pieChart.setPrefSize(400, 300);
		grid.add(pieChart, 1, 1);

		Label quickactions = new Label("Quick Actions");

		quickactions.setStyle("-fx-padding: 0 0 0 4;");
		quickactions.setFont(labelFont);

		newsandquickactionsbox.getChildren().add(quickactions);

		HBox quickactionsbuttonsbox = new HBox();
		quickactionsbuttonsbox.setSpacing(10);

		Button addInvestment = new Button("Add a new investment");
		addInvestment.setFont(buttonFont);
		Button screenshotPortfolio = new Button("Save your portfolio as a screenshot");
		screenshotPortfolio.setFont(buttonFont);

		addInvestment.setOnAction(e -> {
			Stage popupStage = new Stage();

			addInvestmentWindow addWindow = new addInvestmentWindow(investmenthandler, user, this, primaryStage,
					popupStage);
			Scene scene = new Scene(addWindow, 500, 400);
			popupStage.setTitle("Add New Investment");
			popupStage.setScene(scene);
			popupStage.initOwner(primaryStage);
			popupStage.setResizable(false);

			popupStage.show();

		});

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
		exportPortfolio.setFont(buttonFont);

		quickactionsbuttonsbox.getChildren().addAll();

		newsandquickactionsbox.getChildren().addAll(addInvestment, screenshotPortfolio, exportPortfolio);

		Runnable run = () -> Platform.runLater(() -> {
			investmenthandler.updateCurrentValues();
			refresh();
		});

		scheduler.scheduleAtFixedRate(run, 0, 60, TimeUnit.SECONDS);

		primaryStage.setOnCloseRequest(e -> {
			shutdownScheduler();
		});

	}

	public void shutdownScheduler() {
		scheduler.shutdownNow();
		System.out.println("Scheduler closed");
	}

	private VBox displayNewsArticles() {

		List<Investment> userInvestments = investmenthandler.giveAllInvestments().stream()
				.filter(inv -> inv.getUser().equals(user.getUsername())).toList();

		List<String> symbols = userInvestments.stream()
				.sorted(Comparator.comparing(Investment::getCurrentValue).reversed()).map(Investment::getName)
				.distinct().collect(Collectors.toList());

		List<Map<String, String>> lijstje = NewsHandler.requestNews(symbols, 3);

		newsbox = new VBox(10);
		newsbox.setPadding(new Insets(10));

		Label newsboxlabel = new Label("Relevant News Articles");

		newsboxlabel.setFont(labelFont);
		newsboxlabel.setStyle("-fx-padding: 0 0 0 4;");

		newsbox.getChildren().add(newsboxlabel);

		if (lijstje.size() >= 1) {
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

				newsbox.getChildren().add(newsurl);

			}
		} else {
			Label noArticles = new Label("Add some investments to display articles");
			noArticles.setFont(smallLabelFont);
			noArticles.setStyle("-fx-padding: 0 0 0 4;");
			noArticles.setPadding(new Insets(10));

			newsbox.getChildren().add(noArticles);

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

		TableColumn<Investment, String> initialvalueCol = new TableColumn<>("Initial Amount");
		initialvalueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				"€" + cellData.getValue().getInitialValue().toString()));

		TableColumn<Investment, String> currentvalueCol = new TableColumn<>("Current Amount");
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
		lineChart.setCreateSymbols(true);
		lineChart.setAnimated(false);
		lineChart.setHorizontalGridLinesVisible(false);
		lineChart.setVerticalGridLinesVisible(false);
		lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: white;");

		XYChart.Series<String, Number> series = new XYChart.Series<>();

		// Map voor totale waarde per dag
		Map<LocalDate, BigDecimal> dailyTotals = new TreeMap<>();

		for (Investment inv : investmenthandler.giveAllInvestments()) {
			if (!inv.getUser().equals(user.getUsername()))
				continue;

			for (ValuePoint vp : inv.getSimulatedHistory()) {
				LocalDate date = vp.getDate();
				BigDecimal value = vp.getValue();

				dailyTotals.put(date, dailyTotals.getOrDefault(date, BigDecimal.ZERO).add(value));
			}
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		for (Map.Entry<LocalDate, BigDecimal> entry : dailyTotals.entrySet()) {
			String date = entry.getKey().format(formatter);
			Number value = entry.getValue();
			series.getData().add(new XYChart.Data<>(date, value));
		}

		lineChart.getData().add(series);
		return lineChart;
	}

	public void refresh() {
		// Refresh de tabel
		investmentObservableList.setAll(investmenthandler.giveAllInvestments().stream()
				.filter(inv -> inv.getUser().equals(user.getUsername())).toList());

		// Refresh de chart
		updateChart();

		// refresh label

		BigDecimal totalValue = investmenthandler.giveAllInvestments().stream()
				.filter(inv -> inv.getUser().equals(user.getUsername())).map(Investment::getCurrentValue)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		title.setText(String.format(" Total portfolio value: €%.2f", totalValue));

		updatePieChart();

	}

	private void updatePieChart() {
		pieChart.setData(buildPortfolioPieChart().getData());
	}

	private void updateChart() {
		chart.getData().clear();
		chart.getData().add(buildPortfolioChart().getData().get(0));
	}

	private PieChart buildPortfolioPieChart() {
		PieChart pieChart = new PieChart();

		List<Investment> userInvestments = investmenthandler.giveAllInvestments().stream()
				.filter(inv -> inv.getUser().equals(user.getUsername())).toList();

		BigDecimal totalValue = userInvestments.stream().map(Investment::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		for (Investment inv : userInvestments) {
			BigDecimal value = inv.getCurrentValue();
			double percentage = totalValue.compareTo(BigDecimal.ZERO) > 0
					? value.divide(totalValue, 4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100
					: 0;

			String label = String.format("%s (%.1f%%)", inv.getName(), percentage);
			PieChart.Data slice = new PieChart.Data(label, value.doubleValue());
			pieChart.getData().add(slice);
		}

		pieChart.setLegendVisible(false);
		pieChart.setLabelsVisible(true);
		pieChart.setClockwise(true);
		pieChart.setStartAngle(90);

		return pieChart;
	}

}