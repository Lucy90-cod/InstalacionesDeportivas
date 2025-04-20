package poo.model;

import org.json.JSONObject;

public class CanchaMultiproposito extends InstalacionDeportiva {
    private boolean graderia;

    // constructor parametrizado
    public CanchaMultiproposito(String descripcion, double ancho, double largo, boolean graderia) {
        super(descripcion, ancho, largo, 5000.0); // Establece el valor por hora
        this.graderia = graderia;
    }

    // Constructor por defecto
    public CanchaMultiproposito() {
        this("Sin descripción", 10.0, 10.0, false);
    }

    // Constructor copia
    public CanchaMultiproposito(CanchaMultiproposito c) {
        super(c.getDescripcion(), c.getAncho(), c.getLargo(), c.getValorHora());
        this.graderia = c.getGraderia();
    }

    // constructor JSON object
    public CanchaMultiproposito(JSONObject json) {
        super(json);
        json.getBoolean("graderia");
    }

    public boolean getGraderia() {
        return graderia;
    }

    public void setGraderia(boolean graderia) {
        this.graderia = graderia;
    }

    public String getStrGraderia() {
        return graderia ? " con graderia" : " sin graderias";
    }

    @Override
    public String toString() {
        return String.format("%s%nGraderia: %s", super.toString(), getStrGraderia());
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

    @Override
    public String getInstalacion() {
        // devuelve CanchaMultiproposito" ? " con graderia : " sin graderias"
        return "Cancha Multiproposito" + getStrGraderia();
    }// los metodos isGraderia o is... no sirven para nada

    @Override
    public double getCostoFuncionamiento(JSONObject tarifasFuncionamiento) {
        //JSONObject funcionamiento = tarifasFuncionamiento.getJSONObject("funcionamiento");
        JSONObject costosCanchaMulti = tarifasFuncionamiento.getJSONObject("canchaMultiproposito");

        double porcentaje;
        if (this.getGraderia()) {
            porcentaje = costosCanchaMulti.getDouble("conGraderias");
        } else {
            porcentaje = costosCanchaMulti.getDouble("sinGraderias");
        }

        return this.getValorHora() * porcentaje;
    }
}
