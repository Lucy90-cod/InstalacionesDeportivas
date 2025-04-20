package poo.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import poo.helpers.Utils;
import poo.model.Socio;

public class SocioService implements Service<Socio> {

    private List<Socio> list; // El servicio es el encargado de manipular a todos los socios por eso tiene la
                              // lista
    private final String fileName;

    // Constructor por defecto
    public SocioService() throws Exception {
        fileName = Utils.getConfig("archivos").getString("socios");

        if (Utils.fileExists(fileName)) {
            load(); // Inicializar - lo leo o lo traigo a mi lista
        } else {
            list = new ArrayList<>();
        }
    }

    @Override
    public JSONObject add(String strJson) throws Exception {
        Socio c = dataToAddOk(strJson);

        if (list.add(c)) {
            Utils.writeJSON(list, fileName);
        }
        return new JSONObject().put("message", "ok").put("data", c.toJSONObject());
    }

    @Override
    public JSONObject get(int index) {
        return list.get(index).toJSONObject();
    }

    @Override
    public JSONObject get(String id) throws Exception {
        Socio c = getItem(id);
        if (c == null) {
            throw new NoSuchElementException(String.format("No se encontró un socio con ID %s", id));
        }
        return c.toJSONObject();
    }

    @Override
    public Socio getItem(String id) throws Exception {
        int i = list.indexOf(new Socio(id));
        return i > -1 ? list.get(i) : null;
    }

    @Override
    public JSONObject getAll() {
        try {
            JSONArray data = new JSONArray();
            if (Utils.fileExists(fileName)) {
                data = new JSONArray(Utils.readText(fileName));
            }
            return new JSONObject().put("message", "ok").put("data", data).put("size", data.length());
        } catch (IOException | JSONException e) {
            Utils.printStackTrace(e);
            return Utils.keyValueToJson("message", "Sin acceso a datos de socios", "error", e.getMessage());
        }
    }

    @Override
    public final List<Socio> load() throws Exception {
        list = new ArrayList<>();

        JSONArray jsonArr = new JSONArray(Utils.readText(fileName));
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            list.add(new Socio(jsonObj));
        }

        return list;
    }

    @Override
    public JSONObject update(String id, String strJson) throws Exception {
        // crear un JSONObject con las claves y valores que hay que actualizar
        JSONObject newData = new JSONObject(strJson);

        // buscar el socio que se debe actualizar y recordar la posición
        Socio socio = getItem(id);

        if (socio == null) {
            throw new NullPointerException("No se encontró el socio " + id);
        }
        int i = list.indexOf(socio);

        socio = getUpdated(newData, socio);

        // buscar la posición del socio en la lista y actualizarlo
        list.set(i, socio);

        // actualizar el archivo de socios
        Utils.writeJSON(list, fileName);

        // devolver el socio con los cambios realizados
        return new JSONObject().put("message", "ok").put("data", socio.toJSONObject());
    }

    @Override
    public JSONObject remove(String id) throws Exception {
        JSONObject socio = get(id);
        if (socio == null) {
            throw new NoSuchElementException("No existe un socio con la identificación " + id);
        }

        Socio c = new Socio(socio);
        String alquileres = Utils.getConfig("archivos").getString("alquileres");
        // buscar el socio en alquileres y si existe no permitir eliminarlo
        if (Utils.exists(alquileres, "socio", socio)) {
            throw new Exception(String.format("No eliminado. El socio %s tiene alquileres registrados", id));
        }

        if (!list.remove(c)) {
            throw new Exception(String.format("No se pudo eliminar el socio con identificación %s", id));
        }

        Utils.writeJSON(list, fileName);
        return new JSONObject().put("message", "ok").put("data", socio);
    }

    @Override
    public Socio dataToAddOk(String strJson) {
        JSONObject json = new JSONObject(strJson);

        if (!json.has("id") || json.getString("id").isBlank()) {
            json.put("id", Utils.getRandomKey(12));
        }

        Socio c = new Socio(
                json.getString("id"),
                json.getString("nombre"),
                json.getString("direccion"),
                json.getString("telefono"));

        if (list.contains(c)) {
            throw new ArrayStoreException("Ya existe un socio con los datos ingresados");
        }

        return c;
    }

    @Override
    public Socio getUpdated(JSONObject newData, Socio current) {
        // crear un socio con las propiedades del objeto a actualizar
        Socio socio = new Socio(current);

        // actualizar con los valores del socio con los valores recibido como primer
        // argumento
        if (newData.has("nombre")) {
            socio.setNombre(newData.getString("nombre"));
        }

        if (newData.has("direccion")) {
            socio.setDireccion(newData.getString("direccion"));
        }

        if (newData.has("telefono")) {
            socio.setTelefono(newData.getString("telefono"));
        }

        return socio;
    }

    @Override
    public Class<Socio> getDataType() {
        return Socio.class;
    }

    @Override
    public JSONObject size() {
        return new JSONObject().put("size", list.size()).put("message", "ok");
    }

}
