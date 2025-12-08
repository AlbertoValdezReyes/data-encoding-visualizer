package com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Amplitude Modulation (AM)
 * Modula la amplitud de una portadora con una señal mensaje
 * s(t) = [1 + m(t)] * c(t)
 */
public class AMGenerator extends AnalogToAnalogGenerator {
    private double modulationIndex;

    public AMGenerator() {
        super();
        this.modulationIndex = 0.5; // Índice de modulación (0-1)
    }

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        // Configurar parámetros
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

        // Generar señal AM
        double duration = 2.0; // 2 segundos
        for (int i = 0; i < SAMPLES; i++) {
            double t = i * duration / SAMPLES;

            // Señal mensaje (moduladora)
            double message = Math.sin(getAngularFrequency(messageFrequency) * t);

            // Señal portadora
            double carrier = Math.sin(getAngularFrequency(carrierFrequency) * t);

            // AM: [1 + m*m(t)] * c(t)
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
        return "La amplitud de la portadora varía según la señal mensaje";
    }
}