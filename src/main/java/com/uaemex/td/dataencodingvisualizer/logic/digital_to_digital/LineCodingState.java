package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

/**
 * Clase para rastrear el estado en técnicas de codificación de línea
 * Útil para NRZ-I, AMI, B8ZS, HDB3, etc.
 */
public class LineCodingState {
    private int lastLevel;          // Último nivel de voltaje (-1, 0, 1)
    private boolean lastPolarity;   // true = positivo, false = negativo
    private int consecutiveZeros;   // Contador de ceros consecutivos

    public LineCodingState() {
        this.lastLevel = 0;
        this.lastPolarity = true;
        this.consecutiveZeros = 0;
    }

    public int getLastLevel() {
        return lastLevel;
    }

    public void setLastLevel(int lastLevel) {
        this.lastLevel = lastLevel;
    }

    public boolean isLastPolarity() {
        return lastPolarity;
    }

    public void setLastPolarity(boolean lastPolarity) {
        this.lastPolarity = lastPolarity;
    }

    public void togglePolarity() {
        this.lastPolarity = !this.lastPolarity;
    }

    public int getConsecutiveZeros() {
        return consecutiveZeros;
    }

    public void incrementConsecutiveZeros() {
        this.consecutiveZeros++;
    }

    public void resetConsecutiveZeros() {
        this.consecutiveZeros = 0;
    }

    public void setConsecutiveZeros(int count) {
        this.consecutiveZeros = count;
    }
}
