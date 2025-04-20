package poo.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

public class Alquiler {

    private static Set<String> idsGenerados = new HashSet<>();
    private static Random random = new Random();

    private String id;
    private Socio socio;
    private InstalacionDeportiva instalacionDeportiva;
    private LocalDateTime fechaInicioHora;
    private LocalDateTime fechaFinHora;
    private int horas;

    // Constructor parametrizado
    public Alquiler(Socio socio, InstalacionDeportiva instalacionDeportiva, LocalDateTime fechaInicioHora,
            LocalDateTime fechaFinHora) {
        this.id = generarIdUnico();
        setSocio(socio);
        setInstalacionDeportiva(instalacionDeportiva);
        setFechaInicioHora(fechaInicioHora);
        setFechaFinHora(fechaFinHora);
        validateReservaTime();
        calcularHoras(); // Calcular horas al crear el objeto
    }

    //

    // Constructor por defecto
    public Alquiler() {
        this(new Socio(),
                new CanchaMultiproposito("Cancha multiple", 10, 20, false),
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES));
    }

    // Constructor copia
    public Alquiler(Alquiler a) {
        this(a.getSocio(), a.getInstalacionDeportiva(), a.getFechaInicioHora(), a.getFechaFinHora());
    }

    // Constructor JSONObject
    public Alquiler(JSONObject json) {
        // Establecer el ID
        this.id = json.getString("id");
        if (!id.matches("\\d{1,5}")) {
            throw new IllegalArgumentException("El ID debe ser un número de hasta 5 dígitos.");
        }

        this.socio = new Socio(json.getJSONObject("socio"));

        this.fechaInicioHora = LocalDateTime.parse(json.getString("fechaInicioHora")).truncatedTo(ChronoUnit.MINUTES);
        this.fechaFinHora = LocalDateTime.parse(json.getString("fechaFinHora")).truncatedTo(ChronoUnit.MINUTES);

        // tener en cuenta InstalacionDeportiva.getInstance()
        this.instalacionDeportiva = InstalacionDeportiva.getInstance(json.getJSONObject("instalacionDeportiva"));

        // Determinamos qué tipo de instalación crear....estudiar esta parte, no va en
        // el codigo porque se reemplaza por la linea 67
        /*
         * JSONObject jsonInstalacionDeportiva =
         * json.getJSONObject("instalacionDeportiva");
         * String tipoInstalacion = jsonInstalacionDeportiva.getString("tipo");
         * 
         * if (tipoInstalacion.equals("Piscina")) {
         * this.instalacionDeportiva = new Piscina(jsonInstalacionDeportiva);
         * } else if (tipoInstalacion.equals("CanchaTenis")) {
         * this.instalacionDeportiva = new CanchaTenis(jsonInstalacionDeportiva);
         * } else {
         * this.instalacionDeportiva = new
         * CanchaMultiproposito(jsonInstalacionDeportiva);
         * }
         */

        // Horas del JSON o las calculamos si no están presentes
        if (json.has("horas")) {
            this.horas = json.getInt("horas");
        } else {
            calcularHoras();
        }

        // Validamos la reserva
        validateReservaTime();
    }

    // Getters y Setters

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        if (socio == null) {
            throw new IllegalArgumentException("El socio no puede ser nulo.");
        }
        this.socio = socio;
    }

    public InstalacionDeportiva getInstalacionDeportiva() {
        return instalacionDeportiva;
    }

    public void setInstalacionDeportiva(InstalacionDeportiva instalacionDeportiva) {
        if (instalacionDeportiva == null) {
            throw new IllegalArgumentException("La instalación deportiva no puede ser nula.");
        }
        this.instalacionDeportiva = instalacionDeportiva;
    }

    public LocalDateTime getFechaInicioHora() {
        return fechaInicioHora;
    }

    public void setFechaInicioHora(LocalDateTime fechaInicioHora) {
        // Validaciones para la fecha de inicio
        if (fechaInicioHora == null) {
            throw new NullPointerException("La fecha y hora de inicio  no puede ser nula.");

        }

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        // No puede ser anterior a la fecha actual
        if (fechaInicioHora.isBefore(now)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a la fecha actual.");
        }

        // No puede ser mayor a un mes a partir de la fecha actual
        LocalDateTime oneMonthLater = now.plusMonths(1);
        if (fechaInicioHora.isAfter(oneMonthLater)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser mayor a un mes a partir de la fecha actual.");
        }

        LocalTime horaMinima = LocalTime.of(6, 0); // 06:00 AM
        if (fechaInicioHora.toLocalTime().isBefore(horaMinima)) {
            throw new IllegalArgumentException("La hora de inicio no puede ser antes de las 6:00 AM.");
        }

        this.fechaInicioHora = fechaInicioHora.truncatedTo(ChronoUnit.MINUTES);

        // Si ya tenemos fechaFinHora, recalculamos las horas
        if (this.fechaFinHora != null) {
            calcularHoras();
        }
    }

    public LocalDateTime getFechaFinHora() {
        return fechaFinHora;
    }

    public void setFechaFinHora(LocalDateTime fechaFinHora) {
        // Validaciones para la hora de fin
        if (fechaFinHora == null) {
            throw new NullPointerException("La fecha y hora de fin no puede ser nula.");

        }

        // Franja horaria entre 6 y 22 horas (reserva máxima hasta las 21h)
        LocalTime horaFin = fechaFinHora.toLocalTime();
        if (horaFin.isBefore(LocalTime.of(6, 0)) || horaFin.isAfter(LocalTime.of(21, 0))) {
            throw new IllegalArgumentException("La hora de fin debe estar entre las 6:00 y las 21:00.");
        }

        this.fechaFinHora = fechaFinHora.truncatedTo(ChronoUnit.MINUTES);

        // recalcular las horas
        if (this.fechaInicioHora != null) {
            calcularHoras();
        }
    }

    public String getId() {
        return id;
    }

    public int getHoras() {
        return horas;
    }

    // Método para calcular el número de horas
    private void calcularHoras() {
        if (fechaInicioHora != null && fechaFinHora != null) {
            long duracionMinutos = Duration.between(fechaInicioHora, fechaFinHora).toMinutes();
            this.horas = (int) Math.ceil(duracionMinutos / 60.0);
        }
    }

    // Método para validar la franja horaria y tiempo mínimo de reserva
    private void validateReservaTime() {
        // Tiempo mínimo de reserva: 1 hora
        if (!fechaFinHora.isAfter(fechaInicioHora)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }

        long duracionMinutos = Duration.between(fechaInicioHora, fechaFinHora).toMinutes();
        if (duracionMinutos < 60) {
            throw new IllegalArgumentException("La reserva debe tener al menos 1 hora de duración.");
        }
    }

    public double getCosto() {
        return instalacionDeportiva.getValorHora() * horas;
    }

    @Override
    public String toString() {
        return String.format(
                "%nId: %s%nSocio: %s%nInstalación: %s%nFecha Inicio: %s%nHora Fin: %s%nHoras: %d%nCosto: %.2f%n",
                id, socio, instalacionDeportiva, fechaInicioHora, fechaFinHora, horas, getCosto());
    }

    // Método para generar un ID único de 5 dígitos
    private static String generarIdUnico() {
        String nuevoId;
        do {
            nuevoId = String.format("%05d", random.nextInt(100000));
        } while (idsGenerados.contains(nuevoId));
        idsGenerados.add(nuevoId);
        return nuevoId;
    }

    @Override
    public boolean equals(Object obj) {
        // dos alquileres son iguales si tienen el mismo ID o también
        // si tienen la misma fechaHoraInicio, fechaHoraFin, socio e
        // instalacionDeportiva

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Alquiler a = (Alquiler) obj;

        // Si tienen el mismo ID son iguales
        if (id.equals(a.id)) {
            return true;
        }

        // Si tienen los mismos valores para los campos especificados también son
        // iguales
        return fechaInicioHora.equals(a.fechaInicioHora) &&
                fechaFinHora.equals(a.fechaFinHora) &&
                socio.equals(a.socio) &&
                instalacionDeportiva.equals(a.instalacionDeportiva);
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }
}