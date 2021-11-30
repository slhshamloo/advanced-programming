package view;

import controller.GameController;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class GameView extends AbstractView{

    GameController controller = new GameController();

    @FXML
    private BorderPane root;

    @FXML
    public void enterMainMenu() throws IOException {
        enterNewMenu("/fxml/mainmenu.fxml", root);
    }
}
