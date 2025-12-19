package com.uaemex.td.dataencodingvisualizer.logic.digital_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Quadrature Amplitude Modulation (QAM)
 * Combina modulación de amplitud y fase
 * Procesa 2 bits a la vez (4-QAM)
 * 00, 01, 10, 11 -> diferentes combinaciones de amplitud y fase
 */
public class QAMGenerator extends DigitalToAnalogGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

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

        // Procesar bits de 2 en 2 (4-QAM)
        for (int bitIdx = 0; bitIdx < input.length(); bitIdx += 2) {
            String dibits;
            if (bitIdx + 1 < input.length()) {
                dibits = input.substring(bitIdx, bitIdx + 2);
            } else {
                dibits = input.substring(bitIdx) + "0"; // Padding si es impar
            }

            // Mapeo 4-QAM (escalado por amplitud)
            double amplitudeI, amplitudeQ;
            double scaleFactor = amplitude * 0.5;
            switch (dibits) {
                case "00":
                    amplitudeI = scaleFactor; amplitudeQ = scaleFactor;
                    break;
                case "01":
                    amplitudeI = scaleFactor; amplitudeQ = -scaleFactor;
                    break;
                case "10":
                    amplitudeI = -scaleFactor; amplitudeQ = scaleFactor;
                    break;
                case "11":
                    amplitudeI = -scaleFactor; amplitudeQ = -scaleFactor;
                    break;
                default:
                    amplitudeI = 0; amplitudeQ = 0;
            }

            // Generar señal QAM: I*cos(ωt) + Q*sin(ωt)
            for (int i = 0; i < SAMPLES_PER_BIT; i++) {
                double t = time + (i / (double) SAMPLES_PER_BIT) * bitDuration;
                double inPhase = amplitudeI * Math.cos(omega * t);
                double quadrature = amplitudeQ * Math.sin(omega * t);
                double y = inPhase + quadrature;
                data.add(new SignalData(t, y));
            }
            time += bitDuration;
        }

        return data;
    }

    @Override
    public String getName() {
        return "QAM (Quadrature Amplitude Modulation)";
    }

    @Override
    public String getDescription() {
        return "Combina amplitud y fase. Procesa 2 bits simultáneamente (4-QAM)";
    }
}
