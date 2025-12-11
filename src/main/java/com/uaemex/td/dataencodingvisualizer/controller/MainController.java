package com.uaemex.td.dataencodingvisualizer.controller;

import com.uaemex.td.dataencodingvisualizer.logic.*;
import com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital.*;
import com.uaemex.td.dataencodingvisualizer.logic.digital_to_analog.*;
import com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog.*;
import com.uaemex.td.dataencodingvisualizer.logic.analog_to_digital.*;
import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.util.*;

/**
 * Controlador principal de la aplicación Data Encoding Visualizer.
 *
 * Esta clase gestiona toda la lógica de la interfaz de usuario, incluyendo:
 * - Inicialización de generadores de señales
 * - Manejo de eventos de los controles JavaFX
 * - Generación y visualización de señales codificadas
 * - Validación de entrada del usuario
 * - Evaluación de funciones matemáticas para señales analógicas
 *
 * @author UAEMEX - Transmisión de Datos
 */
public class MainController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> techniqueComboBox;
    @FXML private TextField inputTextField;
    @FXML private LineChart<Number, Number> signalChart;
    @FXML private TextArea descriptionTextArea;
    @FXML private Button generateButton;

    /**
     * Mapa que almacena los generadores organizados por categoría.
     * Key: Nombre de la categoría
     * Value: Lista de generadores pertenecientes a esa categoría
     */
    private Map<String, List<IGenerator>> generatorsByCategory;

    /**
     * Generador actualmente seleccionado por el usuario.
     */
    private IGenerator currentGenerator;

    /**
     * Método de inicialización del controlador.
     * Se invoca automáticamente después de que JavaFX carga el archivo FXML.
     *
     * Este método debe ejecutarse sin errores para que la aplicación funcione correctamente.
     * Cualquier excepción aquí impedirá que los ComboBox se llenen con datos.
     */
    @FXML
    public void initialize() {
        System.out.println("=== INICIO DE INICIALIZACION ===");

        try {
            verifyFXMLComponents();
            initializeGenerators();
            setupCategoryComboBox();
            setupEventHandlers();
            setDefaultValues();

            System.out.println("=== INICIALIZACION COMPLETADA EXITOSAMENTE ===");

        } catch (Exception e) {
            System.err.println("ERROR CRITICO durante la inicialización:");
            e.printStackTrace();
            showCriticalError("Error de Inicialización",
                    "No se pudo inicializar la aplicación: " + e.getMessage());
        }
    }

    /**
     * Verifica que todos los componentes FXML necesarios hayan sido inyectados correctamente.
     *
     * @throws IllegalStateException si algún componente crítico es null
     */
    private void verifyFXMLComponents() {
        System.out.println("Verificando componentes FXML...");

        if (categoryComboBox == null) {
            throw new IllegalStateException("categoryComboBox no fue inyectado");
        }
        if (techniqueComboBox == null) {
            throw new IllegalStateException("techniqueComboBox no fue inyectado");
        }
        if (inputTextField == null) {
            throw new IllegalStateException("inputTextField no fue inyectado");
        }
        if (signalChart == null) {
            throw new IllegalStateException("signalChart no fue inyectado");
        }
        if (descriptionTextArea == null) {
            throw new IllegalStateException("descriptionTextArea no fue inyectado");
        }
        if (generateButton == null) {
            throw new IllegalStateException("generateButton no fue inyectado");
        }

        System.out.println("Todos los componentes FXML verificados correctamente.");
    }

    /**
     * Inicializa todos los generadores de señales organizados por categoría.
     */
    private void initializeGenerators() {
        System.out.println("Inicializando generadores de señales...");

        generatorsByCategory = new LinkedHashMap<>();

        // Categoría 1: Digital a Digital - 8 técnicas
        List<IGenerator> digitalToDigital = new ArrayList<>();
        digitalToDigital.add(new NRZ_L_Generator());
        digitalToDigital.add(new NRZ_I_Generator());
        digitalToDigital.add(new ManchesterGenerator());
        digitalToDigital.add(new DifferentialManchesterGenerator());
        digitalToDigital.add(new AMIGenerator());
        digitalToDigital.add(new PseudoternaryGenerator());
        digitalToDigital.add(new B8ZSGenerator());
        digitalToDigital.add(new HDB3Generator());
        generatorsByCategory.put("Digital → Digital (Codificación de Línea)", digitalToDigital);
        System.out.println("  - Cargadas 8 técnicas Digital → Digital");

        // Categoría 2: Digital a Analógico - 4 técnicas
        List<IGenerator> digitalToAnalog = new ArrayList<>();
        digitalToAnalog.add(new ASKGenerator());
        digitalToAnalog.add(new FSKGenerator());
        digitalToAnalog.add(new PSKGenerator());
        digitalToAnalog.add(new QAMGenerator());
        generatorsByCategory.put("Digital → Analógico (Modulación)", digitalToAnalog);
        System.out.println("  - Cargadas 4 técnicas Digital → Analógico");

        // Categoría 3: Analógico a Analógico - 3 técnicas
        List<IGenerator> analogToAnalog = new ArrayList<>();
        analogToAnalog.add(new AMGenerator());
        analogToAnalog.add(new FMGenerator());
        analogToAnalog.add(new PMGenerator());
        generatorsByCategory.put("Analógico → Analógico", analogToAnalog);
        System.out.println("  - Cargadas 3 técnicas Analógico → Analógico");

        // Categoría 4: Analógico a Digital - 2 técnicas
        List<IGenerator> analogToDigital = new ArrayList<>();
        analogToDigital.add(new PCMGenerator());
        analogToDigital.add(new DMGenerator());
        generatorsByCategory.put("Analógico → Digital", analogToDigital);
        System.out.println("  - Cargadas 2 técnicas Analógico → Digital");

        System.out.println("Total de categorías: " + generatorsByCategory.size());
    }

    /**
     * Configura el ComboBox de categorías con las opciones disponibles.
     */
    private void setupCategoryComboBox() {
        System.out.println("Configurando ComboBox de categorías...");

        List<String> categories = new ArrayList<>(generatorsByCategory.keySet());
        ObservableList<String> observableCategories = FXCollections.observableArrayList(categories);

        Platform.runLater(() -> {
            categoryComboBox.setItems(observableCategories);

            if (!categories.isEmpty()) {
                categoryComboBox.getSelectionModel().selectFirst();
                System.out.println("Categoría seleccionada por defecto: " + categories.get(0));
                updateTechniqueComboBox();
            }
        });

        System.out.println("ComboBox de categorías configurado con " + categories.size() + " opciones");
    }

    /**
     * Configura todos los manejadores de eventos para los controles de la interfaz.
     */
    private void setupEventHandlers() {
        System.out.println("Configurando manejadores de eventos...");

        categoryComboBox.setOnAction(event -> {
            System.out.println("Evento: Cambio de categoría");
            updateTechniqueComboBox();
        });

        techniqueComboBox.setOnAction(event -> {
            System.out.println("Evento: Cambio de técnica");
            updateDescription();
        });

        generateButton.setOnAction(event -> {
            System.out.println("Evento: Click en botón generar");
            handleGenerate();
        });

        System.out.println("Manejadores de eventos configurados correctamente");
    }

    /**
     * Establece los valores por defecto en los campos de entrada.
     */
    private void setDefaultValues() {
        System.out.println("Estableciendo valores por defecto...");
        inputTextField.setText("10110010");
        inputTextField.setPromptText("Ingrese bits (ej: 10110010)");
    }

    /**
     * Actualiza el ComboBox de técnicas basándose en la categoría seleccionada.
     */
    private void updateTechniqueComboBox() {
        String selectedCategory = categoryComboBox.getValue();

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            return;
        }

        System.out.println("Actualizando técnicas para categoría: " + selectedCategory);

        List<IGenerator> generators = generatorsByCategory.get(selectedCategory);

        if (generators == null || generators.isEmpty()) {
            techniqueComboBox.setItems(FXCollections.observableArrayList());
            return;
        }

        List<String> techniqueNames = new ArrayList<>();
        for (IGenerator generator : generators) {
            techniqueNames.add(generator.getName());
        }

        ObservableList<String> observableTechniques = FXCollections.observableArrayList(techniqueNames);

        Platform.runLater(() -> {
            techniqueComboBox.setItems(observableTechniques);

            if (!techniqueNames.isEmpty()) {
                techniqueComboBox.getSelectionModel().selectFirst();
                System.out.println("Técnica seleccionada por defecto: " + techniqueNames.get(0));
                updateDescription();
            }
        });

        System.out.println("Cargadas " + techniqueNames.size() + " técnicas");
    }

    /**
     * Actualiza la descripción mostrada y establece el generador actual.
     */
    private void updateDescription() {
        String selectedTechnique = techniqueComboBox.getValue();

        if (selectedTechnique == null || selectedTechnique.isEmpty()) {
            return;
        }

        System.out.println("Actualizando descripción para: " + selectedTechnique);

        boolean generatorFound = false;

        for (Map.Entry<String, List<IGenerator>> entry : generatorsByCategory.entrySet()) {
            for (IGenerator generator : entry.getValue()) {
                if (generator.getName().equals(selectedTechnique)) {
                    currentGenerator = generator;
                    descriptionTextArea.setText(generator.getDescription());
                    generatorFound = true;

                    String category = categoryComboBox.getValue();
                    updateInputFieldPlaceholder(category);

                    System.out.println("Generador establecido: " + generator.getClass().getSimpleName());
                    return;
                }
            }
        }

        if (!generatorFound) {
            System.err.println("ERROR: No se encontró generador para: " + selectedTechnique);
        }
    }

    /**
     * Actualiza el placeholder del campo de entrada según la categoría seleccionada.
     *
     * @param category La categoría actualmente seleccionada
     */
    private void updateInputFieldPlaceholder(String category) {
        if (category.contains("Analógico → Analógico") ||
                category.contains("Analógico → Digital")) {
            inputTextField.setPromptText("Función: sin(2*pi*t), cos(t), sin(t)+cos(2*t), etc.");
            inputTextField.clear();
        } else {
            inputTextField.setPromptText("Ej: 10110010");
            if (inputTextField.getText().isEmpty()) {
                inputTextField.setText("10110010");
            }
        }
    }

    /**
     * Maneja el evento de generación de señal.
     */
    @FXML
    private void handleGenerate() {
        System.out.println("=== INICIANDO GENERACION DE SEÑAL ===");

        if (currentGenerator == null) {
            System.err.println("ERROR: No hay generador seleccionado");
            showAlert("Error",
                    "Por favor seleccione una técnica de codificación primero",
                    Alert.AlertType.ERROR);
            return;
        }

        String input = inputTextField.getText().trim();
        String category = categoryComboBox.getValue();

        System.out.println("Entrada del usuario: '" + input + "'");
        System.out.println("Categoría: " + category);
        System.out.println("Técnica: " + currentGenerator.getName());

        // Validación de entrada para técnicas digitales
        if (requiresDigitalInput(category)) {
            if (!isValidDigitalInput(input)) {
                System.err.println("ERROR: Entrada inválida para técnica digital");
                showAlert("Error de Entrada",
                        "Para técnicas digitales, ingrese solo 0s y 1s.\nEjemplo: 10110010",
                        Alert.AlertType.ERROR);
                return;
            }

            if (input.length() < 4) {
                System.out.println("ADVERTENCIA: Entrada corta (menos de 4 bits)");
            }
        }

        try {
            Map<String, Object> params = new HashMap<>();

            // Si es técnica analógica y hay función, parsearla
            if (requiresAnalogInput(category) && !input.isEmpty()) {
                System.out.println("Parseando función matemática: " + input);
                params.put("customFunction", input);
            }

            List<SignalData> signalData = currentGenerator.generate(input, params);

            if (signalData == null || signalData.isEmpty()) {
                System.err.println("ERROR: No se generaron datos de señal");
                showAlert("Error de Generación",
                        "No se pudo generar la señal. Verifique su entrada.",
                        Alert.AlertType.ERROR);
                return;
            }

            System.out.println("Señal generada exitosamente: " + signalData.size() + " puntos");
            plotSignal(signalData);

            System.out.println("=== GENERACION COMPLETADA ===");

        } catch (Exception e) {
            System.err.println("EXCEPCION durante la generación:");
            e.printStackTrace();
            showAlert("Error de Generación",
                    "Ocurrió un error al generar la señal:\n" + e.getMessage() +
                            "\n\nPara funciones, use: sin(t), cos(t), sin(2*pi*t), etc.",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Verifica si la categoría requiere entrada digital (binaria).
     *
     * @param category La categoría a verificar
     * @return true si requiere entrada digital
     */
    private boolean requiresDigitalInput(String category) {
        return category.contains("Digital → Digital") ||
                category.contains("Digital → Analógico");
    }

    /**
     * Verifica si la categoría requiere entrada analógica (función).
     *
     * @param category La categoría a verificar
     * @return true si requiere entrada analógica
     */
    private boolean requiresAnalogInput(String category) {
        return category.contains("Analógico → Analógico") ||
                category.contains("Analógico → Digital");
    }

    /**
     * Valida que la entrada sea una cadena binaria válida.
     *
     * @param input La cadena a validar
     * @return true si es válida
     */
    private boolean isValidDigitalInput(String input) {
        return input != null && !input.isEmpty() && input.matches("[01]+");
    }

    /**
     * Grafica la señal generada en el LineChart.
     *
     * @param data Lista de puntos SignalData a graficar
     */
    private void plotSignal(List<SignalData> data) {
        System.out.println("Graficando señal con " + data.size() + " puntos...");

        signalChart.getData().clear();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(currentGenerator.getName());

        for (SignalData point : data) {
            series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
        }

        signalChart.getData().add(series);
        signalChart.setCreateSymbols(false);
        signalChart.setLegendVisible(true);
        signalChart.setAnimated(false);

        System.out.println("Gráfico actualizado correctamente");
    }

    /**
     * Muestra un cuadro de diálogo de alerta al usuario.
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Muestra un error crítico.
     */
    private void showCriticalError(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Error Crítico");
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @FXML
    private void onMouseEntered() {
        generateButton.setStyle(
                "-fx-background-color: #2980b9; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"
        );
    }

    @FXML
    private void onMouseExited() {
        generateButton.setStyle(
                "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
        );
    }
}