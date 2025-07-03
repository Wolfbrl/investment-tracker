package gui;

import domain.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainDashboard extends BorderPane {

	private InvestmentHandler investmenthandler;

	private Stage primaryStage;

	private User user;

	public MainDashboard(InvestmentHandler investmenthandler, Stage primaryStage, User selectedUser) {
		this.investmenthandler = investmenthandler;
		this.primaryStage = primaryStage;
		this.user = selectedUser;
		build();
	}

	private void build() {

		// top: menubar

		MenuBar menubar = new MenuBar();
		Menu menu = new Menu("Menu");

		MenuItem dashboard = new MenuItem("Dashboard");
		MenuItem news = new MenuItem("News");
		MenuItem settings = new MenuItem("Settings");
		MenuItem logout = new MenuItem("Sign out");

		menu.getItems().add(dashboard);
		menu.getItems().add(news);
		menu.getItems().add(settings);
		menu.getItems().add(logout);
		menubar.getMenus().add(menu);
		this.setTop(menubar);

		// center/top: graph van portfolio

		// center/bottom/left: lijst van alle investments

		// center/bottom/right: watchlist

	}

}
