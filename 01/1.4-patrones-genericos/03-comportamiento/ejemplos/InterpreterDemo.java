import java.util.Objects;

/**
 * Evalúa un lenguaje pequeño de reglas mediante un árbol de expresiones, sin
 * parser textual.
 */
public class InterpreterDemo {
    record Contexto(double temperatura, int ocupacion) {
        Contexto {
            if (!Double.isFinite(temperatura) || ocupacion < 0)
                throw new IllegalArgumentException("Contexto inválido");
        }
    }

    enum Magnitud {
        TEMPERATURA, OCUPACION
    }

    interface Expresion {
        boolean interpretar(Contexto contexto);
    }

    /** Expresión terminal: consulta una magnitud y la compara con un umbral. */
    record MayorIgual(Magnitud magnitud, double umbral) implements Expresion {
        MayorIgual {
            Objects.requireNonNull(magnitud);
            if (!Double.isFinite(umbral))
                throw new IllegalArgumentException("Umbral no finito");
        }

        @Override
        public boolean interpretar(Contexto contexto) {
            Objects.requireNonNull(contexto);
            double valor = magnitud == Magnitud.TEMPERATURA ? contexto.temperatura() : contexto.ocupacion();
            return valor >= umbral;
        }
    }

    /**
     * No terminal: interpreta a sus dos hijos, con cortocircuito de izquierda a
     * derecha.
     */
    record Y(Expresion izquierda, Expresion derecha) implements Expresion {
        Y {
            Objects.requireNonNull(izquierda);
            Objects.requireNonNull(derecha);
        }

        @Override
        public boolean interpretar(Contexto contexto) {
            return izquierda.interpretar(contexto) && derecha.interpretar(contexto);
        }
    }

    record O(Expresion izquierda, Expresion derecha) implements Expresion {
        O {
            Objects.requireNonNull(izquierda);
            Objects.requireNonNull(derecha);
        }

        @Override
        public boolean interpretar(Contexto contexto) {
            return izquierda.interpretar(contexto) || derecha.interpretar(contexto);
        }
    }

    record No(Expresion operando) implements Expresion {
        No {
            Objects.requireNonNull(operando);
        }

        @Override
        public boolean interpretar(Contexto contexto) {
            return !operando.interpretar(contexto);
        }
    }

    public static void main(String[] args) {
        // Regla: (temperatura >= 28 Y ocupación >= 1) O temperatura >= 40.
        Expresion regla = new O(
                new Y(new MayorIgual(Magnitud.TEMPERATURA, 28), new MayorIgual(Magnitud.OCUPACION, 1)),
                new MayorIgual(Magnitud.TEMPERATURA, 40));
        System.out.println("29 °C, 5 personas: " + regla.interpretar(new Contexto(29, 5)));
        System.out.println("29 °C, 0 personas: " + regla.interpretar(new Contexto(29, 0)));
        System.out.println("40 °C, 0 personas: " + regla.interpretar(new Contexto(40, 0)));
        System.out.println("Negación a 29 °C y 5 personas: " + new No(regla).interpretar(new Contexto(29, 5)));
    }
}
