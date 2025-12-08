package com.uaemex.td.dataencodingvisualizer.logic.analog_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Delta Modulation (DM)
 * Codifica la diferencia entre muestras consecutivas
 * Output: 1 si la señal sube, 0 si baja
 * Reconstrucción: escalera que sigue la señal original
 */
public class DMGenerator extends AnalogToDigitalGenerator {
    private double stepSize;

    public DMGenerator() {
        super();
        this.stepSize = 0.1; // Tamaño del escalón delta
    }

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (params != null) {
            if (params.containsKey("samplingRate")) {
                samplingRate = (Integer) params.get("samplingRate");
            }
            if (params.containsKey("stepSize")) {
                stepSize = (Double) params.get("stepSize");
            }
        }

        double duration = 2.0;
        double sampleInterval = duration / samplingRate;

        // Valor aproximado inicial
        double approximation = 0.0;

        for (int i = 0; i < samplingRate; i++) {
            double t = i * sampleInterval;
            double actualValue = generateTestSignal(t);

            // Comparar y ajustar
            if (actualValue > approximation) {
                // Bit 1: subir un escalón
                approximation += stepSize;
            } else {
                // Bit 0: bajar un escalón
                approximation -= stepSize;
            }

            // Limitar entre -1 y 1
            approximation = Math.max(-1.0, Math.min(1.0, approximation));

            // Añadir segmento horizontal
            double nextT = (i + 1) * sampleInterval;
            int segments = 10;
            for (int j = 0; j < segments; j++) {
                double x = t + j * (nextT - t) / segments;
                data.add(new SignalData(x, approximation));
            }
        }

        return data;
    }

    @Override
    public String getName() {
        return "DM (Delta Modulation)";
    }

    @Override
    public String getDescription() {
        return "Aproxima la señal con escalones: sube (+Δ) o baja (-Δ) en cada muestra";
    }
}