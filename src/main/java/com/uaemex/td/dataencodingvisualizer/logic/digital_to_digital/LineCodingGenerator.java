package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

import com.uaemex.td.dataencodingvisualizer.logic.IGenerator;
import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta base para técnicas de codificación de línea (Digital a Digital)
 */
public abstract class LineCodingGenerator implements IGenerator {
    protected static final int SAMPLES_PER_BIT = 50; // Puntos por bit para suavidad
    protected LineCodingState state;

    public LineCodingGenerator() {
        this.state = new LineCodingState();
    }

    /**
     * Verifica y alterna la polaridad según el algoritmo específico
     */
    protected void checkPolarity() {
        state.togglePolarity();
    }

    /**
     * Añade una transición suave entre niveles
     */
    protected void addTransition(List<SignalData> data, double startX,
                                 double startY, double endY) {
        int transitionSamples = 5;
        for (int i = 0; i <= transitionSamples; i++) {
            double x = startX + (i * (1.0 / SAMPLES_PER_BIT));
            double y = startY + ((endY - startY) * i / transitionSamples);
            data.add(new SignalData(x, y));
        }
    }

    /**
     * Valida que la entrada sea una cadena binaria válida
     */
    protected boolean isValidBinaryString(String input) {
        return input != null && input.matches("[01]+");
    }
}