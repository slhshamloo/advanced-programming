package view;

import controller.AbstractController;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class WelcomeView extends AbstractView {

    @FXML
    private BorderPane root;
    @FXML
    private ImageView headerLogo;

    public void setListeners() {
        headerLogo.fitWidthProperty().bind(headerLogo.getScene().getWindow().widthProperty().subtract(100));
    }

    public void enterSignUpMenu() throws Exception {
        root.getScene().getWindow().setOnCloseRequest(event -> {
            try {
                AbstractController.saveData();
            } catch (IOException exception) {
                closeApplicationRaw();
            }
        });
        enterNewMenu("/fxml/signup.fxml", root);
    }

    public void enterLoginMenu() throws Exception {
        root.getScene().getWindow().setOnCloseRequest(event -> {
            try {
                AbstractController.saveData();
            } catch (IOException exception) {
                closeApplicationRaw();
            }
        });
        enterNewMenu("/fxml/login.fxml", root);
    }

    public void closeApplicationRaw() {
        System.exit(0);
    }
}
