import java.util.Objects;

/** Adapta un equipo legado que entrega décimas de Fahrenheit al contrato Celsius. */
public class AdapterDemo {
    /** Contrato que necesita el cliente: valores finitos expresados en Celsius. */
    interface SensorTemperatura { double leerCelsius(); }

    /** Simula una biblioteca externa que no implementa nuestro contrato. */
    static final class TermometroLegado {
        private final int decimas;
        TermometroLegado(int decimas) { this.decimas = decimas; }
        // -9999 es un código de fallo del protocolo, no una temperatura válida.
        int obtenerDecimasFahrenheit() { return decimas; }
    }

    /** Adaptador de objetos: conserva el equipo y traduce operación, unidad y error. */
    static final class AdaptadorFahrenheit implements SensorTemperatura {
        private final TermometroLegado equipo;
        AdaptadorFahrenheit(TermometroLegado equipo) {
            this.equipo = Objects.requireNonNull(equipo, "Equipo obligatorio");
        }
        @Override public double leerCelsius() {
            int dato = equipo.obtenerDecimasFahrenheit();
            if (dato == -9999) throw new IllegalStateException("Equipo sin lectura");
            // La división decimal evita perder las décimas por división entera.
            double fahrenheit = dato / 10.0;
            return (fahrenheit - 32.0) * 5.0 / 9.0;
        }
    }

    /** Cliente: desconoce tanto el protocolo legado como su unidad de medida. */
    static final class Estacion {
        private final SensorTemperatura sensor;
        Estacion(SensorTemperatura sensor) {
            this.sensor = Objects.requireNonNull(sensor, "Sensor obligatorio");
        }
        double medir() { return sensor.leerCelsius(); }
    }

    public static void main(String[] args) {
        SensorTemperatura sensor = new AdaptadorFahrenheit(new TermometroLegado(770));
        Estacion estacion = new Estacion(sensor);
        System.out.println("Temperatura: " + estacion.medir() + " °C");
        try {
            new Estacion(new AdaptadorFahrenheit(new TermometroLegado(-9999))).medir();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
