package com.uaemex.td.dataencodingvisualizer.controller;

import com.uaemex.td.dataencodingvisualizer.logic.*;
import com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital.*;
import com.uaemex.td.dataencodingvisualizer.logic.digital_to_analog.*;
import com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog.*;
import com.uaemex.td.dataencodingvisualizer.logic.analog_to_digital.*;
import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.util.*;

public class MainController {
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> techniqueComboBox;
    @FXML private TextField inputTextField;
    @FXML private LineChart<Number, Number> signalChart;
    @FXML private TextArea descriptionTextArea;
    @FXML private Button generateButton;

    private Map<String, List<IGenerator>> generatorsByCategory;
    private IGenerator currentGenerator;

    @FXML
    public void initialize() {
        initializeGenerators();
        setupCategoryComboBox();
        setupEventHandlers();
    }

    private void initializeGenerators() {
        generatorsByCategory = new HashMap<>();

        // Digital a Digital
        List<IGenerator> digitalToDigital = Arrays.asList(
                new NRZ_L_Generator(),
                new NRZ_I_Generator(),
                new ManchesterGenerator(),
                new DifferentialManchesterGenerator(),
                new AMIGenerator(),
                new PseudoternaryGenerator(),
                new B8ZSGenerator(),
                new HDB3Generator()
        );
        generatorsByCategory.put("Digital → Digital (Codificación de Línea)", digitalToDigital);

        // Digital a Analógico
        List<IGenerator> digitalToAnalog = Arrays.asList(
                new ASKGenerator(),
                new FSKGenerator(),
                new PSKGenerator(),
                new QAMGenerator()
        );
        generatorsByCategory.put("Digital → Analógico (Modulación)", digitalToAnalog);

        // Analógico a Analógico
        List<IGenerator> analogToAnalog = Arrays.asList(
                new AMGenerator(),
                new FMGenerator(),
                new PMGenerator()
        );
        generatorsByCategory.put("Analógico → Analógico", analogToAnalog);

        // Analógico a Digital
        List<IGenerator> analogToDigital = Arrays.asList(
                new PCMGenerator(),
                new DMGenerator()
        );
        generatorsByCategory.put("Analógico → Digital", analogToDigital);
    }

    private void setupCategoryComboBox() {
        categoryComboBox.setItems(FXCollections.observableArrayList(
                generatorsByCategory.keySet()
        ));
        categoryComboBox.getSelectionModel().selectFirst();
        updateTechniqueComboBox();
    }

    private void setupEventHandlers() {
        categoryComboBox.setOnAction(e -> updateTechniqueComboBox());
        techniqueComboBox.setOnAction(e -> updateDescription());
        generateButton.setOnAction(e -> handleGenerate());

        // Valor por defecto
        inputTextField.setText("10110010");
    }

    private void updateTechniqueComboBox() {
        String category = categoryComboBox.getValue();
        if (category != null) {
            List<IGenerator> generators = generatorsByCategory.get(category);
            List<String> names = new ArrayList<>();
            for (IGenerator gen : generators) {
                names.add(gen.getName());
            }
            techniqueComboBox.setItems(FXCollections.observableArrayList(names));
            techniqueComboBox.getSelectionModel().selectFirst();
            updateDescription();
        }
    }

    private void updateDescription() {
        String techniqueName = techniqueComboBox.getValue();
        if (techniqueName != null) {
            for (List<IGenerator> generators : generatorsByCategory.values()) {
                for (IGenerator gen : generators) {
                    if (gen.getName().equals(techniqueName)) {
                        currentGenerator = gen;
                        descriptionTextArea.setText(gen.getDescription());

                        // Sugerir input según la categoría
                        String category = categoryComboBox.getValue();
                        if (category.contains("Analógico → Analógico") ||
                                category.contains("Analógico → Digital")) {
                            inputTextField.setPromptText("Parámetros automáticos");
                        } else {
                            inputTextField.setPromptText("Ej: 10110010");
                        }
                        return;
                    }
                }
            }
        }
    }

    @FXML
    private void handleGenerate() {
        if (currentGenerator == null) {
            showAlert("Error", "Seleccione una técnica primero");
            return;
        }

        String input = inputTextField.getText().trim();
        String category = categoryComboBox.getValue();

        // Validar input según categoría
        if (category.contains("Digital") && category.contains("Digital")) {
            if (!input.matches("[01]+")) {
                showAlert("Error", "Para técnicas digitales, ingrese solo 0s y 1s (ej: 10110010)");
                return;
            }
        } else if (category.contains("Digital") && category.contains("Analógico")) {
            if (!input.matches("[01]+")) {
                showAlert("Error", "Para modulación digital, ingrese solo 0s y 1s (ej: 10110010)");
                return;
            }
        }

        try {
            Map<String, Object> params = new HashMap<>();
            List<SignalData> data = currentGenerator.generate(input, params);

            if (data.isEmpty()) {
                showAlert("Advertencia", "No se generaron datos. Verifique su entrada.");
                return;
            }

            plotSignal(data);
        } catch (Exception e) {
            showAlert("Error", "Error al generar la señal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void plotSignal(List<SignalData> data) {
        signalChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(currentGenerator.getName());

        for (SignalData point : data) {
            series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
        }

        signalChart.getData().add(series);
        signalChart.setCreateSymbols(false); // Sin puntos, solo línea
        signalChart.setLegendVisible(true);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}