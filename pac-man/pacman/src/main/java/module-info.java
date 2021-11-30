module pacman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires com.google.gson;

    opens view to javafx.fxml;
    opens main to javafx.fxml, javafx.media, javafx.graphics;
    opens model.user to com.google.gson;
    opens model.user.database to com.google.gson;

    exports view;
    exports main;
    exports model.user;
    exports model.user.database;
}