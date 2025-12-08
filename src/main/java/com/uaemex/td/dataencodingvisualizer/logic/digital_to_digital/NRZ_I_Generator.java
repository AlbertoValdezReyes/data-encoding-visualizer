package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Non-Return to Zero, Inverted (NRZ-I)
 * Bit 1 = transición (cambio de nivel)
 * Bit 0 = sin cambio (mantiene el nivel anterior)
 */
public class NRZ_I_Generator extends LineCodingGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        double currentLevel = 1.0; // Nivel inicial positivo
        double time = 0;

        for (char bit : input.toCharArray()) {
            // Si es '1', invertir el nivel
            if (bit == '1') {
                currentLevel = -currentLevel;
            }
            // Si es '0', mantener el nivel actual

            // Crear segmento horizontal para todo el bit
            for (int i = 0; i < SAMPLES_PER_BIT; i++) {
                double x = time + (i / (double) SAMPLES_PER_BIT);
                data.add(new SignalData(x, currentLevel));
            }
            time++;
        }

        return data;
    }

    @Override
    public String getName() {
        return "NRZ-I (Non-Return to Zero, Inverted)";
    }

    @Override
    public String getDescription() {
        return "Codificación por transición: bit '1' causa cambio de nivel, bit '0' mantiene el nivel anterior";
    }
}