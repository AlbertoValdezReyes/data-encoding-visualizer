package com.uaemex.td.dataencodingvisualizer.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Evaluador de funciones matemáticas simples.
 *
 * Permite evaluar expresiones matemáticas que contienen:
 * - Funciones trigonométricas: sin, cos, tan
 * - Constantes: pi, e
 * - Operadores: +, -, *, /, ^
 * - Variable: t (tiempo)
 *
 * Ejemplos de funciones válidas:
 * - sin(t)
 * - cos(2*pi*t)
 * - sin(t) + cos(t)
 * - 2*sin(pi*t)
 *
 * @author UAEMEX - Transmisión de Datos
 * @version 1.0
 */
public class FunctionEvaluator {

    /**
     * Evalúa una función matemática para un valor dado de t.
     *
     * @param function La expresión matemática como string
     * @param t El valor de la variable t (tiempo)
     * @return El resultado de evaluar la función
     * @throws IllegalArgumentException si la función es inválida
     */
    public static double evaluate(String function, double t) {
        if (function == null || function.trim().isEmpty()) {
            throw new IllegalArgumentException("La función no puede estar vacía");
        }

        try {
            String processedFunction = preprocessFunction(function, t);
            return evaluateExpression(processedFunction);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al evaluar la función: " + e.getMessage());
        }
    }

    /**
     * Preprocesa la función reemplazando variables y constantes.
     *
     * @param function La función original
     * @param t El valor de t
     * @return La función procesada
     */
    private static String preprocessFunction(String function, double t) {
        String processed = function.toLowerCase().trim();

        // Reemplazar constantes
        processed = processed.replace("pi", String.valueOf(Math.PI));
        processed = processed.replace("e", String.valueOf(Math.E));

        // Reemplazar variable t
        processed = processed.replace("t", String.valueOf(t));

        // Evaluar funciones trigonométricas
        processed = evaluateTrigFunctions(processed);

        return processed;
    }

    /**
     * Evalúa las funciones trigonométricas en la expresión.
     *
     * @param expression La expresión a procesar
     * @return La expresión con funciones evaluadas
     */
    private static String evaluateTrigFunctions(String expression) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < expression.length()) {
            // Buscar funciones trigonométricas
            if (expression.startsWith("sin(", i)) {
                int endIndex = findMatchingParenthesis(expression, i + 3);
                String arg = expression.substring(i + 4, endIndex);
                double value = evaluateExpression(arg);
                result.append(Math.sin(value));
                i = endIndex + 1;
            } else if (expression.startsWith("cos(", i)) {
                int endIndex = findMatchingParenthesis(expression, i + 3);
                String arg = expression.substring(i + 4, endIndex);
                double value = evaluateExpression(arg);
                result.append(Math.cos(value));
                i = endIndex + 1;
            } else if (expression.startsWith("tan(", i)) {
                int endIndex = findMatchingParenthesis(expression, i + 3);
                String arg = expression.substring(i + 4, endIndex);
                double value = evaluateExpression(arg);
                result.append(Math.tan(value));
                i = endIndex + 1;
            } else if (expression.startsWith("sqrt(", i)) {
                int endIndex = findMatchingParenthesis(expression, i + 4);
                String arg = expression.substring(i + 5, endIndex);
                double value = evaluateExpression(arg);
                result.append(Math.sqrt(value));
                i = endIndex + 1;
            } else if (expression.startsWith("abs(", i)) {
                int endIndex = findMatchingParenthesis(expression, i + 3);
                String arg = expression.substring(i + 4, endIndex);
                double value = evaluateExpression(arg);
                result.append(Math.abs(value));
                i = endIndex + 1;
            } else {
                result.append(expression.charAt(i));
                i++;
            }
        }

        return result.toString();
    }

    /**
     * Encuentra el paréntesis de cierre correspondiente.
     *
     * @param expression La expresión
     * @param startIndex El índice donde comienza el paréntesis de apertura
     * @return El índice del paréntesis de cierre
     */
    private static int findMatchingParenthesis(String expression, int startIndex) {
        int count = 1;
        int i = startIndex + 1;

        while (i < expression.length() && count > 0) {
            if (expression.charAt(i) == '(') {
                count++;
            } else if (expression.charAt(i) == ')') {
                count--;
            }
            i++;
        }

        return i - 1;
    }

    /**
     * Evalúa una expresión matemática simple (números y operadores).
     * Usa el algoritmo Shunting Yard para convertir a notación postfija.
     *
     * @param expression La expresión a evaluar
     * @return El resultado
     */
    private static double evaluateExpression(String expression) {
        expression = expression.replace(" ", "");

        if (expression.isEmpty()) {
            return 0.0;
        }

        // Intentar parsear como número simple
        try {
            return Double.parseDouble(expression);
        } catch (NumberFormatException e) {
            // Continuar con evaluación de expresión compleja
        }

        // Convertir a notación postfija y evaluar
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            // Saltar espacios
            if (c == ' ') {
                i++;
                continue;
            }

            // Si es un número (o número negativo)
            if (Character.isDigit(c) || c == '.' ||
                    (c == '-' && (i == 0 || expression.charAt(i-1) == '(' || isOperator(expression.charAt(i-1))))) {
                StringBuilder num = new StringBuilder();

                // Leer el número completo
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) ||
                                expression.charAt(i) == '.' ||
                                (expression.charAt(i) == '-' && num.length() == 0))) {
                    num.append(expression.charAt(i++));
                }

                values.push(Double.parseDouble(num.toString()));
                continue;
            }

            // Si es un paréntesis de apertura
            if (c == '(') {
                operators.push(c);
            }
            // Si es un paréntesis de cierre
            else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                if (!operators.isEmpty()) {
                    operators.pop(); // Remover '('
                }
            }
            // Si es un operador
            else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(c) <= precedence(operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }

            i++;
        }

        // Aplicar operadores restantes
        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.isEmpty() ? 0.0 : values.pop();
    }

    /**
     * Verifica si un carácter es un operador válido.
     */
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    /**
     * Retorna la precedencia de un operador.
     */
    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return 0;
        }
    }

    /**
     * Aplica un operador a dos operandos.
     */
    private static double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("División por cero");
                }
                return a / b;
            case '^':
                return Math.pow(a, b);
            default:
                return 0;
        }
    }

    /**
     * Valida que una función sea sintácticamente correcta.
     *
     * @param function La función a validar
     * @return true si es válida
     */
    public static boolean isValidFunction(String function) {
        if (function == null || function.trim().isEmpty()) {
            return false;
        }

        try {
            // Intentar evaluar con t=0
            evaluate(function, 0.0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene ejemplos de funciones válidas.
     *
     * @return Mapa con nombre y expresión de funciones de ejemplo
     */
    public static Map<String, String> getExampleFunctions() {
        Map<String, String> examples = new HashMap<>();
        examples.put("Seno simple", "sin(t)");
        examples.put("Coseno simple", "cos(t)");
        examples.put("Seno con frecuencia", "sin(2*pi*t)");
        examples.put("Suma de señales", "sin(t) + cos(2*t)");
        examples.put("Modulación de amplitud", "sin(t) * cos(10*t)");
        examples.put("Señal cuadrada aproximada", "sin(t) + sin(3*t)/3 + sin(5*t)/5");
        return examples;
    }
}