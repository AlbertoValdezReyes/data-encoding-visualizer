package com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Phase Modulation (PM)
 * Modula la fase de una portadora con una señal mensaje
 * s(t) = A * sin(ωc*t + kp*m(t))
 */
public class PMGenerator extends AnalogToAnalogGenerator {
    private double phaseDeviation;

    public PMGenerator() {
        super();
        this.phaseDeviation = 2.0; // Desviación de fase (radianes)
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
            if (params.containsKey("phaseDeviation")) {
                phaseDeviation = (Double) params.get("phaseDeviation");
            }
        }

        double duration = 2.0;

        for (int i = 0; i < SAMPLES; i++) {
            double t = i * duration / SAMPLES;

            // Señal mensaje (moduladora)
            double message = Math.sin(getAngularFrequency(messageFrequency) * t);

            // PM: A * sin(ωc*t + kp*m(t))
            double carrierPhase = getAngularFrequency(carrierFrequency) * t;
            double modulatedPhase = phaseDeviation * message;

            double y = Math.sin(carrierPhase + modulatedPhase);

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
        return "La fase de la portadora varía directamente con la amplitud de la señal mensaje";
    }
}