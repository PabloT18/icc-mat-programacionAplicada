
// package edu.ups.pap.u02;
import java.util.regex.*;

public class AsercionesDemo {
    static final Pattern TEMP = Pattern.compile(
            "(?<=TEMP=)-?[0-9]+(?:[.][0-9]+)?(?=C(?: |$))");

    public static void main(String[] args) {
        String texto = "TEMP=23.5C HUM=40PCT TEMP=-2C TEMP=7F TEMP=8CX";
        Matcher m = TEMP.matcher(texto);
        int n = 0;
        while (m.find()) {
            double valor = Double.parseDouble(m.group());
            System.out.println(valor + " en [" + m.start() + "," + m.end() + ")");
            n++;
        }
        if (n != 2)
            throw new AssertionError("Se esperaban dos temperaturas");
        System.out.println("Coincidencias: " + n);
    }
}
