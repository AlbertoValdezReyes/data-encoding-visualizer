package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * High Density Bipolar 3-Zero (HDB3)
 * Basado en AMI, reemplaza 4 ceros consecutivos
 * Si número de pulsos desde última sustitución es impar: 000V
 * Si es par: B00V
 */
public class HDB3Generator extends LineCodingGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        double time = 0;
        boolean lastPolarityPositive = true;
        int pulsesSinceSubstitution = 0;
        List<Character> bits = new ArrayList<>();

        for (char c : input.toCharArray()) {
            bits.add(c);
        }

        int i = 0;
        while (i < bits.size()) {
            // Detectar 4 ceros consecutivos
            if (i + 3 < bits.size() && allZeros(bits, i, 4)) {
                double[] pattern = new double[4];

                if (pulsesSinceSubstitution % 2 == 1) {
                    // Impar: 000V
                    pattern[0] = 0; pattern[1] = 0; pattern[2] = 0;
                    pattern[3] = lastPolarityPositive ? 1.0 : -1.0; // Violación
                } else {
                    // Par: B00V
                    pattern[0] = lastPolarityPositive ? 1.0 : -1.0; // B
                    pattern[1] = 0; pattern[2] = 0;
                    pattern[3] = lastPolarityPositive ? 1.0 : -1.0; // Violación
                }

                lastPolarityPositive = !lastPolarityPositive;
                pulsesSinceSubstitution = 0;

                for (double level : pattern) {
                    addBitSegment(data, time, level);
                    time++;
                }
                i += 4;
            } else {
                // AMI normal
                char bit = bits.get(i);
                double level;

                if (bit == '0') {
                    level = 0.0;
                } else {
                    level = lastPolarityPositive ? 1.0 : -1.0;
                    lastPolarityPositive = !lastPolarityPositive;
                    pulsesSinceSubstitution++;
                }

                addBitSegment(data, time, level);
                time++;
                i++;
            }
        }

        return data;
    }

    private boolean allZeros(List<Character> bits, int start, int count) {
        for (int i = 0; i < count; i++) {
            if (bits.get(start + i) != '0') {
                return false;
            }
        }
        return true;
    }

    private void addBitSegment(List<SignalData> data, double time, double level) {
        for (int i = 0; i < SAMPLES_PER_BIT; i++) {
            double x = time + (i / (double) SAMPLES_PER_BIT);
            data.add(new SignalData(x, level));
        }
    }

    @Override
    public String getName() {
        return "HDB3 (High Density Bipolar 3)";
    }

    @Override
    public String getDescription() {
        return "AMI que reemplaza 4 ceros consecutivos con 000V (impar) o B00V (par)";
    }
}