import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fija el proceso de importación y permite especializar cómo se interpreta cada
 * línea.
 */
public class TemplateMethodDemo {
    record Lectura(String id, double valor, String unidad) {
        Lectura {
            if (id == null || id.isBlank() || unidad == null || unidad.isBlank() || !Double.isFinite(valor))
                throw new IllegalArgumentException("Lectura inválida");
        }
    }

    static abstract class Importador {
        /** Template Method: la secuencia no puede ser reemplazada por una subclase. */
        final List<Lectura> importar(List<String> lineas) {
            Objects.requireNonNull(lineas, "Lista obligatoria");
            List<Lectura> resultado = new ArrayList<>();
            for (int i = 0; i < lineas.size(); i++) {
                try {
                    String linea = Objects.requireNonNull(lineas.get(i), "Línea nula");
                    if (linea.isBlank())
                        continue;
                    resultado.add(interpretar(linea));
                } catch (IllegalArgumentException | NullPointerException e) {
                    throw new IllegalArgumentException("Línea " + (i + 1) + ": " + e.getMessage(), e);
                }
            }
            List<Lectura> copia = List.copyOf(resultado);
            alCompletar(copia.size()); // Hook opcional, ejecutado una vez tras terminar correctamente.
            return copia;
        }

        protected abstract Lectura interpretar(String linea);

        protected void alCompletar(int cantidad) {
            /* Por defecto, no añade efectos. */ }

        // Paso común de apoyo; no selecciona ni crea una implementación de lector.
        protected final Lectura desdeCampos(String[] campos) {
            if (campos.length != 3)
                throw new IllegalArgumentException("Se requieren tres campos");
            return new Lectura(campos[0].trim(), Double.parseDouble(campos[1].trim()), campos[2].trim());
        }
    }

    static final class ImportadorPuntoComa extends Importador {
        @Override
        protected Lectura interpretar(String linea) {
            return desdeCampos(linea.split(";", -1));
        }
    }

    static final class ImportadorTabulacion extends Importador {
        @Override
        protected Lectura interpretar(String linea) {
            return desdeCampos(linea.split("\t", -1));
        }

        @Override
        protected void alCompletar(int cantidad) {
            System.out.println("Importadas con tabulación: " + cantidad);
        }
    }

    public static void main(String[] args) {
        Importador primero = new ImportadorPuntoComa();
        System.out.println(primero.importar(List.of("S01;22.5;°C", "")));
        Importador segundo = new ImportadorTabulacion();
        System.out.println(segundo.importar(List.of("S02\t18\tpersonas")));
        try {
            primero.importar(List.of("", "S03;NaN;°C"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
