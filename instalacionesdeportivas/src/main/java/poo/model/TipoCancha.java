package poo.model;

public enum TipoCancha {
    LADRILLO("Ladrillo"),
    CESPED("Césped");

    private String value;

    private TipoCancha(String value) {
        this.value = value;
    }

    public static TipoCancha getEnum(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        for (TipoCancha v : values()) {
            if (value.equalsIgnoreCase(v.value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("No se encontró una constante enumerada: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}