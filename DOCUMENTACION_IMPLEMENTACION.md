# Documentacion de Implementacion - Data Encoding Visualizer

## 1. Estructura de Paquetes

### 1.1 Vista General del Proyecto

```
data-encoding-visualizer/
├── src/main/java/com/uaemex/td/dataencodingvisualizer/
│   ├── App.java                    # Punto de entrada JavaFX
│   ├── Launcher.java               # Lanzador de la aplicacion
│   ├── controller/
│   │   └── MainController.java     # Controlador principal de UI
│   ├── logic/
│   │   ├── IGenerator.java         # Interfaz base para generadores
│   │   ├── digital_to_digital/     # Codificacion de Linea (8 tecnicas)
│   │   ├── digital_to_analog/      # Modulacion Digital (4 tecnicas)
│   │   ├── analog_to_analog/       # Modulacion Analogica (3 tecnicas)
│   │   └── analog_to_digital/      # Conversion A/D (2 tecnicas)
│   ├── model/
│   │   └── SignalData.java         # Modelo de datos para puntos de senal
│   └── util/
│       └── FunctionEvaluator.java  # Evaluador de funciones matematicas
├── src/main/resources/
│   └── main-view.fxml              # Interfaz grafica JavaFX
├── pom.xml                         # Configuracion Maven
└── mvnw, mvnw.cmd                  # Wrapper Maven
```

### 1.2 Descripcion de Paquetes

| Paquete | Proposito |
|---------|-----------|
| `controller` | Contiene el controlador MVC que maneja la interaccion entre la UI y la logica |
| `logic` | Implementacion de los 17 algoritmos de codificacion organizados en 4 subcategorias |
| `model` | Clases de modelo de datos (SignalData) |
| `util` | Utilidades como el evaluador de expresiones matematicas |

### 1.3 Tecnologias Utilizadas

- **Java 17**: Version de compilacion y ejecucion
- **JavaFX 21**: Framework para interfaz grafica
- **Maven**: Gestor de dependencias y construccion
- **Patron Strategy**: Implementado en `IGenerator` para intercambiabilidad de algoritmos

---

## 2. Logica de Negocio

### 2.1 Arquitectura de Generadores

El proyecto implementa el **Patron Strategy** mediante la interfaz `IGenerator`, permitiendo intercambiar algoritmos de codificacion de manera transparente.

```
IGenerator (Interfaz)
    ├── LineCodingGenerator (Clase Base Abstracta)
    │   ├── NRZLGenerator
    │   ├── NRZIGenerator
    │   ├── ManchesterGenerator
    │   ├── DifferentialManchesterGenerator
    │   ├── AMIGenerator
    │   ├── PseudoternaryGenerator
    │   ├── B8ZSGenerator
    │   └── HDB3Generator
    │
    ├── DigitalToAnalogGenerator (Clase Base Abstracta)
    │   ├── ASKGenerator
    │   ├── FSKGenerator
    │   ├── PSKGenerator
    │   └── QAMGenerator
    │
    ├── AnalogToAnalogGenerator (Clase Base Abstracta)
    │   ├── AMGenerator
    │   ├── FMGenerator
    │   └── PMGenerator
    │
    └── AnalogToDigitalGenerator (Clase Base Abstracta)
        ├── PCMGenerator
        └── DMGenerator
```

### 2.2 Algoritmos Implementados

#### 2.2.1 Digital a Digital (Codificacion de Linea)

| Tecnica | Descripcion | Algoritmo |
|---------|-------------|-----------|
| **NRZ-L** | Non-Return to Zero, Level | Bit 0 = -1V, Bit 1 = +1V |
| **NRZ-I** | Non-Return to Zero, Inverted | Bit 1 = transicion, Bit 0 = sin cambio |
| **Manchester** | IEEE 802.3 | Transicion en la mitad del bit (0: alto-bajo, 1: bajo-alto) |
| **Differential Manchester** | IEEE 802.5 (Token Ring) | Siempre transicion en medio, bit 0 = transicion al inicio |
| **AMI** | Alternate Mark Inversion | Bit 0 = 0V, Bit 1 alterna +V/-V |
| **Pseudoternary** | Inverso de AMI | Bit 1 = 0V, Bit 0 alterna +V/-V |
| **B8ZS** | Bipolar 8-Zero Substitution | AMI mejorado, reemplaza 8 ceros con patron especial |
| **HDB3** | High Density Bipolar 3 | Reemplaza 4 ceros consecutivos con patron de violacion |

#### 2.2.2 Digital a Analogico (Modulacion Digital)

| Tecnica | Formula | Parametros |
|---------|---------|------------|
| **ASK** | `s(t) = A * d(t) * cos(2*pi*fc*t)` | Frecuencia portadora, Amplitud |
| **FSK** | `s(t) = A * cos(2*pi*f_bit*t)` donde f_bit depende del bit | f1 (bit 0), f2 (bit 1) |
| **PSK/BPSK** | `s(t) = A * cos(2*pi*fc*t + phase)` | phase = 0° (bit 0), 180° (bit 1) |
| **QAM** | Combinacion de ASK en dos portadoras en cuadratura | 4-QAM implementado |

#### 2.2.3 Analogico a Analogico (Modulacion Analogica)

| Tecnica | Formula |
|---------|---------|
| **AM** | `s(t) = [1 + na*m(t)] * A * cos(2*pi*fc*t)` |
| **FM** | `s(t) = A * cos(2*pi*fc*t + kf * integral(m(t)))` |
| **PM** | `s(t) = A * sin(2*pi*fc*t + kp*m(t))` |

#### 2.2.4 Analogico a Digital (Conversion A/D)

**PCM (Pulse Code Modulation)**:
1. **Muestreo**: Cumple teorema de Nyquist (fs >= 2*fm)
2. **Cuantizacion**: L = 2^n niveles
3. **Codificacion**: Representacion binaria de cada nivel
4. **SNR Teorico**: 6.02n + 1.76 dB

**DM (Delta Modulation)**:
- 1 bit por muestra
- Bit 1: senal > aproximacion → sube delta
- Bit 0: senal < aproximacion → baja delta
- Detecta problemas de Slope Overload y Granular Noise

### 2.3 Evaluador de Funciones Matematicas

El `FunctionEvaluator` permite evaluar expresiones matematicas para senales analogicas:

**Operaciones soportadas**:
- Funciones: `sin`, `cos`, `tan`, `sqrt`, `abs`
- Operadores: `+`, `-`, `*`, `/`, `^` (potencia)
- Constantes: `pi`, `e`
- Variable: `t` (tiempo)

**Algoritmo**: Shunting Yard (conversion a notacion postfija)

**Ejemplos validos**:
- `sin(t)`
- `cos(2*pi*t)`
- `sin(t) + cos(t)`
- `2*sin(pi*t)`

### 2.4 Persistencia de Datos

**El proyecto NO implementa persistencia**:
- No hay archivos de configuracion
- No hay base de datos
- No hay serializacion de senales
- Los datos se generan en memoria y se visualizan en tiempo real

**Almacenamiento en Memoria**:
```java
Map<String, List<IGenerator>> generatorsByCategory  // Almacena los 17 generadores
List<SignalData>                                     // Puntos de senal generados
```

**Modelo de Datos (SignalData)**:
```java
private double x;  // Tiempo (segundos)
private double y;  // Amplitud (voltios)
```

### 2.5 Manejo de Excepciones

#### 2.5.1 Inicializacion del Controlador

```java
try {
    verifyFXMLComponents();        // IllegalStateException si falla
    initializeGenerators();
    setupCategoryComboBox();
    setupEventHandlers();
    setDefaultValues();
    initializeHarmonicsPanel();
    initializeParametersPanel();
} catch (Exception e) {
    System.err.println("ERROR CRITICO");
    e.printStackTrace();
    showCriticalError("Error de Inicializacion", e.getMessage());
}
```

#### 2.5.2 Generacion de Senales

```java
try {
    List<SignalData> signalData = currentGenerator.generate(input, params);
    if (signalData == null || signalData.isEmpty()) {
        showAlert("Error de Generacion", ...);
        return;
    }
    plotSignal(signalData);
} catch (Exception e) {
    System.err.println("EXCEPCION durante generacion:");
    e.printStackTrace();
    showAlert("Error de Generacion", e.getMessage(), Alert.AlertType.ERROR);
}
```

#### 2.5.3 Evaluacion de Funciones

```java
try {
    String processedFunction = preprocessFunction(function, t);
    return evaluateExpression(processedFunction);
} catch (Exception e) {
    throw new IllegalArgumentException("Error al evaluar funcion: " + e.getMessage());
}
```

#### 2.5.4 Validaciones Implementadas

| Validacion | Metodo |
|------------|--------|
| Entrada binaria | `input.matches("[01]+")` |
| Parentesis balanceados | Contador de apertura/cierre |
| Funciones validas | Lista whitelist (sin, cos, tan, sqrt, abs, pi, e) |
| Operadores consecutivos | Validacion de sintaxis |
| Parametros de Nyquist | `fs >= 2*fm` para conversion A/D |

---

## 3. Manejo de Versiones

### 3.1 Estrategia de Ramas

El proyecto utiliza una estrategia simple con una **unica rama principal**:

```
main (unica rama)
   └── Desarrollo directo en main
```

**Ramas existentes**:
- `main` - Rama principal de desarrollo
- `remotes/origin/main` - Rama remota en GitHub

### 3.2 Historial de Commits

| Hash | Autor | Fecha | Descripcion |
|------|-------|-------|-------------|
| `82179ab` | Roger_Zuriaga | Reciente | Merge branch 'main' |
| `e31f2b2` | Roger_Zuriaga | Reciente | parametros tecnicos |
| `1d6ca14` | albertovaldezreyes | 3 dias | fix dm |
| `d11d089` | albertovaldezreyes | 3 dias | add docs |
| `f2f3b75` | Roger_Zuriaga | 3 dias | Correcion en pcm y dm |
| `25ffdf9` | Roger_Zuriaga | 3 dias | ventana desplegable |
| `2718419` | Roger_Zuriaga | 8 dias | Ajuste en las graficas |
| `b8318f4` | Roger_Zuriaga | 8 dias | Menus de funciones armonicas |
| `20d0525` | albertovaldezreyes | 8 dias | fixing am |
| `780fdc5` | albertovaldezreyes | 8 dias | drafting analog input |
| `81c5f6c` | albertovaldezreyes | 11 dias | show view |
| `a5e9f48` | albertovaldezreyes | 11 dias | logic, adding digital to analog generators |
| `2a7f8e3` | albertovaldezreyes | 11 dias | replacing the default view |
| `66a7faf` | albertovaldezreyes | 11 dias | adding the view |
| `5bf904b` | albertovaldezreyes | 11 dias | drafting logic, digital to digital |
| `3cc069b` | albertovaldezreyes | 11 dias | drafting logic, analog to analog and analog to digital |
| `31b4036` | albertovaldezreyes | 11 dias | first commit |

### 3.3 Flujo de Trabajo en Git

1. **Desarrollo**: Ambos colaboradores trabajan directamente en `main`
2. **Commits**: Commits descriptivos en espanol/ingles segun la funcionalidad
3. **Sincronizacion**: Pull/Push regular con merges cuando hay cambios remotos
4. **Colaboradores**:
   - `Roger_Zuriaga` - UI, graficas, parametros tecnicos, correcciones PCM/DM
   - `albertovaldezreyes` - Logica de generadores, vistas, documentacion

### 3.4 Convencion de Commits

El proyecto sigue un estilo informal de commits:
- Commits cortos y descriptivos
- Mezcla de espanol e ingles
- Enfoque en la funcionalidad implementada

**Ejemplos de mensajes**:
- `first commit` - Commit inicial
- `drafting logic, digital to digital` - Trabajo en progreso
- `Correcion en pcm y dm` - Correccion de bugs
- `parametros tecnicos` - Nueva funcionalidad

---

## 4. Flujo de Ejecucion

```
1. Launcher.main()
       ↓
2. App.main() → App.start(Stage)
       ↓
3. FXMLLoader carga main-view.fxml
       ↓
4. MainController.initialize()
   ├── verifyFXMLComponents()
   ├── initializeGenerators() [17 generadores]
   ├── setupCategoryComboBox()
   ├── setupEventHandlers()
   ├── setDefaultValues()
   ├── initializeHarmonicsPanel()
   └── initializeParametersPanel()
       ↓
5. Usuario interactua:
   ├── Selecciona categoria → actualiza tecnicas
   ├── Selecciona tecnica → actualiza descripcion
   ├── Ajusta parametros con sliders
   └── Ingresa entrada (binaria o funcion)
       ↓
6. Click "Generar":
   ├── Validacion de entrada
   ├── Recopilacion de parametros
   ├── generator.generate(input, params)
   └── Obtiene List<SignalData>
       ↓
7. Visualizacion:
   ├── Limpia grafico anterior
   ├── Anade serie de datos
   ├── Configura escala de ejes
   └── Renderiza grafico
```

---

## 5. Parametros Configurables

### Por Categoria

| Categoria | Parametros |
|-----------|------------|
| Digital → Analogico | Frecuencia Portadora (Hz), Amplitud (V), Bit Rate (bps) |
| Analogico → Analogico | Frecuencia Portadora (Hz), Amplitud (V), Funcion personalizada |
| Analogico → Digital (PCM) | Sampling Rate, Bits per Sample |
| Analogico → Digital (DM) | Sampling Rate, Delta |

---

*Documento generado el 18 de diciembre de 2025*