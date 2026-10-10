# Programación Aplicada

## 1.1c. Boxing, unboxing, for-each y varargs

![UPS · Carrera de Computación](../../../assets/logos/ups-computacion.png)

**Autor:** Ing. Pablo Torres, Mgtr.  
**Unidad:** 1. Programación genérica

[Inicio del curso](../../../README.md) · [Ver en el navegador](index.html)

---

## 1. Conceptos y ejemplos

<a id="concepto-1"></a>

### 1.1. Primitivos y clases envolventes

Un **tipo primitivo** representa directamente un valor, como un entero o un booleano. Una **clase envolvente** permite representar ese valor mediante un objeto. Esta distinción importa porque los argumentos de los genéricos deben ser tipos de referencia: una colección de enteros se declara `List<Integer>`, no `List<int>`.

| Primitivo | Clase envolvente | Primitivo | Clase envolvente |
|---|---|---|---|
| `byte` | `Byte` | `long` | `Long` |
| `short` | `Short` | `float` | `Float` |
| `int` | `Integer` | `double` | `Double` |
| `char` | `Character` | `boolean` | `Boolean` |

Los envolventes son inmutables: una operación que cambia una variable `Integer` no modifica el objeto anterior. Además, una referencia envolvente puede ser `null`, mientras que una variable primitiva no puede.

### 1.2. Boxing y unboxing

**Boxing** es la conversión de un primitivo a su envolvente; **unboxing**, la conversión inversa. Cuando el compilador incorpora estas conversiones automáticamente, se habla de autoboxing y auto-unboxing.

```java
Integer cantidad = 12;       // Autoboxing.
int recuperada = cantidad;  // Unboxing.

Integer explicita = Integer.valueOf(12);
int primitivo = explicita.intValue();
```

Una operación como `cantidad + 1` utiliza el valor primitivo. Si el resultado se asigna nuevamente a `Integer`, interviene también boxing. En recorridos intensivos, estas conversiones y el almacenamiento de referencias pueden tener un costo; usa primitivos cuando no necesites un contrato basado en objetos.

<a id="concepto-2"></a>

### 1.3. Nulos, igualdad y conversión de texto

El unboxing de `null` lanza `NullPointerException`. No representa un cero ni un dato ausente que Java pueda completar automáticamente.

```java
Integer pendiente = null;
// int valor = pendiente; // NullPointerException durante la ejecución.
```

Entre dos referencias envolventes, `==` compara identidad. Algunos valores pueden compartir objetos por caché; por ello, no debes usar ese resultado como comparación numérica. Utiliza `equals` para envolventes del mismo tipo, o convierte conscientemente a primitivos cuando corresponda.

`Integer.valueOf(5).equals(Long.valueOf(5))` es `false`: `equals` también exige compatibilidad de tipo. Si uno de los operandos de `==` es primitivo, puede producirse unboxing del otro, con el riesgo de nulos mencionado.

El **parsing** interpreta una representación textual. `Integer.parseInt("12")` devuelve `int` y puede lanzar `NumberFormatException`; no es boxing. `Integer.valueOf("12")` interpreta texto y devuelve `Integer`.

### 1.4. Variables y alcance en un recorrido

Una variable local tiene un tipo y un ámbito. En `for (Integer valor : datos)`, `valor` existe dentro del cuerpo del bucle y recibe cada elemento sucesivamente. Reasignarla no reemplaza una posición de la colección.

```java
Integer[] datos = {1, 2};
for (Integer valor : datos) {
    valor = 0;
}
// datos sigue conteniendo 1 y 2.
```

Si el elemento fuera un objeto modificable, sí podrías cambiar su estado mediante sus métodos. Reasignar la variable y modificar el objeto al que apunta son operaciones distintas.

<a id="concepto-3"></a>

### 1.5. for-each: recorrido por elementos

El **for mejorado**, o `for-each`, recorre arreglos y objetos que implementan `Iterable`. Expresa que interesa cada elemento, sin manejar explícitamente una posición.

```java
for (String nombre : List.of("Ana", "Luis")) {
    System.out.println(nombre);
}
```

Conviene usar un `for` con índice cuando se necesita reemplazar posiciones o relacionar elementos por su ubicación. Para eliminar durante la iteración, utiliza una operación apropiada de la colección o un iterador que admita eliminación. Modificar estructuralmente un `ArrayList` mientras se recorre con `for-each` puede producir `ConcurrentModificationException`.

El orden del recorrido depende del contenedor: una lista conserva su secuencia; no debe suponerse un orden general para cualquier colección. El bucle no introduce concurrencia ni paralelismo.

<a id="concepto-4"></a>

### 1.6. Varargs: cantidad variable de argumentos

**Varargs** permite recibir cero o más argumentos mediante un parámetro declarado con `...`. Dentro del método se trata como un arreglo. Debe existir como máximo un parámetro varargs y ocupar la última posición.

```java
static int contar(String... nombres) {
    return nombres.length;
}
// contar();                 devuelve 0
// contar("Ana", "Luis");    devuelve 2
```

El cliente también puede pasar un arreglo existente. En ese caso, modificar el arreglo recibido afecta al mismo arreglo del cliente. Varargs no implica copia defensiva.

`int...` permite números primitivos sin boxing para formar el arreglo; `Integer...` admite referencias, incluidos elementos nulos. `args` es un nombre convencional, no una palabra reservada. En `main(String[] args)`, representa los argumentos de la línea de comandos; la sintaxis habitual de `main` ya recibe un arreglo.

### 1.7. Varargs genéricos y advertencias

Una declaración `<T> void procesar(T... valores)` combina arreglos con un tipo cuyo argumento se borra. Puede generar una advertencia por posible **contaminación del heap**: una referencia parametrizada termina apuntando a un contenido incompatible con su contrato, normalmente por operaciones inseguras con arreglos o tipos crudos.

`@SafeVarargs` declara que la implementación no realiza operaciones inseguras con ese arreglo; no verifica ni repara el código automáticamente. Solo se permite en constructores y métodos estáticos, finales o privados. Para recibir colecciones genéricas, una firma con `List<T>` suele evitar esta dificultad y expresar mejor el contrato.


---

<a id="demostracion"></a>

## 2. Demostración: reunir y recorrer valores

El ejemplo reúne enteros mediante varargs, los almacena en una lista y los suma con un `for-each`. También comprueba que reasignar la variable del bucle no reemplaza los elementos de la lista.

**Archivo:** `app/src/main/java/edu/ups/pap/u01/ConversionesDemo.java`.

```java
package edu.ups.pap.u01;

import java.util.ArrayList;
import java.util.List;

/** Relaciona autoboxing, unboxing, recorrido for-each y varargs. */
public class ConversionesDemo {
    /**
     * Recibe cero o más Integer y devuelve una lista nueva.
     * @throws IllegalArgumentException si el arreglo o un elemento son nulos
     */
    static List<Integer> reunir(Integer... valores) {
        if (valores == null) throw new IllegalArgumentException("Arreglo nulo");
        List<Integer> resultado = new ArrayList<>();
        for (Integer valor : valores) { // Recorre el arreglo generado por varargs.
            if (valor == null) throw new IllegalArgumentException("Elemento nulo");
            resultado.add(valor);
        }
        return resultado;
    }

    /** Suma mediante unboxing; recibe una lista validada por reunir(). */
    static long sumar(List<Integer> valores) {
        long total = 0;
        for (int valor : valores) { // Cada Integer se convierte a int.
            total += valor;
        }
        return total;
    }

    public static void main(String[] args) {
        List<Integer> valores = reunir(10, 20, 30); // int -> Integer: autoboxing.
        System.out.println("Valores: " + valores);
        System.out.println("Suma: " + sumar(valores));
        System.out.println("Sin argumentos: " + reunir().size());

        for (Integer valor : valores) {
            valor = 0; // Reasigna la variable local, no una posición de la lista.
        }
        System.out.println("Después del recorrido: " + valores);
        try {
            reunir(10, null); // Se rechaza antes de intentar el unboxing.
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
```

Desde la raíz del proyecto Java, con JDK 17 o superior:

```bash
java app/src/main/java/edu/ups/pap/u01/ConversionesDemo.java
```

**Salida esperada:**

```text
Valores: [10, 20, 30]
Suma: 60
Sin argumentos: 0
Después del recorrido: [10, 20, 30]
Elemento nulo
```

`reunir(10, 20, 30)` realiza boxing para recibir `Integer`; el recorrido de suma realiza unboxing para trabajar con `int`. La llamada sin argumentos crea un arreglo vacío, y un elemento nulo se rechaza antes de la suma. `valor = 0` solo modifica la variable local del recorrido.

## Referencias técnicas

- [Autoboxing y unboxing — Dev.java](https://dev.java/learn/language/constructs/numbers-strings/autoboxing/)
- [Bucle for — Oracle](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/for.html)
- [Varargs — Oracle](https://docs.oracle.com/javase/tutorial/java/javaOO/arguments.html)
- [SafeVarargs — API Java](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/SafeVarargs.html)
