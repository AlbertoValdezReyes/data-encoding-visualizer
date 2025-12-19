package com.uaemex.td.dataencodingvisualizer.logic.digital_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Frequency Shift Keying (FSK)
 * Modula la frecuencia de la portadora según el bit
 * Bit 0 = frecuencia baja (f1)
 * Bit 1 = frecuencia alta (f2)
 * Usado en módems y transmisión de datos
 *
 * Implementa continuidad de fase para evitar discontinuidades en la señal
 */
public class FSKGenerator extends DigitalToAnalogGenerator {
    private double frequencyLow;   // f1 para bit '0'
    private double frequencyHigh;  // f2 para bit '1'

    public FSKGenerator() {
        super();
        this.frequencyLow = 3.0;   // 3 Hz para '0'
        this.frequencyHigh = 7.0;  // 7 Hz para '1'
    }

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (!isValidBinaryString(input)) {
            return data;
        }

        // Parámetros opcionales
        if (params != null) {
            if (params.containsKey("frequencyLow")) {
                frequencyLow = (Double) params.get("frequencyLow");
            }
            if (params.containsKey("frequencyHigh")) {
                frequencyHigh = (Double) params.get("frequencyHigh");
            }
            if (params.containsKey("carrierFrequency")) {
                // Para FSK, ajustamos las frecuencias basadas en la frecuencia portadora
                double fc = (Double) params.get("carrierFrequency");
                frequencyLow = fc * 0.6;   // 60% de fc para bit '0'
                frequencyHigh = fc * 1.4;  // 140% de fc para bit '1'
            }
            if (params.containsKey("amplitude")) {
                amplitude = (Double) params.get("amplitude");
            }
            if (params.containsKey("bitDuration")) {
                bitDuration = (Double) params.get("bitDuration");
            }
        }

        double time = 0;

        // Calcular ciclos enteros por bit para cada frecuencia
        int cyclesLow = Math.max(1, (int) Math.round(frequencyLow * bitDuration));
        int cyclesHigh = Math.max(2, (int) Math.round(frequencyHigh * bitDuration));
        // Asegurar que las frecuencias sean distinguibles
        if (cyclesLow >= cyclesHigh) cyclesHigh = cyclesLow + 1;

        double omegaLow = 2 * Math.PI * cyclesLow / bitDuration;
        double omegaHigh = 2 * Math.PI * cyclesHigh / bitDuration;

        for (char bit : input.toCharArray()) {
            // Seleccionar frecuencia ajustada según el bit
            double omega = (bit == '1') ? omegaHigh : omegaLow;

            // Generar onda sinusoidal - cada bit empieza en fase 0
            for (int i = 0; i < SAMPLES_PER_BIT; i++) {
                double globalTime = time + (i / (double) SAMPLES_PER_BIT) * bitDuration;
                double localTime = (i / (double) SAMPLES_PER_BIT) * bitDuration;
                double y = amplitude * Math.sin(omega * localTime);
                data.add(new SignalData(globalTime, y));
            }

            time += bitDuration;
        }

        return data;
    }

    @Override
    public String getName() {
        return "FSK (Frequency Shift Keying)";
    }

    @Override
    public String getDescription() {
        return "Modula la frecuencia de la portadora: bit '1'=frecuencia alta (f2), bit '0'=frecuencia baja (f1). Usado en módems";
    }
}
