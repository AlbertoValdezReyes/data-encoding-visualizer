package com.uaemex.td.dataencodingvisualizer.model;

/**
 * Clase modelo que representa un punto en la señal
 * Contiene las coordenadas X (tiempo) e Y (amplitud/voltaje)
 *
 * Esta clase es fundamental para la visualización de señales,
 * ya que cada punto de la señal se representa como un objeto SignalData
 *
 * @author UAEMEX - Transmisión de Datos
 * @version 1.0
 */
public class SignalData {
    private double x;  // Coordenada X (tiempo en segundos)
    private double y;  // Coordenada Y (amplitud en voltios)

    /**
     * Constructor sin parámetros
     * Inicializa x e y en 0.0
     */
    public SignalData() {
        this.x = 0.0;
        this.y = 0.0;
    }

    /**
     * Constructor con parámetros
     * @param x Coordenada X (tiempo)
     * @param y Coordenada Y (amplitud)
     */
    public SignalData(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Obtiene la coordenada X (tiempo)
     * @return valor de x
     */
    public double getX() {
        return x;
    }

    /**
     * Establece la coordenada X (tiempo)
     * @param x nuevo valor de x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Obtiene la coordenada Y (amplitud)
     * @return valor de y
     */
    public double getY() {
        return y;
    }

    /**
     * Establece la coordenada Y (amplitud)
     * @param y nuevo valor de y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Compara este objeto con otro
     * @param obj objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SignalData that = (SignalData) obj;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    /**
     * Genera código hash para el objeto
     * @return código hash
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Representa el objeto como cadena
     * @return representación en texto del punto
     */
    @Override
    public String toString() {
        return String.format("SignalData{x=%.4f, y=%.4f}", x, y);
    }

    /**
     * Crea una copia del objeto
     * @return nueva instancia con los mismos valores
     */
    public SignalData copy() {
        return new SignalData(this.x, this.y);
    }

    /**
     * Calcula la distancia euclidiana entre dos puntos
     * @param other otro punto
     * @return distancia entre los puntos
     */
    public double distanceTo(SignalData other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Interpola linealmente entre dos puntos
     * @param other punto destino
     * @param t factor de interpolación (0.0 a 1.0)
     * @return nuevo punto interpolado
     */
    public SignalData interpolate(SignalData other, double t) {
        double newX = this.x + (other.x - this.x) * t;
        double newY = this.y + (other.y - this.y) * t;
        return new SignalData(newX, newY);
    }

    /**
     * Normaliza el valor Y entre -1 y 1
     * @param minY valor mínimo original
     * @param maxY valor máximo original
     */
    public void normalize(double minY, double maxY) {
        if (maxY != minY) {
            this.y = 2 * ((this.y - minY) / (maxY - minY)) - 1;
        }
    }

    /**
     * Escala el valor Y por un factor
     * @param factor factor de escala
     */
    public void scale(double factor) {
        this.y *= factor;
    }

    /**
     * Desplaza el punto en el eje X
     * @param offset desplazamiento en X
     */
    public void shiftX(double offset) {
        this.x += offset;
    }

    /**
     * Desplaza el punto en el eje Y
     * @param offset desplazamiento en Y
     */
    public void shiftY(double offset) {
        this.y += offset;
    }
}