# Programación Aplicada

## 1.4a. Patrones creacionales

![UPS · Carrera de Computación](../../../assets/logos/ups-computacion.png)

**Autor:** Ing. Pablo Torres, Mgtr.  
**Unidad:** 1. Programación genérica



---

## 1. Conceptos y ejemplos



### 1.1. Fundamentos

Un **patrón de diseño** describe una solución recurrente a un problema de responsabilidades y colaboración entre objetos. No es una biblioteca ni una plantilla que deba aplicarse a todo programa. Identifica el problema, los participantes, el punto que puede cambiar y el costo de separarlo.

Los patrones **creacionales** organizan cómo se construyen y suministran los objetos. Separan decisiones de creación del código que utiliza los productos. El objetivo no es ocultar cada `new`, sino evitar que los clientes repitan decisiones de construcción que necesitan variar.

Este bloque cubre los cinco patrones creacionales del catálogo clásico GoF. Los ejemplos son originales y se ejecutan en consola. Las clases auxiliares están anidadas para que cada archivo sea autocontenido; en una aplicación pueden separarse por responsabilidad.

| N.º | Patrón | Demostración |
|---|---|---|
| 1 | Factory Method | `FactoryMethodDemo.java` |
| 2 | Abstract Factory | `AbstractFactoryDemo.java` |
| 3 | Builder | `BuilderDemo.java` |
| 4 | Prototype | `PrototypeDemo.java` |
| 5 | Singleton | `SingletonDemo.java` |



### 1.2. Factory Method

**Intención.** Permitir que una subclase decida qué producto construir, mientras la clase creadora conserva un procedimiento común. Sus participantes son producto, productos concretos, creador y creadores concretos. El punto de variación es una operación de creación que puede redefinirse.

Una aplicación de exportación puede preparar un documento usando un exportador obtenido mediante ese método. Agregar otra variante exige otro creador, no modificar el procedimiento base. Su costo es aumentar la cantidad de subclases. Un método estático con un `switch` que devuelve diferentes objetos es una fábrica simple, no basta para demostrar Factory Method. Los genéricos pueden relacionar un creador con su familia de productos, aunque no son un requisito del patrón.



### 1.3. Abstract Factory

**Intención.** Construir familias de productos relacionados sin acoplar al cliente a sus clases concretas. La fábrica abstracta declara una operación para cada tipo de producto; cada fábrica concreta crea variantes compatibles de toda la familia.

En una interfaz visual, una familia puede contener botón y menú de un mismo tema. El cliente recibe una fábrica y obtiene ambos elementos de ella, evitando mezclar variantes. Es útil cuando cambian conjuntos completos de objetos. Agregar una familia suele ser sencillo; agregar un nuevo tipo de producto obliga a extender todas las fábricas. A diferencia de Factory Method, el énfasis está en coordinar varios productos, no únicamente en redefinir una operación de creación.



### 1.4. Builder

**Intención.** Separar la construcción por etapas de un objeto de su uso posterior. El constructor progresivo conserva la configuración, permite establecer opciones y crea el producto final cuando su estado es válido. Un director puede definir secuencias reutilizables de construcción, pero no es obligatorio en la variante fluida del ejemplo.

Resulta útil con muchas opciones, restricciones entre campos o distintas representaciones. No es necesario para cualquier objeto pequeño: puede añadir complejidad donde basta un constructor. El producto no se vuelve inmutable automáticamente; eso depende de sus campos y de las copias defensivas. El ejemplo combina un builder mutable con un producto que copia su lista de secciones.

### 1.5. Prototype

**Intención.** Crear objetos a partir de otros ya configurados, utilizando una operación de copia. El cliente conoce el contrato de clonación o copia y no necesita reconstruir todas las decisiones de configuración del objeto original.

Debe definirse qué se comparte y qué se duplica. Una copia superficial de una lista comparte los elementos; una copia profunda también duplica el estado mutable que deba independizarse. Java no obliga a usar `Cloneable`: un método `copiar()` o un constructor de copia pueden expresar el contrato con mayor claridad. Es útil para plantillas configuradas; puede ser costoso o ambiguo cuando el objeto representa conexiones o recursos que no deben duplicarse. En el ejemplo, las cadenas son inmutables y se copia la lista.

### 1.6. Singleton

**Intención.** Proporcionar una única instancia de un servicio dentro de un ámbito definido y un punto de acceso a ella. Debe distinguirse unicidad de ausencia de estado mutable y de seguridad frente a concurrencia.

La variante con `enum` evita que el cliente cree instancias mediante `new`. La instancia corresponde a esa clase cargada; no implica una única instancia distribuida entre procesos o servidores. El acceso global puede ocultar dependencias y dificultar las pruebas. Antes de utilizarlo, considera crear un objeto y entregarlo explícitamente a quienes lo necesitan. Compartir una instancia mediante inyección de dependencias no exige convertir la clase en Singleton. El ejemplo usa configuración inmutable para no introducir problemas de sincronización ajenos al patrón.

### 1.7. Criterios de elección

Factory Method especializa una operación de creación mediante herencia; Abstract Factory entrega familias relacionadas; Builder reúne pasos antes de construir; Prototype parte de otro objeto configurado; Singleton controla la instancia accesible. Decide primero qué problema de creación existe. Compartir un objeto puede resolverse suministrándolo por constructor sin volverlo global.

---



## 2. Demostraciones comentadas

Ubica los siguientes archivos en `app/src/main/java/edu/ups/pap/u01/patrones/creacionales/`. Cada archivo contiene su propia clase principal y sus colaboradores internos; es independiente de los demás demos.

Con JDK 17 o superior, desde la raíz del proyecto Java, ejecuta un archivo así; sustituye el nombre para probar los demás:

```bash
java app/src/main/java/edu/ups/pap/u01/patrones/creacionales/FactoryMethodDemo.java
```

Los comentarios identifican los roles y las decisiones del patrón. Las lambdas implementan interfaces funcionales y los `record` agrupan datos mediante accesores generados; no son requisitos de los patrones. Los ejemplos son secuenciales y se limitan a consola.

### 2.1. Factory Method · `FactoryMethodDemo.java`

```java
package edu.ups.pap.u01.patrones.creacionales;

/** Factory Method: las subclases eligen el producto de un proceso común. */
public class FactoryMethodDemo {
    interface Documento { String renderizar(); } // Producto abstracto.
    static class DocumentoTexto implements Documento {
        public String renderizar() { return "Informe TXT"; }
    }
    static class DocumentoHtml implements Documento {
        public String renderizar() { return "<h1>Informe</h1>"; }
    }
    static abstract class Exportador {
        /** Punto de extensión: cada creador suministra su producto. */
        protected abstract Documento crearDocumento();
        final void exportar() {
            Documento documento = crearDocumento();
            System.out.println(documento.renderizar()); // Proceso estable.
        }
    }
    static class ExportadorTexto extends Exportador {
        protected Documento crearDocumento() { return new DocumentoTexto(); }
    }
    static class ExportadorHtml extends Exportador {
        protected Documento crearDocumento() { return new DocumentoHtml(); }
    }
    public static void main(String[] args) {
        new ExportadorTexto().exportar();
        new ExportadorHtml().exportar();
    }
}
```

**Salida esperada:**

```text
Informe TXT
<h1>Informe</h1>
```

`exportar()` conserva su implementación; solo cambia `crearDocumento()` en cada subclase.

### 2.2. Abstract Factory · `AbstractFactoryDemo.java`

```java
package edu.ups.pap.u01.patrones.creacionales;

/** Abstract Factory: una fábrica mantiene consistente una familia visual. */
public class AbstractFactoryDemo {
    interface Boton { String dibujar(); }
    interface Menu { String dibujar(); }
    interface Tema { // Fábrica abstracta con dos clases de producto.
        Boton crearBoton();
        Menu crearMenu();
    }
    static class TemaClaro implements Tema {
        public Boton crearBoton() { return () -> "Botón claro"; }
        public Menu crearMenu() { return () -> "Menú claro"; }
    }
    static class TemaOscuro implements Tema {
        public Boton crearBoton() { return () -> "Botón oscuro"; }
        public Menu crearMenu() { return () -> "Menú oscuro"; }
    }
    /** El cliente utiliza contratos; las lambdas son productos concretos. */
    static void mostrar(Tema tema) {
        System.out.println(tema.crearBoton().dibujar() + " / "
                + tema.crearMenu().dibujar());
    }
    public static void main(String[] args) {
        mostrar(new TemaClaro());
        mostrar(new TemaOscuro());
    }
}
```

**Salida esperada:**

```text
Botón claro / Menú claro
Botón oscuro / Menú oscuro
```

El cliente recibe una sola fábrica para obtener dos productos compatibles; no selecciona sus clases individualmente.

### 2.3. Builder · `BuilderDemo.java`

```java
package edu.ups.pap.u01.patrones.creacionales;

import java.util.ArrayList;
import java.util.List;

/** Builder fluido: configura por etapas y entrega un informe independiente. */
public class BuilderDemo {
    record Informe(String titulo, List<String> secciones) {
        Informe { secciones = List.copyOf(secciones); } // Copia defensiva.
    }
    static class ConstructorInforme {
        private String titulo;
        private final List<String> secciones = new ArrayList<>();
        ConstructorInforme titulo(String valor) {
            titulo = valor;
            return this; // Permite encadenar etapas.
        }
        ConstructorInforme seccion(String valor) {
            secciones.add(valor);
            return this;
        }
        /** Valida el estado reunido antes de crear el producto. */
        Informe construir() {
            if (titulo == null || titulo.isBlank()) {
                throw new IllegalArgumentException("Título obligatorio");
            }
            return new Informe(titulo, secciones);
        }
    }
    public static void main(String[] args) {
        Informe informe = new ConstructorInforme().titulo("Resumen")
                .seccion("Datos").seccion("Conclusiones").construir();
        System.out.println(informe.titulo() + ": " + informe.secciones());
    }
}
```

**Salida esperada:**

```text
Resumen: [Datos, Conclusiones]
```

Las etapas configuran el builder; el producto final contiene una copia de las secciones y no depende de cambios posteriores en el builder.

### 2.4. Prototype · `PrototypeDemo.java`

```java
package edu.ups.pap.u01.patrones.creacionales;

import java.util.ArrayList;
import java.util.List;

/** Prototype: duplica una plantilla conservando independencia de su lista. */
public class PrototypeDemo {
    interface Copiable<T> { T copiar(); } // El genérico conserva el tipo del clon.
    static class Plantilla implements Copiable<Plantilla> {
        private final List<String> campos;
        Plantilla(List<String> campos) { this.campos = new ArrayList<>(campos); }
        void agregar(String campo) { campos.add(campo); }
        public Plantilla copiar() {
            return new Plantilla(campos); // Nueva lista; cadenas compartidas seguras.
        }
        public String toString() { return campos.toString(); }
    }
    public static void main(String[] args) {
        Plantilla original = new Plantilla(List.of("nombre"));
        Plantilla copia = original.copiar();
        copia.agregar("correo");
        System.out.println("Original: " + original);
        System.out.println("Copia: " + copia);
    }
}
```

**Salida esperada:**

```text
Original: [nombre]
Copia: [nombre, correo]
```

Modificar la lista de la copia no modifica la del original; el contrato no promete copiar profundamente objetos arbitrarios.

### 2.5. Singleton · `SingletonDemo.java`

```java
package edu.ups.pap.u01.patrones.creacionales;

/** Singleton mediante enum: acceso a una configuración compartida. */
public class SingletonDemo {
    enum Configuracion {
        INSTANCIA;
        private final String formato = "texto"; // Estado inmutable del ejemplo.
        String formato() { return formato; }
    }
    public static void main(String[] args) {
        Configuracion a = Configuracion.INSTANCIA;
        Configuracion b = Configuracion.INSTANCIA;
        System.out.println("Misma instancia: " + (a == b));
        System.out.println("Formato: " + a.formato());
        // La unicidad no haría seguros futuros atributos mutables por sí sola.
    }
}
```

**Salida esperada:**

```text
Misma instancia: true
Formato: texto
```

Las dos variables apuntan a la misma instancia; no se demuestra exclusión mutua ni coordinación entre procesos.

## Referencias técnicas

- Gamma, E.; Helm, R.; Johnson, R.; Vlissides, J. *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994. Catálogo original de 23 patrones.
- [Ficha y contenido de la obra — Addison-Wesley / InformIT](https://www.informit.com/store/design-patterns-elements-of-reusable-object-oriented-software-9780201633610)
- [Catálogo complementario — Refactoring.Guru](https://refactoring.guru/es/design-patterns/catalog)
- [API Java](https://docs.oracle.com/en/java/javase/17/docs/api/index.html)
