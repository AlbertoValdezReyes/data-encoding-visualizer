package com.uaemex.td.dataencodingvisualizer.logic;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.List;
import java.util.Map;

/**
 * Interfaz genérica para todos los generadores de señales
 * Define el contrato que deben cumplir todas las técnicas de codificación
 *
 * Esta interfaz utiliza el patrón Strategy para permitir que diferentes
 * algoritmos de codificación sean intercambiables
 *
 * Categorías de técnicas implementadas:
 * - Digital a Digital (Codificación de Línea): NRZ, Manchester, AMI, etc.
 * - Digital a Analógico (Modulación): ASK, FSK, PSK, QAM
 * - Analógico a Analógico: AM, FM, PM
 * - Analógico a Digital: PCM, DM
 *
 * @author UAEMEX - Transmisión de Datos
 * @version 1.0
 */
public interface IGenerator {

    /**
     * Genera los datos de la señal basándose en la entrada proporcionada
     *
     * Este es el metodo principal que implementa cada técnica de codificación.
     * Procesa la entrada y produce una lista de puntos SignalData que representan
     * la señal modulada/codificada para ser visualizada.
     *
     * @param input Cadena de entrada que varía según la técnica:
     *              - Para técnicas digitales: cadena binaria (ej: "10110010")
     *              - Para técnicas analógicas: puede ser null (usa señal de prueba)
     *              - Para técnicas especiales: puede incluir parámetros específicos
     *
     * @param params Mapa de parámetros adicionales opcionales, puede incluir:
     *               - "carrierFrequency": Double - Frecuencia de la portadora (Hz)
     *               - "messageFrequency": Double - Frecuencia del mensaje (Hz)
     *               - "samplingRate": Integer - Tasa de muestreo
     *               - "quantizationLevels": Integer - Niveles de cuantización
     *               - "modulationIndex": Double - Índice de modulación
     *               - "frequencyDeviation": Double - Desviación de frecuencia
     *               - Otros parámetros específicos de cada técnica
     *
     * @return Lista de objetos SignalData que representan los puntos de la señal.
     *         Cada punto contiene coordenadas (x, y) donde:
     *         - x = tiempo en segundos
     *         - y = amplitud/voltaje
     *         Retorna lista vacía si la entrada es inválida
     *
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    List<SignalData> generate(String input, Map<String, Object> params);

    /**
     * Retorna el nombre completo de la técnica de codificación
     *
     * Este nombre se muestra en la interfaz gráfica para que el usuario
     * identifique la técnica seleccionada
     *
     * @return Nombre descriptivo de la técnica
     *         Ejemplos: "NRZ-L (Non-Return to Zero, Level)",
     *                   "ASK (Amplitude Shift Keying)",
     *                   "Manchester (IEEE 802.3)"
     */
    String getName();

    /**
     * Retorna una descripción detallada de cómo funciona la técnica
     *
     * Esta descripción se muestra en la interfaz para educar al usuario
     * sobre el funcionamiento de cada técnica de codificación
     *
     * @return Descripción breve pero informativa que explica:
     *         - El principio de funcionamiento
     *         - Cómo codifica/modula la información
     *         - Ventajas principales (opcional)
     *         - Aplicaciones comunes (opcional)
     */
    default String getDescription() {
        return "Técnica de codificación de señales para transmisión de datos";
    }

    /**
     * Retorna la categoría a la que pertenece la técnica
     *
     * @return Categoría de la técnica:
     *         - "Digital → Digital"
     *         - "Digital → Analógico"
     *         - "Analógico → Analógico"
     *         - "Analógico → Digital"
     */
    default String getCategory() {
        return "General";
    }

    /**
     * Indica si la técnica requiere entrada binaria
     *
     * @return true si requiere cadena binaria (0s y 1s), false en caso contrario
     */
    default boolean requiresBinaryInput() {
        return false;
    }

    /**
     * Retorna el número mínimo de bits/muestras recomendado para visualización
     *
     * @return Número mínimo de bits recomendado (por defecto 8)
     */
    default int getMinimumInputLength() {
        return 8;
    }

    /**
     * Retorna el número máximo de bits/muestras recomendado para visualización
     *
     * @return Número máximo de bits recomendado (por defecto 32)
     */
    default int getMaximumInputLength() {
        return 32;
    }

    /**
     * Valida si la entrada proporcionada es válida para esta técnica
     *
     * @param input Cadena de entrada a validar
     * @return true si la entrada es válida, false en caso contrario
     */
    default boolean validateInput(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // Validar longitud
        if (input.length() < getMinimumInputLength() ||
                input.length() > getMaximumInputLength()) {
            return false;
        }

        // Validar formato binario si es requerido
        if (requiresBinaryInput() && !input.matches("[01]+")) {
            return false;
        }

        return true;
    }

    /**
     * Retorna un ejemplo de entrada válida para esta técnica
     *
     * @return Cadena de ejemplo que puede usarse como entrada de prueba
     */
    default String getExampleInput() {
        return "10110010";
    }

    /**
     * Retorna información sobre los parámetros configurables
     *
     * @return Mapa con nombres de parámetros y sus descripciones
     */
    default Map<String, String> getConfigurableParameters() {
        return Map.of();
    }

    /**
     * Indica si la técnica soporta análisis en tiempo real
     *
     * @return true si soporta procesamiento en tiempo real
     */
    default boolean supportsRealTimeProcessing() {
        return true;
    }

    /**
     * Retorna el número de muestras por bit/símbolo que genera la técnica
     *
     * @return Número de muestras para suavizar la visualización
     */
    default int getSamplesPerBit() {
        return 50;
    }

    /**
     * Calcula la tasa de bits (bit rate) teórica para esta técnica
     *
     * @param symbolRate Tasa de símbolos por segundo
     * @return Tasa de bits en bits por segundo (bps)
     */
    default double calculateBitRate(double symbolRate) {
        return symbolRate; // Por defecto 1 bit por símbolo
    }

    /**
     * Calcula el ancho de banda requerido para esta técnica
     *
     * @param bitRate Tasa de bits en bps
     * @return Ancho de banda en Hz
     */
    default double calculateBandwidth(double bitRate) {
        return bitRate; // Aproximación simple
    }
}