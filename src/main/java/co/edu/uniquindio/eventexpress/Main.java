package co.edu.uniquindio.eventexpress;

import co.edu.uniquindio.eventexpress.config.ConfiguracionPlataforma;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uniquindio/eventexpress/login.fxml"));
        Scene scene = new Scene(loader.load(), 900, 620);
        scene.getStylesheets().add(
                getClass().getResource("/co/edu/uniquindio/eventexpress/styles.css").toExternalForm());
        stage.setTitle(ConfiguracionPlataforma.getInstancia().getNombrePlataforma());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
