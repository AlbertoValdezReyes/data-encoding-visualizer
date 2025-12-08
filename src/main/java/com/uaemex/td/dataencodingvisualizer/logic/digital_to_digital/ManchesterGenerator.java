package com.uaemex.td.dataencodingvisualizer.logic.digital_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manchester Encoding (IEEE 802.3)
 * Bit 0 = transición de alto a bajo en la mitad del bit
 * Bit 1 = transición de bajo a alto en la mitad del bit
 * Siempre hay una transición en el medio del bit (sincronización)
 */
public class ManchesterGenerator extends LineCodingGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        double time = 0;
        int halfSamples = SAMPLES_PER_BIT / 2;

        for (char bit : input.toCharArray()) {
            double firstHalfLevel, secondHalfLevel;

            if (bit == '1') {
                // Bit 1: transición de bajo (-1) a alto (+1)
                firstHalfLevel = -1.0;
                secondHalfLevel = 1.0;
            } else {
                // Bit 0: transición de alto (+1) a bajo (-1)
                firstHalfLevel = 1.0;
                secondHalfLevel = -1.0;
            }

            // Primera mitad del bit
            for (int i = 0; i < halfSamples; i++) {
                double x = time + (i / (double) SAMPLES_PER_BIT);
                data.add(new SignalData(x, firstHalfLevel));
            }

            // Segunda mitad del bit (con transición)
            for (int i = halfSamples; i < SAMPLES_PER_BIT; i++) {
                double x = time + (i / (double) SAMPLES_PER_BIT);
                data.add(new SignalData(x, secondHalfLevel));
            }

            time++;
        }

        return data;
    }

    @Override
    public String getName() {
        return "Manchester (IEEE 802.3)";
    }

    @Override
    public String getDescription() {
        return "Transición garantizada en medio de cada bit: '1'=bajo→alto, '0'=alto→bajo. Usada en Ethernet 10BASE-T";
    }
}