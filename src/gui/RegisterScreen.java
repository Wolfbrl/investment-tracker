package gui;

import domain.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class RegisterScreen extends SplitPane {

	private Label alertText;

	private InvestmentHandler investmenthandler;

	public RegisterScreen(InvestmentHandler investmenthandler) {
		this.investmenthandler = investmenthandler;
		build();
	}

	private void build() {

		Font poppinstitle = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Black.ttf"), 24);
		Font poppinslighttext = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Medium.ttf"), 10);
		Font poppinslighttextbutton = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Medium.ttf"), 12);

		this.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

		StackPane leftPane = new StackPane();
		leftPane.setPrefSize(550, 600);
		leftPane.setMinSize(550, 600);
		leftPane.setMaxSize(550, 600);

		Image leftimage = new Image(getClass().getResourceAsStream("/images/welcomepicture.png"));
		ImageView imageView = new ImageView(leftimage);
		imageView.setPreserveRatio(false);
		imageView.fitWidthProperty().bind(leftPane.widthProperty());
		imageView.fitHeightProperty().bind(leftPane.heightProperty());

		leftPane.getChildren().add(imageView);

		// Rechterzijde
		VBox formPane = new VBox(13);
		formPane.setStyle("-fx-background-color: white;");
		formPane.setPadding(new Insets(40));
		formPane.setAlignment(Pos.CENTER_LEFT);
		formPane.setPrefWidth(500);

		Label title = new Label("Welcome!");

		title.setFont(poppinstitle);

		Label usernamelabel = new Label("Username");
		TextField emailField = new TextField();

		usernamelabel.setFont(poppinslighttext);

		Label passwordLabel = new Label("Password");
		PasswordField passwordField = new PasswordField();

		Label passwordLabelConfirm = new Label("Confirm password");
		PasswordField passwordFieldConfirm = new PasswordField();

		passwordLabel.setFont(poppinslighttext);
		passwordLabelConfirm.setFont(poppinslighttext);

		Button registerButton = new Button("Register");

		registerButton.setFont(poppinslighttextbutton);

		registerButton.setOnAction(e -> {

			try {

				if (emailField.getText().isBlank() || passwordField.getText().isBlank()
						|| passwordFieldConfirm.getText().isBlank()) {
					throw new IllegalArgumentException("Please fill in all the fields");
				}

				alertText.setText("");
				User user = investmenthandler.giveAllUsers().stream()
						.filter(x -> x.getUsername().equals(emailField.getText())).findFirst().orElse(null);

				if (user != null) {
					throw new IllegalArgumentException("Username already exists");
				}

				if (!passwordField.getText().equals(passwordFieldConfirm.getText())) {
					throw new IllegalArgumentException("Confirmed password does not match your password");
				}

				if (passwordField.getText().isBlank() || passwordField.getText().isEmpty()) {
					throw new IllegalArgumentException("Your password can't be blank");
				}

				if (passwordField.getText().length() <= 7) {
					throw new IllegalArgumentException("Your password length must be 8 characters or more");
				}

				investmenthandler.createUser(emailField.getText(), passwordField.getText());

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("User created");
				alert.setContentText(String.format("User %s has successfully been created", emailField.getText()));
				alert.show();
				emailField.setText("");
				passwordField.setText("");
				passwordFieldConfirm.setText("");

			} catch (IllegalArgumentException i) {
				alertText.setText(i.getMessage());
			}

		});

		Hyperlink backToLoginPage = new Hyperlink("Back to loginpage");

		backToLoginPage.setOnAction(e -> {

			this.getScene().setRoot(new RegisterLoginScreen(this.investmenthandler));

		});

		backToLoginPage.setFont(poppinslighttextbutton);

		alertText = new Label("");
		alertText.setStyle("-fx-text-fill: red;");

		formPane.getChildren().addAll(title, usernamelabel, emailField, passwordLabel, passwordField,
				passwordLabelConfirm, passwordFieldConfirm, registerButton, alertText, backToLoginPage);

		this.getItems().addAll(leftPane, formPane);

		this.setDividerPositions(0.55);
		this.setStyle("-fx-background-color: white; -fx-divider-width: 0;");
		this.setPadding(Insets.EMPTY); // !

	}

}
