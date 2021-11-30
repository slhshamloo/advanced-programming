package view;

import controller.AbstractController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractView {

    public static void enterNewMenu(String urlString, Node oldRoot) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(AbstractView.class.getResource(urlString)));
        oldRoot.getScene().setRoot(newRoot);
    }

    public static void enterWelcomeMenu(Parent oldRoot) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                AbstractView.class.getResource("/fxml/welcome.fxml")));
        Parent newRoot = loader.load();
        WelcomeView welcomeView = loader.getController();

        oldRoot.getScene().setRoot(newRoot);
        welcomeView.setListeners();
    }

    public void closeApplication() throws IOException {
        AbstractController.closeApplication();
    }

    // source: https://gist.github.com/jewelsea/5415891
    public static void resample(ImageView imageView, int scaleFactor) {
        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        WritableImage resampledImage = new WritableImage(width * scaleFactor, height * scaleFactor);
        PixelReader reader = imageView.getImage().getPixelReader();
        PixelWriter writer = resampledImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int argb = reader.getArgb(x, y);
                for (int dy = 0; dy < scaleFactor; dy++) {
                    for (int dx = 0; dx < scaleFactor; dx++) {
                        writer.setArgb(x * scaleFactor + dx, y * scaleFactor + dy, argb);
                    }
                }
            }
        }

        imageView.setImage(resampledImage);
    }
}
