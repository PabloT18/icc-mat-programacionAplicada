import java.util.Objects;

/** Controla las operaciones válidas de una sesión según su estado actual. */
public class StateDemo {
    interface EstadoSesion {
        void iniciar(SesionMuestreo sesion);

        void pausar(SesionMuestreo sesion);

        void cerrar(SesionMuestreo sesion);

        String nombre();
    }

    static final class SesionMuestreo {
        private EstadoSesion estado = new Preparada();

        void cambiar(EstadoSesion nuevo) {
            estado = Objects.requireNonNull(nuevo);
        }

        void iniciar() {
            estado.iniciar(this);
        }

        void pausar() {
            estado.pausar(this);
        }

        void cerrar() {
            estado.cerrar(this);
        }

        String estado() {
            return estado.nombre();
        }
    }

    static final class Preparada implements EstadoSesion {
        @Override
        public void iniciar(SesionMuestreo s) {
            s.cambiar(new Activa());
        }

        @Override
        public void pausar(SesionMuestreo s) {
            throw new IllegalStateException("La sesión no está activa");
        }

        @Override
        public void cerrar(SesionMuestreo s) {
            s.cambiar(new Cerrada());
        }

        @Override
        public String nombre() {
            return "Preparada";
        }
    }

    static final class Activa implements EstadoSesion {
        @Override
        public void iniciar(SesionMuestreo s) {
            /* Operación idempotente: ya está activa. */ }

        @Override
        public void pausar(SesionMuestreo s) {
            s.cambiar(new Pausada());
        }

        @Override
        public void cerrar(SesionMuestreo s) {
            s.cambiar(new Cerrada());
        }

        @Override
        public String nombre() {
            return "Activa";
        }
    }

    static final class Pausada implements EstadoSesion {
        @Override
        public void iniciar(SesionMuestreo s) {
            s.cambiar(new Activa());
        }

        @Override
        public void pausar(SesionMuestreo s) {
            /* Ya está pausada. */ }

        @Override
        public void cerrar(SesionMuestreo s) {
            s.cambiar(new Cerrada());
        }

        @Override
        public String nombre() {
            return "Pausada";
        }
    }

    static final class Cerrada implements EstadoSesion {
        @Override
        public void iniciar(SesionMuestreo s) {
            throw new IllegalStateException("Sesión cerrada");
        }

        @Override
        public void pausar(SesionMuestreo s) {
            throw new IllegalStateException("Sesión cerrada");
        }

        @Override
        public void cerrar(SesionMuestreo s) {
            /* Cerrar nuevamente no modifica el estado. */ }

        @Override
        public String nombre() {
            return "Cerrada";
        }
    }

    public static void main(String[] args) {
        SesionMuestreo sesion = new SesionMuestreo();
        System.out.println(sesion.estado());
        sesion.iniciar();
        System.out.println(sesion.estado());
        sesion.pausar();
        System.out.println(sesion.estado());
        sesion.iniciar();
        System.out.println(sesion.estado());
        sesion.cerrar();
        System.out.println(sesion.estado());
        try {
            sesion.iniciar();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
