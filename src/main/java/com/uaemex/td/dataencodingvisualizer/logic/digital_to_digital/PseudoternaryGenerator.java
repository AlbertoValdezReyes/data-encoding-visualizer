package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pseudoternary (Inverso de AMI)
 * Bit 1 = nivel cero (0V)
 * Bit 0 = alterna entre voltaje positivo (+V) y negativo (-V)
 * Es la inversión lógica de AMI
 */
public class PseudoternaryGenerator extends LineCodingGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        double time = 0;
        boolean nextPositive = true; // Próximo '0' será positivo

        for (char bit : input.toCharArray()) {
            double level;

            if (bit == '1') {
                // Bit 1: voltaje cero
                level = 0.0;
            } else {
                // Bit 0: alternar entre +1 y -1
                level = nextPositive ? 1.0 : -1.0;
                nextPositive = !nextPositive; // Alternar para el próximo '0'
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
        return "Pseudoternary";
    }

    @Override
    public String getDescription() {
        return "Inverso de AMI: '1'=0V, '0' alterna entre +V y -V. Mismas ventajas que AMI";
    }
}