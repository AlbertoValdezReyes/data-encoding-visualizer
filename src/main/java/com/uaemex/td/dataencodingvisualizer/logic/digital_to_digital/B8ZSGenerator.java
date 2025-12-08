package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bipolar 8-Zero Substitution (B8ZS)
 * Basado en AMI pero reemplaza 8 ceros consecutivos
 * con un patrón especial para mantener sincronización
 * Patrón de sustitución: 000VB0VB
 * V = Violación (mismo signo que el pulso anterior)
 * B = Bipolar normal (signo alternado)
 */
public class B8ZSGenerator extends LineCodingGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        double time = 0;
        boolean lastPolarityPositive = true;
        List<Character> bits = new ArrayList<>();

        // Convertir string a lista
        for (char c : input.toCharArray()) {
            bits.add(c);
        }

        int i = 0;
        while (i < bits.size()) {
            // Detectar 8 ceros consecutivos
            if (i + 7 < bits.size() && allZeros(bits, i, 8)) {
                // Aplicar sustitución B8ZS: 000VB0VB
                double[] pattern = new double[8];

                // Primeros 3 bits: 000
                pattern[0] = 0;
                pattern[1] = 0;
                pattern[2] = 0;

                // V (Violación): MISMO signo que el último pulso
                pattern[3] = lastPolarityPositive ? 1.0 : -1.0;

                // B (Bipolar normal): signo OPUESTO
                pattern[4] = lastPolarityPositive ? -1.0 : 1.0;

                // 0
                pattern[5] = 0;

                // V (Violación): MISMO signo que el último pulso
                pattern[6] = lastPolarityPositive ? 1.0 : -1.0;

                // B (Bipolar normal): signo OPUESTO
                pattern[7] = lastPolarityPositive ? -1.0 : 1.0;

                // Actualizar polaridad
                lastPolarityPositive = !lastPolarityPositive;

                // Añadir todo el patrón
                for (double level : pattern) {
                    addBitSegment(data, time, level);
                    time++;
                }
                i += 8; // Saltar los 8 bits procesados
            } else {
                // AMI normal
                char bit = bits.get(i);
                double level;

                if (bit == '0') {
                    level = 0.0;
                } else {
                    // Bit 1: alternar polaridad
                    level = lastPolarityPositive ? 1.0 : -1.0;
                    lastPolarityPositive = !lastPolarityPositive;
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
            if (start + i >= bits.size() || bits.get(start + i) != '0') {
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
        return "B8ZS (Bipolar 8-Zero Substitution)";
    }

    @Override
    public String getDescription() {
        return "AMI mejorado: reemplaza 8 ceros consecutivos con patrón 000VB0VB para evitar pérdida de sincronización. Usado en T1/E1";
    }
}
