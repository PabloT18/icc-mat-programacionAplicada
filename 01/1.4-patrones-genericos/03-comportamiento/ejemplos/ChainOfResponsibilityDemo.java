import java.util.Objects;

/**
 * Cadena de validación con rechazo inmediato y aceptación al completar todos
 * los filtros.
 */
public class ChainOfResponsibilityDemo {
    // Dato de entrada todavía no validado: permite demostrar rechazos en los
    // manejadores.
    record Lectura(String id, double valor, String unidad) {
    }

    record Resultado(boolean aceptada, String motivo) {
    }

    /** Cada filtro valida una responsabilidad y decide si continúa la solicitud. */
    static abstract class Filtro {
        private final Filtro siguiente;

        Filtro(Filtro siguiente) {
            this.siguiente = siguiente;
        }

        final Resultado procesar(Lectura lectura) {
            Objects.requireNonNull(lectura, "Lectura obligatoria");
            String error = comprobar(lectura);
            if (error != null)
                return new Resultado(false, error); // Corta la cadena.
            return siguiente == null ? new Resultado(true, "Lectura aceptada")
                    : siguiente.procesar(lectura);
        }

        // null representa ausencia de error únicamente dentro de este contrato
        // protegido.
        protected abstract String comprobar(Lectura lectura);
    }

    static final class FiltroIdentidad extends Filtro {
        FiltroIdentidad(Filtro siguiente) {
            super(siguiente);
        }

        @Override
        protected String comprobar(Lectura lectura) {
            return lectura.id() == null || lectura.id().isBlank() ? "Id obligatorio" : null;
        }
    }

    static final class FiltroUnidad extends Filtro {
        FiltroUnidad(Filtro siguiente) {
            super(siguiente);
        }

        @Override
        protected String comprobar(Lectura lectura) {
            return "°C".equals(lectura.unidad()) ? null : "Unidad no admitida";
        }
    }

    static final class FiltroRango extends Filtro {
        FiltroRango(Filtro siguiente) {
            super(siguiente);
        }

        @Override
        protected String comprobar(Lectura lectura) {
            return !Double.isFinite(lectura.valor()) || lectura.valor() < -50 || lectura.valor() > 100
                    ? "Valor fuera de rango"
                    : null;
        }
    }

    public static void main(String[] args) {
        // Se construye desde el último filtro hacia el primero; no hay enlaces
        // mutables.
        Filtro cadena = new FiltroIdentidad(new FiltroUnidad(new FiltroRango(null)));
        System.out.println(cadena.procesar(new Lectura("S01", 22.5, "°C")));
        System.out.println(cadena.procesar(new Lectura("S02", 22.5, "personas")));
        System.out.println(cadena.procesar(new Lectura("S03", Double.NaN, "°C")));
        // Tiene varios defectos; solo se informa el primero según el orden de filtros.
        System.out.println(cadena.procesar(new Lectura("", 180, "K")));
    }
}
