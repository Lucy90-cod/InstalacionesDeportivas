package poo.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

public abstract class InstalacionDeportiva {

    protected String id;
    protected String descripcion;
    protected double ancho;
    protected double largo;
    protected double valorHora;

    // Set estático para almacenar los IDs ya utilizados
    private static final Set<String> idsUtilizados = new HashSet<>();
    private static final Set<String> descripcionesUtilizadas = new HashSet<>();

    // Constructor parametrizado
    public InstalacionDeportiva(String descripcion, double ancho, double largo, double valorHora) {
        this.id = generadorId();
        setDescripcion(descripcion);
        setAncho(ancho);
        setLargo(largo);
        setValorHora(valorHora);
    }

    // Constructor por defecto
    public InstalacionDeportiva() {
        this("Sin descripción", 10.0, 10.0, 0.0);
    }

    // Constructor copia
    public InstalacionDeportiva(InstalacionDeportiva d) {
        this(d.getDescripcion(), d.getAncho(), d.getLargo(), d.getValorHora());

    }

    // constructor JSON Object
    public InstalacionDeportiva(JSONObject json) {
        this(json.getString("descripcion"),
                json.getDouble("ancho"),
                json.getDouble("largo"),
                json.getDouble("valorHora"));

        if (json.has("id")) {
            String idJson = json.getString("id");
            if (idsUtilizados.contains(idJson)) {
                this.id = generadorId();
            } else {
                setId(idJson);
                idsUtilizados.add(idJson);
            }
        }
    }

    // getArea
    // get valor hora para devolver el valor hora en las subclases
    // Getters y Setters
    public String getId() {
        return id;
    }

    private void setId(String id) {
        if (id == null || id.trim().length() != 5) {
            throw new IllegalArgumentException("El ID debe tener 5 dígitos y no puede estar vacío.");
        }
        // Aplicar trim al ID
        String trimmedId = id.trim();
        for (char c : trimmedId.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException("El ID solo puede contener números.");
            }
        }
        this.id = trimmedId;
    }

    private static String generadorId() {
        Random random = new Random();
        String nuevoId;
        do {
            nuevoId = String.format("%05d", random.nextInt(100000));
        } while (idsUtilizados.contains(nuevoId));

        idsUtilizados.add(nuevoId);
        return nuevoId;
    }

    public String getDescripcion() {
        return descripcion;
    }


    public void setDescripcion(String descripcion) {
        // Validar que la descripción no sea nula o vacía
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        
        // Ya no validamos descripciones duplicadas
        this.descripcion = descripcion;
    }

    public double getAncho() {
        return ancho;
    }

    public void setAncho(double ancho) {
        if (ancho < 1.2) {
            throw new IllegalArgumentException("El ancho no puede ser inferior a 1.2 metros.");
        }
        this.ancho = ancho;
    }

    public double getLargo() {
        return largo;
    }

    public void setLargo(double largo) {
        if (largo < 1.2) {
            throw new IllegalArgumentException("El largo no puede ser menor a 1.2 metros.");
        }
        this.largo = largo;
    }

    // Método de instancia para calcular el área específica de esta instalación
    public double getArea() {
        return ancho * largo;
    }

    public double getValorHora() {
        return valorHora;
    }

    public void setValorHora(double valorHora) {
        if (valorHora < 1000) {
            throw new IllegalArgumentException("El valor por hora no puede ser negativo.");
        }
        this.valorHora = valorHora;
    }

    //
    public String getTipo() {
        return getClass().getSimpleName(); //
    }

    // getInstance debe ser un método estático que devuelva una instalación
    // deportiva
    public static InstalacionDeportiva getInstance(JSONObject json) {
        if (json.has("olimpica")) {
            return new Piscina(json);
        } else if (json.has("tipoCancha")) {
            return new CanchaTenis(json);
        } else if (json.has("graderia")) {
            return new CanchaMultiproposito(json);
        } else {
            throw new IllegalArgumentException("el Json no corresponde a una subclase de instalación Deportiva");
        }
    }

    // devuelve una breve y completa descripcion del tipo de instancia, si un metodo
    // no tiene cuerpo se define como abstract,
    // si la clase tiene un metodo abstracto la clase se tiene que definir como
    // abstract
    abstract public String getInstalacion();

    //método abstracto que recibe las tarifas
    public abstract double getCostoFuncionamiento(JSONObject tarifasFuncionamiento);

    @Override
    public String toString() {
        String data;

        data = String.format(
                "%nID: %s%nDescripción: %s%nAncho: %.2f metros%nLargo: %.2f metros%nValor por Hora: $%.2f",
                id,
                descripcion,
                ancho,
                largo,
                valorHora);
        return data;

    }

    @Override
    public boolean equals(Object obj) {
        // las referencias this y obj apuntan a la misma instancia
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        InstalacionDeportiva other = (InstalacionDeportiva) obj;
        return this.id.equals(other.id) || this.descripcion.equals(other.descripcion);
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }
}
