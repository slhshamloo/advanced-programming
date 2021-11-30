package view;

import controller.AbstractController;
import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.io.IOException;

public class MainMenuView extends AbstractView {

    private static final int SPRITE_SCALE_FACTOR = 4;

    @FXML
    private BorderPane root;
    @FXML
    private ImageView powerPelletImage;
    @FXML
    private ImageView pacmanSprite;
    @FXML
    private ImageView blinkySprite;
    @FXML
    private ImageView pinkySprite;
    @FXML
    private ImageView inkySprite;
    @FXML
    private ImageView clydeSprite;

    @FXML
    public void initialize() {
        resampleImages(); // scale pixel art properly
        playAnimations();
    }

    private void resampleImages() {
        resample(pacmanSprite, 2);
        ImageView[] imageViews = {powerPelletImage, blinkySprite, pinkySprite, inkySprite, clydeSprite};
        for (ImageView imageView : imageViews) {
            resample(imageView, SPRITE_SCALE_FACTOR);
        }
    }

    private void playAnimations() {
        Animation pacmanSpriteAnimation = new SpriteAnimation(pacmanSprite, Duration.millis(250), 3, 4,
                0, 0, 64, 64);
        pacmanSpriteAnimation.setCycleCount(Animation.INDEFINITE);
        pacmanSpriteAnimation.play();

        playGhostAnimation(blinkySprite, 3);
        playGhostAnimation(pinkySprite, 2);
        playGhostAnimation(inkySprite, 1);
        playGhostAnimation(clydeSprite, 0);
    }

    private void playGhostAnimation(ImageView sprite, int yNumber) {
        Animation ghostSpriteAnimation = new SpriteAnimation(sprite, Duration.millis(250), 2, 6 * SPRITE_SCALE_FACTOR,
                80 * SPRITE_SCALE_FACTOR, 20 * SPRITE_SCALE_FACTOR * yNumber,
                14 * SPRITE_SCALE_FACTOR, 14 * SPRITE_SCALE_FACTOR);
        ghostSpriteAnimation.setCycleCount(Animation.INDEFINITE);
        ghostSpriteAnimation.play();
    }

    @FXML
    public void enterWelcomeMenu() throws IOException {
        enterWelcomeMenu(root);
        AbstractController.logoutUser();
    }

    @FXML
    public void startNewGame() throws IOException {
        enterNewMenu("/fxml/game.fxml", root);
    }
}
