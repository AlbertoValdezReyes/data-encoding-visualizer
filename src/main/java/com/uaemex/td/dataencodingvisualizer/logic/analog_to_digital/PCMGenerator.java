package com.uaemex.td.dataencodingvisualizer.logic.analog_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import com.uaemex.td.dataencodingvisualizer.util.FunctionEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pulse Code Modulation (PCM)
 *
 * PCM es la técnica más común para convertir señales analógicas a digitales.
 * El proceso consta de tres pasos:
 *
 * 1. MUESTREO (Sampling):
 *    - Teorema de Nyquist: fs >= 2*fm (frecuencia de muestreo >= 2 * frecuencia máxima)
 *    - Se toman muestras de la señal analógica a intervalos regulares T = 1/fs
 *    - El resultado es una señal PAM (Pulse Amplitude Modulation)
 *
 * 2. CUANTIZACIÓN (Quantization):
 *    - Se divide el rango de amplitudes en L = 2^n niveles discretos
 *    - Cada muestra se aproxima al nivel más cercano
 *    - Introduce error de cuantización (ruido de cuantización)
 *    - SNR = 6.02n + 1.76 dB (donde n = bits por muestra)
 *
 * 3. CODIFICACIÓN (Encoding):
 *    - Cada nivel cuantizado se representa con n bits
 *    - Ejemplo: 8 niveles = 3 bits, 256 niveles = 8 bits
 *    - Telefonía usa 8 bits (256 niveles) con companding (ley-μ o ley-A)
 *
 * Aplicaciones: Telefonía digital (64 kbps), Audio CD (44.1 kHz, 16 bits)
 *
 * Referencia: Stallings, W. "Comunicaciones y Redes de Computadores"
 *
 * @author UAEMEX - Transmisión de Datos
 */
public class PCMGenerator extends AnalogToDigitalGenerator {

    private int bitsPerSample;  // n bits por muestra

    public PCMGenerator() {
        super();
        this.samplingRate = 16;        // Muestras por período
        this.bitsPerSample = 3;        // 3 bits = 8 niveles
        this.quantizationLevels = (int) Math.pow(2, bitsPerSample);  // L = 2^n = 8
    }

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (params != null) {
            if (params.containsKey("samplingRate")) {
                samplingRate = (Integer) params.get("samplingRate");
            }
            if (params.containsKey("bitsPerSample")) {
                bitsPerSample = (Integer) params.get("bitsPerSample");
                quantizationLevels = (int) Math.pow(2, bitsPerSample);
            }
        }

        double duration = 2.0;  // Duración total en segundos (2 períodos de señal de 1Hz)
        int analogSamples = 500;  // Puntos para dibujar señal analógica suave
        String customFunction = null;

        if (params != null && params.containsKey("customFunction")) {
            customFunction = (String) params.get("customFunction");
        }

        // ============= PASO 1: SEÑAL ANALÓGICA ORIGINAL =============
        // Primero generamos la señal analógica continua para visualización
        List<Double> analogTimes = new ArrayList<>();
        List<Double> analogValues = new ArrayList<>();

        double maxAnalog = Double.MIN_VALUE;
        double minAnalog = Double.MAX_VALUE;

        for (int i = 0; i <= analogSamples; i++) {
            double t = i * duration / analogSamples;
            double y = evaluateSignal(customFunction, t);
            analogTimes.add(t);
            analogValues.add(y);
            maxAnalog = Math.max(maxAnalog, y);
            minAnalog = Math.min(minAnalog, y);
        }

        // Agregar margen al rango
        double range = maxAnalog - minAnalog;
        double margin = range * 0.1;
        minAnalog -= margin;
        maxAnalog += margin;

        // ============= PASO 2: MUESTREO (Sampling) =============
        // Según Nyquist: fs >= 2*fm
        // Tomamos muestras a intervalos regulares T = duration/samplingRate
        double sampleInterval = duration / samplingRate;

        List<Double> sampleTimes = new ArrayList<>();
        List<Double> sampleValues = new ArrayList<>();

        for (int i = 0; i < samplingRate; i++) {
            double t = i * sampleInterval;
            double y = evaluateSignal(customFunction, t);
            sampleTimes.add(t);
            sampleValues.add(y);
        }

        // ============= PASO 3: CUANTIZACIÓN (Quantization) =============
        // Dividir el rango en L = 2^n niveles
        double stepSize = (maxAnalog - minAnalog) / quantizationLevels;

        List<Double> quantizedValues = new ArrayList<>();
        List<Integer> quantizedLevels = new ArrayList<>();

        for (double value : sampleValues) {
            // Encontrar el nivel más cercano
            int level = (int) Math.round((value - minAnalog) / stepSize);
            level = Math.max(0, Math.min(quantizationLevels - 1, level));

            double quantizedValue = minAnalog + (level + 0.5) * stepSize;
            quantizedValues.add(quantizedValue);
            quantizedLevels.add(level);
        }

        // ============= PASO 4: CODIFICACIÓN Y VISUALIZACIÓN =============
        // Generar la señal PCM escalonada (staircase)

        // Dibujar la señal cuantizada como escalones
        for (int i = 0; i < samplingRate; i++) {
            double tStart = sampleTimes.get(i);
            double tEnd = (i < samplingRate - 1) ? sampleTimes.get(i + 1) : duration;
            double qValue = quantizedValues.get(i);

            // Línea horizontal en el nivel cuantizado
            data.add(new SignalData(tStart, qValue));
            data.add(new SignalData(tEnd - 0.001, qValue));

            // Transición vertical al siguiente nivel (si hay siguiente)
            if (i < samplingRate - 1) {
                double nextQValue = quantizedValues.get(i + 1);
                data.add(new SignalData(tEnd, qValue));
                data.add(new SignalData(tEnd, nextQValue));
            }
        }

        // Imprimir información de codificación binaria
        StringBuilder binaryOutput = new StringBuilder();
        binaryOutput.append("Codificación PCM (").append(bitsPerSample).append(" bits/muestra):\n");
        for (int i = 0; i < Math.min(8, quantizedLevels.size()); i++) {
            String binary = String.format("%" + bitsPerSample + "s",
                Integer.toBinaryString(quantizedLevels.get(i))).replace(' ', '0');
            binaryOutput.append("Muestra ").append(i).append(": Nivel ")
                       .append(quantizedLevels.get(i)).append(" = ").append(binary).append("\n");
        }
        System.out.println(binaryOutput);

        return data;
    }

    /**
     * Evalúa la señal en el tiempo t
     */
    private double evaluateSignal(String customFunction, double t) {
        if (customFunction != null && !customFunction.trim().isEmpty()) {
            try {
                return FunctionEvaluator.evaluate(customFunction, t);
            } catch (Exception e) {
                return generateTestSignal(t);
            }
        }
        return generateTestSignal(t);
    }

    @Override
    public String getName() {
        return "PCM (Pulse Code Modulation)";
    }

    @Override
    public String getDescription() {
        return "PCM: Conversión analógica-digital en 3 pasos:\n" +
               "1) Muestreo: fs >= 2*fm (Nyquist)\n" +
               "2) Cuantización: L = 2^n niveles (" + quantizationLevels + " niveles, " + bitsPerSample + " bits)\n" +
               "3) Codificación: cada nivel → código binario\n" +
               "SNR = 6.02n + 1.76 dB. Usado en telefonía (8 bits) y CD (16 bits).";
    }
}