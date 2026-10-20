package edu.ups.pap.u01.patrones.creacionales;

import java.util.ArrayList;
import java.util.List;

/** Importa líneas delimitadas conservando un proceso común de validación. */
public class FactoryMethodDemo {
    // El modelo es independiente del formato de entrada.
    record Lectura(String idSensor, double valor, String unidad) {
        Lectura {
            if (idSensor == null || idSensor.isBlank()
                    || unidad == null || unidad.isBlank()
                    || !Double.isFinite(valor)) {
                throw new IllegalArgumentException("Lectura inválida");
            }
        }
    }

    /** Producto: convierte una línea en una lectura del dominio. */
    interface LectorLinea {
        Lectura leer(String linea);
    }

    // Función auxiliar: comparte la validación, no decide qué producto crear.
    static Lectura interpretar(String linea, String separador) {
        String[] campos = linea.split(separador, -1);
        if (campos.length != 3) {
            throw new IllegalArgumentException("Se esperaban id, valor y unidad");
        }
        return new Lectura(campos[0].strip(),
                Double.parseDouble(campos[1].strip()), campos[2].strip());
    }

    static final class LectorPuntoComa implements LectorLinea {
        @Override
        public Lectura leer(String linea) {
            return interpretar(linea, ";");
        }
    }

    static final class LectorTabulacion implements LectorLinea {
        @Override
        public Lectura leer(String linea) {
            return interpretar(linea, "\t");
        }
    }

    /** Creador: mantiene el proceso de importación y delega la creación. */
    static abstract class Importador {
        protected abstract LectorLinea crearLector(); // Factory Method.

        final List<Lectura> importar(List<String> lineas) {
            LectorLinea lector = crearLector(); // Selección polimórfica.
            List<Lectura> resultado = new ArrayList<>();
            for (int i = 0; i < lineas.size(); i++) {
                String linea = lineas.get(i);
                if (linea.isBlank()) continue;
                try {
                    resultado.add(lector.leer(linea));
                } catch (IllegalArgumentException error) {
                    // Añade el número de línea sin perder la causa original.
                    throw new IllegalArgumentException(
                            "Línea " + (i + 1) + ": " + error.getMessage(), error);
                }
            }
            return List.copyOf(resultado);
        }
    }

    static final class ImportadorPuntoComa extends Importador {
        @Override
        protected LectorLinea crearLector() { return new LectorPuntoComa(); }
    }

    static final class ImportadorTabulacion extends Importador {
        @Override
        protected LectorLinea crearLector() { return new LectorTabulacion(); }
    }

    public static void main(String[] args) {
        // La configuración selecciona el creador; importar no tiene un switch.
        Importador delimitado = new ImportadorPuntoComa();
        Importador tabulado = new ImportadorTabulacion();
        System.out.println(delimitado.importar(List.of("S01;22.5;°C", "")));
        System.out.println(tabulado.importar(List.of("S02\t18\tpersonas")));
        try {
            delimitado.importar(List.of("S03;NaN;°C"));
        } catch (IllegalArgumentException error) {
            System.out.println(error.getMessage());
        }
    }
}
