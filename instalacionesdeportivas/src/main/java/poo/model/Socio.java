package poo.model;

import org.json.JSONObject;

public class Socio {

    public String id;
    public String nombre;
    public String direccion;
    public String telefono;
    

    // constructor parametrizado
    public Socio(String id, String nombre, String direccion, String telefono) {
        setId(id);
        setNombre(nombre);
        setDireccion(direccion);
        setTelefono(telefono);
    }

    // constructor por defecto
    public Socio() {
        this("00000", "nn", "No registrada", "0000000000");
    }

    // constructor copia
    public Socio(Socio s) {
        this(s.getId(), s.getNombre(), s.getDireccion(), s.getTelefono());
    }

    //constructor id para la comparacion de contains
    public Socio(String id) {
        this.id = id;
    }

    // constructor JSONObject

    public Socio(JSONObject json) {
        this(json.getString("id"),
                json.getString("nombre"),
                json.getString("direccion"),
                json.getString("telefono"));
    }

    // getters y setters
    // id
    public String getId() {
        return id;
    }

    public void setId(String id) { // no puede ser menor a 8 dígitos ni mayor a 10 dígitos, no vacío, ni letras
        if (id == null || id.isBlank() || id.length() < 8 || id.length() > 10) {
            throw new IllegalArgumentException(
                    "La identificación debe tener entre 8 y 10 dígitos, contener solo números y no puede estar vacía.");
        }

        // Verificar si todos los caracteres son dígitos
        for (char c : id.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException("La identificación solo puede contener números.");
            }
        }

        this.id = id;
    }

    // nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) { // el nombre no puede estar vacio ni ser nulo
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    // direccion
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty() || direccion.length() > 200) {
            throw new IllegalArgumentException("La dirección no puede estar vacía ni superar los 200 caracteres.");
        }
        this.direccion = direccion.trim();
    }
    

    // telefono
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || telefono.length() != 10) { // no puede ser vacía, solo diez dígitos, los caracteres no pueden ser algo diferente
                                                    
            throw new IllegalArgumentException("El teléfono debe tener exactamente 10 dígitos numéricos.");
        }
        for (char c : telefono.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException("El teléfono solo puede contener números.");
            }
        }
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        String data;
        data = String.format(
                "%nid: %s%nNombre: %s%nDirección: %s%nTeléfono: %s%n",
                id, nombre, direccion, telefono);
        return data;
    }

    // equals
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

        Socio s = (Socio) obj;
        return this.id.equals(s.id);
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

}
