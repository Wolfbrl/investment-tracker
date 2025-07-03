package gui;

import domain.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utils.PasswordUtils;

public class RegisterLoginScreen extends SplitPane {

	private Button signInButton;
	private Label alertText;

	private InvestmentHandler investmenthandler;

	private Stage primaryStage;

	public RegisterLoginScreen(InvestmentHandler investmenthandler, Stage primaryStage) {
		this.investmenthandler = investmenthandler;
		this.primaryStage = primaryStage;
		build();
	}

	private void build() {

		// FONTS
		Font poppinstitle = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Black.ttf"), 24);
		Font poppinslighttext = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Medium.ttf"), 10);
		Font poppinslighttextbutton = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Medium.ttf"), 12);

		this.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

		// Linkerzijde
		StackPane leftPane = new StackPane();
		leftPane.setPrefSize(550, 600);
		leftPane.setMinSize(550, 600);
		leftPane.setMaxSize(550, 600);

		Image leftimage = new Image(getClass().getResourceAsStream("/images/loginpicture.png"));
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

		Label title = new Label("Sign in or create an account");

		title.setFont(poppinstitle);

		Label usernamelabel = new Label("Username");
		TextField emailField = new TextField();

		usernamelabel.setFont(poppinslighttext);

		Label passwordLabel = new Label("Password");
		PasswordField passwordField = new PasswordField();

		passwordLabel.setFont(poppinslighttext);

		signInButton = new Button("Sign in");

		signInButton.setFont(poppinslighttextbutton);

		signInButton.setOnAction(e -> {
			try {
				alertText.setText("");
				User user = investmenthandler.giveAllUsers().stream()
						.filter(x -> x.getUsername().equals(emailField.getText())).findFirst().orElse(null);
				if (user == null) {
					throw new IllegalArgumentException("User not found");
				}

				boolean passwordklopt = PasswordUtils.verifyPassword(passwordField.getText(), user.getPassword(),
						user.getSalt());

				if (!passwordklopt) {
					throw new IllegalArgumentException("Incorrect password");
				}

				this.getScene().setRoot(new MainDashboard(this.investmenthandler, this.primaryStage, user));

			} catch (IllegalArgumentException i) {
				alertText.setText(i.getMessage());
			}

		});

		Label noAccountLabel = new Label("Don't have an account?");
		Hyperlink signUpLink = new Hyperlink("Sign up");

		signUpLink.setOnAction(e -> {

			this.getScene().setRoot(new RegisterScreen(this.investmenthandler, this.primaryStage));

		});

		noAccountLabel.setFont(poppinslighttextbutton);
		signUpLink.setFont(poppinslighttextbutton);

		HBox signUpRow = new HBox(5, noAccountLabel, signUpLink);
		signUpRow.setAlignment(Pos.CENTER_LEFT);

		alertText = new Label("");
		alertText.setStyle("-fx-text-fill: red;");

		formPane.getChildren().addAll(title, usernamelabel, emailField, passwordLabel, passwordField, signInButton,
				alertText, signUpRow);

		this.getItems().addAll(leftPane, formPane);

		this.setDividerPositions(0.55);
		this.setStyle("-fx-background-color: white; -fx-divider-width: 0;");
		this.setPadding(Insets.EMPTY); // !

	}
}
