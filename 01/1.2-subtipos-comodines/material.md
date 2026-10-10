# Programación Aplicada

## 1.2. Subtipos y comodines

![UPS · Carrera de Computación](../../assets/logos/ups-computacion.png)

**Autor:** Ing. Pablo Torres, Mgtr.  
**Unidad:** 1. Programación genérica

[Inicio del curso](../../README.md) · [Ver en el navegador](index.html)

---

## 1. Conceptos y ejemplos

<a id="concepto-1"></a>

### 1.1. Subtipado y sustitución

Un tipo es **subtipo** de otro cuando puede utilizarse donde se espera el tipo más general, respetando su contrato. `Integer` es subtipo de `Number`, de modo que una referencia `Number` puede apuntar a un `Integer`.

```java
Number numero = Integer.valueOf(8);
```

La relación entre clases no se traslada automáticamente a sus contenedores. Aunque `Integer` hereda de `Number`, `List<Integer>` no es subtipo de `List<Number>`. Los tipos genéricos de Java son **invariantes** respecto de sus argumentos.

```java
List<Integer> enteros = new ArrayList<>();
// List<Number> numeros = enteros; // No compila.
```

Si esta asignación fuera válida, se podría insertar un `Double` usando `numeros`, y luego recuperar ese mismo elemento como `Integer` desde `enteros`. La prohibición conserva el contrato de la lista original.

### 1.2. Qué representa un comodín

El **comodín `?`** representa un argumento de tipo desconocido. Permite declarar referencias que aceptan diferentes parametrizaciones, restringiendo las operaciones a las que resultan seguras para todas ellas.

`List<?>` significa «lista de algún tipo que no conocemos», no «lista de cualquier objeto». Una variable de ese tipo puede referirse a `List<String>` o a `List<Integer>`, pero no permite insertar indistintamente cadenas y enteros. Al leer, solo se garantiza `Object`.

`List<Object>`, en cambio, tiene un argumento conocido: `Object`. Admite elementos de tipos diferentes, pero no puede recibir por asignación una `List<String>` debido a la invariancia.

<a id="concepto-2"></a>

### 1.3. Cota superior: ? extends T

`List<? extends Number>` acepta una lista cuyo argumento sea `Number` o un subtipo suyo. Es útil para un método que **consume información de la lista**, es decir, que lee los elementos que esa lista produce.

```java
List<? extends Number> origen = List.of(2, 4, 6);
Number primero = origen.get(0);
// origen.add(3.5); // No compila: el tipo concreto podría ser Integer.
```

Se puede leer como `Number`, pero no insertar un `Integer` ni un `Double` arbitrario: se desconoce el argumento exacto. La excepción de tipos es `null`, aunque la colección concreta puede rechazarlo.

Esto no convierte la lista en inmutable. Operaciones como `clear()` o `remove()` pueden seguir disponibles si la implementación las admite. Además, otras referencias podrían modificarla. La restricción afecta a las operaciones seguras a través de esa referencia.

<a id="concepto-3"></a>

### 1.4. Cota inferior: ? super T

`List<? super Integer>` acepta `List<Integer>`, `List<Number>` o `List<Object>`. Permite insertar enteros porque todos esos destinos pueden almacenarlos.

```java
List<? super Integer> destino = new ArrayList<Number>();
destino.add(7);
Object recuperado = destino.get(0);
```

Al recuperar un elemento solo se garantiza `Object`: si el destino real es `List<Object>`, podría contener cadenas agregadas previamente. La cota inferior define lo que podemos introducir con seguridad, no el tipo de todos los elementos que ya existen.

### 1.5. PECS y elección de la firma

La regla **PECS** resume dos usos: *Producer Extends, Consumer Super*. La perspectiva es la del algoritmo: el origen produce valores para el método y el destino consume los valores que el método escribe.

| Firma | Lectura garantizada | Inserción segura por tipo | Uso habitual |
|---|---|---|---|
| `List<T>` | `T` | `T` | Leer y escribir el mismo tipo conocido |
| `List<? extends T>` | `T` | Ningún valor no nulo arbitrario | Origen de datos |
| `List<? super T>` | `Object` | `T` y sus subtipos | Destino de datos |
| `List<?>` | `Object` | Ningún valor no nulo arbitrario | Inspección independiente del tipo |

La tabla describe seguridad de tipos; una lista inmodificable puede rechazar cualquier escritura en ejecución.

La firma `<T> void copiar(List<? extends T> origen, List<? super T> destino)` conecta ambas posiciones mediante `T`. Usar únicamente dos `List<?>` perdería esa relación: no habría garantía de que lo leído del origen sea aceptable en el destino.

<a id="concepto-4"></a>

### 1.6. Parámetro declarado frente a comodín

`<T extends Number>` **declara un nombre** que puede reutilizarse en la firma y en el cuerpo. `? extends Number` expresa un tipo desconocido en una posición de uso. Elige un parámetro cuando necesites relacionar entradas o conservar ese tipo como resultado.

Evita devolver comodines sin una necesidad concreta: el cliente tendría que trabajar con un tipo desconocido. Tampoco uses casts a tipos crudos para «resolver» una incompatibilidad; eso oculta el problema que el compilador está detectando.

En tipos anidados, cada nivel debe analizarse por separado. `List<Caja<Double>>` no se convierte automáticamente en `List<Caja<Number>>`. Un contenedor con valor acotado puede escribirse `Caja<? extends Number>`, y una lista que lo produce requiere su propia cota: `List<? extends Caja<? extends Number>>`. Cada comodín expresa qué argumento se desconoce en ese nivel; no se propaga automáticamente hacia los demás.


### 1.7. Comodines en una función

En `Function<? super T, ? extends R>`, el primer argumento representa lo que la función recibe y el segundo lo que produce. Una función que recibe `Number` puede procesar un `Integer`; una función que devuelve `String` puede producir un resultado utilizado como `CharSequence`.

Estas cotas permiten reutilizar funciones ya existentes. No amplían los tipos que puede contener el objeto original ni convierten una función de `Integer` en una función válida para cualquier `Number`.


---

<a id="demostracion"></a>

## 2. Demostración: consumir y producir tipos compatibles

La transferencia de listas utiliza `extends` para el origen y `super` para el destino. La transformación usa esas mismas relaciones en los parámetros de `Function`.

**Archivo:** `app/src/main/java/edu/ups/pap/u01/ComodinesDemo.java`.

```java
package edu.ups.pap.u01;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** Demuestra cotas de lectura, escritura y transformación de valores. */
public class ComodinesDemo {
    /**
     * El origen produce T y el destino consume T: regla PECS.
     * Copia referencias, no clona los objetos almacenados.
     */
    static <T> void copiar(List<? extends T> origen, List<? super T> destino) {
        Objects.requireNonNull(origen);
        Objects.requireNonNull(destino);
        // La instantánea permite que origen y destino compartan almacenamiento.
        for (T valor : new ArrayList<T>(origen)) destino.add(valor);
    }

    static final class Contenedor<T> {
        private final T valor;
        Contenedor(T valor) { this.valor = Objects.requireNonNull(valor); }
        T obtener() { return valor; }

        /** La función consume T o un supertipo y produce R o un subtipo. */
        <R> Contenedor<R> mapear(Function<? super T, ? extends R> funcion) {
            Objects.requireNonNull(funcion);
            return new Contenedor<>(funcion.apply(valor));
        }
    }

    public static void main(String[] args) {
        List<Integer> enteros = List.of(2, 4);
        List<Number> numeros = new ArrayList<>();
        copiar(enteros, numeros); // Integer puede escribirse en un destino Number.
        System.out.println("Números: " + numeros);

        Contenedor<Integer> origen = new Contenedor<>(12);
        Function<Number, String> formato = n -> "Valor: " + n;
        // Number acepta Integer; String puede utilizarse como CharSequence.
        Contenedor<CharSequence> texto = origen.<CharSequence>mapear(formato);
        System.out.println(texto.obtener());
        System.out.println("Original: " + origen.obtener());
        // copiar(List.of(2.5), new ArrayList<Integer>()); // No compila.
    }
}
```

Desde la raíz del proyecto Java, con JDK 17 o superior:

```bash
java app/src/main/java/edu/ups/pap/u01/ComodinesDemo.java
```

**Salida esperada:**

```text
Números: [2, 4]
Valor: 12
Original: 12
```

El tipo explícito `<CharSequence>` hace visible que el retorno declarado puede ser más general que el `String` producido por la función. Sin comodines, `Function<Number, String>` no coincide con `Function<Integer, CharSequence>`.

---

<a id="pc"></a>

## 3. PC1.2 · Transformaciones flexibles y reporte numérico

Continúa sobre `Lectura<T>` y su método `transformar`, implementado en PC1.1b. CampusMonitor utilizará una función común para distintos tipos numéricos y reunirá sus lecturas en un reporte.

### 3.1. Ampliación de Lectura y clase de reporte

**Actualiza la firma del método existente**, conservando su comportamiento y validaciones:

```java
public <R> Lectura<R> transformar(
        Function<? super T, ? extends R> conversor, String nuevaUnidad)
```

Reemplaza la firma anterior; no agregues una sobrecarga con el mismo nombre y parámetros borrados, porque ambas tendrían la misma firma después del borrado de tipos.

Crea la **clase pública de utilidades `ReporteLecturas`** en `edu.ups.pap.servicio`, con este método público estático:

```java
public static void mostrar(List<? extends Lectura<? extends Number>> lecturas)
```

Debe imprimir identificador, valor y unidad de cada lectura y la cantidad total. Una lista vacía muestra cantidad cero. Rechaza lista o elementos nulos con `IllegalArgumentException` y mensaje descriptivo. No modifiques la lista ni sumes magnitudes con unidades distintas. El primer comodín permite distintos tipos de elementos compatibles con la lectura numérica; el segundo permite distintos tipos de valores numéricos.

### 3.2. Actividades en App.java

Agrega `private static void runComodines()` y llámalo desde `main`, conservando los métodos anteriores.

1. Define una única `Function<Number, String>` que produzca un texto a partir del número recibido. Utiliza esa misma función para transformar una temperatura `Double` y una ocupación `Integer`; no dupliques la función.
2. En una de las llamadas, solicita el resultado como `Lectura<CharSequence>` utilizando el argumento de tipo explícito. Muestra ambos resultados con unidad `texto`.
3. Crea una lista modificable `List<Lectura<? extends Number>>` con las dos lecturas numéricas originales y pásala a `ReporteLecturas.mostrar`.
4. Llama al reporte con una lista vacía y comprueba su resultado. Comprueba por separado lista nula y lista con un elemento nulo, capturando las excepciones.

El reporte de las lecturas originales debe mostrar:

```text
S01 | 22.5 °C
S02 | 18 personas
Total de lecturas: 2
```

### 3.3. Estructura resultante

Archivos relevantes acumulados; conserva la configuración y las evidencias anteriores. `ConversionesDemo` corresponde al contenido 1.1c.

```text
icc-pap-campusmonitor-apellido/
├── app/src/main/java/edu/ups/pap/
│   ├── App.java
│   ├── modelo/
│   │   └── Lectura.java
│   ├── servicio/
│   │   └── ReporteLecturas.java
│   └── u01/
│       ├── TiposDemo.java
│       ├── MetodosDemo.java
│       ├── ConversionesDemo.java
│       └── ComodinesDemo.java
└── evidencias/
    ├── PC1.1a.md
    ├── PC1.1b.md
    └── PC1.2.md
```

Conserva el paquete real de tu `Lectura` si ya estaba en `u01`; ajusta el import del reporte sin duplicar el modelo.

### 3.4. Evidencias

Sube los cambios al repositorio remoto. En `evidencias/PC1.2.md` incluye únicamente:

#### 1. Implementación realizada

En un párrafo breve, explica por qué una `Function<Number, String>` ahora sirve para ambas lecturas y qué permiten `super` y `extends` en la firma actualizada.

#### 2. Ejecución correcta

Agrega una captura con las dos transformaciones y el reporte de las lecturas originales, y otra con el reporte vacío y los mensajes de los casos nulos. Debajo, explica en una o dos frases por qué el reporte puede reunir tipos numéricos distintos sin sumarlos ni modificar sus valores.

## Referencias técnicas

- [Comodines — Dev.java](https://dev.java/learn/language/fp/generics/wildcards/)
- [Herencia y subtipos — Oracle](https://docs.oracle.com/javase/tutorial/java/generics/inheritance.html)
- [Guía de uso de comodines — Oracle](https://docs.oracle.com/javase/tutorial/java/generics/wildcardGuidelines.html)
- [Function — API Java](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/Function.html)
