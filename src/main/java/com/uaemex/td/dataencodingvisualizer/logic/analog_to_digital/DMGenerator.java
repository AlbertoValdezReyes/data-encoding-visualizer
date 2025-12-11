package com.uaemex.td.dataencodingvisualizer.logic.analog_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import com.uaemex.td.dataencodingvisualizer.util.FunctionEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Delta Modulation (DM)
 * Codifica la diferencia entre muestras consecutivas
 * Output: 1 si la señal sube, 0 si baja
 * Reconstrucción: escalera que sigue la señal original
 *
 * Soporta funciones personalizadas para la señal analógica de entrada.
 */
public class DMGenerator extends AnalogToDigitalGenerator {
    private double stepSize;

    public DMGenerator() {
        super();
        this.stepSize = 0.1;
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
        double approximation = 0.0;
        String customFunction = null;

        if (params != null && params.containsKey("customFunction")) {
            customFunction = (String) params.get("customFunction");
            System.out.println("Usando función personalizada para DM: " + customFunction);
        }

        for (int i = 0; i < samplingRate; i++) {
            double t = i * sampleInterval;
            double actualValue;

            if (customFunction != null && !customFunction.trim().isEmpty()) {
                try {
                    actualValue = FunctionEvaluator.evaluate(customFunction, t);
                } catch (Exception e) {
                    System.err.println("Error evaluando función: " + e.getMessage());
                    actualValue = generateTestSignal(t);
                }
            } else {
                actualValue = generateTestSignal(t);
            }

            if (actualValue > approximation) {
                approximation += stepSize;
            } else {
                approximation -= stepSize;
            }

            approximation = Math.max(-1.0, Math.min(1.0, approximation));

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
        return "Aproxima la señal con escalones: sube (+Δ) o baja (-Δ) en cada muestra. " +
                "Puede ingresar una función personalizada como: sin(2*pi*t), cos(t), etc.";
    }
}