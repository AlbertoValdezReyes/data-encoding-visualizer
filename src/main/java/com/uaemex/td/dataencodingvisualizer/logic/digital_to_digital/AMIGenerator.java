package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Alternate Mark Inversion (AMI) - También conocido como Bipolar
 * Bit 0 = nivel cero (0V) - sin voltaje
 * Bit 1 = alterna entre voltaje positivo (+V) y negativo (-V)
 * Ventajas: No hay componente DC, detección de errores
 */
public class AMIGenerator extends LineCodingGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        double time = 0;
        boolean nextPositive = true; // Próximo '1' será positivo

        for (char bit : input.toCharArray()) {
            double level;

            if (bit == '0') {
                // Bit 0: voltaje cero
                level = 0.0;
            } else {
                // Bit 1: alternar entre +1 y -1
                level = nextPositive ? 1.0 : -1.0;
                nextPositive = !nextPositive; // Alternar para el próximo '1'
            }

            // Crear segmento horizontal para este bit
            for (int i = 0; i < SAMPLES_PER_BIT; i++) {
                double x = time + (i / (double) SAMPLES_PER_BIT);
                data.add(new SignalData(x, level));
            }
            time++;
        }

        return data;
    }

    @Override
    public String getName() {
        return "AMI (Alternate Mark Inversion)";
    }

    @Override
    public String getDescription() {
        return "Codificación bipolar: '0'=0V, '1' alterna entre +V y -V. Elimina componente DC y permite detección de errores";
    }
}