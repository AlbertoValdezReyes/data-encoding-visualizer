package com.uaemex.td.dataencodingvisualizer.logic.digital_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Amplitude Shift Keying (ASK)
 * Modula la amplitud de la portadora
 * Bit 0 = amplitud baja (o 0)
 * Bit 1 = amplitud alta
 */
public class ASKGenerator extends DigitalToAnalogGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        // Parámetros
        if (params != null) {
            if (params.containsKey("carrierFrequency")) {
                carrierFrequency = (Double) params.get("carrierFrequency");
            }
            if (params.containsKey("amplitude")) {
                amplitude = (Double) params.get("amplitude");
            }
            if (params.containsKey("bitDuration")) {
                bitDuration = (Double) params.get("bitDuration");
            }
        }

        double time = 0;
        double omega = getAngularFrequency(carrierFrequency);

        for (char bit : input.toCharArray()) {
            double bitAmplitude = (bit == '1') ? amplitude : amplitude * 0.2; // '0' tiene amplitud baja

            for (int i = 0; i < SAMPLES_PER_BIT; i++) {
                double t = time + (i / (double) SAMPLES_PER_BIT) * bitDuration;
                double y = bitAmplitude * Math.sin(omega * t);
                data.add(new SignalData(t, y));
            }
            time += bitDuration;
        }

        return data;
    }

    @Override
    public String getName() {
        return "ASK (Amplitude Shift Keying)";
    }

    @Override
    public String getDescription() {
        return "Modula la amplitud: bit '1' = amplitud alta, bit '0' = amplitud baja";
    }
}