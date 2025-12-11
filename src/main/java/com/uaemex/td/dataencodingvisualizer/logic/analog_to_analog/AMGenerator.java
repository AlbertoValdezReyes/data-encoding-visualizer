package com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import com.uaemex.td.dataencodingvisualizer.util.FunctionEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Amplitude Modulation (AM)
 * Modula la amplitud de una portadora con una señal mensaje
 * s(t) = [1 + m(t)] * c(t)
 *
 * Soporta funciones personalizadas para la señal mensaje.
 */
public class AMGenerator extends AnalogToAnalogGenerator {
    private double modulationIndex;

    public AMGenerator() {
        super();
        this.modulationIndex = 0.5;
    }

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

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

        double duration = 2.0;
        String customFunction = null;

        if (params != null && params.containsKey("customFunction")) {
            customFunction = (String) params.get("customFunction");
            System.out.println("Usando función personalizada para AM: " + customFunction);
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

            double carrier = Math.sin(getAngularFrequency(carrierFrequency) * t);
            double y = (1 + modulationIndex * message) * carrier;

            data.add(new SignalData(t, y));
        }

        return data;
    }

    @Override
    public String getName() {
        return "AM (Amplitude Modulation)";
    }

    @Override
    public String getDescription() {
        return "La amplitud de la portadora varía según la señal mensaje. " +
                "Puede ingresar una función personalizada como: sin(t), cos(2*pi*t), sin(t)+cos(t)";
    }
}