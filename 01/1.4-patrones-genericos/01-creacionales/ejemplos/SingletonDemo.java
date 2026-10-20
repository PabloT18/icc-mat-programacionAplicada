package edu.ups.pap.u01.patrones.creacionales;

import java.time.Duration;
import java.util.Objects;

/** Un único objeto de configuración, accesible a través de un enum. */
public class SingletonDemo {
    // Contrato pequeño: los servicios no necesitan conocer el Singleton.
    interface Parametros {
        Duration intervalo();
        String unidad();
    }

    enum Configuracion implements Parametros {
        INSTANCIA;

        private final Duration intervalo = Duration.ofSeconds(30);
        private final String unidad = "°C";

        @Override
        public Duration intervalo() { return intervalo; }
        @Override
        public String unidad() { return unidad; }
    }

    /** Dependencia explícita: también permite recibir una configuración de prueba. */
    static final class ServicioMuestreo {
        private final Parametros parametros;

        ServicioMuestreo(Parametros parametros) {
            this.parametros = Objects.requireNonNull(parametros);
        }

        String describir(String sensor) {
            return sensor + ": cada " + parametros.intervalo().toSeconds()
                    + " s, unidad " + parametros.unidad();
        }
    }

    public static void main(String[] args) {
        Configuracion primera = Configuracion.INSTANCIA;
        Configuracion segunda = Configuracion.INSTANCIA;
        System.out.println("Misma instancia: " + (primera == segunda));
        ServicioMuestreo servicio = new ServicioMuestreo(primera);
        System.out.println(servicio.describir("S01"));

        // Sustituto de prueba: el servicio no llama al Singleton internamente.
        Parametros prueba = new Parametros() {
            public Duration intervalo() { return Duration.ofSeconds(1); }
            public String unidad() { return "°F"; }
        };
        System.out.println(new ServicioMuestreo(prueba).describir("TEST"));
    }
}
