package com.uaemex.td.dataencodingvisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de inicio de la aplicación
 * Carga la interfaz gráfica JavaFX
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Intentar cargar FXML, si no existe, crear UI programáticamente
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    App.class.getResource("/com/uaemex/td/dataencodingvisualizer/main-view.fxml")
            );
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("Data Encoding Visualizer - UAEMEX");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            // Si no hay FXML, crear UI programáticamente
            Scene scene = createProgrammaticUI();
            stage.setTitle("Data Encoding Visualizer - UAEMEX");
            stage.setScene(scene);
            stage.show();
        }
    }

    private Scene createProgrammaticUI() {
        // Crear UI de forma programática (backup)
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(10);
        root.setPadding(new javafx.geometry.Insets(15));

        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(
                "Data Encoding Visualizer"
        );
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        javafx.scene.control.ComboBox<String> categoryCombo = new javafx.scene.control.ComboBox<>();
        categoryCombo.setPromptText("Seleccionar Categoría");
        categoryCombo.setPrefWidth(300);

        javafx.scene.control.ComboBox<String> techniqueCombo = new javafx.scene.control.ComboBox<>();
        techniqueCombo.setPromptText("Seleccionar Técnica");
        techniqueCombo.setPrefWidth(300);

        javafx.scene.control.TextField inputField = new javafx.scene.control.TextField();
        inputField.setPromptText("Ingrese datos (ej: 10110010)");
        inputField.setPrefWidth(300);

        javafx.scene.control.Button generateBtn = new javafx.scene.control.Button("Generar Señal");

        javafx.scene.chart.NumberAxis xAxis = new javafx.scene.chart.NumberAxis();
        xAxis.setLabel("Tiempo");
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        yAxis.setLabel("Amplitud");

        javafx.scene.chart.LineChart<Number, Number> chart =
                new javafx.scene.chart.LineChart<>(xAxis, yAxis);
        chart.setTitle("Señal Generada");
        chart.setPrefHeight(400);

        javafx.scene.control.TextArea descArea = new javafx.scene.control.TextArea();
        descArea.setEditable(false);
        descArea.setPrefHeight(80);
        descArea.setPromptText("Descripción de la técnica aparecerá aquí");

        root.getChildren().addAll(
                titleLabel,
                new javafx.scene.control.Label("Categoría:"),
                categoryCombo,
                new javafx.scene.control.Label("Técnica:"),
                techniqueCombo,
                new javafx.scene.control.Label("Entrada:"),
                inputField,
                generateBtn,
                chart,
                new javafx.scene.control.Label("Descripción:"),
                descArea
        );

        return new Scene(root, 1000, 700);
    }

    public static void main(String[] args) {
        launch();
    }
}