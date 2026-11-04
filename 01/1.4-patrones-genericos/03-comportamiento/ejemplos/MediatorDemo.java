import java.util.Objects;

/**
 * Coordina temperatura, ventana y ventilador sin referencias directas entre
 * componentes.
 */
public class MediatorDemo {
    interface MediadorClima {
        void temperaturaCambiada(double temperatura);

        void ventanaCambiada(boolean abierta);
    }

    static final class SensorTemperatura {
        private final MediadorClima mediador;

        SensorTemperatura(MediadorClima mediador) {
            this.mediador = Objects.requireNonNull(mediador);
        }

        void publicar(double temperatura) {
            if (!Double.isFinite(temperatura))
                throw new IllegalArgumentException("Temperatura no finita");
            mediador.temperaturaCambiada(temperatura);
        }
    }

    static final class Ventana {
        private final MediadorClima mediador;

        Ventana(MediadorClima mediador) {
            this.mediador = Objects.requireNonNull(mediador);
        }

        void cambiar(boolean abierta) {
            mediador.ventanaCambiada(abierta);
        }
    }

    /**
     * Actuador: no conoce el sensor ni la ventana, y no vuelve a notificar al
     * mediador.
     */
    static final class Ventilador {
        private boolean encendido;

        void establecer(boolean nuevoEstado) {
            if (encendido == nuevoEstado)
                return; // Evita acciones repetidas sin cambio.
            encendido = nuevoEstado;
            System.out.println("Ventilador: " + (encendido ? "encendido" : "apagado"));
        }
    }

    static final class ControlClima implements MediadorClima {
        private final SensorTemperatura sensor;
        private final Ventana ventana;
        private final Ventilador ventilador = new Ventilador();
        private Double temperatura;
        private boolean ventanaAbierta;

        ControlClima() {
            // Los componentes conservan el mediador, pero no llaman durante su
            // construcción.
            sensor = new SensorTemperatura(this);
            ventana = new Ventana(this);
        }

        SensorTemperatura sensor() {
            return sensor;
        }

        Ventana ventana() {
            return ventana;
        }

        @Override
        public void temperaturaCambiada(double temperatura) {
            if (!Double.isFinite(temperatura))
                throw new IllegalArgumentException("Temperatura no finita");
            this.temperatura = temperatura;
            coordinar();
        }

        @Override
        public void ventanaCambiada(boolean abierta) {
            ventanaAbierta = abierta;
            coordinar();
        }

        private void coordinar() {
            // Sin una medición válida, el ventilador permanece apagado.
            ventilador.establecer(temperatura != null && temperatura >= 28 && !ventanaAbierta);
        }
    }

    public static void main(String[] args) {
        ControlClima control = new ControlClima();
        System.out.println("Temperatura 30, ventana cerrada:");
        control.sensor().publicar(30);
        System.out.println("Abrir ventana:");
        control.ventana().cambiar(true);
        System.out.println("Cerrar ventana:");
        control.ventana().cambiar(false);
        System.out.println("Temperatura 24:");
        control.sensor().publicar(24);
    }
}
