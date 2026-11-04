import java.util.List;
import java.util.Objects;

/**
 * Separa dos operaciones de una familia estable de sensores mediante doble
 * despacho.
 */
public class VisitorDemo {
    interface Sensor {
        String aceptar(VisitanteSensor visitante);
    }

    interface VisitanteSensor {
        String visitar(SensorTemperatura sensor);

        String visitar(SensorOcupacion sensor);
    }

    record SensorTemperatura(String id, double celsius) implements Sensor {
        SensorTemperatura {
            if (id == null || id.isBlank() || !Double.isFinite(celsius))
                throw new IllegalArgumentException("Sensor inválido");
        }

        @Override
        public String aceptar(VisitanteSensor visitante) {
            // Aquí this tiene tipo SensorTemperatura: se selecciona esa sobrecarga.
            return Objects.requireNonNull(visitante).visitar(this);
        }
    }

    record SensorOcupacion(String id, int personas) implements Sensor {
        SensorOcupacion {
            if (id == null || id.isBlank() || personas < 0)
                throw new IllegalArgumentException("Sensor inválido");
        }

        @Override
        public String aceptar(VisitanteSensor visitante) {
            return Objects.requireNonNull(visitante).visitar(this);
        }
    }

    static final class Descripcion implements VisitanteSensor {
        @Override
        public String visitar(SensorTemperatura sensor) {
            return sensor.id() + ": " + sensor.celsius() + " °C";
        }

        @Override
        public String visitar(SensorOcupacion sensor) {
            return sensor.id() + ": " + sensor.personas() + " personas";
        }
    }

    static final class Diagnostico implements VisitanteSensor {
        @Override
        public String visitar(SensorTemperatura sensor) {
            return sensor.id() + (sensor.celsius() >= 28 ? ": temperatura alta" : ": temperatura normal");
        }

        @Override
        public String visitar(SensorOcupacion sensor) {
            return sensor.id() + (sensor.personas() > 30 ? ": aforo excedido" : ": aforo permitido");
        }
    }

    public static void main(String[] args) {
        List<Sensor> sensores = List.of(new SensorTemperatura("S01", 30), new SensorOcupacion("S02", 18));
        VisitanteSensor descripcion = new Descripcion();
        VisitanteSensor diagnostico = new Diagnostico();
        for (Sensor sensor : sensores)
            System.out.println(sensor.aceptar(descripcion));
        for (Sensor sensor : sensores)
            System.out.println(sensor.aceptar(diagnostico));
    }
}
