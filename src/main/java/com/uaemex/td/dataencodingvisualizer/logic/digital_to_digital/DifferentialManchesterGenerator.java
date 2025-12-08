package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Differential Manchester (IEEE 802.5)
 * Siempre hay transición en la mitad del bit (sincronización)
 * Bit 0 = transición al inicio del bit
 * Bit 1 = NO hay transición al inicio del bit
 */
public class DifferentialManchesterGenerator extends LineCodingGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        double time = 0;
        int halfSamples = SAMPLES_PER_BIT / 2;
        double currentLevel = 1.0; // Nivel inicial

        for (char bit : input.toCharArray()) {
            double firstHalfLevel, secondHalfLevel;

            if (bit == '0') {
                // Bit 0: HAY transición al inicio
                currentLevel = -currentLevel;
            }
            // Bit 1: NO hay transición al inicio, mantiene el nivel

            // Primera mitad: nivel actual
            firstHalfLevel = currentLevel;

            // Segunda mitad: SIEMPRE hay transición en la mitad
            secondHalfLevel = -currentLevel;

            // Dibujar primera mitad
            for (int i = 0; i < halfSamples; i++) {
                double x = time + (i / (double) SAMPLES_PER_BIT);
                data.add(new SignalData(x, firstHalfLevel));
            }

            // Dibujar segunda mitad
            for (int i = halfSamples; i < SAMPLES_PER_BIT; i++) {
                double x = time + (i / (double) SAMPLES_PER_BIT);
                data.add(new SignalData(x, secondHalfLevel));
            }

            // Actualizar nivel para el próximo bit
            currentLevel = secondHalfLevel;
            time++;
        }

        return data;
    }

    @Override
    public String getName() {
        return "Differential Manchester (IEEE 802.5)";
    }

    @Override
    public String getDescription() {
        return "Transición siempre en la mitad. '0'=transición al inicio, '1'=SIN transición al inicio. Usada en Token Ring";
    }
}