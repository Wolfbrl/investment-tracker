package main;

import java.sql.Connection;

import database.Database;
import domain.InvestmentHandler;
import gui.RegisterLoginScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartUp extends Application {

	public static void main(String[] args) {

//		InvestmentHandler investmenthandler = new InvestmentHandler();
//
//		new TestApplication(investmenthandler).runTest();
		Connection conn = Database.connect();
		if (conn != null) {
			System.out.println("Connectie succesvol!");
		}

		launch(args);

	}

	@Override
	public void start(Stage primaryStage) {

		InvestmentHandler investmenthandler = new InvestmentHandler();
		RegisterLoginScreen startscreen = new RegisterLoginScreen(investmenthandler);

		Scene scene = new Scene(startscreen, 800, 600);

		primaryStage.setTitle("Investment Tracker");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
