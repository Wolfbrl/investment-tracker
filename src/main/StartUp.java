package main;

import database.DatabaseInitializer;
import domain.InvestmentHandler;
import gui.MainDashboard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartUp extends Application {

	public static void main(String[] args) {

		launch(args);

	}

	@Override
	public void start(Stage primaryStage) {
		DatabaseInitializer.initialize();

		InvestmentHandler investmenthandler = new InvestmentHandler();
//		RegisterLoginScreen startscreen = new RegisterLoginScreen(investmenthandler);
//
//		Scene scene = new Scene(startscreen, 1000, 600);

		if (!investmenthandler.doesUserExist("Wolfbrl")) {
			investmenthandler.createUser("Wolfbrl", "gizmofret2604");
		}
		MainDashboard dashboard = new MainDashboard(investmenthandler, primaryStage,
				investmenthandler.giveAllUsers().get(0));
		Scene scene = new Scene(dashboard);
		primaryStage.setMaximized(true);

		primaryStage.setTitle("Investment Tracker");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
