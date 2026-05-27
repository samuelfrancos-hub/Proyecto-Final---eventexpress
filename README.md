# EventExpress

Plataforma de Gestión de Eventos y Venta de Entradas

---

## Integrantes del grupo

- **Samuel Franco Salazar**
- **Juan José López Cuartas**
- **Daniel Gil Fino**

---

## Descripción del proyecto

**EventExpress** es una aplicación de escritorio desarrollada en **Java 17** con **JavaFX** que simula una plataforma de gestión de eventos y venta de entradas. El sistema permite administrar eventos de distintas categorías (conciertos, teatro y conferencias), gestionar recintos con sus zonas y asientos, realizar todo el flujo de compra de entradas (selección de asientos, servicios adicionales, métodos de pago y confirmación), y generar reportes administrativos exportables a PDF y Excel.

La aplicación distingue dos roles de usuario:

- **Cliente:** explora eventos publicados, selecciona asientos, agrega servicios adicionales (VIP, seguro, parqueadero, etc.), elige un método de pago y confirma su compra.
- **Administrador:** publica/cancela eventos, audita operaciones del sistema y genera reportes de ventas, ocupación, ingresos por servicios y tasa de cancelación.

El proyecto se construyó como ejercicio académico para aplicar **patrones de diseño** y **principios SOLID** sobre un caso de negocio realista. La capa de presentación usa JavaFX (FXML + CSS), la lógica de negocio se organiza en gestores y una fachada, y el dominio se modela con clases ricas que encapsulan su propio comportamiento.

### Estructura general del código

```
src/main/java/co/edu/uniquindio/eventexpress/
├── Main.java                  # Punto de entrada (Application de JavaFX)
├── config/                    # Configuración global (Singleton)
├── controller/                # Controladores JavaFX y gestión de sesión
├── modelo/                    # Entidades de dominio e interfaces base
│   └── enums/                 # Enumeraciones del dominio
├── fabrica/                   # Factory Method de eventos
├── decorador/                 # Decorator de servicios sobre entradas
├── strategy/                  # Strategy de métodos de pago
├── state/                     # State de compra y de evento
├── observer/                  # Observer de notificaciones
├── comando/                   # Command de operaciones auditables
├── adaptador/                 # Adapter de exportación de reportes
├── template/                  # Template Method de generación de reportes
├── facade/                    # Facade que orquesta el flujo de compra
└── gestor/                    # Gestores de negocio (asientos, pagos, etc.)
```

---

## Requisitos previos

- **Java Development Kit (JDK) 17** o superior.
- **Apache Maven 3.8+**.
- No es necesario instalar JavaFX por separado: Maven descarga las dependencias automáticamente (`javafx-controls`, `javafx-fxml`, `javafx-graphics`, `javafx-base` versión 17.0.10).

Para verificar las versiones instaladas:

```bash
java -version    # debe mostrar 17 (o superior)
mvn -version
```

---

## Compilación y ejecución

El proyecto está configurado con el **plugin `javafx-maven-plugin`**, por lo que **debe inicializarse desde Maven** usando el objetivo `javafx:run` (no se ejecuta con un simple `java -jar`, ya que JavaFX necesita los módulos en el `module-path`).

### 1. Clonar / ubicarse en el proyecto

```bash
cd eventexpress
```

### 2. Compilar el proyecto

```bash
mvn clean compile
```

### 3. Ejecutar la aplicación

```bash
mvn clean javafx:run
```

La clase principal configurada en el plugin es `co.edu.uniquindio.eventexpress.Main`, que carga la pantalla de inicio de sesión (`login.fxml`).

### 4. (Opcional) Ejecutar las pruebas unitarias

El proyecto incluye **15 clases de prueba** con **88 pruebas** basadas en **JUnit 5** que cubren cada patrón implementado:

```bash
mvn test
```

### Usuarios de prueba

La sesión se inicializa con datos de ejemplo. Puede ingresar con:

| Rol           | Correo            | Contraseña |
|---------------|-------------------|------------|
| Administrador | `admin@event.co`  | `admin123` |
| Cliente       | `carlos@mail.co`  | `pass123`  |
| Cliente       | `laura@mail.co`   | `pass456`  |

---

## Patrones de diseño implementados

A continuación se documenta cada patrón indicando el **requisito que resuelve**, el **problema**, el **propósito** y la **solución** adoptada en el proyecto, acompañados de un fragmento de código representativo.

---

### 1. Singleton — `ConfiguracionPlataforma`

- **Requisito que resuelve:** la plataforma debe tener una única configuración global (nombre, versión, política y porcentaje de reembolso, tiempo límite de cancelación, moneda) accesible desde cualquier capa.
- **Problema:** si cada parte del sistema creara su propia instancia de configuración, existirían valores inconsistentes y no habría una fuente única de verdad para las políticas del negocio.
- **Propósito:** garantizar una sola instancia de la configuración y ofrecer un punto de acceso global a ella.
- **Solución:** constructor privado e instancia estática única obtenida mediante `getInstancia()` con *double-checked locking* (`volatile` + bloque `synchronized`) para que sea segura frente a múltiples hilos.

```java
public class ConfiguracionPlataforma {

    private static volatile ConfiguracionPlataforma instancia;

    private ConfiguracionPlataforma() {
        if (instancia != null) {
            throw new IllegalStateException("ConfiguracionPlataforma ya ha sido inicializada. Use getInstancia().");
        }
        // valores por defecto de la plataforma...
    }

    public static ConfiguracionPlataforma getInstancia() {
        if (instancia == null) {
            synchronized (ConfiguracionPlataforma.class) {
                if (instancia == null) {
                    instancia = new ConfiguracionPlataforma();
                }
            }
        }
        return instancia;
    }
}
```

> El mismo patrón se aplica en `SessionManager`, que mantiene la única sesión activa, los usuarios, los recintos y los eventos cargados.

---

### 2. Factory Method — `FabricaEvento`

- **Requisito que resuelve:** crear eventos de distintas categorías (concierto, teatro, conferencia), cada uno con atributos propios, sin acoplar al cliente a las clases concretas.
- **Problema:** instanciar directamente `new Concierto(...)`, `new Teatro(...)`, etc. dispersa por el código la lógica de construcción y obliga a modificar muchos puntos cuando aparece una nueva categoría.
- **Propósito:** centralizar la creación de objetos `Evento` delegando en una fábrica que decide qué subclase concreta instanciar.
- **Solución:** `FabricaEvento.crearEvento(...)` recibe la `CategoriaEvento` y un mapa de datos específicos, y devuelve la subclase adecuada de `Evento`.

```java
public static Evento crearEvento(CategoriaEvento categoria, String idEvento, String nombre,
                                 String descripcion, String ciudad, LocalDateTime fechaHora,
                                 Recinto recinto, Map<String, Object> datosEspecificos) {
    switch (categoria) {
        case CONCIERTO:   return crearConcierto(...);
        case TEATRO:      return crearTeatro(...);
        case CONFERENCIA: return crearConferencia(...);
        default:
            throw new IllegalArgumentException("Categoría de evento desconocida: " + categoria);
    }
}
```

---

### 3. Builder — `Compra.Builder`

- **Requisito que resuelve:** construir una `Compra`, que tiene muchos atributos opcionales (entradas, pago, estado, observadores, observaciones) y dos obligatorios (usuario y evento).
- **Problema:** un constructor con muchos parámetros (constructor telescópico) es difícil de leer, propenso a errores de orden y no valida combinaciones inválidas.
- **Propósito:** separar la construcción de un objeto complejo de su representación, permitiendo crearlo paso a paso de forma legible y validada.
- **Solución:** clase interna `Builder` con API fluida (encadenable) y validación en `build()`.

```java
Compra compra = Compra.builder()
        .idCompra(UUID.randomUUID().toString())
        .usuario(usuario)
        .evento(evento)
        .estado(new EstadoCreada())
        .build();

// dentro de Builder.build():
public Compra build() {
    if (usuario == null) throw new IllegalStateException("La compra requiere un usuario");
    if (evento == null)  throw new IllegalStateException("La compra requiere un evento");
    return new Compra(idCompra, usuario, evento, fechaCreacion, entradas, pago, estado, observadores, observaciones);
}
```

---

### 4. Prototype — `Zona` y `Asiento`

- **Requisito que resuelve:** duplicar zonas completas (con sus asientos) para reutilizar la configuración de un recinto cambiando solo el precio o la capacidad.
- **Problema:** copiar manualmente una zona y todos sus asientos es repetitivo y propenso a olvidar campos o compartir referencias por error.
- **Propósito:** crear nuevos objetos clonando un prototipo existente en lugar de construirlos desde cero.
- **Solución:** `Zona` y `Asiento` implementan `Cloneable` y sobrescriben `clone()` realizando **copia profunda** (la zona clona también sus asientos). Métodos de conveniencia como `clonarConPrecio(...)` y `clonarConCapacidad(...)` facilitan las variantes.

```java
@Override
public Zona clone() {
    try {
        Zona copia = (Zona) super.clone();
        List<Asiento> asientosClonados = new ArrayList<>();
        for (Asiento asiento : this.asientos) {
            asientosClonados.add(asiento.clone()); // copia profunda
        }
        copia.asientos = asientosClonados;
        return copia;
    } catch (CloneNotSupportedException e) {
        throw new AssertionError(e);
    }
}

public Zona clonarConPrecio(double nuevoPrecio) {
    Zona copia = this.clone();
    copia.setPrecioBase(nuevoPrecio);
    return copia;
}
```

---

### 5. Composite — `IComponenteRecinto` (`Recinto`, `Zona`, `Asiento`)

- **Requisito que resuelve:** consultar la capacidad total y los lugares disponibles de un recinto, tratando de forma uniforme al recinto completo, a una zona o a un asiento individual.
- **Problema:** la estructura es jerárquica (recinto → zonas → asientos); recorrerla con código distinto para cada nivel rompe la uniformidad y duplica lógica de agregación.
- **Propósito:** componer objetos en estructuras de árbol y permitir tratar de igual manera a los objetos individuales (hojas) y a sus contenedores (compuestos).
- **Solución:** una interfaz común `IComponenteRecinto` con `consultarCapacidad()` y `consultarDisponibles()`. `Asiento` es la **hoja** (devuelve 1/0) y `Zona`/`Recinto` son **compuestos** que suman los resultados de sus hijos.

```java
public interface IComponenteRecinto {
    int consultarCapacidad();
    int consultarDisponibles();
}

// Recinto (compuesto): agrega lo que reportan sus zonas
@Override
public int consultarDisponibles() {
    int disponibles = 0;
    for (Zona zona : zonas) {
        disponibles += zona.consultarDisponibles();
    }
    return disponibles;
}

// Asiento (hoja)
@Override
public int consultarDisponibles() {
    return this.estado == EstadoAsiento.DISPONIBLE ? 1 : 0;
}
```

---

### 6. Decorator — `EntradaDecorador` (servicios adicionales)

- **Requisito que resuelve:** añadir servicios opcionales a una entrada (VIP, seguro de cancelación, merchandising, parqueadero, acceso preferencial) que incrementan su precio y descripción, en cualquier combinación.
- **Problema:** crear una subclase por cada combinación posible de servicios produce una explosión combinatoria de clases inmanejable.
- **Propósito:** agregar responsabilidades a un objeto de forma dinámica, envolviéndolo, como alternativa flexible a la herencia.
- **Solución:** `EntradaDecorador` implementa `IEntrada` y envuelve otra `IEntrada`. Cada decorador concreto (`VIPDecorador`, `SeguroCancelacionDecorador`, etc.) delega en el objeto envuelto y le suma su costo/descripción. Los decoradores se pueden apilar.

```java
public abstract class EntradaDecorador implements IEntrada {
    protected IEntrada entradaEnvuelta;
    protected EntradaDecorador(IEntrada entradaEnvuelta) {
        this.entradaEnvuelta = entradaEnvuelta;
    }
}

public class VIPDecorador extends EntradaDecorador {
    private static final double COSTO_VIP = 50000.0;

    @Override
    public double calcularPrecioFinal() {
        return super.calcularPrecioFinal() + costoAdicional; // suma sobre lo envuelto
    }

    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion() + " + VIP";
    }
}
```

---

### 7. Strategy — `IMetodoPago` (medios de pago)

- **Requisito que resuelve:** permitir pagar una compra con diferentes medios (tarjeta de crédito, PSE, efectivo), cada uno con su propia validación y procesamiento.
- **Problema:** resolver el medio de pago con condicionales (`if/else` o `switch`) dentro de la lógica de cobro la vuelve rígida y difícil de extender.
- **Propósito:** definir una familia de algoritmos, encapsular cada uno y hacerlos intercambiables en tiempo de ejecución.
- **Solución:** la interfaz `IMetodoPago` define `procesarPago`, `validar` y `getTipo`. Cada estrategia concreta (`PagoTarjetaCredito`, `PagoPSE`, `PagoEfectivo`) la implementa. La clase `Pago` actúa como **contexto** y delega en la estrategia seleccionada.

```java
public interface IMetodoPago {
    boolean procesarPago(double monto);
    boolean validar();
    TipoMetodoPago getTipo();
}

// Contexto: Pago delega en la estrategia
private IMetodoPago metodo;
public boolean ejecutar() {
    return metodo != null && metodo.procesarPago(monto);
}
```

---

### 8. State — `IEstadoCompra` e `IEstadoEvento`

- **Requisito que resuelve:** una compra y un evento cambian de comportamiento según su estado (una compra creada puede pagarse pero no reembolsarse; un evento en borrador puede publicarse, etc.).
- **Problema:** controlar las transiciones con grandes bloques condicionales sobre un atributo "estado" produce código frágil que se rompe al añadir estados.
- **Propósito:** permitir que un objeto altere su comportamiento cuando cambia su estado interno, como si cambiara de clase.
- **Solución:** una interfaz de estado (`IEstadoCompra` con `pagar`, `confirmar`, `cancelar`, `reembolsar`, `registrarIncidencia`) con una clase por estado (`EstadoCreada`, `EstadoPagada`, `EstadoConfirmada`, `EstadoCancelada`, `EstadoReembolsada`, `EstadoIncidencia`). Cada estado decide la transición válida y lanza excepción ante operaciones no permitidas. Existe un esquema análogo para el evento (`EstadoBorrador`, `EstadoPublicado`, `EstadoPausado`, `EstadoCancelado`, `EstadoFinalizado`).

```java
public class EstadoCreada implements IEstadoCompra {
    @Override
    public void pagar(Compra compra) {
        compra.cambiarEstado(new EstadoPagada());   // transición válida
        compra.notificar("COMPRA_PAGADA", compra);
    }
    @Override
    public void reembolsar(Compra compra) {
        throw new IllegalStateException("No se puede reembolsar una compra que no ha sido pagada");
    }
}
```

---

### 9. Observer — `IObservador` (notificaciones)

- **Requisito que resuelve:** notificar automáticamente a varios interesados cuando ocurre un cambio en una compra o evento (correo al cliente, notificación en la app, registro de incidencias).
- **Problema:** que el objeto que cambia conozca y llame directamente a cada notificador lo acopla a ellos e impide agregar o quitar interesados sin modificarlo.
- **Propósito:** definir una dependencia uno-a-muchos de modo que, al cambiar de estado un objeto (sujeto), todos sus dependientes (observadores) sean notificados.
- **Solución:** la interfaz `IObservador` define `actualizar(evento, datos)`. `Compra` y `Evento` actúan como **sujetos** (mantienen la lista de observadores y exponen `notificar`). Los observadores concretos son `NotificadorCorreo`, `NotificadorApp` y `RegistradorIncidencias`.

```java
public interface IObservador {
    void actualizar(String evento, Object datos);
}

// Sujeto (Compra/Evento): recorre y notifica
public void notificar(String tipoEvento, Object datos) {
    for (IObservador observador : observadores) {
        observador.actualizar(tipoEvento, datos);
    }
}
```

---

### 10. Command — `IComando` y `GestorComandos`

- **Requisito que resuelve:** ejecutar y **auditar** operaciones administrativas (bloquear asiento, cancelar compra, publicar evento, reembolsar) guardando un historial con descripción y fecha.
- **Problema:** ejecutar estas acciones directamente impide registrarlas, agruparlas en lotes o llevar un historial uniforme.
- **Propósito:** encapsular una solicitud como un objeto, permitiendo parametrizar, encolar y registrar las operaciones.
- **Solución:** la interfaz `IComando` define `ejecutar`, `obtenerDescripcion` y `obtenerFechaEjecucion`. Cada comando concreto encapsula su acción, y `GestorComandos` actúa como **invocador**, ejecutándolos y almacenándolos en un historial auditable.

```java
public interface IComando {
    void ejecutar();
    String obtenerDescripcion();
    LocalDateTime obtenerFechaEjecucion();
}

// Invocador: ejecuta y audita
public void ejecutar(IComando comando) {
    if (comando == null) return;
    comando.ejecutar();
    historial.add(comando);
}
```

---

### 11. Adapter — `IGeneradorReporte` (`AdaptadorPDFBox`, `AdaptadorPOI`)

- **Requisito que resuelve:** exportar reportes a PDF y a Excel reutilizando librerías externas (**Apache PDFBox** y **Apache POI**) cuyas APIs son completamente distintas entre sí.
- **Problema:** las librerías externas tienen interfaces incompatibles con lo que el sistema necesita; usarlas directamente acopla la lógica de reportes a una librería concreta.
- **Propósito:** convertir la interfaz de una clase en otra que el cliente espera, permitiendo que clases con interfaces incompatibles trabajen juntas.
- **Solución:** una interfaz propia `IGeneradorReporte` con `exportarDatos(...)`. Cada adaptador (`AdaptadorPDFBox`, `AdaptadorPOI`) traduce esa llamada a la API específica de la librería que envuelve.

```java
public interface IGeneradorReporte {
    void exportarDatos(List<Object[]> datos, String rutaArchivo) throws IOException;
    String getFormatoSoportado();
}

// AdaptadorPDFBox traduce a la API de PDFBox
public class AdaptadorPDFBox implements IGeneradorReporte {
    @Override
    public void exportarDatos(List<Object[]> datos, String rutaArchivo) throws IOException {
        try (PDDocument documento = new PDDocument()) {
            // ... uso de PDPage, PDPageContentStream, etc.
            documento.save(rutaArchivo);
        }
    }
}
```

---

### 12. Template Method — `GeneradorReporteBase`

- **Requisito que resuelve:** todos los reportes (ventas, ocupación por zona, ingresos por servicios, tasa de cancelación) siguen los mismos pasos: obtener datos → filtrar por fechas → calcular métricas → formatear → exportar; solo cambia el contenido de cada paso.
- **Problema:** repetir el esqueleto del algoritmo en cada reporte duplica código y permite que el orden o el manejo de errores difiera entre reportes.
- **Propósito:** definir el esqueleto de un algoritmo en una operación, delegando algunos pasos a las subclases sin alterar la estructura general.
- **Solución:** la clase abstracta `GeneradorReporteBase` define el método plantilla `generarReporte(...)` como **`final`** (fija el orden) y declara como abstractos los pasos variables, que implementa cada reporte concreto (`ReporteVentas`, `ReporteOcupacionZona`, `ReporteIngresoServicios`, `ReporteTasaCancelacion`).

```java
public abstract class GeneradorReporteBase<T> {

    public final void generarReporte(LocalDate desde, LocalDate hasta,
                                     IGeneradorReporte adaptador, String rutaArchivo) {
        List<T> datos        = obtenerDatos();                 // paso variable
        List<T> filtrados    = filtrarPorFechas(datos, desde, hasta);
        Map<String,Object> m = calcularMetricas(filtrados);
        List<Object[]> filas = formatearFilas(m);
        adaptador.exportarDatos(filas, rutaArchivo);           // paso fijo (Adapter)
    }

    protected abstract List<T> obtenerDatos();
    protected abstract List<T> filtrarPorFechas(List<T> datos, LocalDate desde, LocalDate hasta);
    protected abstract Map<String, Object> calcularMetricas(List<T> datos);
    protected abstract List<Object[]> formatearFilas(Map<String, Object> metricas);
}
```

---

### 13. Facade — `FachadaCompra`

- **Requisito que resuelve:** ofrecer a la capa de interfaz (controladores JavaFX) un punto de entrada simple para el flujo de compra, que internamente coordina muchos subsistemas (gestores de asientos, pagos, entradas y comandos, además de fábricas, decoradores y estrategias).
- **Problema:** que cada controlador conozca y orqueste directamente todos los gestores y patrones internos lo vuelve complejo y fuertemente acoplado al subsistema.
- **Propósito:** proporcionar una interfaz unificada y de alto nivel a un conjunto de interfaces de un subsistema, simplificando su uso.
- **Solución:** `FachadaCompra` expone operaciones de negocio claras (`iniciarCompra`, `seleccionarAsientos`, `agregarServicio`, `construirMetodoPago`, `confirmarCompra`, `cancelarCompra`, `generarReporte`, etc.) y delega el trabajo en los gestores y patrones adecuados.

```java
public class FachadaCompra {
    private GestorAsientos gestorAsientos;
    private GestorPagos    gestorPagos;
    private GestorEntradas gestorEntradas;
    private GestorComandos gestorComandos;

    public Compra iniciarCompra(Usuario usuario, Evento evento) {
        return Compra.builder()
                .idCompra(UUID.randomUUID().toString())
                .usuario(usuario).evento(evento)
                .estado(new EstadoCreada())
                .build();
    }
    // confirmarCompra, seleccionarAsientos, agregarServicio... orquestan los gestores
}
```

---

## Principios SOLID aplicados

### S — Responsabilidad Única (*Single Responsibility Principle*)

Cada clase tiene una única razón para cambiar. La lógica de negocio se reparte en **gestores especializados** en lugar de concentrarse en una clase "Dios":

- `GestorPagos` solo registra y procesa pagos.
- `GestorEntradas` solo crea y extrae entradas.
- `GestorAsientos` solo administra el estado de los asientos.
- `GestorComandos` solo ejecuta y audita comandos.

```java
public class GestorPagos {
    public Pago registrarPago(Compra compra, IMetodoPago metodo) { ... }
    public boolean procesarPago(Compra compra) { ... }
}
```

De igual forma, cada `Adaptador*` se ocupa solo de un formato de exportación y cada `Notificador*` solo de un canal de notificación.

---

### O — Abierto/Cerrado (*Open/Closed Principle*)

El sistema está **abierto a la extensión pero cerrado a la modificación**. Se pueden añadir comportamientos nuevos creando clases nuevas, sin tocar las existentes:

- **Nuevo método de pago:** basta con crear una clase que implemente `IMetodoPago`; `GestorPagos` y `Pago` no cambian.
- **Nuevo servicio adicional:** se crea un nuevo `EntradaDecorador` sin modificar `EntradaBase`.
- **Nuevo reporte:** se extiende `GeneradorReporteBase` sin alterar el método plantilla.

```java
// Agregar un nuevo medio de pago no requiere modificar el código existente
public class PagoCripto implements IMetodoPago {
    @Override public boolean procesarPago(double monto) { ... }
    @Override public boolean validar() { ... }
    @Override public TipoMetodoPago getTipo() { ... }
}
```

---

### L — Sustitución de Liskov (*Liskov Substitution Principle*)

Cualquier subtipo puede usarse donde se espera su tipo base sin alterar la corrección del programa:

- Una `EntradaBase` y una entrada decorada (`VIPDecorador`, etc.) son ambas `IEntrada`, por lo que `Compra.calcularTotal()` las trata por igual.
- Un `Concierto`, `Teatro` o `Conferencia` puede usarse en cualquier lugar que espere un `Evento`.
- `Recinto`, `Zona` y `Asiento` son intercambiables como `IComponenteRecinto`.

```java
public double calcularTotal() {
    double suma = 0.0;
    for (IEntrada entrada : entradas) {       // base o decorada: da igual
        suma += entrada.calcularPrecioFinal();
    }
    this.total = suma;
    return suma;
}
```

---

### I — Segregación de Interfaces (*Interface Segregation Principle*)

Las interfaces son **pequeñas y cohesivas**: ninguna obliga a implementar métodos que no usa.

- `IObservador` → un solo método (`actualizar`).
- `IComponenteRecinto` → dos métodos.
- `IComando`, `IEntrada`, `IMetodoPago` → tres métodos cada una.

```java
public interface IObservador {
    void actualizar(String evento, Object datos);   // interfaz mínima y específica
}
```

En lugar de una interfaz general "hazlo todo", el dominio se modela con varias interfaces enfocadas, de modo que cada implementación solo conoce lo que necesita.

---

### D — Inversión de Dependencias (*Dependency Inversion Principle*)

Los módulos de alto nivel dependen de **abstracciones**, no de implementaciones concretas:

- `Pago` depende de `IMetodoPago`, no de `PagoTarjetaCredito`.
- `Compra` depende de `IEstadoCompra` e `IObservador`, no de estados u observadores concretos.
- `GeneradorReporteBase` depende de `IGeneradorReporte`, no de PDFBox ni POI directamente.

```java
public class Pago {
    private IMetodoPago metodo;          // depende de la abstracción
    public boolean ejecutar() {
        return metodo != null && metodo.procesarPago(monto);
    }
}
```

Gracias a esto, la implementación concreta se inyecta desde fuera (por ejemplo, desde `FachadaCompra`), lo que facilita el cambio de comportamiento y las pruebas con dobles/mocks.

---

## Tecnologías y dependencias

| Tecnología        | Versión   | Uso                                   |
|-------------------|-----------|---------------------------------------|
| Java              | 17        | Lenguaje base                         |
| JavaFX            | 17.0.10   | Interfaz gráfica (FXML + CSS)         |
| Apache POI        | 5.2.5     | Exportación de reportes a Excel       |
| Apache PDFBox     | 3.0.3     | Exportación de reportes a PDF         |
| JUnit Jupiter     | 5.10.2    | Pruebas unitarias                     |
| Maven             | 3.8+      | Gestión de dependencias y ejecución   |

---

## Resumen de patrones

| # | Patrón          | Tipo          | Clase(s) principal(es)                              |
|---|-----------------|---------------|-----------------------------------------------------|
| 1 | Singleton       | Creacional    | `ConfiguracionPlataforma`, `SessionManager`         |
| 2 | Factory Method  | Creacional    | `FabricaEvento`                                     |
| 3 | Builder         | Creacional    | `Compra.Builder`                                    |
| 4 | Prototype       | Creacional    | `Zona`, `Asiento`                                   |
| 5 | Composite       | Estructural   | `IComponenteRecinto`, `Recinto`, `Zona`, `Asiento`  |
| 6 | Decorator       | Estructural   | `EntradaDecorador` y decoradores concretos          |
| 7 | Strategy        | Comportamiento| `IMetodoPago` y estrategias de pago                 |
| 8 | State           | Comportamiento| `IEstadoCompra`, `IEstadoEvento` y sus estados      |
| 9 | Observer        | Comportamiento| `IObservador` y notificadores                       |
| 10| Command         | Comportamiento| `IComando`, `GestorComandos`                        |
| 11| Adapter         | Estructural   | `IGeneradorReporte`, `AdaptadorPDFBox`, `AdaptadorPOI`|
| 12| Template Method | Comportamiento| `GeneradorReporteBase` y reportes concretos         |
| 13| Facade          | Estructural   | `FachadaCompra`                                     |

---

*Proyecto académico — Universidad del Quindío.*

