package com.uaemex.td.dataencodingvisualizer.logic.analog_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import com.uaemex.td.dataencodingvisualizer.util.FunctionEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pulse Code Modulation (PCM)
 * Proceso: Muestreo -> Cuantización -> Codificación
 *
 * Convierte señal analógica continua en señal digital discreta mediante:
 * 1. Muestreo: Toma valores a intervalos regulares
 * 2. Cuantización: Redondea valores a niveles discretos
 * 3. Codificación: Convierte a representación digital binaria
 *
 * Soporta funciones personalizadas para la señal analógica de entrada.
 * Usado en telefonía digital, audio CD, comunicaciones digitales.
 *
 * @author UAEMEX - Transmisión de Datos
 */
public class PCMGenerator extends AnalogToDigitalGenerator {

    public PCMGenerator() {
        super();
        this.samplingRate = 20;
        this.quantizationLevels = 8;
    }

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
        String customFunction = null;

        if (params != null && params.containsKey("customFunction")) {
            customFunction = (String) params.get("customFunction");
            System.out.println("Usando función personalizada para PCM: " + customFunction);
        }

        // Generar señal analógica original continua
        List<SignalData> analogSignal = new ArrayList<>();
        for (int i = 0; i < totalSamples; i++) {
            double t = i * duration / totalSamples;
            double y;

            if (customFunction != null && !customFunction.trim().isEmpty()) {
                try {
                    y = FunctionEvaluator.evaluate(customFunction, t);
                } catch (Exception e) {
                    System.err.println("Error evaluando función: " + e.getMessage());
                    y = generateTestSignal(t);
                }
            } else {
                y = generateTestSignal(t);
            }

            analogSignal.add(new SignalData(t, y));
        }

        // Paso 1: Muestreo - tomar muestras a intervalos regulares
        List<SignalData> sampledSignal = new ArrayList<>();
        int sampleInterval = totalSamples / samplingRate;
        for (int i = 0; i < totalSamples; i += sampleInterval) {
            if (i < analogSignal.size()) {
                sampledSignal.add(analogSignal.get(i));
            }
        }

        // Paso 2: Cuantización - redondear a niveles discretos
        double minValue = -1.0;
        double maxValue = 1.0;

        for (SignalData sample : sampledSignal) {
            double quantized = quantize(sample.getY(), minValue, maxValue);

            // Dibujar línea vertical desde cero hasta el nivel cuantizado
            data.add(new SignalData(sample.getX(), 0));
            data.add(new SignalData(sample.getX(), quantized));

            // Dibujar línea horizontal en el nivel cuantizado
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
        return "Muestrea y cuantiza señal analógica en niveles discretos. " +
                "Estándar en telefonía digital y audio CD. " +
                "Puede ingresar función: sin(2*pi*t), cos(t), sin(t)+cos(2*t), etc.";
    }
}
