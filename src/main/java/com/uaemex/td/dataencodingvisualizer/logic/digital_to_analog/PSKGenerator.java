package com.uaemex.td.dataencodingvisualizer.logic.digital_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Phase Shift Keying (PSK) - Implementación BPSK
 * Binary Phase Shift Keying (2-PSK)
 * Modula la fase de la portadora según el bit
 * Bit 0 = fase 0° (sin cambio de fase)
 * Bit 1 = fase 180° (inversión de fase)
 * Más resistente al ruido que ASK
 *
 * Implementa fase continua para una visualizacion correcta
 */
public class PSKGenerator extends DigitalToAnalogGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        // Parámetros opcionales
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

        // Calcular ciclos enteros por bit para visualizacion limpia
        int cyclesPerBit = Math.max(1, (int) Math.round(carrierFrequency * bitDuration));
        double adjustedOmega = 2 * Math.PI * cyclesPerBit / bitDuration;

        for (char bit : input.toCharArray()) {
            // BPSK:
            // bit '0' = fase 0° (sin desplazamiento)
            // bit '1' = fase 180° (π radianes) - senal invertida
            double phaseShift = (bit == '1') ? Math.PI : 0;

            // Generar onda sinusoidal - cada bit empieza en fase 0 (o 180 para bit 1)
            for (int i = 0; i < SAMPLES_PER_BIT; i++) {
                double globalTime = time + (i / (double) SAMPLES_PER_BIT) * bitDuration;
                double localTime = (i / (double) SAMPLES_PER_BIT) * bitDuration;
                double y = amplitude * Math.sin(adjustedOmega * localTime + phaseShift);
                data.add(new SignalData(globalTime, y));
            }

            time += bitDuration;
        }

        return data;
    }

    @Override
    public String getName() {
        return "PSK (Phase Shift Keying - BPSK)";
    }

    @Override
    public String getDescription() {
        return "Modula la fase de la portadora: bit '1'=180° (señal invertida), bit '0'=0° (sin cambio). Más robusto que ASK/FSK";
    }
}
