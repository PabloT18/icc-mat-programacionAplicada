package edu.ups.pap.u02;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class FicherosDemo {
    static final Pattern P = Pattern.compile("(S[0-9]{2});(-?[0-9]+(?:[.][0-9]+)?);C");

    public static void main(String[] args) throws IOException {
        Path archivo = args.length == 0 ? Files.createTempFile("tramas-", ".txt") : Path.of(args[0]);
        boolean temporal = args.length == 0;
        if (temporal)
            Files.writeString(archivo, "S01;23.5;C\nmal\nS01;24.0;C\nS02;-2;C\n");
        Map<String, Integer> conteo = new LinkedHashMap<>();
        int linea = 0, validas = 0, invalidas = 0;
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String texto;
            while ((texto = lector.readLine()) != null) {
                linea++;
                if (texto.isBlank())
                    continue;
                Matcher m = P.matcher(texto);
                if (texto.length() <= 80 && m.matches() && Double.isFinite(Double.parseDouble(m.group(2)))) {
                    conteo.merge(m.group(1), 1, Integer::sum);
                    validas++;
                } else {
                    invalidas++;
                    System.out.println("Rechazada línea " + linea);
                }
            }
        } finally {
            if (temporal)
                Files.deleteIfExists(archivo);
        }
        System.out.println("Aceptadas=" + validas + ", rechazadas=" + invalidas);
        System.out.println(conteo);
    }
}
