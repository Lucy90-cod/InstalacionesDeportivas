package poo.model;

import org.json.JSONObject;

public class Piscina extends InstalacionDeportiva {

    private boolean olimpica;

    // Constructor parametrizado
    public Piscina(String descripcion, double ancho, double largo, boolean olimpica) {
        super( descripcion, ancho, largo, 15000.0); // Establece el valor por hora
        this.olimpica = olimpica;
    }

    // Constructor por defecto
    public Piscina() {
        this( "Sin descripción", 10.0, 10.0, false);
    }

    // Constructor copia
    public Piscina(Piscina p) {
        super( p.getDescripcion(), p.getAncho(), p.getLargo(), p.getValorHora());
        this.olimpica = p.olimpica;
    }

    // Constructor a partir de un JSONObject
    public Piscina(JSONObject json) {
        super(json);
        this.olimpica = json.getBoolean("olimpica");
    }

    public boolean getOlimpica() {
        return olimpica;
    }

    public void setOlimpica(boolean olimpica) {
        this.olimpica = olimpica;
    }

    @Override
    public String toString() {
        return String.format("%s, Olímpica: %s", super.toString(), getOlimpica() ? "Sí" : "No");
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

    @Override
    public String getInstalacion() {
        return olimpica ? "Piscina Olímpica" : "Piscina Normal";
    }

    @Override
    public double getCostoFuncionamiento(JSONObject tarifasFuncionamiento) {
        //JSONObject funcionamiento = tarifasFuncionamiento.getJSONObject("funcionamiento");
        JSONObject costosPiscina = tarifasFuncionamiento.getJSONObject("piscina");
        
        double costoQuimicos = costosPiscina.getDouble("quimicos");
        double costoElectricidad = costosPiscina.getDouble("electricidad");
        double costoAgua = costosPiscina.getDouble("agua");
        double costoLimpieza = costosPiscina.getDouble("limpiezaMantenimiento");
        double incrementoOlimpica = costosPiscina.getDouble("incremento");
        
        double costoTotal = costoQuimicos + costoElectricidad + costoAgua + costoLimpieza;
        
        // Aplicar incremento si es olímpica
        if (this.getOlimpica()) {
            costoTotal = costoTotal * incrementoOlimpica; //  incremento es 1.05, multiplicamos directamente
        }
        
        return costoTotal;
    }
    
}



