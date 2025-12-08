package com.uaemex.td.dataencodingvisualizer.logic.analog_to_digital;

import com.uaemex.td.dataencodingvisualizer.logic.IGenerator;

/**
 * Clase abstracta base para técnicas Analógico a Digital
 */
public abstract class AnalogToDigitalGenerator implements IGenerator {
    protected int samplingRate;     // Frecuencia de muestreo
    protected int quantizationLevels; // Niveles de cuantización

    public AnalogToDigitalGenerator() {
        this.samplingRate = 20;
        this.quantizationLevels = 8;
    }

    /**
     * Cuantiza un valor analógico a un nivel discreto
     */
    protected double quantize(double value, double minValue, double maxValue) {
        double range = maxValue - minValue;
        double step = range / quantizationLevels;

        // Encontrar el nivel más cercano
        int level = (int) Math.round((value - minValue) / step);
        level = Math.max(0, Math.min(quantizationLevels - 1, level));

        return minValue + (level * step);
    }

    /**
     * Genera una señal analógica de prueba (sinusoidal)
     */
    protected double generateTestSignal(double time) {
        return Math.sin(2 * Math.PI * 1.0 * time);
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public void setQuantizationLevels(int quantizationLevels) {
        this.quantizationLevels = quantizationLevels;
    }
}