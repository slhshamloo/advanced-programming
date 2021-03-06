package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import view.WelcomeView;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/welcome.fxml")));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        WelcomeView welcomeView = loader.getController();

        Media media = new Media(Objects.requireNonNull(
                getClass().getResource("/music/shama.mp3")).toExternalForm());
        MediaPlayer player = new MediaPlayer(media);
        player.play();

        stage.setScene(scene);
        stage.setTitle("Pac-Man Clone");
        welcomeView.setListeners();

        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
    }
}
