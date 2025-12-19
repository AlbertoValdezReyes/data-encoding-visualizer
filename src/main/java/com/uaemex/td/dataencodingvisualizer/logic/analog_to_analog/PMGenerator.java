package com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import com.uaemex.td.dataencodingvisualizer.util.FunctionEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Phase Modulation (PM)
 * Modula la fase de una portadora con una señal mensaje
 * s(t) = A * sin(ωc*t + kp*m(t))
 *
 * La fase instantánea varía proporcionalmente con la señal mensaje.
 * Soporta funciones personalizadas para la señal mensaje.
 *
 * @author UAEMEX - Transmisión de Datos
 */
public class PMGenerator extends AnalogToAnalogGenerator {
    private double phaseDeviation;

    public PMGenerator() {
        super();
        this.carrierFrequency = 10.0;
        this.messageFrequency = 1.0;
        this.phaseDeviation = 2.0;
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
            if (params.containsKey("phaseDeviation")) {
                phaseDeviation = (Double) params.get("phaseDeviation");
            }
        }

        double duration = 4.0;
        String customFunction = null;

        if (params != null && params.containsKey("customFunction")) {
            customFunction = (String) params.get("customFunction");
            System.out.println("Usando función personalizada para PM: " + customFunction);
        }

        for (int i = 0; i < SAMPLES; i++) {
            double t = i * duration / SAMPLES;

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

            double carrierPhase = getAngularFrequency(carrierFrequency) * t;
            double modulatedPhase = phaseDeviation * message;
            double y = carrierAmplitude * Math.sin(carrierPhase + modulatedPhase);

            data.add(new SignalData(t, y));
        }

        return data;
    }

    @Override
    public String getName() {
        return "PM (Phase Modulation)";
    }

    @Override
    public String getDescription() {
        return "La fase de la portadora varía directamente con la amplitud de la señal mensaje. " +
                "Relacionado con FM. Puede ingresar función: sin(t), cos(2*pi*t), sin(t)+cos(t), etc.";
    }
}