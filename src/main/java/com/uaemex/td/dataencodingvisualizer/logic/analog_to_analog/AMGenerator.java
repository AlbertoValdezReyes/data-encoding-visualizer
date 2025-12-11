package com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import com.uaemex.td.dataencodingvisualizer.util.FunctionEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Amplitude Modulation (AM)
 *
 * Formula matemática:
 * s(t) = [1 + na * x(t)] * cos(2π * fc * t)
 *
 * Donde:
 * - s(t) = señal modulada AM
 * - na = índice de modulación (0 < na <= 1)
 * - x(t) = señal mensaje normalizada (|x(t)| <= 1)
 * - fc = frecuencia de la portadora
 * - t = tiempo
 *
 * Componentes:
 * - [1 + na * x(t)] = amplitud variable (envolvente)
 * - cos(2π * fc * t) = portadora de alta frecuencia
 *
 * El índice de modulación na determina la profundidad de modulación:
 * - na = 0: sin modulación (solo portadora)
 * - 0 < na < 1: modulación normal
 * - na = 1: modulación 100% (máxima sin distorsión)
 * - na > 1: sobremodulación (distorsión)
 *
 * Soporta funciones personalizadas para la señal mensaje x(t).
 * Usado en radio AM (530-1700 kHz), broadcasting.
 *
 * @author UAEMEX - Transmisión de Datos
 */
public class AMGenerator extends AnalogToAnalogGenerator {

    /**
     * Índice de modulación (na)
     * Debe estar entre 0 y 1 para evitar sobremodulación
     */
    private double modulationIndex;

    public AMGenerator() {
        super();
        this.carrierFrequency = 10.0;      // fc
        this.messageFrequency = 1.0;       // Frecuencia de x(t) por defecto
        this.modulationIndex = 0.8;        // na = 0.8 (80% de modulación)
    }

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        // Configurar parámetros desde el mapa
        if (params != null) {
            if (params.containsKey("carrierFrequency")) {
                carrierFrequency = (Double) params.get("carrierFrequency");
            }
            if (params.containsKey("messageFrequency")) {
                messageFrequency = (Double) params.get("messageFrequency");
            }
            if (params.containsKey("modulationIndex")) {
                modulationIndex = (Double) params.get("modulationIndex");
            }
        }

        // Validar índice de modulación
        if (modulationIndex < 0) {
            System.err.println("ADVERTENCIA: Índice de modulación negativo, usando 0.5");
            modulationIndex = 0.5;
        }
        if (modulationIndex > 1.0) {
            System.err.println("ADVERTENCIA: Sobremodulación (na > 1), puede causar distorsión");
        }

        double duration = 4.0;
        String customFunction = null;

        // Verificar si hay función personalizada
        if (params != null && params.containsKey("customFunction")) {
            customFunction = (String) params.get("customFunction");
            System.out.println("Usando función personalizada para AM: " + customFunction);
            System.out.println("Índice de modulación na = " + modulationIndex);
        }

        // Generar señal AM: s(t) = [1 + na * x(t)] * cos(2π * fc * t)
        for (int i = 0; i < SAMPLES; i++) {
            double t = i * duration / SAMPLES;

            // x(t) - Señal mensaje (normalizada entre -1 y 1)
            double messageSignal;
            if (customFunction != null && !customFunction.trim().isEmpty()) {
                try {
                    messageSignal = FunctionEvaluator.evaluate(customFunction, t);

                    // Normalizar si excede el rango [-1, 1]
                    if (Math.abs(messageSignal) > 1.0) {
                        messageSignal = messageSignal / Math.abs(messageSignal);
                    }
                } catch (Exception e) {
                    System.err.println("Error evaluando función: " + e.getMessage());
                    // Usar señal sinusoidal por defecto
                    messageSignal = Math.sin(getAngularFrequency(messageFrequency) * t);
                }
            } else {
                // Señal mensaje por defecto: sin(2π * fm * t)
                messageSignal = Math.sin(getAngularFrequency(messageFrequency) * t);
            }

            // cos(2π * fc * t) - Portadora
            double carrier = Math.cos(getAngularFrequency(carrierFrequency) * t);

            // s(t) = [1 + na * x(t)] * cos(2π * fc * t)
            double envelope = 1.0 + (modulationIndex * messageSignal);
            double modulatedSignal = envelope * carrier;

            data.add(new SignalData(t, modulatedSignal));
        }

        System.out.println("Señal AM generada con fc=" + carrierFrequency +
                " Hz, na=" + modulationIndex);

        return data;
    }

    @Override
    public String getName() {
        return "AM (Amplitude Modulation)";
    }

    @Override
    public String getDescription() {
        return "Modulación de Amplitud: s(t) = [1 + na*x(t)] * cos(2π*fc*t). " +
                "La amplitud de la portadora varía según la señal mensaje. " +
                "Índice de modulación na = 0.8 (80%). " +
                "Puede ingresar función: sin(t), cos(2*pi*t), sin(t)+cos(t), etc.";
    }

    /**
     * Establece el índice de modulación
     *
     * @param modulationIndex Valor de na (recomendado: 0 < na <= 1)
     */
    public void setModulationIndex(double modulationIndex) {
        this.modulationIndex = modulationIndex;
    }

    /**
     * Obtiene el índice de modulación actual
     *
     * @return Valor de na
     */
    public double getModulationIndex() {
        return modulationIndex;
    }

    /**
     * Calcula el porcentaje de modulación
     *
     * @return Porcentaje (0-100%)
     */
    public double getModulationPercentage() {
        return modulationIndex * 100.0;
    }

    /**
     * Verifica si hay sobremodulación
     *
     * @return true si na > 1 (sobremodulación)
     */
    public boolean isOvermodulated() {
        return modulationIndex > 1.0;
    }
}