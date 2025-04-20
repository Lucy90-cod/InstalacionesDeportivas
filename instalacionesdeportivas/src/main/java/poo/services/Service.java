package poo.services;

import java.util.List;

import org.json.JSONObject;

public interface Service<T> {

  /**
   * Crea una instancia JSONObject a partir de strJson y con ella una instancia java.
   * Si la instancia ya existe en la lista, se genera un error, si no, se agrega a ésta
   * y se intenta guardar los cambios en un archivo JSON.
   * @param strJson Un String representando una instancia
   * @return Un JSONObject con la instancia creada y agregada al archivo.
   * @throws Exception
   */
  public JSONObject add(String strJson) throws Exception;

  /**
   * Busca un elemento dada su posición en la lista.
   * @param index El índice o posición en la que se busca el elemento.
   * @return Un JSONObject que corresponde al elemento encontrado en la posición dada.
   */
  public JSONObject get(int index);

  /**
   * Busca un elemento en la lista, dado su ID único.
   * @param id El identificador único del elemento a buscar.
   * @return Un JSONObject que corresponde al elemento según el ID dado.
   * @throws Exception
   */
  public JSONObject get(String id) throws Exception;

  /**
   * Busca un elemento en la lista, dado su ID único.
   * @param id El identificador único del elemento a buscar.
   * @return Una instancia Java que corresponde al elemento según el ID dado.
   * @throws Exception
   */
  public T getItem(String id) throws Exception;

  /**
   * Si hay acceso al archivo devuelve un JSONObject con la estructura { "message": "ok", "data": [...]},
   * siendo "data" el array de objetos JSON recuperados del archivo. Si no hay acceso se devuleve un 
   * JSONObject con la estructura { "message": "Sin acceso...", "error": "..."}
   * @return Los datos del archivo JSON o un mensaje de error si no hay acceso
   */
  public JSONObject getAll();

  /**
   * Restaura la lista de instancias Java a partir del archivo JSON correspondiente
   * @return Una lista creada a partir de los elementos recuperados del archivo JSON.
   * @throws Exception
   */
  public List<T> load() throws Exception;

  /**
   * Busca una instancia en la lista, según el ID único dado como primer argumento y le modifica
   * los datos según los atributos dados en el segundo argumento.
   * @param id El ID de la instancia a buscar.
   * @param strJson Un JSON con los datos que hay que modificar en la instancia.
   * @return Un JSONObject con los datos actualizados.
   * @throws Exception
   */
  public JSONObject update(String id, String strJson) throws Exception;

  /**
   * Dado un ID, busca una instancia en la lista y la elimina sólo si no está asociada
   * con otra instancia, según las asociaciones del modelo de clases.
   * Esta operación es muy peligrosa y no suele darse en modelos de la vida real. 
   * @param id El identificador único de la instancia a buscar.
   * @return U JSONObject que se corresponde con la instancia eliminada.
   * @throws Exception
   */
  public JSONObject remove(String id) throws Exception;

  /**
   * Devuelve un objeto de tipo T, luego de verificar que los datos dados en strJson sean válidos para crear un objeto de tipo T
   * @param strJson Un JSON con los datos necesarios y válidos para crear una instancia de T
   * @return Una instancia creada a partir de los datos dados como argumento
   */
  public T dataToAddOk(String strJson) throws Exception;

  /**
   * Devuelve el objeto current actualizado con los datos especificados en newData
   * @param newData Los datos que se utilizan para actualizar a current
   * @param current La instancia con los datos actuales
   * @return Una nueva instancia creada a partir de current pero con los datos actualizados según newData
   */
  public T getUpdated(JSONObject newData, T current) throws Exception;

  /**
   * Devuelve un objeto json con la estructura { "size" : totalRegistros, "message" : "ok" } 
   * siendo totalRegistros un valor entero que indica el número de registros de la lista
   * @return
   */
  public JSONObject size();

  /**
   * Permite conocer la clase del parámetro de tipo genérico T. Este método es importante
   * sobre cuando se referencian subclases mediante superclaes y se desea conocer de
   * forma fácil, el tipo con el que se está trabajando.
   * @return Una instancia de tipo Class que representa la clase del parámetro T
   */
  public Class<T> getDataType();
}


  