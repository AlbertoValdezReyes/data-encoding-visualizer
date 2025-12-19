package com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import com.uaemex.td.dataencodingvisualizer.util.FunctionEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Frequency Modulation (FM)
 * Modula la frecuencia de una portadora con la señal mensaje
 * La frecuencia instantánea varía con la amplitud del mensaje
 * f(t) = fc + Δf * m(t)
 *
 * Soporta funciones personalizadas para la señal mensaje.
 * Usado en radio FM (88-108 MHz), televisión y comunicaciones.
 *
 * @author UAEMEX - Transmisión de Datos
 */
public class FMGenerator extends AnalogToAnalogGenerator {
    private double frequencyDeviation;

    public FMGenerator() {
        super();
        this.carrierFrequency = 10.0;
        this.messageFrequency = 1.0;
        this.frequencyDeviation = 3.0;
    }

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (params != null) {
            if (params.containsKey("carrierFrequency")) {
                carrierFrequency = (Double) params.get("carrierFrequency");
            }
            if (params.containsKey("amplitude")) {
                carrierAmplitude = (Double) params.get("amplitude");
            }
            if (params.containsKey("messageFrequency")) {
                messageFrequency = (Double) params.get("messageFrequency");
            }
            if (params.containsKey("frequencyDeviation")) {
                frequencyDeviation = (Double) params.get("frequencyDeviation");
            }
        }

        double duration = 4.0;
        double phase = 0.0;
        String customFunction = null;

        if (params != null && params.containsKey("customFunction")) {
            customFunction = (String) params.get("customFunction");
            System.out.println("Usando función personalizada para FM: " + customFunction);
        }

        for (int i = 0; i < SAMPLES; i++) {
            double t = i * duration / SAMPLES;
            double dt = duration / SAMPLES;

            double message;
            if (customFunction != null && !customFunction.trim().isEmpty()) {
                try {
                    message = FunctionEvaluator.evaluate(customFunction, t);
                } catch (Exception e) {
                    System.err.println("Error evaluando función: " + e.getMessage());
                    message = Math.sin(getAngularFrequency(messageFrequency) * t);
                }
            } else {
                message = Math.sin(getAngularFrequency(messageFrequency) * t);
            }

            double instantFreq = carrierFrequency + frequencyDeviation * message;
            phase += getAngularFrequency(instantFreq) * dt;
            double y = carrierAmplitude * Math.sin(phase);

            data.add(new SignalData(t, y));
        }

        return data;
    }

    @Override
    public String getName() {
        return "FM (Frequency Modulation)";
    }

    @Override
    public String getDescription() {
        return "La frecuencia de la portadora varía según la amplitud del mensaje: f(t)=fc+Δf*m(t). " +
                "Inmune al ruido de amplitud. Puede ingresar función: sin(t), cos(2*pi*t), etc.";
    }
}