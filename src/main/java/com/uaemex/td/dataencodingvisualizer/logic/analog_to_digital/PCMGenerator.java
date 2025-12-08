package com.uaemex.td.dataencodingvisualizer.logic.analog_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pulse Code Modulation (PCM)
 * Proceso: Muestreo -> Cuantización -> Codificación
 * Convierte señal analógica a digital mediante muestras cuantizadas
 */
public class PCMGenerator extends AnalogToDigitalGenerator {

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (params != null) {
            if (params.containsKey("samplingRate")) {
                samplingRate = (Integer) params.get("samplingRate");
            }
            if (params.containsKey("quantizationLevels")) {
                quantizationLevels = (Integer) params.get("quantizationLevels");
            }
        }

        double duration = 2.0;
        int totalSamples = 200;

        // Generar señal analógica original (continua)
        List<SignalData> analogSignal = new ArrayList<>();
        for (int i = 0; i < totalSamples; i++) {
            double t = i * duration / totalSamples;
            double y = generateTestSignal(t);
            analogSignal.add(new SignalData(t, y));
        }

        // Paso 1: Muestreo
        List<SignalData> sampledSignal = new ArrayList<>();
        int sampleInterval = totalSamples / samplingRate;
        for (int i = 0; i < totalSamples; i += sampleInterval) {
            if (i < analogSignal.size()) {
                sampledSignal.add(analogSignal.get(i));
            }
        }

        // Paso 2: Cuantización
        double minValue = -1.0;
        double maxValue = 1.0;

        for (SignalData sample : sampledSignal) {
            double quantized = quantize(sample.getY(), minValue, maxValue);

            // Añadir línea vertical para mostrar el muestreo
            data.add(new SignalData(sample.getX(), 0));
            data.add(new SignalData(sample.getX(), quantized));

            // Añadir línea horizontal para mostrar el nivel cuantizado
            double nextX = sample.getX() + (duration / samplingRate);
            for (int j = 0; j < 10; j++) {
                double x = sample.getX() + j * (nextX - sample.getX()) / 10;
                data.add(new SignalData(x, quantized));
            }
        }

        return data;
    }

    @Override
    public String getName() {
        return "PCM (Pulse Code Modulation)";
    }

    @Override
    public String getDescription() {
        return "Muestrea y cuantiza señal analógica en niveles discretos";
    }
}