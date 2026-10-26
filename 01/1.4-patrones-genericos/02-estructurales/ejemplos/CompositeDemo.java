import java.util.List;

/** Cuenta sensores activos en una jerarquía de zonas sin distinguir hojas y grupos. */
public class CompositeDemo {
    /** Componente: operación válida tanto para un sensor como para una zona. */
    interface NodoMonitoreo { int contarActivos(); }

    /** Hoja: no contiene otros nodos. */
    record Sensor(String id, boolean activo) implements NodoMonitoreo {
        Sensor {
            if (id == null || id.isBlank()) throw new IllegalArgumentException("Id obligatorio");
        }
        @Override public int contarActivos() { return activo ? 1 : 0; }
    }

    /** Compuesto inmutable: recibe hijos ya construidos y conserva una copia no modificable. */
    static final class Zona implements NodoMonitoreo {
        private final String nombre;
        private final List<NodoMonitoreo> hijos;
        Zona(String nombre, List<NodoMonitoreo> hijos) {
            if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
            this.nombre = nombre;
            this.hijos = List.copyOf(hijos); // También rechaza hijos null.
        }
        @Override public int contarActivos() {
            int total = 0;
            // Despacho polimórfico: cada hijo resuelve su parte del cálculo.
            for (NodoMonitoreo hijo : hijos) total += hijo.contarActivos();
            return total;
        }
        String nombre() { return nombre; }
    }

    public static void main(String[] args) {
        NodoMonitoreo s01 = new Sensor("S01", true);
        NodoMonitoreo s02 = new Sensor("S02", false);
        NodoMonitoreo s03 = new Sensor("S03", true);
        Zona laboratorio = new Zona("Laboratorio", List.of(s01, s02));
        Zona edificio = new Zona("Edificio", List.of(laboratorio, s03));
        System.out.println("S01 activos: " + s01.contarActivos());
        System.out.println(laboratorio.nombre() + " activos: " + laboratorio.contarActivos());
        System.out.println(edificio.nombre() + " activos: " + edificio.contarActivos());
        System.out.println("Zona vacía activos: " + new Zona("Reserva", List.of()).contarActivos());
        // La misma hoja en dos posiciones se cuenta dos veces: no se deduplica por id.
        System.out.println("Hoja compartida activos: " + new Zona("Duplicada", List.of(s01, s01)).contarActivos());
    }
}
