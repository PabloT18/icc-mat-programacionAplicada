import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Ofrece una sola operación para interpretar, validar y guardar un lote de lecturas. */
public class FacadeDemo {
    record Lectura(String id, double valor, String unidad) {
        Lectura {
            if (id == null || id.isBlank() || unidad == null || unidad.isBlank() || !Double.isFinite(valor))
                throw new IllegalArgumentException("Lectura inválida");
        }
    }
    /** Subsistema de interpretación: solo maneja líneas simples separadas por punto y coma. */
    static final class ParserLecturas {
        Lectura interpretar(String linea) {
            Objects.requireNonNull(linea, "Línea obligatoria");
            String[] campos = linea.split(";", -1);
            if (campos.length != 3) throw new IllegalArgumentException("Se requieren tres campos");
            return new Lectura(campos[0].trim(), Double.parseDouble(campos[1].trim()), campos[2].trim());
        }
    }
    /** Subsistema de reglas: en este proceso solo se aceptan temperaturas Celsius. */
    static final class ValidadorTemperatura {
        void validar(Lectura lectura) {
            if (!lectura.unidad().equals("°C") || lectura.valor() < -50 || lectura.valor() > 100)
                throw new IllegalArgumentException("Temperatura fuera del contrato");
        }
    }
    /** Almacenamiento simulado. No representa una transacción de base de datos. */
    static final class RepositorioMemoria {
        private final List<Lectura> registros = new ArrayList<>();
        void guardarLote(List<Lectura> lote) { registros.addAll(List.copyOf(lote)); }
        int cantidad() { return registros.size(); }
    }
    /** Fachada: coordina el subsistema; no duplica el parser ni la regla de validación. */
    static final class ImportacionLecturas {
        private final ParserLecturas parser;
        private final ValidadorTemperatura validador;
        private final RepositorioMemoria repositorio;
        ImportacionLecturas(ParserLecturas parser, ValidadorTemperatura validador,
                            RepositorioMemoria repositorio) {
            this.parser = Objects.requireNonNull(parser);
            this.validador = Objects.requireNonNull(validador);
            this.repositorio = Objects.requireNonNull(repositorio);
        }
        int importar(List<String> lineas) {
            Objects.requireNonNull(lineas, "Lista obligatoria");
            List<Lectura> lote = new ArrayList<>();
            for (int i = 0; i < lineas.size(); i++) {
                try {
                    Lectura lectura = parser.interpretar(lineas.get(i));
                    validador.validar(lectura);
                    lote.add(lectura);
                } catch (IllegalArgumentException | NullPointerException e) {
                    // Mantiene la causa para poder diagnosticar el fallo original.
                    throw new IllegalArgumentException("Línea " + (i + 1) + ": " + e.getMessage(), e);
                }
            }
            // Primero se valida TODO el lote: un error de datos no guarda filas parciales.
            repositorio.guardarLote(lote);
            return lote.size();
        }
    }
    public static void main(String[] args) {
        RepositorioMemoria repositorio = new RepositorioMemoria();
        ImportacionLecturas servicio = new ImportacionLecturas(
                new ParserLecturas(), new ValidadorTemperatura(), repositorio);
        System.out.println("Importadas: " + servicio.importar(List.of("S01;22.5;°C", "S02;27.0;°C")));
        try {
            servicio.importar(List.of("S03;20;°C", "S04;180;°C"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("Total almacenado: " + repositorio.cantidad());
        System.out.println("Lote vacío: " + servicio.importar(List.of()));
    }
}
