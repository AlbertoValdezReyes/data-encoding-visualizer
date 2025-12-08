package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Non-Return to Zero, Level (NRZ-L)
 * Bit 0 = nivel bajo (-1)
 * Bit 1 = nivel alto (+1)
 */
public class NRZ_L_Generator extends LineCodingGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        double time = 0;
        for (char bit : input.toCharArray()) {
            double level = (bit == '1') ? 1.0 : -1.0;

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
        return "NRZ-L (Non-Return to Zero, Level)";
    }

    @Override
    public String getDescription() {
        return "El nivel de voltaje determina el bit: alto (+1) para '1', bajo (-1) para '0'";
    }
}