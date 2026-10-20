package edu.ups.pap.u01.patrones.creacionales;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Construye una configuración válida antes de entregarla al servicio. */
public class BuilderDemo {
    static final class PlanMuestreo {
        private final String nombre;
        private final Duration intervalo;
        private final List<String> sensores;

        private PlanMuestreo(Builder builder) {
            nombre = builder.nombre;
            intervalo = builder.intervalo;
            sensores = List.copyOf(builder.sensores); // Copia defensiva.
        }

        public String resumen() {
            return nombre + " | " + intervalo.toSeconds() + " s | " + sensores;
        }

        /** Builder mutable: todavía no representa un plan ejecutable. */
        static final class Builder {
            private final String nombre;
            private Duration intervalo = Duration.ofSeconds(30);
            private final List<String> sensores = new ArrayList<>();

            Builder(String nombre) {
                if (nombre == null || nombre.isBlank()) {
                    throw new IllegalArgumentException("Nombre obligatorio");
                }
                this.nombre = nombre;
            }

            Builder cada(Duration intervalo) {
                this.intervalo = Objects.requireNonNull(intervalo);
                return this;
            }

            Builder agregarSensor(String id) {
                if (id == null || id.isBlank()) {
                    throw new IllegalArgumentException("Identificador obligatorio");
                }
                if (sensores.contains(id)) {
                    throw new IllegalArgumentException("Sensor duplicado: " + id);
                }
                sensores.add(id);
                return this;
            }

            PlanMuestreo construir() {
                // Invariantes del producto: no se entrega un plan incompleto.
                if (intervalo.compareTo(Duration.ofSeconds(1)) < 0) {
                    throw new IllegalArgumentException("Intervalo mínimo: 1 segundo");
                }
                if (sensores.isEmpty()) {
                    throw new IllegalArgumentException("Agrega al menos un sensor");
                }
                return new PlanMuestreo(this);
            }
        }
    }

    public static void main(String[] args) {
        PlanMuestreo.Builder builder = new PlanMuestreo.Builder("Laboratorio")
                .cada(Duration.ofSeconds(10)).agregarSensor("S01");
        PlanMuestreo primero = builder.construir();
        PlanMuestreo segundo = builder.agregarSensor("S02").construir();
        // Reutilizar el builder no cambia el producto que ya fue creado.
        System.out.println(primero.resumen());
        System.out.println(segundo.resumen());
        try {
            new PlanMuestreo.Builder("Vacío").construir();
        } catch (IllegalArgumentException error) {
            System.out.println(error.getMessage());
        }
    }
}
