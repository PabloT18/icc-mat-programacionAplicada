import java.util.Locale;
import java.util.Objects;

/** Agrega calibración y auditoría a una fuente, mostrando que el orden importa. */
public class DecoratorDemo {
    interface FuenteTemperatura { double leer(); }
    static final class FuenteFija implements FuenteTemperatura {
        private final double valor;
        FuenteFija(double valor) {
            if (!Double.isFinite(valor)) throw new IllegalArgumentException("Valor no finito");
            this.valor = valor;
        }
        @Override public double leer() { return valor; }
    }
    /** Comparte el contrato del componente y conserva un único componente envuelto. */
    static abstract class DecoradorFuente implements FuenteTemperatura {
        protected final FuenteTemperatura envuelta;
        DecoradorFuente(FuenteTemperatura envuelta) {
            this.envuelta = Objects.requireNonNull(envuelta, "Fuente obligatoria");
        }
        @Override public double leer() { return envuelta.leer(); }
    }
    static final class FuenteCalibrada extends DecoradorFuente {
        private final double ajuste;
        FuenteCalibrada(FuenteTemperatura envuelta, double ajuste) {
            super(envuelta);
            if (!Double.isFinite(ajuste)) throw new IllegalArgumentException("Ajuste no finito");
            this.ajuste = ajuste;
        }
        @Override public double leer() {
            double resultado = super.leer() + ajuste;
            if (!Double.isFinite(resultado)) throw new IllegalStateException("Resultado no finito");
            return resultado;
        }
    }
    static final class FuenteAuditada extends DecoradorFuente {
        FuenteAuditada(FuenteTemperatura envuelta) { super(envuelta); }
        @Override public double leer() {
            double resultado = super.leer(); // Se lee una sola vez: no consumir dos muestras.
            System.out.printf(Locale.ROOT, "Auditoría: %.1f °C%n", resultado);
            return resultado; // Añade un efecto lateral, pero conserva el valor recibido.
        }
    }
    public static void main(String[] args) {
        FuenteTemperatura base = new FuenteFija(24.0);
        FuenteTemperatura auditarFinal = new FuenteAuditada(new FuenteCalibrada(base, 0.5));
        FuenteTemperatura auditarOriginal = new FuenteCalibrada(new FuenteAuditada(base), 0.5);
        System.out.println("Auditoría exterior:");
        System.out.println("Resultado: " + auditarFinal.leer() + " °C");
        System.out.println("Auditoría interior:");
        System.out.println("Resultado: " + auditarOriginal.leer() + " °C");
    }
}
