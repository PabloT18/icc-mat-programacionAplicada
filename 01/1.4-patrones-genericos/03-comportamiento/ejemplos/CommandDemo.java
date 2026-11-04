import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Encapsula cambios del intervalo de muestreo y permite deshacerlos en orden
 * inverso.
 */
public class CommandDemo {
    /**
     * Receptor: mantiene la regla de dominio, incluso si no se utiliza un comando.
     */
    static final class ConfiguracionMuestreo {
        private int intervalo = 30;

        int intervalo() {
            return intervalo;
        }

        void cambiarIntervalo(int segundos) {
            if (segundos < 1 || segundos > 3600)
                throw new IllegalArgumentException("Intervalo permitido: 1 a 3600");
            intervalo = segundos;
        }
    }

    interface Comando {
        void ejecutar();

        void deshacer();
    }

    static final class CambiarIntervalo implements Comando {
        private final ConfiguracionMuestreo receptor;
        private final int nuevo;
        private Integer anterior;
        private boolean ejecutado;
        private boolean deshecho;

        CambiarIntervalo(ConfiguracionMuestreo receptor, int nuevo) {
            this.receptor = Objects.requireNonNull(receptor);
            this.nuevo = nuevo;
        }

        @Override
        public void ejecutar() {
            if (ejecutado)
                throw new IllegalStateException("Comando de un solo uso");
            int previo = receptor.intervalo();
            receptor.cambiarIntervalo(nuevo); // Puede fallar: aún no se registra como ejecutado.
            anterior = previo;
            ejecutado = true;
        }

        @Override
        public void deshacer() {
            if (!ejecutado || deshecho)
                throw new IllegalStateException("No se puede deshacer");
            receptor.cambiarIntervalo(anterior);
            deshecho = true;
        }
    }

    /** Invocador: conoce el contrato Comando, no la configuración concreta. */
    static final class HistorialComandos {
        private final Deque<Comando> historial = new ArrayDeque<>();

        void ejecutar(Comando comando) {
            Objects.requireNonNull(comando).ejecutar();
            historial.push(comando); // Solo registra comandos que terminaron correctamente.
        }

        boolean deshacerUltimo() {
            if (historial.isEmpty())
                return false;
            historial.peek().deshacer(); // Si falla, se conserva la entrada para diagnóstico.
            historial.pop();
            return true;
        }
    }

    public static void main(String[] args) {
        var configuracion = new ConfiguracionMuestreo();
        var historial = new HistorialComandos();
        historial.ejecutar(new CambiarIntervalo(configuracion, 10));
        historial.ejecutar(new CambiarIntervalo(configuracion, 5));
        System.out.println("Intervalo actual: " + configuracion.intervalo());
        try {
            historial.ejecutar(new CambiarIntervalo(configuracion, 0));
        } catch (IllegalArgumentException e) {
            System.out.println("Rechazado: " + e.getMessage());
        }
        historial.deshacerUltimo();
        System.out.println("Primer deshacer: " + configuracion.intervalo());
        historial.deshacerUltimo();
        System.out.println("Segundo deshacer: " + configuracion.intervalo());
        System.out.println("Otro deshacer disponible: " + historial.deshacerUltimo());
    }
}
