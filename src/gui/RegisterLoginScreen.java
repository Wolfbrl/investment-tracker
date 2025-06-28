package gui;

import domain.InvestmentHandler;
import javafx.scene.layout.GridPane;

public class RegisterLoginScreen extends GridPane {

	private InvestmentHandler investmenthandler;

	public RegisterLoginScreen(InvestmentHandler investmenthandler) {

		this.investmenthandler = new InvestmentHandler();

		build();

	}

	private void build() {

	}

}
