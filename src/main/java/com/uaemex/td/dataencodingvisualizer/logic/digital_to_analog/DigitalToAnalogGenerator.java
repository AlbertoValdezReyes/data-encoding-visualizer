package com.uaemex.td.dataencodingvisualizer.logic.digital_to_analog;

import com.uaemex.td.dataencodingvisualizer.logic.IGenerator;
import com.uaemex.td.dataencodingvisualizer.model.SignalData;
import java.util.Map;

/**
 * Clase abstracta base para técnicas de Digital a Analógico (Modulación Digital)
 *
 * Estas técnicas convierten datos digitales (bits) en señales analógicas
 * para su transmisión a través de medios analógicos como líneas telefónicas,
 * ondas de radio, etc.
 *
 * Técnicas implementadas:
 * - ASK (Amplitude Shift Keying): Modula la amplitud
 * - FSK (Frequency Shift Keying): Modula la frecuencia
 * - PSK (Phase Shift Keying): Modula la fase
 * - QAM (Quadrature Amplitude Modulation): Modula amplitud y fase
 *
 * Aplicaciones:
 * - Módems telefónicos (V.32, V.34, V.90)
 * - Wi-Fi (IEEE 802.11 usa QAM)
 * - Transmisión de datos por radio
 * - Cable módem
 * - DSL (Digital Subscriber Line)
 *
 * @author UAEMEX - Transmisión de Datos
 * @version 1.0
 */
public abstract class DigitalToAnalogGenerator implements IGenerator {

    /** Frecuencia de la señal portadora en Hz */
    protected double carrierFrequency;

    /** Número de muestras por bit para generar señales suaves */
    protected static final int SAMPLES_PER_BIT = 100;

    /** Amplitud de la señal portadora */
    protected double amplitude;

    /** Duración de cada bit en segundos */
    protected double bitDuration;

    /**
     * Constructor por defecto
     * Inicializa los parámetros con valores predeterminados
     */
    public DigitalToAnalogGenerator() {
        this.carrierFrequency = 5.0;    // 5 Hz por defecto
        this.amplitude = 1.0;            // Amplitud normalizada
        this.bitDuration = 1.0;          // 1 segundo por bit
    }

    /**
     * Valida que la entrada sea una cadena binaria válida (solo 0s y 1s)
     *
     * @param input Cadena a validar
     * @return true si es válida, false en caso contrario
     */
    protected boolean isValidBinaryString(String input) {
        return input != null && !input.isEmpty() && input.matches("[01]+");
    }

    /**
     * Calcula la frecuencia angular (omega = 2π * f)
     *
     * @param frequency Frecuencia en Hz
     * @return Frecuencia angular en radianes/segundo
     */
    protected double getAngularFrequency(double frequency) {
        return 2 * Math.PI * frequency;
    }

    /**
     * Convierte un carácter bit a valor entero
     *
     * @param bit Carácter '0' o '1'
     * @return 0 o 1
     */
    protected int bitToInt(char bit) {
        return bit == '1' ? 1 : 0;
    }

    /**
     * Genera una onda sinusoidal para un tiempo dado
     *
     * @param time Tiempo en segundos
     * @param frequency Frecuencia en Hz
     * @param amplitude Amplitud de la onda
     * @param phase Fase en radianes
     * @return Valor de la onda sinusoidal
     */
    protected double generateSineWave(double time, double frequency,
                                      double amplitude, double phase) {
        return amplitude * Math.sin(getAngularFrequency(frequency) * time + phase);
    }

    /**
     * Genera una onda coseno para un tiempo dado
     *
     * @param time Tiempo en segundos
     * @param frequency Frecuencia en Hz
     * @param amplitude Amplitud de la onda
     * @param phase Fase en radianes
     * @return Valor de la onda coseno
     */
    protected double generateCosineWave(double time, double frequency,
                                        double amplitude, double phase) {
        return amplitude * Math.cos(getAngularFrequency(frequency) * time + phase);
    }

    /**
     * Calcula la energía de la señal por bit
     *
     * @return Energía por bit (Eb)
     */
    protected double calculateEnergyPerBit() {
        return amplitude * amplitude * bitDuration / 2.0;
    }

    /**
     * Calcula la tasa de bits (bit rate) basada en la duración del bit
     *
     * @return Tasa de bits en bits por segundo (bps)
     */
    protected double calculateBitRate() {
        return 1.0 / bitDuration;
    }

    /**
     * Calcula la tasa de símbolos (baud rate)
     * Para modulaciones binarias (ASK, FSK, PSK): baud rate = bit rate
     *
     * @return Tasa de símbolos en baudios
     */
    protected double calculateBaudRate() {
        return calculateBitRate(); // Para modulación binaria
    }

    /**
     * Normaliza una amplitud al rango [-1, 1]
     *
     * @param value Valor a normalizar
     * @param maxValue Valor máximo del rango original
     * @return Valor normalizado
     */
    protected double normalizeAmplitude(double value, double maxValue) {
        if (maxValue == 0) return 0;
        return value / maxValue;
    }

    /**
     * Aplica filtro de transición suave entre bits (raised cosine)
     *
     * @param sample Índice de la muestra dentro del bit
     * @param totalSamples Total de muestras por bit
     * @param rollOff Factor de roll-off (0 a 1)
     * @return Factor de suavizado
     */
    protected double raisedCosineFilter(int sample, int totalSamples, double rollOff) {
        double t = (double) sample / totalSamples - 0.5;
        double numerator = Math.sin(Math.PI * t);
        double denominator = Math.PI * t * (1 - 4 * rollOff * rollOff * t * t);

        if (Math.abs(denominator) < 1e-10) {
            return 1.0;
        }
        return numerator / denominator;
    }

    /**
     * Calcula la relación señal a ruido (SNR) teórica
     *
     * @param noiseLevel Nivel de ruido
     * @return SNR en dB
     */
    protected double calculateSNR(double noiseLevel) {
        if (noiseLevel <= 0) return Double.POSITIVE_INFINITY;
        double signalPower = amplitude * amplitude / 2.0;
        double noisePower = noiseLevel * noiseLevel;
        return 10 * Math.log10(signalPower / noisePower);
    }

    // ==================== GETTERS Y SETTERS ====================

    /**
     * Establece la frecuencia de la portadora
     *
     * @param carrierFrequency Frecuencia en Hz (debe ser > 0)
     * @throws IllegalArgumentException si la frecuencia no es positiva
     */
    public void setCarrierFrequency(double carrierFrequency) {
        if (carrierFrequency <= 0) {
            throw new IllegalArgumentException("La frecuencia debe ser positiva");
        }
        this.carrierFrequency = carrierFrequency;
    }

    /**
     * Obtiene la frecuencia de la portadora
     *
     * @return Frecuencia en Hz
     */
    public double getCarrierFrequency() {
        return carrierFrequency;
    }

    /**
     * Establece la amplitud de la señal
     *
     * @param amplitude Amplitud (debe ser > 0)
     * @throws IllegalArgumentException si la amplitud no es positiva
     */
    public void setAmplitude(double amplitude) {
        if (amplitude <= 0) {
            throw new IllegalArgumentException("La amplitud debe ser positiva");
        }
        this.amplitude = amplitude;
    }

    /**
     * Obtiene la amplitud de la señal
     *
     * @return Amplitud
     */
    public double getAmplitude() {
        return amplitude;
    }

    /**
     * Establece la duración de cada bit
     *
     * @param bitDuration Duración en segundos (debe ser > 0)
     * @throws IllegalArgumentException si la duración no es positiva
     */
    public void setBitDuration(double bitDuration) {
        if (bitDuration <= 0) {
            throw new IllegalArgumentException("La duración debe ser positiva");
        }
        this.bitDuration = bitDuration;
    }

    /**
     * Obtiene la duración de cada bit
     *
     * @return Duración en segundos
     */
    public double getBitDuration() {
        return bitDuration;
    }

    /**
     * Obtiene el número de muestras por bit
     *
     * @return Número de muestras
     */
    public int getSamplesPerBit() {
        return SAMPLES_PER_BIT;
    }

    // ==================== IMPLEMENTACIONES POR DEFECTO ====================

    @Override
    public String getCategory() {
        return "Digital → Analógico (Modulación)";
    }

    @Override
    public boolean requiresBinaryInput() {
        return true; // Siempre requiere entrada binaria
    }

    @Override
    public String getExampleInput() {
        return "10110010"; // Ejemplo de 8 bits
    }

    @Override
    public boolean validateInput(String input) {
        if (!isValidBinaryString(input)) {
            return false;
        }
        // Verificar longitud razonable
        return input.length() >= getMinimumInputLength() &&
                input.length() <= getMaximumInputLength();
    }

    @Override
    public int getMinimumInputLength() {
        return 4; // Mínimo 4 bits
    }

    @Override
    public int getMaximumInputLength() {
        return 32; // Máximo 32 bits para buena visualización
    }

    @Override
    public Map<String, String> getConfigurableParameters() {
        return Map.of(
                "carrierFrequency", "Frecuencia de la portadora (Hz)",
                "amplitude", "Amplitud de la señal",
                "bitDuration", "Duración de cada bit (s)"
        );
    }

    @Override
    public double calculateBitRate(double symbolRate) {
        // Para modulación binaria simple (ASK, FSK, BPSK)
        return symbolRate; // 1 bit por símbolo
    }

    @Override
    public double calculateBandwidth(double bitRate) {
        // Ancho de banda aproximado (depende de la técnica específica)
        // Para modulación digital: BW ≈ bit rate
        return bitRate;
    }

    /**
     * Calcula el ancho de banda de Nyquist
     *
     * @return Ancho de banda mínimo en Hz
     */
    protected double calculateNyquistBandwidth() {
        return calculateBitRate() / 2.0;
    }

    /**
     * Valida que la frecuencia de la portadora sea suficientemente alta
     * Se recomienda que fc >= 2 * bitRate
     *
     * @return true si la frecuencia es válida
     */
    protected boolean validateCarrierFrequency() {
        return carrierFrequency >= 2 * calculateBitRate();
    }

    /**
     * Obtiene información técnica sobre la modulación
     *
     * @return Mapa con parámetros técnicos
     */
    public Map<String, Object> getTechnicalInfo() {
        return Map.of(
                "Frecuencia Portadora", carrierFrequency + " Hz",
                "Amplitud", amplitude + " V",
                "Bit Rate", String.format("%.2f bps", calculateBitRate()),
                "Baud Rate", String.format("%.2f baudios", calculateBaudRate()),
                "Ancho de Banda Nyquist", String.format("%.2f Hz", calculateNyquistBandwidth()),
                "Energía por Bit", String.format("%.4f J", calculateEnergyPerBit()),
                "Muestras por Bit", SAMPLES_PER_BIT
        );
    }

    /**
     * Convierte una cadena binaria a array de enteros
     *
     * @param binaryString Cadena binaria
     * @return Array de 0s y 1s
     */
    protected int[] binaryStringToArray(String binaryString) {
        int[] bits = new int[binaryString.length()];
        for (int i = 0; i < binaryString.length(); i++) {
            bits[i] = bitToInt(binaryString.charAt(i));
        }
        return bits;
    }

    /**
     * Calcula la probabilidad de error de bit teórica (BER)
     *
     * @param ebNo Relación Eb/N0 en dB
     * @return Probabilidad de error
     */
    protected double calculateBER(double ebNo) {
        // Aproximación para BPSK/QPSK en canal AWGN
        double ebNoLinear = Math.pow(10, ebNo / 10.0);
        return 0.5 * erfc(Math.sqrt(ebNoLinear));
    }

    /**
     * Función complementaria de error (erfc)
     *
     * @param x Valor de entrada
     * @return erfc(x)
     */
    private double erfc(double x) {
        // Aproximación de erfc usando serie de Taylor
        double t = 1.0 / (1.0 + 0.5 * Math.abs(x));
        double tau = t * Math.exp(-x * x - 1.26551223 +
                t * (1.00002368 +
                        t * (0.37409196 +
                                t * (0.09678418 +
                                        t * (-0.18628806 +
                                                t * (0.27886807 +
                                                        t * (-1.13520398 +
                                                                t * (1.48851587 +
                                                                        t * (-0.82215223 +
                                                                                t * 0.17087277)))))))));
        return x >= 0 ? tau : 2.0 - tau;
    }

    /**
     * Genera ruido blanco gaussiano aditivo (AWGN)
     *
     * @param mean Media del ruido
     * @param stdDev Desviación estándar
     * @return Muestra de ruido
     */
    protected double generateAWGN(double mean, double stdDev) {
        // Método Box-Muller para generar ruido gaussiano
        double u1 = Math.random();
        double u2 = Math.random();
        double z0 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
        return mean + stdDev * z0;
    }
}