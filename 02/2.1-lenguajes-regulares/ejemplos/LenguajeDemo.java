
// package edu.ups.pap.u02;
import java.util.regex.Pattern;

public class LenguajeDemo {
    static boolean automata(String texto) {
        int estado = 0;
        for (char c : texto.toCharArray()) {
            if (estado == 0 && c == 'S')
                estado = 1;
            else if ((estado == 1 || estado == 2) && c >= '0' && c <= '9')
                estado++;
            else
                return false;
        }
        return estado == 3;
    }

    public static void main(String[] args) {
        Pattern patron = Pattern.compile("S[0-9]{2}");
        for (String s : new String[] { "S01", "S9", "XS01", "S001", "S99", "" }) {
            boolean manual = automata(s), regex = patron.matcher(s).matches();
            if (manual != regex)
                throw new AssertionError(s);
            System.out.println("'" + s + "' -> " + regex);
        }
    }
}
