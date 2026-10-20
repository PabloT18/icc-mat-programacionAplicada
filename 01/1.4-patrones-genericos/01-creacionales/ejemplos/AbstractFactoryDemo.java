package edu.ups.pap.u01.patrones.creacionales;

import java.util.Locale;
import java.util.Objects;

/** Crea una fuente y un conversor compatibles como una sola familia. */
public class AbstractFactoryDemo {
    interface FuenteTemperatura { double leer(); }
    interface ConversorTemperatura { double aCelsius(double valor); }

    /** Fábrica abstracta: dos tipos de producto que deben trabajar juntos. */
    interface FabricaEquipo {
        FuenteTemperatura crearFuente();
        ConversorTemperatura crearConversor();
    }

    // Fuentes simuladas: permiten probar las conversiones sin hardware.
    static final class FuenteCelsius implements FuenteTemperatura {
        @Override
        public double leer() { return 25.0; }
    }

    static final class FuenteFahrenheit implements FuenteTemperatura {
        @Override
        public double leer() { return 77.0; }
    }

    static final class ConversorCelsius implements ConversorTemperatura {
        @Override
        public double aCelsius(double valor) { return valor; }
    }

    static final class ConversorFahrenheit implements ConversorTemperatura {
        @Override
        public double aCelsius(double valor) { return (valor - 32) * 5.0 / 9.0; }
    }

    static final class EquipoCelsius implements FabricaEquipo {
        @Override
        public FuenteTemperatura crearFuente() { return new FuenteCelsius(); }
        @Override
        public ConversorTemperatura crearConversor() { return new ConversorCelsius(); }
    }

    static final class EquipoFahrenheit implements FabricaEquipo {
        @Override
        public FuenteTemperatura crearFuente() { return new FuenteFahrenheit(); }
        @Override
        public ConversorTemperatura crearConversor() { return new ConversorFahrenheit(); }
    }

    /** Cliente: solo obtiene los productos mediante una misma fábrica. */
    static final class Estacion {
        private final FuenteTemperatura fuente;
        private final ConversorTemperatura conversor;

        Estacion(FabricaEquipo fabrica) {
            Objects.requireNonNull(fabrica, "Fábrica obligatoria");
            fuente = Objects.requireNonNull(fabrica.crearFuente());
            conversor = Objects.requireNonNull(fabrica.crearConversor());
        }

        double medirCelsius() {
            double valor = conversor.aCelsius(fuente.leer());
            if (!Double.isFinite(valor)) {
                throw new IllegalStateException("Medición no finita");
            }
            return valor;
        }
    }

    public static void main(String[] args) {
        Estacion local = new Estacion(new EquipoCelsius());
        Estacion importada = new Estacion(new EquipoFahrenheit());
        System.out.printf(Locale.ROOT, "Equipo Celsius: %.1f °C%n", local.medirCelsius());
        System.out.printf(Locale.ROOT, "Equipo Fahrenheit: %.1f °C%n", importada.medirCelsius());
        // Si se mezclara FuenteFahrenheit con ConversorCelsius,
        // 77 °F se interpretarían incorrectamente como 77 °C.
    }
}
