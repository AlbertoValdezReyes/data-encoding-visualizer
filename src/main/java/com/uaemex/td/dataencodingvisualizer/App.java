package com.uaemex.td.dataencodingvisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase principal de la aplicación - Data Encoding Visualizer
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 1500, 1200);

            // Cargar estilos CSS
            java.net.URL cssUrl = getClass().getResource("/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("CSS cargado correctamente: " + cssUrl);
            } else {
                System.err.println("ADVERTENCIA: No se encontró el archivo styles.css");
            }

            stage.setTitle("Data Encoding Visualizer - UAEMEX");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar fxml");
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch();
    }
}