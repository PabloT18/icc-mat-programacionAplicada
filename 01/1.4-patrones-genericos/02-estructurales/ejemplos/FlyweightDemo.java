import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Comparte metadatos inmutables; cada sensor conserva su identificador y ubicación. */
public class FlyweightDemo {
    /** Clave por valor: incluye todos los atributos que determinan el tipo compartido. */
    record ClaveTipo(String modelo, String unidad, double minimo, double maximo) {
        ClaveTipo {
            if (modelo == null || modelo.isBlank() || unidad == null || unidad.isBlank()
                    || !Double.isFinite(minimo) || !Double.isFinite(maximo) || minimo > maximo)
                throw new IllegalArgumentException("Tipo inválido");
        }
    }
    /** Flyweight: no almacena el id, la ubicación ni el último valor de cada sensor. */
    static final class TipoSensor {
        private final ClaveTipo clave;
        TipoSensor(ClaveTipo clave) { this.clave = Objects.requireNonNull(clave); }
        boolean admite(double valor) {
            return Double.isFinite(valor) && valor >= clave.minimo() && valor <= clave.maximo();
        }
        String describir(String id, String ubicacion) {
            // Estado extrínseco recibido para esta operación, no retenido en el objeto.
            return id + " @ " + ubicacion + " | " + clave.modelo() + " | " + clave.unidad();
        }
    }
    /** Fábrica local al ejemplo: reutiliza una instancia por clave, sin ser Singleton. */
    static final class FabricaTipos {
        private final Map<ClaveTipo, TipoSensor> tipos = new HashMap<>();
        TipoSensor obtener(ClaveTipo clave) {
            Objects.requireNonNull(clave, "Clave obligatoria");
            return tipos.computeIfAbsent(clave, TipoSensor::new);
        }
        int cantidad() { return tipos.size(); }
    }
    /** Contexto: datos particulares más una referencia al objeto compartido. */
    record Sensor(String id, String ubicacion, TipoSensor tipo) {
        Sensor {
            if (id == null || id.isBlank() || ubicacion == null || ubicacion.isBlank())
                throw new IllegalArgumentException("Sensor inválido");
            Objects.requireNonNull(tipo, "Tipo obligatorio");
        }
        String describir() { return tipo.describir(id, ubicacion); }
    }
    public static void main(String[] args) {
        FabricaTipos fabrica = new FabricaTipos();
        Sensor s01 = new Sensor("S01", "Laboratorio", fabrica.obtener(new ClaveTipo("T100", "°C", -40, 80)));
        Sensor s02 = new Sensor("S02", "Biblioteca", fabrica.obtener(new ClaveTipo("T100", "°C", -40, 80)));
        Sensor s03 = new Sensor("S03", "Aula", fabrica.obtener(new ClaveTipo("O200", "personas", 0, 60)));
        System.out.println(s01.describir());
        System.out.println(s02.describir());
        System.out.println(s03.describir());
        System.out.println("S01 y S02 comparten tipo: " + (s01.tipo() == s02.tipo()));
        System.out.println("Tipos creados: " + fabrica.cantidad());
        System.out.println("Temperatura 90 admitida: " + s01.tipo().admite(90));
    }
}
