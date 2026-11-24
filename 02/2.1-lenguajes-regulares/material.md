# Programación Aplicada

![UPS · Carrera de Computación](../../assets/logos/ups-computacion.png)

## 2.1. Fundamentos de los lenguajes regulares

**Autor:** Ing. Pablo Torres, Mgtr.  
**Unidad:** 2. Expresiones regulares  
**Tema:** 2.1. Fundamentos de los lenguajes regulares

Los lenguajes regulares permiten describir conjuntos de cadenas mediante reglas precisas y reconocerlas con modelos de cómputo de memoria finita. Constituyen la base teórica de las expresiones regulares utilizadas para validar formatos, localizar patrones y procesar texto.

Este material desarrolla los conceptos de alfabeto, cadena, lenguaje, operaciones, expresiones regulares y autómatas finitos. Los ejemplos parten de requisitos de reconocimiento y muestran cómo formularlos, verificarlos y distinguir sus limitaciones. La construcción detallada de expresiones regulares en Java corresponde al tema 2.2.

<a id="indice"></a>

## Índice

1. [Fundamentos de los lenguajes formales](#fundamentos)
2. [Alfabetos, cadenas y lenguajes](#alfabetos)
3. [Operaciones sobre cadenas y lenguajes](#operaciones)
4. [Lenguajes regulares y expresiones regulares](#regulares)
5. [Autómatas finitos](#automatas)
6. [Equivalencia de modelos y criterios de reconocimiento](#equivalencia)
7. [Casos prácticos y trazas de reconocimiento](#casos)
8. [Introducción al reconocimiento en Java](#java)
9. [Análisis y criterios de aplicación](#analisis)
10. [Referencias](#referencias)

---

<a id="fundamentos"></a>

## 1. Fundamentos de los lenguajes formales

Un **lenguaje formal** es un conjunto de cadenas construidas con símbolos de un alfabeto y seleccionadas según una definición matemática. Su descripción establece qué secuencias pertenecen al lenguaje, independientemente de que tengan significado en un idioma natural.

Por ejemplo, un sistema puede admitir identificadores formados por una letra `A` seguida de uno o más dígitos. El lenguaje describe todas las cadenas válidas; un reconocedor determina si una entrada concreta pertenece a ese conjunto.

Conviene distinguir tres niveles:

| Nivel | Pregunta | Ejemplo |
|---|---|---|
| Alfabeto | ¿Qué símbolos están disponibles? | `{A, 0, 1}` |
| Lenguaje | ¿Qué secuencias se admiten? | `A0`, `A1`, `A00`, `A01`, ... |
| Reconocedor | ¿Cómo se verifica una entrada? | Un autómata que acepta `A01` y rechaza `01A` |

La definición del lenguaje y el mecanismo que lo reconoce son aspectos diferentes. Se puede representar el mismo lenguaje regular mediante una expresión regular o mediante distintos autómatas finitos.

[Volver al índice](#indice)

---

<a id="alfabetos"></a>

## 2. Alfabetos, cadenas y lenguajes

### 2.1. Alfabeto

Un **alfabeto**, usualmente denotado por `Σ`, es un conjunto finito de símbolos.

- `Σ = {0, 1}`: alfabeto binario.
- `Σ = {a, b}`: alfabeto con dos letras.
- `Σ = {A, 0, 1}`: alfabeto para un ejemplo simplificado de identificadores.

Un símbolo es una unidad del alfabeto. No debe confundirse con una cadena que contiene ese símbolo.

### 2.2. Cadena y longitud

Una **cadena** es una secuencia finita de símbolos de `Σ`. Su longitud, denotada `|w|`, es la cantidad de símbolos que contiene.

Si `Σ = {a, b}`, entonces `a`, `ab`, `bba` y `aaab` son cadenas válidas sobre ese alfabeto. Por ejemplo, `|abba| = 4`.

La **cadena vacía**, escrita `ε`, no contiene símbolos y cumple `|ε| = 0`. No es lo mismo que un espacio en blanco ni que la cadena de dos caracteres `"ε"`.

### 2.3. Conjunto de cadenas y lenguaje

- `Σ*` representa **todas las cadenas finitas** sobre `Σ`, incluida `ε`.
- `Σ+` representa todas las cadenas **no vacías** sobre `Σ`.
- Un **lenguaje** `L` sobre `Σ` es cualquier subconjunto de `Σ*`.

Para `Σ = {0, 1}`:

```text
Σ* = {ε, 0, 1, 00, 01, 10, 11, 000, ...}
Σ+ = {0, 1, 00, 01, 10, 11, 000, ...}
L  = {0, 00, 000, 0000, ...}
```

En el último caso, `L` contiene una o más apariciones del símbolo `0`. Por tanto, `0 ∈ L`, pero `ε ∉ L` y `01 ∉ L`.

**Distinción importante:** `∅` denota un lenguaje sin cadenas. `{ε}` denota un lenguaje con un elemento: la cadena vacía. Sus cardinalidades son 0 y 1, respectivamente.

[Volver al índice](#indice)

---

<a id="operaciones"></a>

## 3. Operaciones sobre cadenas y lenguajes

### 3.1. Concatenación de cadenas

La concatenación coloca una cadena después de otra. Si `u = ab` y `v = ba`, entonces `uv = abba`. En general, `uv` no tiene por qué ser igual a `vu`.

La cadena vacía es el elemento neutro: `wε = εw = w`.

### 3.2. Operaciones sobre lenguajes

Para `L₁ = {a, ab}` y `L₂ = {b, ba}`:

| Operación | Notación | Resultado o interpretación |
|---|---|---|
| Unión | `L₁ ∪ L₂` | `{a, ab, b, ba}` |
| Concatenación | `L₁L₂` | `{ab, aba, abb, abba}` |
| Potencia | `L₁²` | `L₁L₁ = {aa, aab, aba, abab}` |
| Clausura de Kleene | `L₁*` | Cero o más concatenaciones de elementos de `L₁`; incluye `ε` |
| Clausura positiva | `L₁+` | Una o más concatenaciones de elementos de `L₁` |

La **clausura de Kleene** se define como `L* = ⋃ₙ₌₀^∞ Lⁿ`, con `L⁰ = {ε}`. La clausura positiva comienza con `L¹`.

La unión opera sobre conjuntos de cadenas; la concatenación construye nuevas cadenas. Por ejemplo, `{a}{b} = {ab}`, mientras que `{a} ∪ {b} = {a, b}`.

**Caso límite:** `∅* = {ε}`. Aunque `∅` no tiene elementos, se admite la concatenación de cero elementos, cuyo resultado es `ε`.

[Volver al índice](#indice)

---

<a id="regulares"></a>

## 4. Lenguajes regulares y expresiones regulares

Un lenguaje es **regular** si puede ser descrito por una expresión regular formal o reconocido por un autómata finito. Los lenguajes regulares se construyen a partir de casos básicos mediante operaciones que preservan la regularidad.

### 4.1. Definición inductiva

Sobre un alfabeto `Σ`, son expresiones regulares básicas:

1. `∅`, que denota el lenguaje vacío.
2. `ε`, que denota el lenguaje `{ε}`.
3. Cada símbolo `a ∈ Σ`, que denota el lenguaje `{a}`.

Si `r` y `s` son expresiones regulares, también lo son:

- `(r|s)`: alternativa, que denota la unión.
- `(rs)`: concatenación.
- `(r*)`: repetición de cero o más cadenas del lenguaje de `r`.

Estas reglas permiten construir expresiones más complejas. Para facilitar su lectura se usan paréntesis; la clausura se aplica antes que la concatenación, y la concatenación antes que la alternativa.

### 4.2. Interpretación de expresiones

Con `Σ = {a, b}`:


| Expresión formal | Lenguaje descrito | Acepta | Rechaza |
| :--- | :--- | :--- | :--- |
| `a*` | Cero o más letras `a` | `ε`, `a`, `aaa` | `ab` |
| `ab` | Exactamente `a` seguida de `b` | `ab` | `a`, `abb` |
| `(a\|b)*` | Todas las cadenas sobre `{a, b}` | `ε`, `baab` | `c` |
| `a(a\|b)*` | Cadenas que empiezan por `a` | `a`, `abba` | `ba`, `ε` |
| `(a\|b)*ab` | Cadenas que terminan en `ab` | `ab`, `baab` | `aba`, `baa` |

Una expresión regular representa un **lenguaje completo**. Identificar una subcadena coincidente dentro de un texto es una operación diferente y depende de la API de búsqueda utilizada.

### 4.3. Notación formal y motores de programación

Las expresiones regulares de los lenguajes formales y las expresiones de motores como `java.util.regex` están relacionadas, pero no son idénticas. Los motores incluyen sintaxis práctica adicional y, en algunos casos, construcciones cuyo poder expresivo excede el de los autómatas finitos clásicos, como las retroreferencias.

Por ello, no debe asumirse que todas las características de una biblioteca de expresiones regulares pertenecen a la definición matemática de lenguaje regular. El diseño de patrones y sus operadores específicos se desarrolla en el tema 2.2.

[Volver al índice](#indice)

---

<a id="automatas"></a>

## 5. Autómatas finitos

Un **autómata finito** procesa una cadena símbolo por símbolo y cambia entre un número finito de estados. Al terminar la entrada, decide su aceptación según el estado alcanzado.

### 5.1. Autómata finito determinista

Un **AFD** se define mediante la quíntupla `M = (Q, Σ, δ, q₀, F)`:

| Elemento | Significado |
|---|---|
| `Q` | Conjunto finito de estados |
| `Σ` | Alfabeto de entrada |
| `δ: Q × Σ → Q` | Función de transición |
| `q₀ ∈ Q` | Estado inicial |
| `F ⊆ Q` | Conjunto de estados de aceptación |

En un AFD completo existe exactamente una transición definida para cada combinación de estado y símbolo del alfabeto.

**Ejemplo:** reconocer cadenas binarias que terminan en `1`.

- `Q = {q0, q1}`; estado inicial `q0`.
- `F = {q1}`.
- `q0` significa que aún no se ha leído un `1` final; `q1`, que el último símbolo leído es `1`.

| Estado actual | Entrada `0` | Entrada `1` | ¿Acepta al terminar? |
|---|---|---|---|
| `q0` | `q0` | `q1` | No |
| `q1` | `q0` | `q1` | Sí |

La cadena `101` produce `q0 → q1 → q0 → q1`, por lo que es aceptada. La cadena `100` termina en `q0` y es rechazada. La cadena vacía también es rechazada porque `q0` no es final.

### 5.2. Autómata finito no determinista

Un **AFN** puede ofrecer varias transiciones posibles para un mismo estado y símbolo, o ninguna. En su variante con transiciones vacías, también puede cambiar de estado sin consumir un símbolo de entrada.

Una cadena se acepta si **existe al menos un recorrido** que consume la entrada completa y termina en un estado de aceptación. No es necesario que todos los recorridos acepten.

Aunque cambie la forma de describir los recorridos, AFD y AFN reconocen exactamente la misma clase de lenguajes: los regulares.

### 5.3. Qué significa tener memoria finita

Los estados permiten recordar una cantidad limitada de información relevante, como si apareció un símbolo, cuál fue el último carácter o si un prefijo coincide con un patrón. No permiten almacenar un contador arbitrariamente grande con valores distintos para todas las entradas.

Por ejemplo, el lenguaje `{aⁿbⁿ | n ≥ 0}` exige igual cantidad de letras `a` seguidas de letras `b`, para cualquier `n`. No es regular y no puede ser reconocido por un autómata finito clásico.

[Volver al índice](#indice)

---

<a id="equivalencia"></a>

## 6. Equivalencia de modelos y criterios de reconocimiento

Un lenguaje es regular **si y solo si** existe un autómata finito que lo reconoce. Esta equivalencia permite describir un requisito con una expresión regular y comprobar su comportamiento mediante transiciones entre estados.

| Representación | Qué expresa | Ventaja para el análisis |
|---|---|---|
| Descripción verbal | Requisito que debe cumplir una cadena | Acerca el problema al dominio |
| Expresión regular formal | Conjunto de cadenas mediante operaciones | Notación compacta |
| AFD | Un único siguiente estado por símbolo | Recorrido determinista y verificable |
| AFN | Alternativas posibles de transición | Construcción flexible del reconocedor |

**Procedimiento recomendado:** identificar primero el alfabeto, especificar qué cadenas se aceptan y cuáles se rechazan, construir una representación formal y comprobar casos normales y límites.

No basta con observar que un patrón funciona con unas pocas entradas. Una especificación correcta debe cubrir todas las cadenas del lenguaje pretendido.

[Volver al índice](#indice)

---

<a id="casos"></a>

## 7. Casos prácticos y trazas de reconocimiento

### 7.1. Identificadores con prefijo obligatorio

Un sistema admite identificadores que empiezan con `A` y continúan con uno o más dígitos binarios. El alfabeto es `Σ = {A, 0, 1}` y la expresión formal es:

```text
A(0|1)(0|1)*
```

| Entrada | Resultado | Justificación |
|---|---|---|
| `A0` | Aceptada | Prefijo y un dígito |
| `A101` | Aceptada | Prefijo y tres dígitos |
| `A` | Rechazada | Falta al menos un dígito |
| `01A` | Rechazada | El prefijo no está al inicio |
| `A102` | Rechazada | `2` no pertenece al alfabeto |
| `ε` | Rechazada | El prefijo es obligatorio |

La expresión podría escribirse también como `A(0|1)+` usando una abreviatura habitual. La definición básica de expresiones regulares, sin embargo, ya es suficiente para describir el lenguaje.

### 7.2. Secuencias binarias terminadas en `1`

El requisito admite `1`, `01`, `111` y `101`, pero rechaza `ε`, `0` y `100`. Una expresión formal equivalente es:

```text
(0|1)*1
```

La tabla de transiciones del apartado 5.1 implementa exactamente este mismo lenguaje. Permite comprobar que cualquier `0` final lleva al estado de rechazo y cualquier `1` final lleva al de aceptación.

### 7.3. Requisito que no es regular

Un protocolo hipotético exige `n` caracteres `a` seguidos de exactamente `n` caracteres `b`, con `n` sin límite superior. Las cadenas `ε`, `ab`, `aabb` y `aaabbb` son válidas, pero `aab` y `abab` no lo son.

Una expresión regular clásica no puede reconocer este conjunto para todos los valores de `n`. El ejemplo permite distinguir un problema de sintaxis de un **límite del modelo de reconocimiento**.

[Volver al índice](#indice)

---

<a id="java"></a>

## 8. Introducción al reconocimiento en Java

Java ofrece `java.util.regex.Pattern` y `Matcher` para trabajar con expresiones regulares. En este apartado solo se ilustra la diferencia entre comprobar una cadena completa y buscar una coincidencia parcial. La sintaxis completa y el desarrollo de patrones corresponden al tema 2.2.

```java
import java.util.regex.Pattern;

public class ReconocimientoBasico {
    public static void main(String[] args) {
        Pattern identificador = Pattern.compile("A[01]+");

        System.out.println(identificador.matcher("A101").matches());
        System.out.println(identificador.matcher("A").matches());
        System.out.println(identificador.matcher("X-A101-Y").find());
    }
}
```

**Resultado:**

```text
true
false
true
```

`matches()` exige que el patrón coincida con la entrada completa. `find()` busca una subsecuencia coincidente, por eso encuentra `A101` dentro de `X-A101-Y`. El símbolo `+` representa una o más repeticiones y `[01]` es una clase de caracteres de la sintaxis de Java.

En un problema de validación, utilizar `find()` en lugar de `matches()` puede aceptar texto adicional no previsto por el requisito. La elección depende de si se necesita **validar una entrada completa** o **localizar información dentro de un texto**.

[Volver al índice](#indice)

---

<a id="analisis"></a>

## 9. Análisis y criterios de aplicación

### 9.1. Preguntas de análisis

1. Un sistema acepta `ε`, `a` y `aa`, pero nunca `b`. ¿Cuál sería una descripción del lenguaje que incluya también todas las cadenas más largas permitidas?
2. ¿Qué diferencia funcional existe entre exigir que una entrada completa pertenezca a un lenguaje y localizar una subcadena que sí pertenece a él?
3. Si un AFD reconoce cadenas binarias terminadas en `1`, ¿qué estado debe alcanzar después de leer `1010` y por qué?
4. ¿Por qué el lenguaje `{aⁿbⁿ | n ≥ 0}` no puede resolverse con un número finito de estados para todos los valores de `n`?
5. Si una especificación permite identificadores como `A0` y `A01`, pero rechaza `A`, ¿por qué `A(0|1)*` no representa correctamente el requisito?

### 9.2. Errores conceptuales frecuentes

| Confusión | Corrección |
|---|---|
| `ε` y `∅` significan lo mismo | `ε` es una cadena; `∅` es un conjunto sin elementos |
| Una expresión regular siempre busca texto parcial | Depende de la operación de reconocimiento utilizada |
| Un AFN puede reconocer más lenguajes que un AFD | Ambos reconocen exactamente los lenguajes regulares |
| Todos los motores de regex implementan solo lenguajes regulares | Algunas extensiones, como las retroreferencias, superan el modelo regular clásico |
| Una cadena pertenece al lenguaje solo porque usa símbolos del alfabeto | Además de pertenecer a `Σ*`, debe satisfacer las reglas específicas de `L` |

### 9.3. Criterio de diseño

Antes de escribir una expresión regular, es necesario definir el lenguaje mediante casos aceptados y rechazados, precisar si se validará la entrada completa y verificar si el requisito pertenece al alcance de los lenguajes regulares.

Las expresiones regulares son adecuadas para muchos formatos textuales y secuencias reconocibles con memoria finita. No reemplazan analizadores sintácticos generales ni validaciones semánticas o de negocio.

[Volver al índice](#indice)

---

<a id="referencias"></a>

## 10. Referencias

1. Hopcroft, J. E., Motwani, R. y Ullman, J. D. *Introduction to Automata Theory, Languages, and Computation*. 3.ª ed. Pearson, 2006.
2. Sipser, M. *Introduction to the Theory of Computation*. 3.ª ed. Cengage Learning, 2012.
3. Oracle. [Clase Pattern, Java SE](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/regex/Pattern.html).
4. Oracle. [Clase Matcher, Java SE](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/regex/Matcher.html).

[Volver al índice](#indice)
