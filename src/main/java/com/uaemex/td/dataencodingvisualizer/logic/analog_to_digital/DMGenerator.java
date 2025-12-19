package com.uaemex.td.dataencodingvisualizer.logic.analog_to_digital;

import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import com.uaemex.td.dataencodingvisualizer.util.FunctionEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Delta Modulation (DM)
 *
 * Delta Modulation es una forma simplificada de PCM que transmite solo 1 bit
 * por muestra, indicando si la señal subió o bajó respecto a la aproximación anterior.
 *
 * PRINCIPIO DE FUNCIONAMIENTO:
 * - La señal de entrada se compara con una aproximación escalonada
 * - Si la señal real > aproximación: se transmite "1" y la aproximación sube δ (delta)
 * - Si la señal real < aproximación: se transmite "0" y la aproximación baja δ (delta)
 *
 * VENTAJAS:
 * - Implementación simple (solo 1 bit por muestra)
 * - No requiere codificación compleja
 * - Bajo costo de hardware
 *
 * PROBLEMAS:
 *
 * 1. SLOPE OVERLOAD (Sobrecarga de pendiente):
 *    - Ocurre cuando la señal cambia más rápido que δ*fs
 *    - La aproximación no puede seguir cambios rápidos de la señal
 *    - Solución: aumentar δ o fs
 *    - Condición: |dm(t)/dt| <= δ*fs
 *
 * 2. GRANULAR NOISE (Ruido granular):
 *    - Ocurre cuando la señal cambia lentamente o es constante
 *    - La aproximación oscila alrededor del valor real
 *    - Solución: reducir δ
 *
 * COMPROMISO: δ grande reduce slope overload pero aumenta ruido granular
 *             δ pequeño reduce ruido granular pero aumenta slope overload
 *
 * SOLUCIÓN: Adaptive Delta Modulation (ADM) - ajusta δ dinámicamente
 *
 *
 * @author UAEMEX - Transmisión de Datos
 */
public class DMGenerator extends AnalogToDigitalGenerator {

    private double delta;  // Tamaño del paso δ

    public DMGenerator() {
        super();
        this.samplingRate = 32;  // Alta tasa de muestreo necesaria para DM
        this.delta = 0.15;       // Tamaño del paso delta
    }

    @Override
    public List<SignalData> generate(String input, Map<String, Object> params) {
        List<SignalData> data = new ArrayList<>();

        if (params != null) {
            if (params.containsKey("samplingRate")) {
                samplingRate = (Integer) params.get("samplingRate");
            }
            if (params.containsKey("delta")) {
                delta = (Double) params.get("delta");
            }
        }

        double duration = 2.0;  // Duración en segundos
        double sampleInterval = duration / samplingRate;
        String customFunction = null;

        if (params != null && params.containsKey("customFunction")) {
            customFunction = (String) params.get("customFunction");
        }

        // Aproximación inicial (empieza en el centro del rango)
        double approximation = 0.0;

        // Para almacenar los bits transmitidos
        StringBuilder bitStream = new StringBuilder();


        // Variables para detectar slope overload y granular noise
        int slopeOverloadCount = 0;
        int granularNoiseCount = 0;
        int consecutiveSameBits = 0;
        int lastBit = -1;

        // Guardamos el valor previo para poder dibujar el escalón vertical correctamente
        double prevApproximation = approximation;

        for (int i = 0; i < samplingRate; i++) {
            double t = i * sampleInterval;
            double tNext = (i + 1) * sampleInterval;

            // Obtener valor real de la señal
            double actualValue = evaluateSignal(customFunction, t);

            // Comparar con la aproximación y decidir el bit
            int bit;
            if (actualValue >= approximation) {
                bit = 1;
                approximation += delta;  // Subir la aproximación
            } else {
                bit = 0;
                approximation -= delta;  // Bajar la aproximación
            }

            bitStream.append(bit);

            // Detectar slope overload (código original...)
            if (bit == lastBit) {
                consecutiveSameBits++;
                if (consecutiveSameBits >= 3) {
                    slopeOverloadCount++;
                }
            } else {
                if (consecutiveSameBits == 0 && lastBit != -1) {
                    granularNoiseCount++;
                }
                consecutiveSameBits = 0;
            }
            lastBit = bit;

            // Añadimos un punto en el tiempo 't' con el valor ANTERIOR
            data.add(new SignalData(t, prevApproximation));

            // Obliga a la gráfica a dibujar una línea vertical recta
            data.add(new SignalData(t, approximation));

            // Añadimos el punto al final del intervalo con el valor NUEVO
            data.add(new SignalData(tNext, approximation));

            // Actualizamos el valor previo para la siguiente vuelta
            prevApproximation = approximation;
        }

        // Imprimir información de la modulación
        System.out.println("=== Delta Modulation (DM) ===");
        System.out.println("Tasa de muestreo: " + samplingRate + " muestras/s");
        System.out.println("Delta (δ): " + delta);
        System.out.println("Bits transmitidos: " + bitStream.toString().substring(0, Math.min(32, bitStream.length())) +
                          (bitStream.length() > 32 ? "..." : ""));
        System.out.println("Total bits: " + bitStream.length());

        if (slopeOverloadCount > samplingRate / 8) {
            System.out.println("ADVERTENCIA: Posible SLOPE OVERLOAD detectado");
            System.out.println("Solución: Aumentar δ o la tasa de muestreo");
        }
        if (granularNoiseCount > samplingRate / 4) {
            System.out.println("ADVERTENCIA: Posible RUIDO GRANULAR detectado");
            System.out.println("Solución: Reducir δ");
        }

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
        return "DM (Delta Modulation)";
    }

    @Override
    public String getDescription() {
        return "DM : Transmite 1 bit por muestra.\n" +
               "• Bit 1: señal > aproximación → aproximación sube δ\n" +
               "• Bit 0: señal < aproximación → aproximación baja δ\n" +
               "Problemas: Slope Overload (señal cambia muy rápido) y\n" +
               "Ruido Granular (señal casi constante → oscilación).\n" +
               "δ actual: " + delta + ", Muestras: " + samplingRate;
    }
}