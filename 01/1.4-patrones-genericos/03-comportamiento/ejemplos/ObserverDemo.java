import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Notifica lecturas a suscriptores registrados, de forma síncrona y en orden.
 */
public class ObserverDemo {
    record Lectura(String id, double valor) {
        Lectura {
            if (id == null || id.isBlank() || !Double.isFinite(valor))
                throw new IllegalArgumentException("Lectura inválida");
        }
    }

    interface ObservadorLectura {
        void actualizar(Lectura lectura);
    }

    static final class FuenteLecturas {
        private final List<ObservadorLectura> observadores = new ArrayList<>();

        void suscribir(ObservadorLectura observador) {
            Objects.requireNonNull(observador);
            if (!observadores.contains(observador))
                observadores.add(observador);
        }

        void retirar(ObservadorLectura observador) {
            observadores.remove(observador);
        }

        void publicar(Lectura lectura) {
            Objects.requireNonNull(lectura);
            // La instantánea estabiliza los destinatarios de ESTA publicación.
            for (ObservadorLectura observador : List.copyOf(observadores))
                observador.actualizar(lectura);
            // Política deliberada: si un observador falla, la excepción interrumpe el
            // envío.
        }
    }

    static final class Pantalla implements ObservadorLectura {
        @Override
        public void actualizar(Lectura lectura) {
            System.out.println("Pantalla: " + lectura.id() + " = " + lectura.valor() + " °C");
        }
    }

    static final class AlertaUmbral implements ObservadorLectura {
        private final double umbral;

        AlertaUmbral(double umbral) {
            if (!Double.isFinite(umbral))
                throw new IllegalArgumentException("Umbral no finito");
            this.umbral = umbral;
        }

        @Override
        public void actualizar(Lectura lectura) {
            if (lectura.valor() >= umbral)
                System.out.println("Alerta: " + lectura.id());
        }
    }

    public static void main(String[] args) {
        FuenteLecturas fuente = new FuenteLecturas();
        Pantalla pantalla = new Pantalla();
        AlertaUmbral alerta = new AlertaUmbral(28);
        fuente.suscribir(pantalla);
        fuente.suscribir(alerta);
        fuente.publicar(new Lectura("S01", 27));
        fuente.publicar(new Lectura("S02", 30));
        fuente.retirar(alerta);
        System.out.println("Después de retirar la alerta:");
        fuente.publicar(new Lectura("S03", 31));
    }
}
