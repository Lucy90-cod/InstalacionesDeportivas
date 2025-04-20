
package poo.helpers;

import java.io.Console;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Keyboard {

    /**
     * Utilidad basada en la clase Scanner para el ingreso básico validado de datos
     * por consola
     * 
     * @author Carlos Cuesta Iglesias
     */

    private static final Console con = System.console();
    public static Scanner sc = new Scanner(con.reader()).useDelimiter("[\n]+|[\r\n]+");

    private Keyboard() {
    } /////////////////////////////////////

    public static String readString(String message) {
        message = String.format("%s%s%s", Color.BLUE, message, Color.RESET);
        System.out.print(message);
        return sc.nextLine();
    }

    public static String readString(int from, int to, String message) {
        String value;
        int tmp = Math.min(from, to);
        if (tmp == to) {
            to = from;
            from = tmp;
        }
        int length;
        do {
            value = readString(message);
            length = value.length();
            if (length != 0 && length < from || length > to) {
                System.out.printf("%sLongitud no permitida. %s", Color.RED, Color.RESET, message);
            }
        } while (length != 0 && length < from || length > to);
        return value;
    }

    public static char readChar(String mensaje) {
        String s = readString(1, 1, mensaje);
        return s.charAt(0);
    }

    public static int readInt(String message) {
        message = String.format("%s%s%s", Color.BLUE, message, Color.RESET);
        boolean ok;
        int value = Integer.MIN_VALUE;
        System.out.print(message);

        do {
            try {
                ok = true;
                value = sc.nextInt();
            } catch (InputMismatchException e) {
                ok = false;
                System.out.printf(">> %sValor erróneo%s. %s", Color.RED, Color.RESET, message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);

        return value;
    }

    public static int readInt(int from, int to, String mensaje) {
        int value;
        int tmp = Math.min(from, to);
        if (tmp == to) {
            to = from;
            from = tmp;
        }

        do {
            value = readInt(mensaje);
            if (value != 0 && (value < from || value > to)) {
                System.out.printf("%sRango inválido. %s", Color.RED, Color.RESET);
            }
        } while (value != 0 && (value < from || value > to));
        return value;
    }

    public static long readLong(String message) {
        message = String.format("%s%s%s", Color.BLUE, message, Color.RESET);
        boolean ok;
        long value = Long.MIN_VALUE;
        System.out.print(message);

        do {
            try {
                ok = true;
                value = sc.nextLong();
            } catch (InputMismatchException e) {
                ok = false;
                System.out.printf(">> %sValor erróneo%s. %s", Color.RED, Color.RESET, message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);

        return value;
    }

    public static long readLong(long from, long to, String mensaje) {
        long value;
        long tmp = Math.min(from, to);
        if (tmp == to) {
            to = from;
            from = tmp;
        }

        do {
            value = readLong(mensaje);
            if (value != 0 && (value < from || value > to)) {
                System.out.printf("%sRango inválido. %s", Color.RED, Color.RESET);
            }
        } while (value != 0 && (value < from || value > to));
        return value;
    }

    public static double readDouble(String message) {
        message = String.format("%s%s%s", Color.BLUE, message, Color.RESET);
        boolean ok;
        double value = Double.NaN;
        System.out.print(message);

        do {
            try {
                ok = true;
                value = sc.nextDouble();
            } catch (InputMismatchException e) {
                ok = false;
                System.out.printf(">> %sValor erróneo%s. %s", Color.RED, Color.RESET, message);
            } finally {
                sc.nextLine();
            }
        } while (!ok);

        return value;
    }

    public static double readDouble(double from, double to, String mensaje) {
        double value;
        double tmp = Math.min(from, to);
        if (tmp == to) {
            to = from;
            from = tmp;
        }

        do {
            value = readDouble(mensaje);
            if (value != 0 && (value < from || value > to)) {
                System.out.printf("%sRango inválido. %s", Color.RED, Color.RESET);
            }
        } while (value != 0 && (value < from || value > to));
        return value;
    }

    public static boolean readBoolean(String message) {
        message = String.format("%s%s%s", Color.BLUE, message, Color.RESET);
        boolean ok;
        boolean value = false;
        System.out.print(message);

        do {
            try {
                ok = true;
                String str = ' ' + sc.nextLine().toLowerCase().trim() + ' ';
                if (" si s true t yes y ".contains(str)) {
                    value = true;
                } else if (" no n false f not ".contains(str)) {
                    value = false;
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                ok = false;
                System.out.printf("%s>> Se esperaba [si|s|true|t|yes|y|no|not|n|false|f]%s %s", Color.RED, Color.RESET,
                        message);
            } finally {
                // sc.nextLine();
            }
        } while (!ok);

        return value;
    }

    /**
     * Permite leer una fecha en cualquier formato válido. Si ingresa "hoy" se
     * asigna la fecha actual
     * 
     * @param message La petición de la fecha o la fecha y la hora
     * @return Un objeto de tipo Calendar con la hora ingresada
     */
    public static LocalDate readDate(String message) {
        message = String.format("%s%s%s", Color.BLUE, message, Color.RESET);
        boolean ok;
        LocalDate date = LocalDate.now();
        System.out.print(message);

        do {
            try {
                ok = true;
                String strDate = sc.nextLine().trim().toLowerCase();
                if (!"hoy|now".contains(strDate)) {
                    date = LocalDate.parse(strDate);
                }
            } catch (DateTimeParseException dtpe) {
                ok = false;
                System.out.printf(">> %sFecha errónea%s. %s", Color.RED, Color.RESET, message);
            } finally {
                // sc.nextLine();
            }
        } while (!ok);

        return date;
    }

    public static LocalDate readDate(String from, String to, String mensaje) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        LocalDate value;

        if (fromDate.isAfter(toDate)) {
            LocalDate tmp = fromDate;
            toDate = fromDate;
            fromDate = tmp;
        }

        do {
            value = readDate(mensaje);
            if (value.isBefore(fromDate) || value.isAfter(toDate)) {
                System.out.printf("%sRango inválido. %s", Color.RED, Color.RESET);
            }
        } while (value.isBefore(fromDate) || value.isAfter(toDate));
        return value;
    }

    /**
     * Permite leer una fecha/hora en formato de 24 horas (AAAA-MM-DD HH:MM)
     * 
     * @param message La petición que se hace al usuario
     * @return La fecha y la hora ingresada o la del momento si se ingresa "ahora"
     */
    public static LocalDateTime readDateTime(String message) {
        message = String.format("%s%s%s", Color.BLUE, message, Color.RESET);
        boolean ok;
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.print(message);

        do {
            try {
                ok = true;
                String strDateTime = sc.nextLine().trim().toLowerCase();
                if (!"ahora|now".contains(strDateTime)) {
                    dateTime = LocalDateTime.parse(strDateTime.replace(" ", "T"));
                }
            } catch (DateTimeParseException dtpe) {
                // System.out.println(dtpe.getMessage());
                ok = false;
                System.out.printf(">> %sFecha y hora errónea%s. %s", Color.RED, Color.RESET, message);
            } finally {
                // sc.nextLine();
            }
        } while (!ok);

        return dateTime;
    }

    public static LocalDateTime readDateTime(LocalDateTime from, LocalDateTime to, String mensaje) {
        LocalDateTime value;

        if (from.isAfter(to)) {
            LocalDateTime tmp = from;
            to = from;
            from = tmp;
        }

        do {
            value = readDateTime(mensaje);
            if (value.isBefore(from) || value.isAfter(to)) {
                System.out.printf("%sRango inválido. %s", Color.RED, Color.RESET);
            }
        } while (value.isBefore(from) || value.isAfter(to));
        return value;
    }

    private static Duration toDuration(String strDuration) {
        String[] aDuration = strDuration.trim().split(":");
        if (aDuration.length != 2) {
            throw new IllegalArgumentException("Se esperaba HH:MM");
        }
        return Duration.parse(String.format("PT%sH%sM", aDuration[0].trim(), aDuration[1].trim()));
    }

    public static Duration readDuration(String message) {
        // https://www.baeldung.com/java-period-duration
        message = String.format("%s%s%s", Color.BLUE, message, Color.RESET);
        boolean ok;
        Duration duration = Duration.ZERO;
        System.out.print(message);

        do {
            try {
                ok = true;
                String strDuration = sc.nextLine();
                duration = toDuration(strDuration);
            } catch (Exception e) {
                ok = false;
                System.out.printf(">> %sDuración errónea%s. %s", Color.RED, Color.RESET, message);
            }
        } while (!ok);

        return duration;
    }

    public static Duration readDuration(String from, String to, String mensaje) {
        Duration fromDuration = toDuration(from);
        Duration toDuration = toDuration(to);

        Duration value;

        if (fromDuration.compareTo(toDuration) > 0) {
            Duration tmp = fromDuration;
            fromDuration = toDuration;
            toDuration = tmp;
        }

        do {
            value = readDuration(mensaje);
            if (value.compareTo(fromDuration) < 0 || value.compareTo(toDuration) > 0) {
                System.out.printf("%sRango inválido. %s", Color.RED, Color.RESET);
            }
        } while (value.compareTo(fromDuration) < 0 || value.compareTo(toDuration) > 0);

        return value;
    }

    /**
     * Un poco de genéricos y Reflection para permitir elegir constantes enumeradas
     * 
     * @param <T> El tipo de enum que se utiliza
     * @param c   Una referencia a la enumeración
     * @return Uno de los elementos del enum
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T readEnum(Class<T> c, String message) {
        message = String.format("%s%s%s", Color.BLUE, message, Color.RESET);
        Object[] allItems = (EnumSet.allOf(c)).toArray();

        int i;
        for (i = 0; i < allItems.length; i++) {
            message += String.format("%n%3d - %s", i + 1, allItems[i]);
        }
        message = String.format("%s%nElija una opción entre 1 y %d: ", message, allItems.length);

        do {
            i = readInt(message);
            System.out.println();
        } while (i < 1 || i > allItems.length);

        return (T) allItems[i - 1];
    }

    public static boolean OverlapSchedules(
            LocalDateTime fechaHora1Inicio, LocalDateTime fechaHora1Fin,
            LocalDateTime fechaHora2Inicio, LocalDateTime fechaHora2Fin) {
        return fechaHora1Inicio.isBefore(fechaHora2Fin) && fechaHora2Inicio.isBefore(fechaHora1Fin);
    }

}