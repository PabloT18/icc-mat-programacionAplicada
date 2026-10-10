package edu.ups.pap.u01;

import java.util.*;
import java.util.function.*;

public class PatronesDemo {
    record Sensor(String id, String ubicacion) {
    }

    interface Repositorio<ID, T> {
        void guardar(T valor);

        Optional<T> buscar(ID id);

        List<T> listar();
    }

    static class Memoria<ID, T> implements Repositorio<ID, T> {
        private final Map<ID, T> datos = new LinkedHashMap<>();
        private final Function<T, ID> identidad;

        Memoria(Function<T, ID> identidad) {
            this.identidad = identidad;
        }

        public void guardar(T valor) {
            Objects.requireNonNull(valor);
            ID id = Objects.requireNonNull(identidad.apply(valor));
            if (datos.putIfAbsent(id, valor) != null)
                throw new IllegalArgumentException("ID duplicado");
        }

        public Optional<T> buscar(ID id) {
            return Optional.ofNullable(datos.get(id));
        }

        public List<T> listar() {
            return List.copyOf(datos.values());
        }
    }

    public static void main(String[] args) {
        Repositorio<String, Sensor> repo = new Memoria<>(Sensor::id);
        Supplier<Sensor> fabrica = () -> new Sensor("S01", "Lab");
        repo.guardar(fabrica.get());
        repo.guardar(new Sensor("S02", "Biblioteca"));
        Predicate<Sensor> enLab = s -> s.ubicacion().equals("Lab");
        System.out.println(repo.listar().stream().filter(enLab).toList());
        System.out.println(repo.buscar("S99"));
        try {
            repo.guardar(fabrica.get());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
