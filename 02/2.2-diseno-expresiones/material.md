# Programación Aplicada

![UPS · Carrera de Computación](../../assets/logos/ups-computacion.png)

## 2.2. Diseño de expresiones regulares

**Autor:** Ing. Pablo Torres, Mgtr.  
**Unidad:** 2. Expresiones regulares  
**Tema:** 2.2. Diseño de expresiones regulares  
**Proyecto de trabajo:** CampusMonitor

Las expresiones regulares permiten especificar y reconocer estructuras de texto mediante patrones. Diseñar una expresión implica partir de un contrato de entrada, identificar sus símbolos permitidos, construir una regla de reconocimiento y verificar tanto los casos aceptados como los rechazados.

Este tema desarrolla las clases de caracteres, cuantificadores, alternancia, agrupación, capturas y APIs de reconocimiento. Como caso de aplicación, se interpreta una trama de CampusMonitor con identificador, valor y unidad. Se distinguen las reglas de formato de las reglas del dominio y se incluyen implementaciones equivalentes en Java, Python y JavaScript.

[Inicio del curso](../../README.md) · [Tema anterior: 2.1. Fundamentos de los lenguajes regulares](../2.1-lenguajes-regulares/material.md)

<a id="indice"></a>

## Índice

1. [Del lenguaje regular al patrón de reconocimiento](#fundamentos)
2. [Componentes de una expresión regular](#componentes)
3. [Construcción progresiva de un patrón](#construccion)
4. [Capturas, escapes y texto literal](#capturas)
5. [Reconocimiento completo, búsqueda y flags](#reconocimiento)
6. [Caso práctico: tramas de CampusMonitor](#caso)
7. [Implementación en Java](#java)
8. [Implementaciones equivalentes en Python y JavaScript](#otros-lenguajes)
9. [PC2.2 · Práctica de clase](#pc)
10. [Análisis y criterios de diseño](#analisis)
11. [Referencias](#referencias)

---

<a id="fundamentos"></a>

## 1. Del lenguaje regular al patrón de reconocimiento

En el tema 2.1 se estudió que un lenguaje formal es un conjunto de cadenas definidas sobre un alfabeto. Una **expresión regular** representa un conjunto de cadenas mediante operaciones de combinación, elección y repetición. En programación, los motores regex añaden una sintaxis práctica para describir y reconocer formatos de texto.

Una expresión regular no sustituye la definición de un requisito. Antes de escribir el patrón deben establecerse las reglas aceptadas: longitud de un identificador, separadores, signos, unidades y política de espacios.

Por ejemplo, para aceptar únicamente códigos compuestos por `S` seguida de dos dígitos ASCII:

```regex
S[0-9]{2}
```

La expresión describe una secuencia. La **validación completa** determina si toda la entrada pertenece al lenguaje, mientras que la **búsqueda** localiza una secuencia compatible dentro de un texto mayor.

| Entrada | Coincidencia parcial del patrón | Validación completa |
|---|---|---|
| `S01` | Sí | Sí |
| `ID=S01` | Sí | No |
| `S1` | No | No |
| `S012` | Sí: `S01` | No |

**Distinción:** reconocer el formato de una cadena no implica que sus datos sean válidos para una regla de negocio. `O001;12.5;PERSONAS` puede tener sintaxis decimal correcta, pero no representar una cantidad entera de personas.

[Volver al índice](#indice)

---

<a id="componentes"></a>

## 2. Componentes de una expresión regular

### 2.1. Literales, clases y rangos

Una clase de caracteres define el conjunto de símbolos permitidos **en una posición**. Un literal representa un símbolo concreto. Los rangos ayudan a expresar conjuntos contiguos de caracteres.

| Sintaxis | Significado | Ejemplo que acepta |
|---|---|---|
| `A` | Letra `A` literal | `A` |
| `[0-9]` | Un dígito ASCII | `7` |
| `[A-Z]` | Una letra mayúscula ASCII | `T` |
| `[TO]` | `T` u `O` | `O` |
| `[^;]` | Un carácter distinto de punto y coma | `x` |
| `[.]` | Un punto literal | `.` |
| `\d` | Clase de dígitos del motor; su alcance varía | `7` |
| `\s` | Clase de espacios del motor; su alcance varía | espacio |

En un protocolo con identificadores y números ASCII, `[0-9]` expresa mejor el contrato que una clase abreviada cuyo comportamiento Unicode puede variar entre motores y opciones.

### 2.2. Cuantificadores

Los cuantificadores controlan cuántas veces puede aparecer un elemento o grupo.

| Sintaxis | Cantidad | Ejemplo |
|---|---|---|
| `X?` | Cero o una | `X`, cadena vacía |
| `X*` | Cero o más | `XX`, cadena vacía |
| `X+` | Una o más | `XXX` |
| `X{3}` | Exactamente tres | `XXX` |
| `X{2,4}` | Entre dos y cuatro | `XX`, `XXX`, `XXXX` |
| `X{2,}` | Dos o más | `XXXX` |

Para `T001`, `[0-9]{3}` exige exactamente tres dígitos. La alternativa `[0-9]+` aceptaría longitudes no previstas como `T1` o `T000001`.

### 2.3. Concatenación, alternancia y agrupación

La **concatenación** exige elementos sucesivos. La **alternancia** `|` admite una opción u otra. Los paréntesis agrupan expresiones para controlar el alcance de las operaciones.

```regex
C|PERSONAS
```

Acepta `C` o `PERSONAS` cuando se usa con una operación de coincidencia completa.

```regex
(?:T|O)[0-9]{3}
```

Acepta identificadores que empiezan con `T` u `O`, seguidos de tres dígitos. `(?:...)` agrupa sin registrar una captura.

**Importancia del agrupamiento:** `T|O[0-9]{3}` representa `T` **o** `O` seguido de tres dígitos. No equivale a `(?:T|O)[0-9]{3}`.

### 2.4. Capturas y repetición codiciosa

Los grupos capturadores `( ... )` conservan fragmentos reconocidos para consultarlos posteriormente. Los grupos no capturadores `(?: ... )` únicamente organizan el patrón. Los grupos con nombre permiten consultar un dato mediante su significado, sin depender del número de paréntesis anteriores.

Los cuantificadores `*` y `+` son **codiciosos** por defecto: intentan consumir la mayor cantidad posible de caracteres, aunque el motor puede retroceder para completar una coincidencia. Las variantes `*?` y `+?` son reacias. En un protocolo delimitado, es preferible especificar caracteres y cantidades permitidas en lugar de usar `.*` de manera indiscriminada.

[Volver al índice](#indice)

---

<a id="construccion"></a>

## 3. Construcción progresiva de un patrón

Supongamos un protocolo de una línea con este formato:

```text
identificador;valor;unidad
```

El contrato define:

- Identificador: `T` u `O`, seguido de tres dígitos ASCII.
- Separador: punto y coma literal.
- Valor: signo menos opcional, uno o más dígitos y parte decimal opcional con punto.
- Unidad: `C` o `PERSONAS`, respetando mayúsculas.
- No se admiten espacios, signos `+`, separador decimal con coma, exponentes ni saltos de línea.
- Toda la entrada debe coincidir; no se aceptan prefijos o sufijos adicionales.

### 3.1. Descomposición

| Campo | Regla | Patrón |
|---|---|---|
| Identificador | Prefijo `T` u `O` y tres dígitos | `[TO][0-9]{3}` |
| Valor entero | Signo negativo opcional y uno o más dígitos | `-?[0-9]+` |
| Fracción | Punto y uno o más dígitos, opcionales como conjunto | `(?:[.][0-9]+)?` |
| Valor decimal | Entero y fracción opcional | `-?[0-9]+(?:[.][0-9]+)?` |
| Unidad | Dos alternativas literales | `(?:C|PERSONAS)` |

Al concatenar los componentes:

```regex
[TO][0-9]{3};-?[0-9]+(?:[.][0-9]+)?;(?:C|PERSONAS)
```

### 3.2. Pruebas de aceptación y rechazo

| Entrada | Resultado sintáctico | Motivo |
|---|---|---|
| `T001;23.5;C` | Aceptar | Cumple todos los campos |
| `O001;12;PERSONAS` | Aceptar | Entero permitido por la gramática |
| `O001;12.5;PERSONAS` | Aceptar | La gramática admite fracciones; la regla del dominio decide después |
| `T01;23;C` | Rechazar | Faltan dígitos del identificador |
| `T001;+23;C` | Rechazar | No se admite signo positivo explícito |
| `T001;23,5;C` | Rechazar | Separador decimal incorrecto |
| `T001;23.;C` | Rechazar | El punto requiere al menos un dígito posterior |
| `T001;NaN;C` | Rechazar | `NaN` no pertenece a la gramática |
| `X001;23;C` | Rechazar | Prefijo no permitido |
| ` T001;23;C` | Rechazar | Espacio inicial no permitido |

**Regla de trabajo:** primero probar el reconocimiento completo del formato; después convertir y validar los valores capturados. No se debe forzar toda la lógica de negocio dentro de una expresión regular.

[Volver al índice](#indice)

---

<a id="capturas"></a>

## 4. Capturas, escapes y texto literal

### 4.1. Grupos con nombre

Los motores de Java, Python y JavaScript admiten grupos con nombre, pero **su sintaxis no es idéntica**:

| Lenguaje | Grupo con nombre | Acceso a la captura |
|---|---|---|
| Java | `(?<sensor>...)` | `matcher.group("sensor")` |
| Python | `(?P<sensor>...)` | `match.group("sensor")` |
| JavaScript | `(?<sensor>...)` | `match.groups.sensor` |

Para Java:

```regex
(?<sensor>[TO][0-9]{3});(?<valor>-?[0-9]+(?:[.][0-9]+)?);(?<unidad>C|PERSONAS)
```

`valor` sigue siendo una cadena capturada. El nombre del grupo facilita la extracción; no realiza conversiones ni validaciones de dominio.

### 4.2. Escape del patrón y escape del lenguaje

La expresión regular y el código fuente poseen reglas distintas para representar algunos caracteres.

| Objetivo | Regex conceptual | Java como `String` | Python raw string | JavaScript literal regex |
|---|---|---|---|---|
| Un dígito | `\d` | `"\\d"` | `r"\d"` | `/\d/` |
| Un punto literal | `\.` | `"\\."` | `r"\."` | `/\./` |
| Un punto con clase | `[.]` | `"[.]"` | `r"[.]"` | `/[.]/` |

En Java, `"\d"` **no es un literal de cadena válido**: la barra inversa debe escaparse como `"\\d"`. Usar `[0-9]` y `[.]` evita parte de este problema cuando el contrato se limita a ASCII y punto decimal.

### 4.3. Texto externo y reemplazos

No concatenes directamente una entrada externa dentro de un patrón si debe buscarse como texto literal. En Java, `Pattern.quote()` protege su interpretación regex:

```java
Pattern literal = Pattern.compile(Pattern.quote("sensor.a"));
```

`Matcher.quoteReplacement()` cumple otra función: protege el contenido de **reemplazo** frente a la interpretación especial de `$` y `\`:

```java
String reemplazoSeguro = Matcher.quoteReplacement("$5");
```

Los dos métodos no son intercambiables. En Python se dispone de `re.escape()` para construir fragmentos literales del patrón; en JavaScript hay que elegir un mecanismo de escape adecuado para construir expresiones dinámicas, en lugar de interpolar entradas sin protección.

[Volver al índice](#indice)

---

<a id="reconocimiento"></a>

## 5. Reconocimiento completo, búsqueda y flags

### 5.1. Reconocimiento frente a búsqueda

| Objetivo | Java | Python | JavaScript |
|---|---|---|---|
| Validar toda una entrada | `matcher.matches()` | `pattern.fullmatch(texto)` | Regex anclada y `test()` o `exec()` |
| Buscar un fragmento | `matcher.find()` | `pattern.search(texto)` | Regex no anclada y `test()` o `exec()` |
| Extraer campos | `matcher.group("nombre")` | `match.group("nombre")` | `match.groups.nombre` |

En JavaScript no existe una función nativa equivalente a `fullmatch()` de Python. Para el protocolo se utilizan `^` y `$` con una expresión sin el flag `m`. Para entradas arbitrarias con saltos de línea al final, el ancla `$` puede tener comportamientos que requieren cuidado; una validación estricta también debe controlar o excluir saltos de línea. La implementación demostrativa utiliza una gramática que no permite saltos y valida la entrada delimitada a una sola línea.

### 5.2. Reutilización y estado

En Java, `Pattern` conserva la expresión compilada y se puede reutilizar. `Matcher` mantiene el estado de una operación sobre una entrada y no debe compartirse simultáneamente entre hilos. Compilar una vez un patrón estable evita trabajo repetido.

En Python, `re.compile()` produce un patrón reutilizable. En JavaScript, un objeto `RegExp` puede reutilizarse, pero los flags `g` e `y` hacen que `lastIndex` afecte búsquedas posteriores: no deben activarse sin necesidad en un validador simple.

### 5.3. Flags y alcance

| Flag | Efecto general | Criterio de uso |
|---|---|---|
| Insensibilidad a mayúsculas | Modifica la comparación de letras | Activarlo solo si el protocolo admite variantes de caso |
| Multilínea | Modifica la interpretación de anclas de línea | No usarlo para validar una única trama completa |
| Dotall | Hace que `.` también pueda reconocer saltos de línea | No hace falta para un protocolo delimitado |
| Unicode | Modifica determinadas reglas según motor y flag | Definir explícitamente si se admiten caracteres fuera de ASCII |

La sintaxis y los detalles de los flags cambian entre lenguajes. En este ejemplo, el protocolo exige los prefijos y unidades exactos, por lo que no se activa la insensibilidad a mayúsculas.

[Volver al índice](#indice)

---

<a id="caso"></a>

## 6. Caso práctico: tramas de CampusMonitor

### 6.1. Problema y contrato

CampusMonitor recibe lecturas mediante cadenas con tres campos separados por punto y coma. El sistema necesita detectar entradas mal formadas, identificar los valores y comunicar errores sin insertar datos incorrectos en el registro de lecturas.

La solución se organiza en tres etapas:

1. **Reconocimiento:** comprobar que toda la trama respeta el patrón y extraer los grupos.
2. **Conversión:** transformar `valor` a un número y rechazar resultados no finitos.
3. **Validación de dominio:** comprobar que `T...` use unidad `C` y que `O...` use `PERSONAS` con un entero no negativo.

El ejemplo limita la entrada a 80 caracteres y considera el valor inicial no nulo. No incluye almacenamiento; el parser devuelve una estructura de datos que otro componente podría utilizar.

### 6.2. Estructura de responsabilidades

| Elemento | Responsabilidad |
|---|---|
| Patrón compilado | Definir el formato reconocido |
| Coincidencia y grupos | Extraer `sensor`, `valor` y `unidad` |
| Parser | Coordinar reconocimiento y conversión |
| Validación de dominio | Verificar coherencia entre sensor, unidad y magnitud |
| Aplicación cliente | Presentar el resultado o reportar el error |

Esta separación permite cambiar la política de almacenamiento sin modificar el patrón y modificar el formato sin mezclarlo con la presentación del resultado.

### 6.3. Casos que deben distinguirse

| Trama | Sintaxis | Dominio | Resultado |
|---|---|---|---|
| `T001;23.5;C` | Válida | Válido | Lectura de temperatura |
| `O001;12;PERSONAS` | Válida | Válido | Lectura de ocupación |
| `O001;12.5;PERSONAS` | Válida | Inválido | Personas no enteras |
| `T001;25;PERSONAS` | Válida | Inválido | Unidad incompatible |
| `O001;-1;PERSONAS` | Válida | Inválido | Ocupación negativa |
| `T01;23;C` | Inválida | No se evalúa | Identificador incorrecto |
| `T001;NaN;C` | Inválida | No se evalúa | Número fuera del formato |

El resultado debe indicar si el fallo pertenece al formato o a la regla de dominio. Reemplazar valores inválidos por cero ocultaría errores, porque cero puede ser una medida legítima.

[Volver al índice](#indice)

---

<a id="java"></a>

## 7. Implementación en Java

**Código completo y comentado:** [RegexDemo.java](ejemplos/RegexDemo.java).

### 7.1. Tipos y métodos utilizados

| Elemento | Función |
|---|---|
| `record Trama` | Conservar los tres campos ya interpretados |
| `Pattern.compile()` | Compilar la expresión una vez |
| `Matcher.matches()` | Exigir coincidencia con toda la entrada |
| `Matcher.group("valor")` | Obtener la captura identificada por nombre |
| `Double.parseDouble()` | Convertir texto numérico a `double` |
| `Double.isFinite()` | Rechazar infinitos y `NaN` tras la conversión |
| `Math.rint()` | Comprobar que el valor de ocupación sea integral |

### 7.2. Fragmento central

```java
private static final Pattern TRAMA = Pattern.compile(
        "(?<sensor>[TO][0-9]{3});(?<valor>-?[0-9]+(?:[.][0-9]+)?);(?<unidad>C|PERSONAS)");

Matcher matcher = TRAMA.matcher(linea);
if (!matcher.matches()) {
    throw new IllegalArgumentException("Formato inválido");
}

String sensor = matcher.group("sensor");
String unidad = matcher.group("unidad");
double valor = Double.parseDouble(matcher.group("valor"));
```

El código completo también controla entrada nula, longitud, resultados no finitos, compatibilidad entre prefijos y unidades, y la regla de ocupación. La validación posterior **no se incorpora a la regex**, porque pertenece a la semántica de la lectura.

### 7.3. Secuencia de ejecución

1. `main()` recorre varias tramas de prueba.
2. `parsear()` valida la entrada inicial y crea un `Matcher` para esa línea.
3. `matches()` rechaza la cadena completa si no satisface el patrón.
4. `group()` extrae los campos definidos por sus nombres.
5. Se convierte el valor, se aplican las reglas del dominio y se devuelve `Trama`.
6. El cliente imprime el registro o el motivo del rechazo.

### 7.4. Ejecución

Desde el directorio que contiene el archivo Java, con una versión de Java que permita ejecutar archivos fuente:

```bash
java RegexDemo.java
```

Salida esperada:

```text
Trama[sensor=T001, valor=23.5, unidad=C]
Trama[sensor=O001, valor=12.0, unidad=PERSONAS]
O001;12.5;PERSONAS -> Ocupación debe ser un entero no negativo
T01;23;C -> Formato inválido
T001;NaN;C -> Formato inválido
T001;25;PERSONAS -> Unidad incompatible con temperatura
O001;-1;PERSONAS -> Ocupación debe ser un entero no negativo
```

**Observación:** el programa se puede ejecutar sin Gradle y sin dependencias externas. Los mensajes con tildes requieren una terminal con codificación compatible con UTF-8.

[Volver al índice](#indice)

---

<a id="otros-lenguajes"></a>

## 8. Implementaciones equivalentes en Python y JavaScript

Los tres lenguajes resuelven el mismo problema con el mismo conjunto de casos. Las diferencias importantes están en la sintaxis de las capturas, la API de validación completa y la representación de los números.

### 8.1. Python

**Código completo:** [regex_demo.py](ejemplos/regex_demo.py).

```python
import re

PATRON = re.compile(
    r"(?P<sensor>[TO][0-9]{3});(?P<valor>-?[0-9]+(?:[.][0-9]+)?);(?P<unidad>C|PERSONAS)"
)

coincidencia = PATRON.fullmatch("T001;23.5;C")
if coincidencia:
    sensor = coincidencia["sensor"]
    valor = float(coincidencia["valor"])
    unidad = coincidencia["unidad"]
```

Python utiliza `(?P<nombre>...)` para los grupos con nombre y `fullmatch()` para validar toda la cadena. El archivo completo emplea `math.isfinite()` y `float.is_integer()` cuando corresponde.

Ejecución:

```bash
python regex_demo.py
```

### 8.2. JavaScript

**Código completo:** [regex-demo.mjs](ejemplos/regex-demo.mjs).

```javascript
const TRAMA = /^(?<sensor>[TO][0-9]{3});(?<valor>-?[0-9]+(?:[.][0-9]+)?);(?<unidad>C|PERSONAS)$/u;

const coincidencia = TRAMA.exec('T001;23.5;C');
if (coincidencia) {
  const { sensor, unidad } = coincidencia.groups;
  const valor = Number(coincidencia.groups.valor);
}
```

JavaScript admite grupos con nombre mediante `(?<nombre>...)`. El patrón se ancla para validar la entrada completa. El archivo completo comprueba `Number.isFinite()` y exige `Number.isSafeInteger()` en la ocupación para no aceptar enteros fuera del rango de precisión segura del tipo `number`.

Ejecución con Node.js:

```bash
node regex-demo.mjs
```

### 8.3. Comparación de decisiones

| Aspecto | Java | Python | JavaScript |
|---|---|---|---|
| Grupo nombrado | `(?<x>...)` | `(?P<x>...)` | `(?<x>...)` |
| Coincidencia completa | `matches()` | `fullmatch()` | Patrón anclado y `exec()` |
| Conversión | `Double.parseDouble()` | `float()` | `Number()` |
| Valor finito | `Double.isFinite()` | `math.isfinite()` | `Number.isFinite()` |
| Número integral | `Math.rint()` | `float.is_integer()` | `Number.isSafeInteger()` |
| Tratamiento de errores | Excepciones | Excepciones | Excepciones |

**Límite de equivalencia:** el ejemplo Java y el de Python comprueban integralidad de `double`/`float`; JavaScript añade precisión segura. Si CampusMonitor exige que los contadores conserven exactamente valores muy grandes, conviene definir un límite común de magnitud o convertir directamente desde el texto a un entero exacto antes de aceptar los datos.

[Volver al índice](#indice)

---

<a id="pc"></a>

## 9. PC2.2 · Práctica de clase

### 9.1. Objetivo

Ampliar CampusMonitor para recibir tramas `id;valor;unidad`, reconocer los campos mediante expresiones regulares y diferenciar los errores de formato de las reglas del dominio.

Esta actividad se desarrolla en el repositorio personal `icc-pap-campusmonitor-apellido`. Se conserva la funcionalidad implementada previamente; los ejemplos resueltos no se copian como sustituto de la solución personal.

### 9.2. Requisitos

1. Implementa un parser para identificadores `T001` y `O001`, con tres dígitos y grupos con nombre para los campos `sensor`, `valor` y `unidad`.
2. Acepta valores decimales con signo menos opcional y punto decimal. No aceptes espacios, exponentes, `NaN`, separadores con coma ni caracteres sobrantes.
3. Rechaza entradas nulas o que superen 80 caracteres y comunica un motivo identificable.
4. Admite únicamente `C` para sensores `T` y `PERSONAS` para sensores `O`.
5. Comprueba que las lecturas `PERSONAS` sean enteros no negativos y que los valores convertidos sean finitos.
6. Integra un comando `validar-trama` que muestre los campos reconocidos o un mensaje de error. Conserva la cadena original al reportar entradas inválidas.
7. Mantén separado el reconocimiento del almacenamiento de lecturas.

### 9.3. Pruebas mínimas

| Entrada | Resultado requerido |
|---|---|
| `T001;23.5;C` | Válida: temperatura `23.5` |
| `O001;12;PERSONAS` | Válida: ocupación `12` |
| `O001;12.5;PERSONAS` | Error de dominio: ocupación no integral |
| `T001;25;PERSONAS` | Error de dominio: unidad incompatible |
| `O001;-1;PERSONAS` | Error de dominio: ocupación negativa |
| `T01;23;C` | Error de formato: identificador |
| `T001;NaN;C` | Error de formato: valor |
| `T001;23,5;C` | Error de formato: separador decimal |

### 9.4. Evidencia y entrega

En `evidencias/PC2.2.md`, registra el cambio implementado, los archivos principales, los comandos de ejecución, la tabla con resultados reales, una explicación de un caso válido y de uno inválido y el enlace al commit final. No se requiere una rúbrica para esta PC.

Comandos de referencia:

```bash
git status
git add src evidencias
git commit -m "feat(pc2.2): diseno-de-expresiones-regulares"
git push
git log -1 --format=%H
```

Ajusta las rutas de `git add` a los archivos modificados. El último comando muestra el identificador real del commit para incluirlo en la evidencia.

[Volver al índice](#indice)

---

<a id="analisis"></a>

## 10. Análisis y criterios de diseño

### 10.1. Preguntas de análisis

1. Si `T001;23.5;C` se considera válido, ¿por qué `ID=T001;23.5;C` debe rechazarse al importar una trama completa, aunque contenga un fragmento reconocible?
2. ¿Cuál es el efecto de sustituir `[TO][0-9]{3}` por `T|O[0-9]{3}`? Construye un caso que evidencie el cambio de significado.
3. Si la expresión acepta `O001;12.5;PERSONAS`, ¿en qué etapa debe rechazarse y por qué no se trata de un error de sintaxis?
4. ¿Qué diferencia hay entre escapar el punto decimal en la expresión regular y escapar la barra inversa de un literal Java?
5. ¿Por qué compilar una expresión una sola vez y crear un `Matcher` por operación es preferible a compartir el mismo `Matcher` entre solicitudes concurrentes?
6. Si se incorpora una unidad `K` para temperaturas, ¿qué parte del contrato y qué pruebas deben actualizarse?
7. Si un valor tiene miles de dígitos, ¿por qué pasar la regex no garantiza que `double` pueda representarlo correctamente?

### 10.2. Errores frecuentes

| Error | Consecuencia | Corrección |
|---|---|---|
| Usar búsqueda parcial para validar tramas | Acepta textos con prefijos y sufijos | Exigir coincidencia completa |
| Usar `.*` para cada campo | Se pierden límites concretos del protocolo | Definir clases y cuantificadores específicos |
| Olvidar agrupar alternativas | Cambia el alcance de las opciones | Utilizar `(?:...)` cuando corresponda |
| Convertir antes de verificar el formato | Excepciones confusas y aceptación accidental | Reconocer, extraer, convertir y validar |
| Aceptar `NaN` o infinito | Resultados sin significado para las lecturas | Definir gramática numérica y verificar finitud |
| Compartir estado de `Matcher` o `RegExp` global con flags `g`/`y` | Resultados dependientes de llamadas previas | Usar instancias de coincidencia independientes y flags mínimos |

### 10.3. Limitaciones

La demostración procesa una línea en memoria y no implementa recepción desde red, archivos ni base de datos. La regex comprueba la **forma** de cada entrada, pero no asegura precisión física, límites propios del sensor, existencia real del identificador ni autorización para registrar lecturas. Esos requisitos pertenecen a otras capas del sistema.

Para entradas no confiables, se debe considerar además el costo de evaluación de expresiones regulares complejas. Un patrón innecesariamente ambiguo, combinado con repeticiones anidadas, puede provocar tiempos de ejecución elevados. El diseño de este tema usa clases y límites simples para evitar depender de expresiones genéricas difíciles de analizar.

**Criterio de diseño:** una buena expresión regular representa exactamente el formato requerido; un buen parser transforma sus capturas y delega las reglas de dominio a validaciones explícitas.

[Volver al índice](#indice)

---

<a id="referencias"></a>

## 11. Referencias

1. Oracle. [Pattern — Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/regex/Pattern.html).
2. Oracle. [Matcher — Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/regex/Matcher.html).
3. Python Software Foundation. [Módulo `re` — Python 3.14](https://docs.python.org/3.14/library/re.html).
4. MDN Web Docs. [Regular expressions — JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_expressions).
5. MDN Web Docs. [Grupos con nombre — JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Regular_expressions/Named_capturing_group).

[Volver al índice](#indice)
