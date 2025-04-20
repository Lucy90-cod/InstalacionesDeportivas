package poo.services;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import poo.helpers.Utils;
import poo.model.CanchaMultiproposito;
import poo.model.CanchaTenis;
import poo.model.InstalacionDeportiva;
import poo.model.Piscina;
import poo.model.TipoCancha;

public class InstalacionDeportivaService implements Service<InstalacionDeportiva> {

    private List<InstalacionDeportiva> list;
    private final String fileName;

    public InstalacionDeportivaService() throws Exception {
        fileName = Utils.getConfig("archivos").getString("InstalacionesDeportivas");

        if (Utils.fileExists(fileName)) {
            load(); // Inicializar - lo leo o lo traigo a mi lista
        } else {
            list = new ArrayList<>();
        }

    }

    @Override
    public JSONObject add(String strJson) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public JSONObject get(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public JSONObject get(String id) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public InstalacionDeportiva getItem(String id) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getItem'");
    }

    @Override
    public JSONObject getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public List<InstalacionDeportiva> load() throws Exception {
        list = new ArrayList<>(); // inicializar lista

        // Obtener configuración de archivos
        JSONObject archivosConfig = Utils.getConfig("archivos");

        // Cargar cada tipo de instalación
        cargarInstalacionesPorTipo(Piscina.class, archivosConfig.getString("Piscinas"));
        cargarInstalacionesPorTipo(CanchaTenis.class, archivosConfig.getString("CanchasTenis"));
        cargarInstalacionesPorTipo(CanchaMultiproposito.class, archivosConfig.getString("CanchasMultiproposito"));

        return list;
    }

    private void cargarInstalacionesPorTipo(Class<? extends InstalacionDeportiva> tipo, String nombreArchivo)
            throws Exception {
        if (!Utils.fileExists(nombreArchivo)) {
            return; // Si el archivo no existe, salimos sin hacer nada
        }

        JSONArray jsonArr = new JSONArray(Utils.readText(nombreArchivo));
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject json = jsonArr.getJSONObject(i);
            InstalacionDeportiva instalacion = null;

            if (Piscina.class.isAssignableFrom(tipo)) {
                instalacion = new Piscina(json);
            } else if (CanchaTenis.class.isAssignableFrom(tipo)) {
                instalacion = new CanchaTenis(json);
            } else if (CanchaMultiproposito.class.isAssignableFrom(tipo)) {
                instalacion = new CanchaMultiproposito(json);
            }

            if (instalacion != null) {
                list.add(instalacion);
            }
        }
    }

    @Override
    public JSONObject update(String id, String strJson) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public JSONObject remove(String id) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

    @Override
    public InstalacionDeportiva dataToAddOk(String strJson) throws Exception {
        JSONObject json = new JSONObject(strJson);
        InstalacionDeportiva instalacion;

        // Determinar el tipo de instalación deportiva según los campos en el JSON
        if (json.has("olimpica")) {
            instalacion = new Piscina(
                    json.getString("descripcion"),
                    json.getDouble("ancho"),
                    json.getDouble("largo"),
                    json.getBoolean("olimpica"));
        } else if (json.has("tipoCancha")) {
            instalacion = new CanchaTenis(
                    json.getString("descripcion"),
                    json.getDouble("ancho"),
                    json.getDouble("largo"),
                    TipoCancha.valueOf(json.getString("tipoCancha")));
        } else if (json.has("graderia")) {
            instalacion = new CanchaMultiproposito(
                    json.getString("descripcion"),
                    json.getDouble("ancho"),
                    json.getDouble("largo"),
                    json.getBoolean("graderia"));
        } else {
            throw new IllegalArgumentException(
                    "El JSON no contiene los campos necesarios para crear una instalación deportiva");
        }

        if (list.contains(instalacion)) {
            throw new ArrayStoreException("Ya existe una instalación deportiva con los datos ingresados");
        }

        return instalacion;
    }

    @Override
    public InstalacionDeportiva getUpdated(JSONObject newData, InstalacionDeportiva current) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUpdated'");
    }

    @Override
    public JSONObject size() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'size'");
    }

    @Override
    public Class<InstalacionDeportiva> getDataType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDataType'");
    }

}
