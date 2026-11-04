import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;

/**
 * Intercambia el algoritmo de resumen sin modificar el servicio que lo utiliza.
 */
public class StrategyDemo {
    interface EstrategiaResumen {
        OptionalDouble calcular(List<Double> valores);
    }

    static final class Promedio implements EstrategiaResumen {
        @Override
        public OptionalDouble calcular(List<Double> valores) {
            if (valores.isEmpty())
                return OptionalDouble.empty();
            double suma = 0;
            for (double valor : valores)
                suma += valor;
            double resultado = suma / valores.size();
            if (!Double.isFinite(resultado))
                throw new ArithmeticException("Promedio fuera de rango");
            return OptionalDouble.of(resultado);
        }
    }

    static final class Mediana implements EstrategiaResumen {
        @Override
        public OptionalDouble calcular(List<Double> valores) {
            if (valores.isEmpty())
                return OptionalDouble.empty();
            List<Double> ordenados = new ArrayList<>(valores); // Nunca ordenar la entrada del cliente.
            ordenados.sort(Double::compare);
            int medio = ordenados.size() / 2;
            // Dividir antes de sumar evita desbordamiento en la suma de dos valores
            // extremos.
            double valor = ordenados.size() % 2 == 1 ? ordenados.get(medio)
                    : ordenados.get(medio - 1) / 2.0 + ordenados.get(medio) / 2.0;
            return OptionalDouble.of(valor);
        }
    }

    static final class Analizador {
        private EstrategiaResumen estrategia;

        Analizador(EstrategiaResumen estrategia) {
            usar(estrategia);
        }

        void usar(EstrategiaResumen estrategia) {
            this.estrategia = Objects.requireNonNull(estrategia);
        }

        OptionalDouble resumir(List<Double> valores) {
            List<Double> copia = List.copyOf(valores); // Rechaza lista o elementos null.
            for (double valor : copia)
                if (!Double.isFinite(valor))
                    throw new IllegalArgumentException("Valor no finito");
            return estrategia.calcular(copia);
        }
    }

    public static void main(String[] args) {
        List<Double> datos = List.of(20.0, 21.0, 100.0);
        Analizador analizador = new Analizador(new Promedio());
        System.out.println("Promedio: " + analizador.resumir(datos).orElseThrow());
        analizador.usar(new Mediana());
        System.out.println("Mediana: " + analizador.resumir(datos).orElseThrow());
        System.out.println("Lista original: " + datos);
        System.out.println("Lista vacía: " + analizador.resumir(List.of()));
        try {
            analizador.resumir(List.of(Double.NaN));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
