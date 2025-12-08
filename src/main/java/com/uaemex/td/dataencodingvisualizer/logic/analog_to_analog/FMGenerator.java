package com.uaemex.td.dataencodingvisualizer.logic.analog_to_analog;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Frequency Modulation (FM)
 * Modula la frecuencia de la portadora con la señal mensaje
 * La frecuencia instantánea varía proporcionalmente
 * con la amplitud de la señal moduladora
 * f(t) = fc + Δf * m(t)
 * Usado en radio FM, televisión
 */
public class FMGenerator extends AnalogToAnalogGenerator {
    private double frequencyDeviation; // Δf - desviación de frecuencia

    public FMGenerator() {
        super();
        this.carrierFrequency = 10.0;     // Frecuencia portadora
        this.messageFrequency = 1.0;      // Frecuencia del mensaje
        this.frequencyDeviation = 3.0;    // Desviación máxima
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
            if (params.containsKey("frequencyDeviation")) {
                frequencyDeviation = (Double) params.get("frequencyDeviation");
            }
        }

        double duration = 2.0; // 2 segundos de señal
        double phase = 0.0;    // Fase acumulada

        for (int i = 0; i < SAMPLES; i++) {
            double t = i * duration / SAMPLES;
            double dt = duration / SAMPLES;

            // Señal mensaje (moduladora): sinusoidal
            double message = Math.sin(getAngularFrequency(messageFrequency) * t);

            // Frecuencia instantánea: fc + Δf * m(t)
            double instantFreq = carrierFrequency + frequencyDeviation * message;

            // Integrar la fase: φ(t) = ∫ 2π*f(t) dt
            phase += getAngularFrequency(instantFreq) * dt;

            // Señal FM: A * sin(φ(t))
            double y = Math.sin(phase);

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
        return "La frecuencia de la portadora varía según la amplitud del mensaje: f(t)=fc+Δf*m(t). Inmune al ruido de amplitud";
    }
}
