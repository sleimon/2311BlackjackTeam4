package GUI;



import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BlackjackMenu extends Application {
    @Override
    public void start(Stage stage) {
        // Title
        Text title = new Text("♠️ Blackjack ♣️");
        title.setFont(new Font("Arial", 36));

        // Buttons
        Button startGameButton = new Button("Start Game");
        Button rulesButton = new Button("Rules");
        Button exitButton = new Button("Exit");

        // Button Actions
        startGameButton.setOnAction(e -> startGame());
        rulesButton.setOnAction(e -> showRules());
        exitButton.setOnAction(e -> stage.close());

        // Layout
        VBox layout = new VBox(20, title, startGameButton, rulesButton, exitButton);
        layout.setStyle("-fx-alignment: center; -fx-padding: 50; -fx-background-color: darkgreen;");

        // Scene Setup
        Scene scene = new Scene(layout, 400, 300);
        stage.setTitle("Blackjack Menu");
        stage.setScene(scene);
        stage.show();
    }

    private void startGame() {
        System.out.println("Starting game...");
        // TODO: Load Blackjack game scene
    }

    private void showRules() {
        System.out.println("Showing rules...");
        // TODO: Display Blackjack rules
    }

    public static void main(String[] args) {
//        System.setProperty("javafx.preloader", "com.sun.javafx.application.NoPreloader");
        launch();
    }
}

