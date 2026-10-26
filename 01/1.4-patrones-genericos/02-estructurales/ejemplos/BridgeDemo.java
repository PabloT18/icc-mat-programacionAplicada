import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Separa dos dimensiones: contenido del reporte y representación de sus filas. */
public class BridgeDemo {
    /** Dato del dominio; no es una abstracción ni un implementador del patrón. */
    record Lectura(String id, double valor) {
        Lectura {
            if (id == null || id.isBlank() || !Double.isFinite(valor))
                throw new IllegalArgumentException("Lectura inválida");
        }
    }
    /** Implementador: primitivas de representación, sin decidir qué se reporta. */
    interface FormatoReporte { String fila(String etiqueta, String dato); }
    static final class FormatoTexto implements FormatoReporte {
        @Override public String fila(String etiqueta, String dato) {
            return etiqueta + ": " + dato;
        }
    }
    static final class FormatoCsv implements FormatoReporte {
        // Duplica comillas internas y delimita ambos campos, incluso si contienen comas.
        private String campo(String valor) { return "\"" + valor.replace("\"", "\"\"") + "\""; }
        @Override public String fila(String etiqueta, String dato) {
            return campo(etiqueta) + "," + campo(dato);
        }
    }
    /** Abstracción de alto nivel: conserva el puente hacia el formato elegido. */
    static abstract class Reporte {
        protected final FormatoReporte formato;
        Reporte(FormatoReporte formato) {
            this.formato = Objects.requireNonNull(formato, "Formato obligatorio");
        }
        abstract String generar(List<Lectura> lecturas);
    }
    static final class ReporteDetalle extends Reporte {
        ReporteDetalle(FormatoReporte formato) { super(formato); }
        @Override String generar(List<Lectura> lecturas) {
            // La copia valida la lista y sus elementos; no modifica la entrada.
            var filas = new java.util.ArrayList<String>();
            for (Lectura lectura : List.copyOf(lecturas))
                filas.add(formato.fila(lectura.id(), String.valueOf(lectura.valor())));
            return String.join("\n", filas);
        }
    }
    static final class ReporteResumen extends Reporte {
        ReporteResumen(FormatoReporte formato) { super(formato); }
        @Override String generar(List<Lectura> lecturas) {
            List<Lectura> copia = List.copyOf(lecturas);
            if (copia.isEmpty()) return formato.fila("Promedio", "Sin datos");
            double suma = 0;
            for (Lectura lectura : copia) suma += lectura.valor();
            // El ejemplo presupone magnitud/unidad homogéneas y valores moderados.
            return formato.fila("Promedio", String.format(Locale.ROOT, "%.2f", suma / copia.size()));
        }
    }
    public static void main(String[] args) {
        var datos = List.of(new Lectura("S01", 22.5), new Lectura("S02", 27.5));
        System.out.println("Detalle / texto:");
        System.out.println(new ReporteDetalle(new FormatoTexto()).generar(datos));
        System.out.println("Resumen / CSV:");
        System.out.println(new ReporteResumen(new FormatoCsv()).generar(datos));
        System.out.println("Resumen / texto:");
        System.out.println(new ReporteResumen(new FormatoTexto()).generar(datos));
        System.out.println(new ReporteResumen(new FormatoTexto()).generar(List.of()));
    }
}
