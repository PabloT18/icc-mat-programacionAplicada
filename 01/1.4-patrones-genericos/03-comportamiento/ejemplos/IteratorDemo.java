import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Recorre una instantánea del inventario sin exponer su lista interna. */
public class IteratorDemo {
    record Sensor(String id, String ubicacion) {
        Sensor {
            if (id == null || id.isBlank() || ubicacion == null || ubicacion.isBlank())
                throw new IllegalArgumentException("Sensor inválido");
        }
    }

    static final class Inventario implements Iterable<Sensor> {
        private final List<Sensor> sensores = new ArrayList<>();

        void agregar(Sensor sensor) {
            sensores.add(Objects.requireNonNull(sensor));
        }

        @Override
        public Iterator<Sensor> iterator() {
            // Cada recorrido obtiene lista y cursor propios. No observa altas posteriores.
            return new IteradorInventario(List.copyOf(sensores));
        }
    }

    static final class IteradorInventario implements Iterator<Sensor> {
        private final List<Sensor> instantanea;
        private int posicion;

        IteradorInventario(List<Sensor> instantanea) {
            this.instantanea = List.copyOf(instantanea);
        }

        @Override
        public boolean hasNext() {
            return posicion < instantanea.size();
        }

        @Override
        public Sensor next() {
            if (!hasNext())
                throw new NoSuchElementException("Recorrido agotado");
            return instantanea.get(posicion++); // Devuelve el actual y avanza una posición.
        }
        // remove() conserva la implementación de Iterator, que no admite la operación.
    }

    public static void main(String[] args) {
        Inventario inventario = new Inventario();
        inventario.agregar(new Sensor("S01", "Laboratorio"));
        inventario.agregar(new Sensor("S02", "Biblioteca"));
        Iterator<Sensor> recorrido = inventario.iterator();
        inventario.agregar(new Sensor("S03", "Aula"));
        System.out.println("Instantánea anterior:");
        while (recorrido.hasNext())
            System.out.println(recorrido.next().id());
        System.out.println("Nuevo recorrido con for-each:");
        for (Sensor sensor : inventario)
            System.out.println(sensor.id());
        try {
            recorrido.next();
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }
}
