# Programación Aplicada

## 1.4c. Patrones de comportamiento

![UPS · Carrera de Computación](../../../assets/logos/ups-computacion.png)

**Autor:** Ing. Pablo Torres, Mgtr.  
**Unidad:** 1. Programación genérica

[Inicio del curso](../../../README.md) · [Ver en el navegador](index.html)

---

## 1. Conceptos y ejemplos



### 1.1. Fundamentos

Los patrones **de comportamiento** distribuyen algoritmos, decisiones y comunicación entre objetos. Definen quién conoce una regla, quién coordina una interacción y qué partes pueden reemplazarse o evolucionar de forma independiente.

Este bloque cubre los once patrones de comportamiento del catálogo GoF, incluido Interpreter. No es necesario aplicar todos a una aplicación. Elige según la variación real: algoritmos intercambiables, estados, peticiones, recorridos, eventos o nuevas operaciones sobre una estructura.

Una interfaz genérica puede relacionar el tipo de dato entre colaboradores, como `Regla<T>`, `Observador<E>` o `Iterator<E>`. El patrón organiza las responsabilidades; el genérico comprueba la compatibilidad de datos. Una lambda implementa una interfaz funcional, pero su sola presencia no demuestra que exista un patrón.

| N.º | Patrón | Demostración |
|---|---|---|
| 1 | Chain of Responsibility | `ChainOfResponsibilityDemo.java` |
| 2 | Command | `CommandDemo.java` |
| 3 | Interpreter | `InterpreterDemo.java` |
| 4 | Iterator | `IteratorDemo.java` |
| 5 | Mediator | `MediatorDemo.java` |
| 6 | Memento | `MementoDemo.java` |
| 7 | Observer | `ObserverDemo.java` |
| 8 | State | `StateDemo.java` |
| 9 | Strategy | `StrategyDemo.java` |
| 10 | Template Method | `TemplateMethodDemo.java` |
| 11 | Visitor | `VisitorDemo.java` |



### 1.2. Chain of Responsibility

**Intención.** Pasar una petición por una cadena de manejadores, permitiendo que cada uno la atienda o la delegue. El emisor conoce el inicio de la cadena, no todos los posibles receptores.

Es útil para escalamiento de solicitudes o procesamiento configurable. Debe definirse si la cadena termina al atender una petición o si todos los manejadores participan. El orden afecta al resultado, y una petición podría quedar sin atender. En el ejemplo, cada nivel aprueba hasta cierto monto; si ninguno puede hacerlo, se rechaza. Los montos son enteros para aislar la decisión del manejo monetario. No se deben esconder errores bajo una delegación indefinida ni crear ciclos entre manejadores.



### 1.3. Command

**Intención.** Representar una petición como un objeto, separando quien la solicita de quien realiza el trabajo. Los participantes son comando, receptor, invocador y cliente que conecta los objetos.

Un botón puede ejecutar diferentes acciones mediante el mismo contrato. Convertir acciones en objetos permite encolarlas o registrar un historial, pero esas capacidades requieren implementación adicional. Deshacer no surge automáticamente: el comando debe conservar el estado o la operación inversa necesaria. El ejemplo guarda el estado previo de una luz para restaurarlo. Está diseñado para una ejecución seguida de una restauración; un historial general requeriría administrar varias instancias y su orden de ejecución.



### 1.4. Interpreter

**Intención.** Representar una gramática sencilla mediante objetos y evaluar expresiones construidas con ella. Los terminales representan valores o variables; los no terminales combinan otras expresiones; el contexto aporta los datos de evaluación.

Una regla como «activo Y NO bloqueado» puede representarse mediante un árbol de expresiones. Interpreter resulta útil para lenguajes pequeños y estables; una gramática compleja suele requerir herramientas de análisis más apropiadas. El ejemplo construye el árbol directamente en Java y no incluye un parser de texto. Esa diferencia es importante: interpretar un árbol ya construido no equivale a reconocer cualquier cadena escrita por un usuario. Este patrón completa los once patrones de comportamiento del catálogo clásico.

### 1.5. Iterator

**Intención.** Recorrer una estructura sin exponer su representación interna. Un iterador mantiene la posición de recorrido y proporciona operaciones para comprobar si quedan elementos y obtener el siguiente.

`Iterator<E>` conserva mediante genéricos el tipo de cada elemento. `Iterable<E>` permite solicitar iteradores y habilita el `for-each`. Son contratos relacionados, pero distintos: el contenedor produce un iterador y el iterador representa un recorrido concreto. El ejemplo usa una copia estable de una lista y produce un iterador nuevo para cada recorrido. Debe lanzar `NoSuchElementException` cuando ya no quedan valores. No incorpora eliminación ni detección de modificaciones concurrentes porque la colección interna no se modifica.

### 1.6. Mediator

**Intención.** Centralizar reglas de colaboración entre varios objetos para que no necesiten mantener referencias directas entre todos ellos. Los colegas se comunican con el mediador, y este coordina quién recibe cada interacción.

Una sala de conversación puede distribuir mensajes entre participantes registrados sin que cada participante conozca a los demás. El beneficio es reducir conexiones directas; el riesgo es convertir al mediador en una clase que concentra demasiadas reglas. El ejemplo muestra una sala en memoria que entrega mensajes de forma síncrona y excluye al emisor. No representa un servicio de mensajería real. A diferencia de Observer, la intención principal es coordinar colaboración, no únicamente informar que ocurrió un evento.

### 1.7. Memento

**Intención.** Capturar y restaurar el estado de un objeto sin exponer sus detalles internos a quien guarda el historial. El originador crea la instantánea, el memento la conserva y el cuidador administra cuándo almacenarla o restaurarla.

Puede utilizarse para deshacer cambios en un editor. La instantánea debe contener lo necesario y respetar el aislamiento del estado mutable; guardar solo referencias no garantiza una restauración independiente. El costo crece con la cantidad y tamaño de las instantáneas. En el ejemplo el estado es una cadena inmutable y el cuidador solo conserva el memento. Se muestra una restauración; una pila de historial sería una ampliación del mismo mecanismo.

### 1.8. Observer

**Intención.** Permitir que varios interesados se suscriban a los eventos de un emisor. El emisor notifica mediante una interfaz y no depende de las clases concretas de sus observadores.

Es útil para actualizar vistas, registrar sucesos o reaccionar a cambios. El contrato debe definir suscripción, cancelación, orden y política frente a errores. Notificar observadores no significa ejecutar en paralelo: en el ejemplo las llamadas son síncronas y una excepción detendría la publicación actual. Se recorre una copia de suscriptores para que una modificación del registro no altere ese recorrido. El parámetro genérico `E` relaciona el tipo de evento del emisor con el que recibe cada observador.

### 1.9. State

**Intención.** Cambiar el comportamiento de un objeto según su estado interno, delegando operaciones a objetos que representan cada estado. El contexto conserva el estado actual y las transiciones determinan qué comportamiento se utiliza después.

Un pedido puede aceptar pago antes del envío, pero rechazar un envío prematuro. Concentrar esas reglas en estados evita repetir condiciones en cada operación del contexto. Debe quedar claro quién controla las transiciones y cuáles son válidas. State se parece estructuralmente a Strategy, pero representa etapas del ciclo de vida, no alternativas elegidas libremente para un cálculo. El ejemplo implementa solo pago y envío; no pretende cubrir un sistema completo de pedidos.

### 1.10. Strategy

**Intención.** Encapsular alternativas de un algoritmo detrás de una interfaz común. El contexto recibe una estrategia y delega la decisión variable; el cliente selecciona la implementación adecuada.

Por ejemplo, un clasificador puede aceptar por longitud mínima o por un prefijo. El recorrido o el procedimiento que usa la regla permanece estable. El patrón es útil si existen criterios intercambiables; puede ser excesivo cuando solo hay una operación simple que no cambia. Una interfaz funcional puede implementarse mediante lambdas, pero clases con nombre facilitan configuración y validación. En el ejemplo, `Regla<T>` conserva el tipo procesado y `Clasificador<T>` no inspecciona las clases de las estrategias.

### 1.11. Template Method

**Intención.** Definir el orden estable de un algoritmo en una clase base y permitir que las subclases completen o especialicen ciertos pasos. El método plantilla controla la secuencia; las operaciones primitivas o los hooks aportan las variaciones.

Es útil para procesos que comparten etapas y difieren en detalles. La reutilización se basa en herencia, por lo que las subclases quedan vinculadas al diseño de la clase base. Strategy suele ser más apropiado cuando se necesita sustituir una colaboración por composición. El ejemplo limpia una entrada, la convierte y muestra el resultado siempre en ese orden. El método plantilla es `final` para evitar que una subclase omita esas etapas.

### 1.12. Visitor

**Intención.** Agregar operaciones sobre una estructura de objetos separando esas operaciones de las clases de los elementos. Cada elemento acepta un visitante y llama a la operación correspondiente a su propio tipo.

La selección combina el tipo del elemento y la implementación del visitante, mecanismo conocido como doble despacho. Es útil cuando las clases de elementos cambian poco y se añaden operaciones con frecuencia. Agregar un nuevo tipo de elemento obliga a actualizar los visitantes, por lo que no conviene en jerarquías muy variables. El ejemplo aplica un exportador a párrafos e imágenes sin colocar la lógica de exportación en cada elemento. No utiliza `instanceof` para elegir la operación.

### 1.13. Criterios de elección

Strategy y State pueden tener clases parecidas: la diferencia es que Strategy selecciona una política y State expresa el estado del contexto y sus transiciones. Observer notifica suscriptores, mientras Mediator coordina colaboraciones. Command representa una petición y Memento conserva estado para restaurarlo; pueden combinarse, pero no son equivalentes.

---



## 2. Demostraciones comentadas

Ubica los siguientes archivos en `app/src/main/java/edu/ups/pap/u01/patrones/comportamiento/`. Cada archivo contiene su propia clase principal y sus colaboradores internos; es independiente de los demás demos.

Con JDK 17 o superior, desde la raíz del proyecto Java, ejecuta un archivo así; sustituye el nombre para probar los demás:

```bash
java app/src/main/java/edu/ups/pap/u01/patrones/comportamiento/ChainOfResponsibilityDemo.java
```

Los comentarios identifican los roles y las decisiones del patrón. Las lambdas implementan interfaces funcionales y los `record` agrupan datos mediante accesores generados; no son requisitos de los patrones. Los ejemplos son secuenciales y se limitan a consola.

### 2.1. Chain of Responsibility · `ChainOfResponsibilityDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

/** Cadena de aprobación: cada nivel atiende o delega la solicitud. */
public class ChainOfResponsibilityDemo {
    static class Aprobador {
        private final String nombre;
        private final int limite;
        private final Aprobador siguiente;
        Aprobador(String nombre, int limite, Aprobador siguiente) {
            this.nombre = nombre;
            this.limite = limite;
            this.siguiente = siguiente;
        }
        String aprobar(int monto) {
            if (monto < 0) throw new IllegalArgumentException("Monto negativo");
            if (monto <= limite) return nombre;
            return siguiente == null ? "Rechazado" : siguiente.aprobar(monto);
        }
    }
    public static void main(String[] args) {
        Aprobador direccion = new Aprobador("Dirección", 1000, null);
        Aprobador equipo = new Aprobador("Equipo", 100, direccion);
        System.out.println("80: " + equipo.aprobar(80));
        System.out.println("500: " + equipo.aprobar(500));
        System.out.println("1500: " + equipo.aprobar(1500));
    }
}
```

**Salida esperada:**

```text
80: Equipo
500: Dirección
1500: Rechazado
```

El cliente invoca el primer nivel; cada manejador decide si resuelve o continúa.

### 2.2. Command · `CommandDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

/** Command: un botón ejecuta una petición sin conocer los detalles de la luz. */
public class CommandDemo {
    static class Luz { boolean encendida; } // Receptor.
    interface Comando { void ejecutar(); void deshacer(); }
    static class Encender implements Comando {
        private final Luz luz;
        private boolean anterior;
        Encender(Luz luz) { this.luz = luz; }
        public void ejecutar() {
            anterior = luz.encendida; // Captura lo necesario para restaurar.
            luz.encendida = true;
        }
        public void deshacer() { luz.encendida = anterior; }
    }
    record Boton(Comando comando) { // Invocador.
        void pulsar() { comando.ejecutar(); }
    }
    public static void main(String[] args) {
        Luz luz = new Luz();
        Comando accion = new Encender(luz);
        new Boton(accion).pulsar();
        System.out.println("Encendida: " + luz.encendida);
        accion.deshacer();
        System.out.println("Restaurada: " + luz.encendida);
    }
}
```

**Salida esperada:**

```text
Encendida: true
Restaurada: false
```

El invocador conoce Comando; el comando conoce al receptor y conserva el estado necesario para deshacer.

### 2.3. Interpreter · `InterpreterDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

import java.util.Map;

/** Interpreter: evalúa una gramática booleana construida como árbol. */
public class InterpreterDemo {
    interface Expresion { boolean evaluar(Map<String, Boolean> contexto); }
    record Variable(String nombre) implements Expresion { // Terminal.
        public boolean evaluar(Map<String, Boolean> contexto) {
            Boolean valor = contexto.get(nombre);
            if (valor == null) throw new IllegalArgumentException("Variable ausente");
            return valor;
        }
    }
    record Y(Expresion izquierda, Expresion derecha) implements Expresion {
        public boolean evaluar(Map<String, Boolean> contexto) {
            return izquierda.evaluar(contexto) && derecha.evaluar(contexto);
        }
    }
    record No(Expresion expresion) implements Expresion {
        public boolean evaluar(Map<String, Boolean> contexto) {
            return !expresion.evaluar(contexto);
        }
    }
    public static void main(String[] args) {
        Expresion regla = new Y(new Variable("activo"), new No(new Variable("bloqueado")));
        System.out.println(regla.evaluar(Map.of("activo", true, "bloqueado", false)));
        System.out.println(regla.evaluar(Map.of("activo", true, "bloqueado", true)));
    }
}
```

**Salida esperada:**

```text
true
false
```

Los objetos Y y No forman el árbol; el mapa aporta valores y no contiene el algoritmo de interpretación.

### 2.4. Iterator · `IteratorDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Iterator genérico: cada recorrido conserva su propio índice. */
public class IteratorDemo {
    static class Catalogo<T> implements Iterable<T> {
        private final List<T> elementos;
        Catalogo(List<T> elementos) { this.elementos = List.copyOf(elementos); }
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private int indice;
                public boolean hasNext() { return indice < elementos.size(); }
                public T next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    return elementos.get(indice++);
                }
            };
        }
    }
    public static void main(String[] args) {
        Catalogo<String> catalogo = new Catalogo<>(List.of("A", "B"));
        for (String elemento : catalogo) System.out.println(elemento);
        System.out.println("Otro recorrido: " + catalogo.iterator().next());
    }
}
```

**Salida esperada:**

```text
A
B
Otro recorrido: A
```

La nueva llamada a iterator() empieza desde el inicio; el estado del recorrido pertenece a cada iterador.

### 2.5. Mediator · `MediatorDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

import java.util.ArrayList;
import java.util.List;

/** Mediator: la sala coordina la entrega entre participantes. */
public class MediatorDemo {
    static class Sala {
        private final List<Usuario> usuarios = new ArrayList<>();
        void registrar(Usuario usuario) { usuarios.add(usuario); }
        void distribuir(Usuario emisor, String texto) {
            for (Usuario usuario : usuarios) {
                if (usuario != emisor) usuario.recibir(texto);
            }
        }
    }
    static class Usuario {
        private final String nombre;
        private final Sala sala;
        Usuario(String nombre, Sala sala) {
            this.nombre = nombre;
            this.sala = sala;
            sala.registrar(this);
        }
        void enviar(String texto) { sala.distribuir(this, texto); }
        void recibir(String texto) { System.out.println(nombre + " recibe: " + texto); }
    }
    public static void main(String[] args) {
        Sala sala = new Sala();
        Usuario ana = new Usuario("Ana", sala);
        new Usuario("Luis", sala);
        new Usuario("Eva", sala);
        ana.enviar("Hola");
    }
}
```

**Salida esperada:**

```text
Luis recibe: Hola
Eva recibe: Hola
```

Ana no conserva referencias a Luis o Eva; todos colaboran a través de la sala.

### 2.6. Memento · `MementoDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

/** Memento: restaura texto sin revelar el contenido de la instantánea al cuidador. */
public class MementoDemo {
    static class Editor {
        private String texto = "";
        static final class Recuerdo {
            private final String texto;
            private Recuerdo(String texto) { this.texto = texto; }
        }
        void escribir(String valor) { texto = valor; }
        String leer() { return texto; }
        Recuerdo guardar() { return new Recuerdo(texto); }
        void restaurar(Recuerdo recuerdo) { texto = recuerdo.texto; }
    }
    public static void main(String[] args) {
        Editor editor = new Editor();
        editor.escribir("Borrador");
        Editor.Recuerdo anterior = editor.guardar(); // Cuidador: conserva, no interpreta.
        editor.escribir("Versión modificada");
        System.out.println(editor.leer());
        editor.restaurar(anterior);
        System.out.println(editor.leer());
    }
}
```

**Salida esperada:**

```text
Versión modificada
Borrador
```

El editor crea e interpreta su recuerdo; el cliente lo utiliza únicamente como una instantánea opaca.

### 2.7. Observer · `ObserverDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

import java.util.ArrayList;
import java.util.List;

/** Observer: publicación tipada con suscripción y cancelación explícitas. */
public class ObserverDemo {
    interface Observador<E> { void recibir(E evento); }
    static class Emisor<E> {
        private final List<Observador<E>> observadores = new ArrayList<>();
        void suscribir(Observador<E> observador) { observadores.add(observador); }
        void quitar(Observador<E> observador) { observadores.remove(observador); }
        void publicar(E evento) {
            for (Observador<E> o : List.copyOf(observadores)) o.recibir(evento);
        }
    }
    public static void main(String[] args) {
        Emisor<String> emisor = new Emisor<>();
        Observador<String> consola = e -> System.out.println("Consola: " + e);
        Observador<String> registro = e -> System.out.println("Registro: " + e);
        emisor.suscribir(consola);
        emisor.suscribir(registro);
        emisor.publicar("Inicio");
        emisor.quitar(registro);
        emisor.publicar("Fin");
    }
}
```

**Salida esperada:**

```text
Consola: Inicio
Registro: Inicio
Consola: Fin
```

La segunda publicación ya no llega al observador retirado; las notificaciones suceden en el mismo hilo.

### 2.8. State · `StateDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

/** State: cada estado define las operaciones válidas de un pedido. */
public class StateDemo {
    interface Estado {
        void pagar(Pedido pedido);
        void enviar(Pedido pedido);
    }
    static class Nuevo implements Estado {
        public void pagar(Pedido p) { p.estado = new Pagado(); System.out.println("Pagado"); }
        public void enviar(Pedido p) { throw new IllegalStateException("Falta pagar"); }
    }
    static class Pagado implements Estado {
        public void pagar(Pedido p) { throw new IllegalStateException("Ya pagado"); }
        public void enviar(Pedido p) { p.estado = new Enviado(); System.out.println("Enviado"); }
    }
    static class Enviado implements Estado {
        public void pagar(Pedido p) { throw new IllegalStateException("Pedido enviado"); }
        public void enviar(Pedido p) { throw new IllegalStateException("Ya enviado"); }
    }
    static class Pedido {
        private Estado estado = new Nuevo();
        void pagar() { estado.pagar(this); }
        void enviar() { estado.enviar(this); }
    }
    public static void main(String[] args) {
        Pedido pedido = new Pedido();
        try { pedido.enviar(); }
        catch (IllegalStateException e) { System.out.println(e.getMessage()); }
        pedido.pagar();
        pedido.enviar();
    }
}
```

**Salida esperada:**

```text
Falta pagar
Pagado
Enviado
```

Pedido delega; Nuevo, Pagado y Enviado deciden tanto el comportamiento como la transición permitida.

### 2.9. Strategy · `StrategyDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

import java.util.Objects;

/** Strategy genérico: el contexto delega en una regla intercambiable. */
public class StrategyDemo {
    interface Regla<T> { boolean cumple(T valor); }
    record LongitudMinima(int minimo) implements Regla<String> {
        public boolean cumple(String texto) { return texto.length() >= minimo; }
    }
    record EmpiezaCon(String prefijo) implements Regla<String> {
        public boolean cumple(String texto) { return texto.startsWith(prefijo); }
    }
    static class Clasificador<T> {
        private final Regla<T> regla;
        Clasificador(Regla<T> regla) { this.regla = Objects.requireNonNull(regla); }
        boolean aceptar(T valor) { return regla.cumple(Objects.requireNonNull(valor)); }
    }
    public static void main(String[] args) {
        Clasificador<String> longitud = new Clasificador<>(new LongitudMinima(5));
        Clasificador<String> prefijo = new Clasificador<>(new EmpiezaCon("J"));
        System.out.println("Longitud: " + longitud.aceptar("Java"));
        System.out.println("Prefijo: " + prefijo.aceptar("Java"));
    }
}
```

**Salida esperada:**

```text
Longitud: false
Prefijo: true
```

Ambos clasificadores procesan String; cambia la regla recibida, sin modificar aceptar().

### 2.10. Template Method · `TemplateMethodDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

import java.util.Locale;

/** Template Method: conserva la secuencia y especializa un paso. */
public class TemplateMethodDemo {
    static abstract class Proceso {
        final void ejecutar(String entrada) {
            String limpia = entrada.strip();
            String resultado = convertir(limpia); // Paso variable.
            System.out.println(resultado);
        }
        protected abstract String convertir(String entrada);
    }
    static class Mayusculas extends Proceso {
        protected String convertir(String entrada) {
            return entrada.toUpperCase(Locale.ROOT);
        }
    }
    static class EntreCorchetes extends Proceso {
        protected String convertir(String entrada) { return "[" + entrada + "]"; }
    }
    public static void main(String[] args) {
        new Mayusculas().ejecutar("  Hola  ");
        new EntreCorchetes().ejecutar("  Hola  ");
    }
}
```

**Salida esperada:**

```text
HOLA
[Hola]
```

Las subclases cambian convertir(); ejecutar() mantiene limpieza, conversión y salida en el mismo orden.

### 2.11. Visitor · `VisitorDemo.java`

```java
package edu.ups.pap.u01.patrones.comportamiento;

import java.util.List;

/** Visitor: agrega una operación de exportación a distintos elementos. */
public class VisitorDemo {
    interface Visitante {
        String visitar(Parrafo parrafo);
        String visitar(Imagen imagen);
    }
    interface Elemento { String aceptar(Visitante visitante); }
    record Parrafo(String texto) implements Elemento {
        public String aceptar(Visitante v) { return v.visitar(this); }
    }
    record Imagen(String nombre) implements Elemento {
        public String aceptar(Visitante v) { return v.visitar(this); }
    }
    static class Exportador implements Visitante {
        public String visitar(Parrafo p) { return "Texto: " + p.texto(); }
        public String visitar(Imagen i) { return "Imagen: " + i.nombre(); }
    }
    public static void main(String[] args) {
        List<Elemento> elementos = List.of(new Parrafo("Hola"), new Imagen("logo.png"));
        Visitante exportador = new Exportador();
        for (Elemento e : elementos) System.out.println(e.aceptar(exportador));
    }
}
```

**Salida esperada:**

```text
Texto: Hola
Imagen: logo.png
```

Cada aceptar() selecciona la sobrecarga para su tipo; la implementación de Visitante aporta la operación concreta.


---



## 3. PC1.4 · Integración de Adapter y Strategy

Continúa sobre el proyecto de PC1.2. CampusMonitor recibirá temperaturas desde una API simulada en Fahrenheit y evaluará alertas con criterios intercambiables. Se aplicarán **Adapter** para integrar el origen y **Strategy** para seleccionar la regla de alerta.

### 3.1. Clases e interfaces que debes incorporar

Conserva `Lectura<T>`, su método `transformar` y `ReporteLecturas`. Implementa los siguientes elementos públicos, cada uno en su archivo:

| Paquete y elemento | Tipo y contrato |
|---|---|
| `integracion.FuenteLectura<T>` | Interfaz con `Lectura<T> leer()`; contrato de acceso a una lectura |
| `integracion.TermometroExterno` | Clase simulada con constructor `TermometroExterno(String codigo, double valorF)`, y métodos `String codigo()` y `double tomarMuestraF()` que devuelven esos datos |
| `integracion.AdaptadorTermometro` | Clase que implementa `FuenteLectura<Double>`; recibe `TermometroExterno` por constructor y entrega una lectura en `°C` |
| `alertas.ReglaAlerta<T>` | Interfaz con `boolean evaluar(T valor)`; `true` representa una alerta |
| `alertas.SobreUmbral` | Clase que implementa `ReglaAlerta<Double>`; recibe `double umbral` y alerta cuando el valor es estrictamente mayor |
| `alertas.FueraDeRango` | Clase que implementa `ReglaAlerta<Double>`; recibe `double minimo, double maximo` y alerta fuera del intervalo inclusivo |
| `servicio.Monitor<T>` | Clase genérica con constructor `Monitor(FuenteLectura<T> fuente, ReglaAlerta<T> regla)` y método `String revisar()` |

Todos los paquetes tienen el prefijo `edu.ups.pap`. `Monitor.revisar()` debe leer una vez, evaluar el valor y devolver identificador, valor, unidad y estado `NORMAL` o `ALERTA`. El monitor utiliza las interfaces; no debe distinguir estrategias u orígenes mediante `instanceof` ni casts.

En el adaptador, representa primero la muestra externa como `Lectura<Double>` en `°F` y reutiliza **`Lectura.transformar`** para convertirla con `C = (F − 32) × 5.0 / 9.0`. Así se conserva el identificador y se aprovecha el avance de PC1.1b y PC1.2.

Rechaza dependencias nulas, temperaturas o límites no finitos y rangos con mínimo mayor que máximo mediante `IllegalArgumentException` descriptiva. Conserva las validaciones del modelo para identificadores y unidades. La fuente simulada permite probar la integración sin hardware.

### 3.2. Actividades en App.java

Crea `private static void runPatrones()` e invócalo desde `main`, conservando los métodos y funcionalidades anteriores.

1. Crea tres termómetros simulados: `S01` con `68.0 °F`, `S02` con `77.0 °F` y `S03` con `86.0 °F`. Conecta cada uno mediante su adaptador.
2. Evalúa las lecturas con `SobreUmbral(28.0)` y muestra el resultado.
3. Evalúa las mismas fuentes con `FueraDeRango(18.0, 24.0)`. Cambia el objeto de estrategia suministrado al monitor, sin modificar su implementación.
4. Comprueba un rango invertido y una fuente nula, capturando las excepciones para mostrar ambos mensajes.

La consola debe permitir contrastar estos resultados:

| Sensor | Lectura adaptada | Umbral mayor que 28 | Fuera del rango 18–24 |
|---|---|---|---|
| S01 | `20.0 °C` | NORMAL | NORMAL |
| S02 | `25.0 °C` | NORMAL | ALERTA |
| S03 | `30.0 °C` | ALERTA | ALERTA |

También comprueba los límites: `28.0` no activa `SobreUmbral(28.0)`; `18.0` y `24.0` no activan `FueraDeRango(18.0, 24.0)`.

### 3.3. Estructura resultante

Archivos relevantes acumulados. Las carpetas de demos contienen los archivos presentados en los tres bloques de patrones; no sustituyen las clases del proyecto.

```text
icc-pap-campusmonitor-apellido/
├── app/src/main/java/edu/ups/pap/
│   ├── App.java
│   ├── modelo/
│   │   └── Lectura.java
│   ├── integracion/
│   │   ├── FuenteLectura.java
│   │   ├── TermometroExterno.java
│   │   └── AdaptadorTermometro.java
│   ├── alertas/
│   │   ├── ReglaAlerta.java
│   │   ├── SobreUmbral.java
│   │   └── FueraDeRango.java
│   ├── servicio/
│   │   ├── ReporteLecturas.java
│   │   └── Monitor.java
│   └── u01/
│       ├── TiposDemo.java
│       ├── MetodosDemo.java
│       ├── ConversionesDemo.java
│       ├── ComodinesDemo.java
│       └── patrones/
│           ├── creacionales/
│           ├── estructurales/
│           └── comportamiento/
└── evidencias/
    ├── PC1.1a.md
    ├── PC1.1b.md
    ├── PC1.2.md
    └── PC1.4.md
```

Si tu modelo permanece en `u01`, utiliza ese paquete en los imports de las clases nuevas; no dupliques `Lectura`.

### 3.4. Evidencias

Sube los cambios al repositorio remoto. En `evidencias/PC1.4.md` incluye únicamente:

#### 1. Implementación realizada

En un párrafo breve, identifica el adaptador, las estrategias y el contexto. Explica cómo reutilizaste `Lectura.transformar` para convertir la muestra externa.

#### 2. Ejecución correcta

Agrega una captura con los tres sensores evaluados mediante ambas estrategias y las comprobaciones de los límites, y otra con los mensajes del rango invertido y la fuente nula. Debajo, explica en una o dos frases por qué cambió el resultado de S02 al sustituir la estrategia sin modificar el monitor.

## Referencias técnicas

- Gamma, E.; Helm, R.; Johnson, R.; Vlissides, J. *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994. Catálogo original de 23 patrones.
- [Ficha y contenido de la obra — Addison-Wesley / InformIT](https://www.informit.com/store/design-patterns-elements-of-reusable-object-oriented-software-9780201633610)
- [Catálogo complementario — Refactoring.Guru](https://refactoring.guru/es/design-patterns/catalog)
- [API Java](https://docs.oracle.com/en/java/javase/17/docs/api/index.html)
