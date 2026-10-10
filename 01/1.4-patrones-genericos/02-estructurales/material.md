# Programación Aplicada

## 1.4b. Patrones estructurales

![UPS · Carrera de Computación](../../../assets/logos/ups-computacion.png)

**Autor:** Ing. Pablo Torres, Mgtr.  
**Unidad:** 1. Programación genérica

[Inicio del curso](../../../README.md) · [Ver en el navegador](index.html)

---

## 1. Conceptos y ejemplos



### 1.1. Fundamentos

Los patrones **estructurales** organizan la composición de clases y objetos. Permiten adaptar contratos, construir jerarquías o incorporar capas sin que los clientes dependan de todos los detalles internos.

La **composición** expresa que un objeto utiliza a otro mediante una referencia. La **herencia** establece una relación de tipos. Muchos patrones estructurales favorecen la composición para combinar comportamientos sin multiplicar subclases. Ninguna de las dos técnicas es por sí sola un patrón.

Este bloque cubre los siete patrones estructurales del catálogo GoF. Distingue especialmente Adapter, que traduce una interfaz; Decorator, que añade comportamiento; y Proxy, que controla el acceso. Una estructura de clases parecida puede responder a intenciones diferentes.

| N.º | Patrón | Demostración |
|---|---|---|
| 1 | Adapter | `AdapterDemo.java` |
| 2 | Bridge | `BridgeDemo.java` |
| 3 | Composite | `CompositeDemo.java` |
| 4 | Decorator | `DecoratorDemo.java` |
| 5 | Facade | `FacadeDemo.java` |
| 6 | Flyweight | `FlyweightDemo.java` |
| 7 | Proxy | `ProxyDemo.java` |



### 1.2. Adapter

**Intención.** Hacer compatible una interfaz existente con la que espera un cliente. El adaptador implementa el contrato destino y delega en el objeto adaptado, traduciendo nombres, formatos o unidades cuando corresponde.

Es útil al integrar una biblioteca antigua o un proveedor externo sin modificar su implementación. Debe conservar la semántica: convertir un nombre de método no basta si las unidades o los errores tienen significados distintos. El ejemplo traduce una API que informa Fahrenheit a un contrato que entrega Celsius. Se utiliza composición, aunque también existen adaptadores basados en herencia. A diferencia de Decorator, su propósito es compatibilidad entre contratos, no añadir capas de comportamiento detrás de la misma interfaz.



### 1.3. Bridge

**Intención.** Separar una abstracción de su implementación para que ambas puedan variar de manera independiente. La abstracción mantiene una referencia a un implementador y delega en él las operaciones de bajo nivel.

Por ejemplo, pueden variar tanto el tipo de mensaje como el canal de presentación. Sin el puente aparecerían clases para cada combinación. Bridge permite combinar una notificación normal o urgente con un canal simple o decorado. El precio es una capa adicional de delegación. Adapter suele incorporar compatibilidad a interfaces ya existentes; Bridge diseña de antemano dos dimensiones de variación. En el ejemplo, la jerarquía de mensajes y las implementaciones de canal no dependen de las clases concretas de la otra dimensión.



### 1.4. Composite

**Intención.** Tratar de manera uniforme objetos individuales y agrupaciones mediante una interfaz común. Las hojas implementan la operación directamente; los compuestos la delegan a sus hijos, formando una estructura de árbol.

Un archivo puede calcular su tamaño directamente y una carpeta sumar el tamaño de sus componentes. El cliente pide la misma operación sin distinguir sus clases. Es útil en jerarquías parte-todo; no toda colección constituye un Composite. Debe decidirse dónde se permiten agregar hijos y cómo evitar ciclos, porque la recursión supone una estructura válida. El ejemplo reserva `agregar` a la carpeta y requiere un árbol sin ciclos. No todas las operaciones del compuesto necesitan existir en las hojas.

### 1.5. Decorator

**Intención.** Añadir responsabilidades envolviendo un objeto detrás de su misma interfaz. Cada decorador conserva una referencia al componente y amplía o transforma su comportamiento antes o después de delegar.

Esto permite combinar capacidades sin crear una subclase por combinación. Un formato de texto puede recibir un marco y una etiqueta en capas independientes. El orden influye en el resultado y debe quedar claro para quien arma la cadena. Decorator conserva el contrato; Adapter cambia la interfaz esperada y Proxy controla el acceso. Demasiadas capas pueden dificultar el seguimiento de errores. El ejemplo produce cadenas y no modifica el componente interno, por lo que cada combinación es fácil de observar en consola.

### 1.6. Facade

**Intención.** Ofrecer una entrada simple a un conjunto de operaciones de un subsistema. La fachada coordina colaboradores y evita que el cliente conozca todos los detalles del flujo.

Una publicación puede requerir validar texto, convertirlo y enviarlo a una salida. El cliente invoca una sola operación, mientras los componentes conservan sus responsabilidades. La fachada no sustituye necesariamente las interfaces internas ni impide utilizarlas directamente. El riesgo es concentrar toda la lógica en una clase demasiado grande. A diferencia de Adapter, aquí no se traduce únicamente una interfaz incompatible: se simplifica el uso de varios componentes. El ejemplo limita la salida a consola; no representa una publicación real ni una transacción.

### 1.7. Flyweight

**Intención.** Compartir estado común entre muchos objetos para reducir duplicación. El estado intrínseco compartido debe distinguirse del estado extrínseco, que depende de cada uso y se proporciona desde fuera.

Varios símbolos de un mapa pueden compartir forma y color, pero mantener posiciones distintas. Una fábrica administra la reutilización de la parte común. El ahorro debe justificarse con volumen y costo real; introducir una caché también consume memoria y requiere una política de vida. Los objetos compartidos suelen ser inmutables para evitar efectos cruzados. El ejemplo reutiliza un estilo por color y pasa las coordenadas a cada dibujo. No demuestra una caché concurrente ni una política de expulsión de entradas.

### 1.8. Proxy

**Intención.** Sustituir un objeto por un representante que controla el acceso y mantiene el mismo contrato. Puede retrasar una creación costosa, verificar permisos o coordinar un acceso remoto.

En un proxy virtual, el objeto real se crea cuando se usa por primera vez. El cliente no necesita saber cuándo ocurre esa inicialización. Deben definirse los errores y los efectos del acceso: una operación que parece local podría ser lenta o fallar. Proxy se enfoca en el control de acceso; Decorator añade responsabilidades combinables. El ejemplo registra una carga simulada y reutiliza el contenido en la segunda llamada. No realiza lectura de archivos ni implementa seguridad o concurrencia.

### 1.9. Criterios de elección

La estructura debe preservar los contratos. Un Adapter que cambia unidades debe convertir valores, no solo nombres. Un Decorator debe seguir siendo utilizable como el componente original. Un Composite necesita una jerarquía válida. Bridge separa dos dimensiones de cambio, mientras Facade reduce lo que el cliente necesita conocer.

---



## 2. Demostraciones comentadas

Ubica los siguientes archivos en `app/src/main/java/edu/ups/pap/u01/patrones/estructurales/`. Cada archivo contiene su propia clase principal y sus colaboradores internos; es independiente de los demás demos.

Con JDK 17 o superior, desde la raíz del proyecto Java, ejecuta un archivo así; sustituye el nombre para probar los demás:

```bash
java app/src/main/java/edu/ups/pap/u01/patrones/estructurales/AdapterDemo.java
```

Los comentarios identifican los roles y las decisiones del patrón. Las lambdas implementan interfaces funcionales y los `record` agrupan datos mediante accesores generados; no son requisitos de los patrones. Los ejemplos son secuenciales y se limitan a consola.

### 2.1. Adapter · `AdapterDemo.java`

```java
package edu.ups.pap.u01.patrones.estructurales;

/** Adapter: traduce una API de Fahrenheit a un contrato en Celsius. */
public class AdapterDemo {
    interface Temperatura { double celsius(); } // Contrato esperado por el cliente.
    static class ApiAntigua {
        double leerFahrenheit() { return 86.0; } // API existente que no modificamos.
    }
    static class Adaptador implements Temperatura {
        private final ApiAntigua api;
        Adaptador(ApiAntigua api) { this.api = api; }
        public double celsius() {
            return (api.leerFahrenheit() - 32.0) * 5.0 / 9.0;
        }
    }
    public static void main(String[] args) {
        Temperatura temperatura = new Adaptador(new ApiAntigua());
        System.out.println("Celsius: " + temperatura.celsius());
    }
}
```

**Salida esperada:**

```text
Celsius: 30.0
```

El cliente utiliza Temperatura; el adaptador conoce la API antigua y realiza la traducción de unidad.

### 2.2. Bridge · `BridgeDemo.java`

```java
package edu.ups.pap.u01.patrones.estructurales;

/** Bridge: combina tipos de mensaje con canales independientes. */
public class BridgeDemo {
    interface Canal { void mostrar(String texto); } // Implementador.
    static class CanalSimple implements Canal {
        public void mostrar(String texto) { System.out.println(texto); }
    }
    static class CanalEnmarcado implements Canal {
        public void mostrar(String texto) { System.out.println("[" + texto + "]"); }
    }
    static abstract class Mensaje {
        protected final Canal canal;
        Mensaje(Canal canal) { this.canal = canal; }
        abstract void enviar(String texto);
    }
    static class Normal extends Mensaje {
        Normal(Canal canal) { super(canal); }
        void enviar(String texto) { canal.mostrar(texto); }
    }
    static class Urgente extends Mensaje {
        Urgente(Canal canal) { super(canal); }
        void enviar(String texto) { canal.mostrar("URGENTE: " + texto); }
    }
    public static void main(String[] args) {
        new Normal(new CanalSimple()).enviar("Reunión");
        new Urgente(new CanalEnmarcado()).enviar("Reunión");
        // Cambiar el canal no exige otra subclase de Mensaje.
    }
}
```

**Salida esperada:**

```text
Reunión
[URGENTE: Reunión]
```

Normal/Urgente y CanalSimple/CanalEnmarcado son dimensiones independientes que se combinan por composición.

### 2.3. Composite · `CompositeDemo.java`

```java
package edu.ups.pap.u01.patrones.estructurales;

import java.util.ArrayList;
import java.util.List;

/** Composite: suma tamaños utilizando el mismo contrato para hojas y grupos. */
public class CompositeDemo {
    interface Nodo { int tamano(); }
    record Archivo(int bytes) implements Nodo {
        public int tamano() { return bytes; } // Hoja: resultado directo.
    }
    static class Carpeta implements Nodo {
        private final List<Nodo> hijos = new ArrayList<>();
        void agregar(Nodo hijo) { hijos.add(hijo); }
        public int tamano() {
            int total = 0;
            for (Nodo hijo : hijos) total += hijo.tamano(); // Delegación recursiva.
            return total;
        }
    }
    public static void main(String[] args) {
        Carpeta interna = new Carpeta();
        interna.agregar(new Archivo(20));
        Carpeta raiz = new Carpeta();
        raiz.agregar(new Archivo(10));
        raiz.agregar(interna);
        System.out.println("Tamaño: " + raiz.tamano());
    }
}
```

**Salida esperada:**

```text
Tamaño: 30
```

El cliente llama tamano() sobre la raíz; cada compuesto delega sin preguntar si el hijo es archivo o carpeta.

### 2.4. Decorator · `DecoratorDemo.java`

```java
package edu.ups.pap.u01.patrones.estructurales;

/** Decorator: combina capas que mantienen la interfaz de texto. */
public class DecoratorDemo {
    interface Texto { String obtener(); }
    record Simple(String contenido) implements Texto {
        public String obtener() { return contenido; }
    }
    record Marco(Texto interior) implements Texto {
        public String obtener() { return "[" + interior.obtener() + "]"; }
    }
    record Etiqueta(Texto interior) implements Texto {
        public String obtener() { return "Dato: " + interior.obtener(); }
    }
    public static void main(String[] args) {
        Texto base = new Simple("42");
        System.out.println(new Marco(new Etiqueta(base)).obtener());
        System.out.println(new Etiqueta(new Marco(base)).obtener());
        // El mismo conjunto de decoradores, en distinto orden, produce otra forma.
    }
}
```

**Salida esperada:**

```text
[Dato: 42]
Dato: [42]
```

Las capas comparten el contrato Texto; cambiar su orden cambia el resultado visible.

### 2.5. Facade · `FacadeDemo.java`

```java
package edu.ups.pap.u01.patrones.estructurales;

/** Facade: presenta una operación pública que coordina tres colaboradores. */
public class FacadeDemo {
    static class Validador {
        void validar(String texto) {
            if (texto == null || texto.isBlank()) {
                throw new IllegalArgumentException("Texto vacío");
            }
        }
    }
    static class Formateador {
        String convertir(String texto) { return "Informe: " + texto.strip(); }
    }
    static class Salida {
        void escribir(String texto) { System.out.println(texto); }
    }
    static class Publicador { // Fachada: coordina; no absorbe los tres trabajos.
        private final Validador validador = new Validador();
        private final Formateador formato = new Formateador();
        private final Salida salida = new Salida();
        void publicar(String texto) {
            validador.validar(texto);
            salida.escribir(formato.convertir(texto));
        }
    }
    public static void main(String[] args) {
        new Publicador().publicar("  Ventas  ");
    }
}
```

**Salida esperada:**

```text
Informe: Ventas
```

El cliente conoce publicar(); el subsistema conserva validación, formato y salida como responsabilidades distintas.

### 2.6. Flyweight · `FlyweightDemo.java`

```java
package edu.ups.pap.u01.patrones.estructurales;

import java.util.HashMap;
import java.util.Map;

/** Flyweight: comparte estilos, mientras la posición pertenece a cada uso. */
public class FlyweightDemo {
    record Estilo(String color) {
        void dibujar(int x, int y) {
            System.out.println(color + " en " + x + "," + y); // Estado extrínseco.
        }
    }
    static class FabricaEstilos {
        private final Map<String, Estilo> estilos = new HashMap<>();
        Estilo obtener(String color) {
            return estilos.computeIfAbsent(color, Estilo::new);
        }
    }
    public static void main(String[] args) {
        FabricaEstilos fabrica = new FabricaEstilos();
        Estilo a = fabrica.obtener("azul");
        Estilo b = fabrica.obtener("azul");
        a.dibujar(1, 2);
        b.dibujar(8, 9);
        System.out.println("Estilo compartido: " + (a == b));
    }
}
```

**Salida esperada:**

```text
azul en 1,2
azul en 8,9
Estilo compartido: true
```

El estilo es el objeto compartido; las coordenadas no se almacenan dentro de él.

### 2.7. Proxy · `ProxyDemo.java`

```java
package edu.ups.pap.u01.patrones.estructurales;

/** Proxy virtual: crea el documento real únicamente en el primer acceso. */
public class ProxyDemo {
    interface Documento { String leer(); }
    static class DocumentoReal implements Documento {
        DocumentoReal() { System.out.println("Cargando documento"); }
        public String leer() { return "Contenido"; }
    }
    static class DocumentoProxy implements Documento {
        private DocumentoReal real;
        public String leer() {
            if (real == null) real = new DocumentoReal(); // Carga diferida.
            return real.leer();
        }
    }
    public static void main(String[] args) {
        Documento documento = new DocumentoProxy();
        System.out.println("Proxy creado");
        System.out.println(documento.leer());
        System.out.println(documento.leer());
    }
}
```

**Salida esperada:**

```text
Proxy creado
Cargando documento
Contenido
Contenido
```

La carga ocurre una sola vez al leer; construir el proxy no crea todavía el objeto real.

## Referencias técnicas

- Gamma, E.; Helm, R.; Johnson, R.; Vlissides, J. *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994. Catálogo original de 23 patrones.
- [Ficha y contenido de la obra — Addison-Wesley / InformIT](https://www.informit.com/store/design-patterns-elements-of-reusable-object-oriented-software-9780201633610)
- [Catálogo complementario — Refactoring.Guru](https://refactoring.guru/es/design-patterns/catalog)
- [API Java](https://docs.oracle.com/en/java/javase/17/docs/api/index.html)
