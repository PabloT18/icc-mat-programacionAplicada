package edu.ups.pap.u01.patrones.creacionales;

import java.util.ArrayList;
import java.util.List;

/** Duplica un perfil y sus reglas mutables para configurarlos por separado. */
public class PrototypeDemo {
    interface Copiable<T> { T copiar(); }

    static final class ReglaUmbral {
        private final String magnitud;
        private double limite;

        ReglaUmbral(String magnitud, double limite) {
            if (magnitud == null || magnitud.isBlank()) {
                throw new IllegalArgumentException("Magnitud obligatoria");
            }
            this.magnitud = magnitud;
            cambiarLimite(limite);
        }

        void cambiarLimite(double nuevo) {
            if (!Double.isFinite(nuevo)) {
                throw new IllegalArgumentException("Límite no finito");
            }
            limite = nuevo;
        }

        ReglaUmbral copiar() { return new ReglaUmbral(magnitud, limite); }

        @Override
        public String toString() { return magnitud + " > " + limite; }
    }

    static final class PerfilSensor implements Copiable<PerfilSensor> {
        private String nombre;
        private final List<ReglaUmbral> reglas = new ArrayList<>();

        PerfilSensor(String nombre) { renombrar(nombre); }

        void renombrar(String nuevo) {
            if (nuevo == null || nuevo.isBlank()) {
                throw new IllegalArgumentException("Nombre obligatorio");
            }
            nombre = nuevo;
        }

        void agregarRegla(String magnitud, double limite) {
            reglas.add(new ReglaUmbral(magnitud, limite));
        }

        void ajustarLimite(int indice, double limite) {
            reglas.get(indice).cambiarLimite(limite);
        }

        @Override
        public PerfilSensor copiar() {
            PerfilSensor copia = new PerfilSensor(nombre);
            // No basta new ArrayList<>(reglas): compartiría las reglas mutables.
            for (ReglaUmbral regla : reglas) {
                copia.reglas.add(regla.copiar());
            }
            return copia;
        }

        @Override
        public String toString() { return nombre + ": " + reglas; }
    }

    public static void main(String[] args) {
        PerfilSensor base = new PerfilSensor("Base");
        base.agregarRegla("temperatura", 28.0);
        base.agregarRegla("humedad", 70.0);

        // El cliente no reconstruye la configuración de cada regla.
        PerfilSensor laboratorio = base.copiar();
        laboratorio.renombrar("Laboratorio");
        laboratorio.ajustarLimite(0, 25.0);

        System.out.println(base);
        System.out.println(laboratorio);
        System.out.println("Mismo perfil: " + (base == laboratorio));
    }
}
