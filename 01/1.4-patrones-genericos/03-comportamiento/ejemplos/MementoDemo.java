import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Guarda y restaura configuraciones sin permitir al historial leer sus campos.
 */
public class MementoDemo {
    /** Contrato opaco para el cuidador; no expone los valores de la instantánea. */
    interface Memento {
    }

    static final class PlanMuestreo {
        private final Object identidad = new Object();
        private int intervalo = 30;
        private double umbral = 28;

        /** Solo el originador utiliza los campos privados de esta implementación. */
        private record Instantanea(Object propietario, int intervalo, double umbral) implements Memento {
        }

        void configurar(int intervalo, double umbral) {
            if (intervalo < 1 || intervalo > 3600 || !Double.isFinite(umbral))
                throw new IllegalArgumentException("Configuración inválida");
            this.intervalo = intervalo;
            this.umbral = umbral;
        }

        Memento guardar() {
            return new Instantanea(identidad, intervalo, umbral);
        }

        void restaurar(Memento memento) {
            // Evita restaurar un estado que pertenece a otro originador.
            if (!(memento instanceof Instantanea copia) || copia.propietario() != identidad)
                throw new IllegalArgumentException("Instantánea ajena");
            intervalo = copia.intervalo();
            umbral = copia.umbral();
        }

        String describir() {
            return "Intervalo=" + intervalo + ", umbral=" + umbral;
        }
    }

    static final class Historial {
        private final PlanMuestreo plan;
        private final Deque<Memento> estados = new ArrayDeque<>();

        Historial(PlanMuestreo plan) {
            this.plan = Objects.requireNonNull(plan);
        }

        void guardar() {
            estados.push(plan.guardar());
        }

        boolean deshacer() {
            if (estados.isEmpty())
                return false;
            plan.restaurar(estados.peek());
            estados.pop();
            return true;
        }
    }

    public static void main(String[] args) {
        PlanMuestreo plan = new PlanMuestreo();
        Historial historial = new Historial(plan);
        historial.guardar(); // Guardar ANTES del cambio que se desea poder deshacer.
        plan.configurar(10, 30);
        historial.guardar();
        plan.configurar(5, 35);
        System.out.println(plan.describir());
        historial.deshacer();
        System.out.println(plan.describir());
        historial.deshacer();
        System.out.println(plan.describir());
        System.out.println("Otro estado disponible: " + historial.deshacer());
        try {
            plan.restaurar(new PlanMuestreo().guardar());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
