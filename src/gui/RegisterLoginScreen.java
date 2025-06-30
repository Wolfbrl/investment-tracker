package gui;

import domain.InvestmentHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RegisterLoginScreen extends SplitPane {

	private InvestmentHandler investmenthandler;

	public RegisterLoginScreen(InvestmentHandler investmenthandler) {
		this.investmenthandler = investmenthandler;
		build();
	}

	private void build() {

		this.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

		// Linkerzijde: illustratie of paarse achtergrond
		StackPane leftPane = new StackPane();
		leftPane.setStyle("-fx-background-color: #A278F6;");
		leftPane.setPrefWidth(500);

		// Rechterzijde: formulier
		VBox formPane = new VBox(13);
		formPane.setPadding(new Insets(40));
		formPane.setAlignment(Pos.CENTER_LEFT);
		formPane.setPrefWidth(500);

		Label title = new Label("Sign in or create an account");
		title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		Label usernamelabel = new Label("Username");
		TextField emailField = new TextField();

		Label passwordLabel = new Label("Password");
		PasswordField passwordField = new PasswordField();

		Button signInButton = new Button("Sign in");
		signInButton.setStyle("-fx-background-color: #6A0DAD; -fx-text-fill: white;");

		Label noAccountLabel = new Label("Don't have an account?");
		Hyperlink signUpLink = new Hyperlink("Sign up");

		HBox signUpRow = new HBox(5, noAccountLabel, signUpLink);
		signUpRow.setAlignment(Pos.CENTER_LEFT);

		formPane.getChildren().addAll(title, usernamelabel, emailField, passwordLabel, passwordField, signInButton,
				signUpRow);

		this.getItems().addAll(leftPane, formPane);

		this.setDividerPositions(0.55);
		this.setStyle("-fx-divider-width: 0;");
	}
}
