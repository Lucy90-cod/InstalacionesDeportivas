package poo.model;

import org.json.JSONObject;

public class CanchaTenis extends InstalacionDeportiva {
    private TipoCancha tipoCancha;

    // constructor parametrizado
    public CanchaTenis(String descripcion, double ancho, double largo, TipoCancha tipoCancha) {
        super(descripcion, ancho, largo, 7000.0); // Establece el valor por hora
        setTipoCancha(tipoCancha);
    }

    // Constructor por defecto
    public CanchaTenis() {
        this("Sin descripción", 10.0, 10.0, TipoCancha.LADRILLO);
    }

    // constructor copia
    public CanchaTenis(CanchaTenis ct) {
        super(ct);
        this.tipoCancha = ct.getTipoCancha();
    }

    // constructor JSON Object
    public CanchaTenis(JSONObject json) {
        super(json);
        setTipoCancha(TipoCancha.valueOf(json.getString("tipoCancha")));
    }

    public TipoCancha getTipoCancha() {
        return tipoCancha;
    }

    public void setTipoCancha(TipoCancha tipoCancha) {
        if (tipoCancha == null) {
            throw new IllegalArgumentException("El tipo de cancha no puede ser nulo.");
        }
        this.tipoCancha = tipoCancha;
    }

    public String getStrTipoCancha() {
        return tipoCancha == TipoCancha.LADRILLO ? "ladrillo" : "césped";
    }

    @Override
    public String getInstalacion() {
        return "Cancha de tenis en " + getStrTipoCancha();
    }

    @Override
    public String toString() {
        String data;

        data = String.format("%s%nTipo de Cancha: %s", super.toString(), tipoCancha);
        return data;
    }

    public JSONObject toJSONObject() {
        return new JSONObject(this);
    }

    @Override
    public double getCostoFuncionamiento(JSONObject tarifasFuncionamiento) {
        //JSONObject funcionamiento = tarifasFuncionamiento.getJSONObject("funcionamiento");
        JSONObject costosCanchaTenis = tarifasFuncionamiento.getJSONObject("canchaDeTenis");

        double costoMantenimiento;
        if (this.tipoCancha == TipoCancha.LADRILLO) {
            costoMantenimiento = costosCanchaTenis.getDouble("ladrillo");
        } else { // CESPED
            costoMantenimiento = costosCanchaTenis.getDouble("cesped");
        }

        double costoElectricidad = costosCanchaTenis.getDouble("electricidad");

        return costoMantenimiento + costoElectricidad;
    }

}
