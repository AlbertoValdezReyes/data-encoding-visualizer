package com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog;

import com.uaemex.td.dataencodingvisualizer.logic.IGenerator;
import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.Map;

/**
 * Clase abstracta base para técnicas de Analógico a Analógico
 *
 * Estas técnicas modulan una señal portadora analógica de alta frecuencia
 * con una señal mensaje analógica de baja frecuencia para transmisión.
 *
 * Técnicas implementadas:
 * - AM (Amplitude Modulation): Modula la amplitud
 * - FM (Frequency Modulation): Modula la frecuencia
 * - PM (Phase Modulation): Modula la fase
 *
 * Aplicaciones:
 * - Radio AM/FM comercial
 * - Televisión analógica
 * - Comunicaciones de radio
 * - Broadcasting
 *
 * @author UAEMEX - Transmisión de Datos
 * @version 1.0
 */
public abstract class AnalogToAnalogGenerator implements IGenerator {

    /** Frecuencia de la señal portadora en Hz */
    protected double carrierFrequency;

    /** Frecuencia de la señal mensaje (moduladora) en Hz */
    protected double messageFrequency;

    /** Número de muestras para generar la señal */
    protected static final int SAMPLES = 500;

    /** Amplitud de la señal portadora */
    protected double carrierAmplitude;

    /** Amplitud de la señal mensaje */
    protected double messageAmplitude;

    /**
     * Constructor por defecto
     * Inicializa las frecuencias con valores predeterminados
     */
    public AnalogToAnalogGenerator() {
        this.carrierFrequency = 10.0;    // 10 Hz portadora
        this.messageFrequency = 1.0;     // 1 Hz mensaje
        this.carrierAmplitude = 1.0;     // Amplitud normalizada
        this.messageAmplitude = 1.0;     // Amplitud normalizada
    }

    /**
     * Genera una señal sinusoidal básica
     *
     * @param time Tiempo en segundos
     * @param frequency Frecuencia en Hz
     * @param amplitude Amplitud de la señal
     * @return Valor de la señal en el tiempo dado
     */
    protected double generateSineWave(double time, double frequency, double amplitude) {
        return amplitude * Math.sin(2 * Math.PI * frequency * time);
    }

    /**
     * Genera una señal coseno
     *
     * @param time Tiempo en segundos
     * @param frequency Frecuencia en Hz
     * @param amplitude Amplitud de la señal
     * @return Valor de la señal en el tiempo dado
     */
    protected double generateCosineWave(double time, double frequency, double amplitude) {
        return amplitude * Math.cos(2 * Math.PI * frequency * time);
    }

    /**
     * Calcula la frecuencia angular (omega = 2π * f)
     *
     * @param frequency Frecuencia en Hz
     * @return Frecuencia angular en radianes/segundo
     */
    protected double getAngularFrequency(double frequency) {
        return 2 * Math.PI * frequency;
    }

    /**
     * Genera una señal mensaje de prueba (sinusoidal simple)
     *
     * @param time Tiempo en segundos
     * @return Valor de la señal mensaje
     */
    protected double generateMessageSignal(double time) {
        return generateSineWave(time, messageFrequency, messageAmplitude);
    }

    /**
     * Genera una señal portadora no modulada
     *
     * @param time Tiempo en segundos
     * @return Valor de la señal portadora
     */
    protected double generateCarrierSignal(double time) {
        return generateSineWave(time, carrierFrequency, carrierAmplitude);
    }

    /**
     * Normaliza un valor entre -1 y 1
     *
     * @param value Valor a normalizar
     * @param min Valor mínimo del rango original
     * @param max Valor máximo del rango original
     * @return Valor normalizado
     */
    protected double normalize(double value, double min, double max) {
        if (max == min) return 0;
        return 2 * ((value - min) / (max - min)) - 1;
    }

    /**
     * Aplica una ventana de Hamming para suavizar la señal
     *
     * @param index Índice actual de la muestra
     * @param totalSamples Total de muestras
     * @return Factor de ventana (0 a 1)
     */
    protected double hammingWindow(int index, int totalSamples) {
        return 0.54 - 0.46 * Math.cos(2 * Math.PI * index / (totalSamples - 1));
    }

    /**
     * Calcula el índice de modulación para AM
     *
     * @param messageAmp Amplitud del mensaje
     * @param carrierAmp Amplitud de la portadora
     * @return Índice de modulación (0 a 1)
     */
    protected double calculateModulationIndex(double messageAmp, double carrierAmp) {
        if (carrierAmp == 0) return 0;
        return messageAmp / carrierAmp;
    }

    // ==================== GETTERS Y SETTERS ====================

    /**
     * Establece la frecuencia de la portadora
     *
     * @param carrierFrequency Frecuencia en Hz (debe ser > 0)
     */
    public void setCarrierFrequency(double carrierFrequency) {
        if (carrierFrequency <= 0) {
            throw new IllegalArgumentException("La frecuencia debe ser positiva");
        }
        this.carrierFrequency = carrierFrequency;
    }

    /**
     * Obtiene la frecuencia de la portadora
     *
     * @return Frecuencia en Hz
     */
    public double getCarrierFrequency() {
        return carrierFrequency;
    }

    /**
     * Establece la frecuencia del mensaje
     *
     * @param messageFrequency Frecuencia en Hz (debe ser > 0)
     */
    public void setMessageFrequency(double messageFrequency) {
        if (messageFrequency <= 0) {
            throw new IllegalArgumentException("La frecuencia debe ser positiva");
        }
        this.messageFrequency = messageFrequency;
    }

    /**
     * Obtiene la frecuencia del mensaje
     *
     * @return Frecuencia en Hz
     */
    public double getMessageFrequency() {
        return messageFrequency;
    }

    /**
     * Establece la amplitud de la portadora
     *
     * @param carrierAmplitude Amplitud (debe ser > 0)
     */
    public void setCarrierAmplitude(double carrierAmplitude) {
        if (carrierAmplitude <= 0) {
            throw new IllegalArgumentException("La amplitud debe ser positiva");
        }
        this.carrierAmplitude = carrierAmplitude;
    }

    /**
     * Obtiene la amplitud de la portadora
     *
     * @return Amplitud
     */
    public double getCarrierAmplitude() {
        return carrierAmplitude;
    }

    /**
     * Establece la amplitud del mensaje
     *
     * @param messageAmplitude Amplitud (debe ser > 0)
     */
    public void setMessageAmplitude(double messageAmplitude) {
        if (messageAmplitude <= 0) {
            throw new IllegalArgumentException("La amplitud debe ser positiva");
        }
        this.messageAmplitude = messageAmplitude;
    }

    /**
     * Obtiene la amplitud del mensaje
     *
     * @return Amplitud
     */
    public double getMessageAmplitude() {
        return messageAmplitude;
    }

    // ==================== IMPLEMENTACIONES POR DEFECTO ====================

    @Override
    public String getCategory() {
        return "Analógico → Analógico";
    }

    @Override
    public boolean requiresBinaryInput() {
        return false; // No requiere entrada binaria
    }

    @Override
    public String getExampleInput() {
        return ""; // No requiere entrada (usa señal de prueba)
    }

    @Override
    public boolean validateInput(String input) {
        return true; // Siempre válido (no usa entrada)
    }

    @Override
    public int getMinimumInputLength() {
        return 0; // No requiere longitud mínima
    }

    @Override
    public int getMaximumInputLength() {
        return 0; // No requiere longitud máxima
    }

    @Override
    public Map<String, String> getConfigurableParameters() {
        return Map.of(
                "carrierFrequency", "Frecuencia de la portadora (Hz)",
                "messageFrequency", "Frecuencia de la señal mensaje (Hz)",
                "carrierAmplitude", "Amplitud de la portadora",
                "messageAmplitude", "Amplitud de la señal mensaje"
        );
    }

    @Override
    public double calculateBitRate(double symbolRate) {
        // Para señales analógicas, la tasa de bits depende del muestreo
        return symbolRate * Math.log(2); // Aproximación
    }

    @Override
    public double calculateBandwidth(double bitRate) {
        // Para modulación analógica, el ancho de banda depende de la técnica
        // AM: BW ≈ 2 * fm
        // FM: BW ≈ 2 * (Δf + fm) (Regla de Carson)
        return 2 * messageFrequency; // Aproximación básica
    }

    /**
     * Valida que la frecuencia de la portadora sea mayor que la del mensaje
     * (Teorema de Nyquist: fc >> fm)
     *
     * @return true si la relación de frecuencias es válida
     */
    protected boolean validateFrequencyRatio() {
        return carrierFrequency >= 2 * messageFrequency;
    }

    /**
     * Obtiene el número de muestras para la generación
     *
     * @return Número de muestras
     */
    protected int getSampleCount() {
        return SAMPLES;
    }

    /**
     * Calcula la duración total de la señal en segundos
     *
     * @return Duración en segundos
     */
    protected double getSignalDuration() {
        return 2.0; // 2 segundos por defecto
    }

    /**
     * Calcula el período de muestreo
     *
     * @return Período en segundos
     */
    protected double getSamplingPeriod() {
        return getSignalDuration() / SAMPLES;
    }

    /**
     * Información adicional sobre la técnica de modulación
     *
     * @return Mapa con información técnica
     */
    public Map<String, Object> getTechnicalInfo() {
        return Map.of(
                "Frecuencia Portadora", carrierFrequency + " Hz",
                "Frecuencia Mensaje", messageFrequency + " Hz",
                "Relación fc/fm", String.format("%.2f", carrierFrequency / messageFrequency),
                "Muestras", SAMPLES,
                "Duración", getSignalDuration() + " s"
        );
    }
}