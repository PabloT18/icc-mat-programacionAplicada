import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/** Controla el acceso y difiere la creación de un catálogo hasta una consulta autorizada. */
public class ProxyDemo {
    record Usuario(String nombre, boolean autorizado) {
        Usuario {
            if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
        }
    }
    /** Sujeto: cliente, proxy y servicio real comparten la misma operación. */
    interface CatalogoSensores { List<String> listar(); }
    static final class CatalogoReal implements CatalogoSensores {
        CatalogoReal() { System.out.println("Inicializando catálogo real"); }
        @Override public List<String> listar() { return List.of("S01", "S02"); }
    }
    /** Proxy de protección y virtual. Uso secuencial; no incluye autenticación real. */
    static final class CatalogoProtegido implements CatalogoSensores {
        private final Usuario usuario;
        private final Supplier<CatalogoSensores> proveedor;
        private CatalogoSensores real; // null significa que aún no se creó el servicio.
        CatalogoProtegido(Usuario usuario, Supplier<CatalogoSensores> proveedor) {
            this.usuario = Objects.requireNonNull(usuario);
            this.proveedor = Objects.requireNonNull(proveedor);
        }
        @Override public List<String> listar() {
            // Verificar ANTES de construir o consultar el servicio real.
            if (!usuario.autorizado()) throw new SecurityException("Acceso denegado a " + usuario.nombre());
            if (real == null) real = Objects.requireNonNull(proveedor.get(), "Proveedor sin catálogo");
            return real.listar(); // Conserva el servicio, no una caché del resultado.
        }
    }
    public static void main(String[] args) {
        CatalogoSensores invitado = new CatalogoProtegido(new Usuario("invitado", false), CatalogoReal::new);
        try { invitado.listar(); }
        catch (SecurityException e) { System.out.println(e.getMessage()); }
        CatalogoSensores operador = new CatalogoProtegido(new Usuario("operador", true), CatalogoReal::new);
        System.out.println("Primera consulta: " + operador.listar());
        System.out.println("Segunda consulta: " + operador.listar());
    }
}
