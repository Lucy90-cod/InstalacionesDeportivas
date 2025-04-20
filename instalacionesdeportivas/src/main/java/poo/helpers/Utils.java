
package poo.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // <-------- OJO
import java.util.List;
import java.util.Properties;
import java.util.Random; // <-------- OJO

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.Property;

public class Utils {

  public static final String PATH = "./data/";
  public static boolean trace = false;

  private Utils() {} // lo mismo en Keyboard

  public static void printStackTrace(Exception e) {
    if (Utils.trace) {
      System.out.printf("%s%s%s%s%s%n", Color.RED, "-".repeat(30), " Reporte de excepciones ", "-".repeat(30), Color.RESET);
      e.printStackTrace(System.out);
      System.out.printf("%s%s%s%s%s%n", Color.RED, "-".repeat(30), " Fin del reporte de excepciones ", "-".repeat(30), Color.RESET);
    }
  }

  /**
   * Genera un string de caracteres alfanuméricos aleatorios de una longitud dada
   * Ver: https://www.baeldung.com/java-random-string
   * @param stringLength La longitud que se requiere para el string
   * @return Un string de caracteres alfanuméricos aleatorios de una longitud
   */
  public static String getRandomKey(int stringLength) {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 90; // letter 'Z'
    Random random = new Random();

    String generatedString = random
        .ints(leftLimit, rightLimit + 1) // definir el rango de caracteres ['0'..'Z']
        .filter(i -> (i <= 57 || i >= 65)) // filtrar entre ['0'..'9'] o entre ['A'..'Z']
        .limit(stringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();

    return generatedString;
  }

  public static String strDateTime(LocalDateTime dateTime) {
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    return dateTime.format(format);
  }

  public static boolean fileExists(String fileName) {
    Path dirPath = Paths.get(fileName);
    return Files.exists(dirPath) && !Files.isDirectory(dirPath);
  }

  public static boolean pathExists(String path) {
    Path folder = Paths.get(path);
    return Files.exists(folder) && Files.isDirectory(folder);
  }

  public static void createFolderIfNotExist(String folder) throws IOException {
    // si no existe o si existe y no es una carpeta, crear la carpeta
    if (!pathExists(folder)) {
      Path dirPath = Paths.get(folder);
      Files.createDirectories(dirPath);
    }
  }

  public static String getPath(String path) {
    Path parentPath = Paths.get(path).getParent();
    return parentPath == null ? null : parentPath.toString();
  }

  /**
   * Crea la ruta padre indicada en el argumento recibido si no existe
   * @param filePath Un String que representa una ruta válida
   * @return Una instancia de Path con la ruta original
   * @throws IOException
   */
  public static Path initPath(String filePath) throws IOException {
    String path = getPath(filePath);
    createFolderIfNotExist(path);
    return Paths.get(filePath);
  }

  public static String readText(String fileName) throws IOException {
    Path path = Paths.get(fileName);
    return Files.readString(path, StandardCharsets.UTF_8);
  }

  public static void _writeText(List<?> list, String fileName) throws Exception {
    initPath(fileName);
    try (FileWriter fw = new FileWriter(new File(fileName), StandardCharsets.UTF_8); BufferedWriter writer = new BufferedWriter(fw)) {
      for (int i = 0; i < list.size(); i++) {
        writer.append(list.get(i).toString());
        writer.newLine();
      }
    }
  }

  public static void writeText(List<?> list, String fileName) throws Exception {
    // https://www.tabnine.com/code/java/methods/java.nio.file.Files/newBufferedWriter
    Path path = initPath(fileName);
    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      for (Object o : list) {
        writer.append(o.toString());
        writer.newLine();
      }
    }
  }

  public static void writeText(String content, String fileName) throws IOException {
    Path path = initPath(fileName);
    Files.write(path, content.getBytes(StandardCharsets.UTF_8));
  }

  public static void writeJSON(List<?> list, String fileName) throws IOException {
    JSONArray jsonArray = new JSONArray(list);
    writeText(jsonArray.toString(2), fileName);
  }

  /**
   * Convierte parámetros de una URL en una representación JSON
   * @param s Algo así como param1=value1&param2=value2...
   * @return Un String JSON con los pares paramX=valueX de s
   * @throws IOException
   */
  public static String paramsToJson(String s) throws IOException {
    s = s.replace("&", "\n");
    StringReader reader = new StringReader(s);
    Properties properties = new Properties();
    properties.load(reader);
    return Property.toJSONObject(properties).toString(2);
  }

  /**
   * Convierte un número par de strings en una representación json {key:value, ...}
   * @param strings los strings (en número par) que se convierten a json
   * @return Un String JSON con los pares key=value de strings
   */
  public static JSONObject keyValueToJson(String... strings) {
    JSONObject json = new JSONObject();
    for (int i = 0; i < strings.length; i += 2) {
      json.put(strings[i], strings[i + 1]);
    }
    return json;
  }

  public static String keyValueToStrJson(String... strings) {
    return keyValueToJson(strings).toString();
  }

  /**
   * Verifica en cualquier archivo de tipo JSON si un objeto está contenido en uno de los objetos
   * JSON que conforman el array de objetos JSON contenido en el archivo.
   * @param fileName El nombre del archivo sin extensión, que contiene el array de objetos JSON
   * @param key La clave o atributo que identifica el objeto JSON a buscar dentro de cada objeto
   * @param search El objeto JSON a buscar
   * @return True si se encuentra que search alguno de los objetos del array
   * @throws Exception
   */
  public static boolean exists(String fileName, String key, JSONObject search) throws Exception {
    if (!Utils.fileExists(fileName + ".json")) {
      return false;
    }

    String data = readText(fileName + ".json");
    JSONArray jsonArrayData = new JSONArray(data);

    for (int i = 0; i < jsonArrayData.length(); i++) {
      // obtener el JSONObject del array en la iteración actual
      JSONObject jsonObj = jsonArrayData.getJSONObject(i);

      if (jsonObj.has(key)) {
        // De la instancia actual obtener el objeto JSON que se requiere verificar
        jsonObj = jsonObj.getJSONObject(key);
        // OJO >>> comparar los strings de ambos JSONObject
        if (jsonObj.toString().equals(search.toString())) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Verifica en cualquier archivo de tipo JSON si un objeto con una propiedad determinada, está
   *  contenido en uno de los objetos JSON que conforman el array de objetos JSON contenido en el archivo.
   * @param fileName El nombre del archivo sin extensión, que contiene el array de objetos JSON
   * @param key La clave o atributo que identifica el objeto JSON a buscar dentro de cada objeto
   * @param search El objeto JSON a buscar
   * @param property La clave del objeto que se usa para hacer la comparación. Ej.: "id"
   * @return True si se encuentra que search alguno de los objetos del array
   * @throws Exception
   */
  public static boolean exists(String fileName, String key, JSONObject search, String property) throws Exception {
    if (!Utils.fileExists(fileName + ".json")) {
      return false;
    }
    String data = readText(fileName + ".json");
    JSONArray jsonArrayData = new JSONArray(data);

    for (int i = 0; i < jsonArrayData.length(); i++) {
      // obtener el JSONObject del array en la iteración actual
      JSONObject jsonObj = jsonArrayData.getJSONObject(i);

      if (jsonObj.has(key)) {
        // De la instancia actual obtener el objeto JSON que se requiere verificar
        jsonObj = jsonObj.getJSONObject(key);
        // OJO >>> utilizar una de las propiedades de los objetos para hacer la comparación
        if (jsonObj.optString(property).equals(search.optString(property))) {
          return true;
        }
      }
    }

    return false;
  }

  public static String MD5(String s) throws Exception {
    MessageDigest m = MessageDigest.getInstance("MD5");
    m.update(s.getBytes(), 0, s.length());
    return new BigInteger(1, m.digest()).toString(16);
  }

  public static String stringOk(String key, int length, JSONObject json) {
    String value = json.getString(key);
    if (value.length() < length) {
      throw new IllegalArgumentException(String.format("Se esperaban al menos %s caracteres: %s='%s'", length, key, value));
    }
    return value;
  }

  public static double doubleOk(String key, double min, double max, JSONObject json) {
    double value = json.getDouble(key);
    if (value < min || value > max) {
      throw new IllegalArgumentException(String.format("Se esperaba un valor entre %.2f y %.2f: %s=%.2f", min, max, key, value));
    }
    return value;
  }


  public static void ifExistsUpdateFile(JSONObject search, String fileName) throws IOException {
    String data = readText(PATH + fileName + ".json");
    JSONArray jsonArray = new JSONArray(data);

    for (int i = 0; i < jsonArray.length(); i++) {
      // obtener el JSONObject del array en la iteración actual
      JSONObject jsonObj = jsonArray.getJSONObject(i);

      if (fileName.equals("Mercancia")) {
        if (clientFound(search, jsonObj, "cliente")) {
          jsonArray.getJSONObject(i).put("cliente", search);
        }
      } else {
        if (clientFound(search, jsonObj, "remitente")) {
          jsonArray.getJSONObject(i).put("remitente", search);
        }
        if (clientFound(search, jsonObj, "destinatario")) {
          jsonArray.getJSONObject(i).put("destinatario", search);
        }
      }
    }
    writeText(jsonArray.toString(2), PATH + fileName + ".json");
  }

  private static boolean clientFound(JSONObject search, JSONObject jsonObj, String key) {
    return (jsonObj.getJSONObject(key).getString("id").equals(search.getString("id")));
  }

  public static boolean OverlapSchedules(
      LocalDateTime fechaHora1Inicio, LocalDateTime fechaHora1Fin,
      LocalDateTime fechaHora2Inicio, LocalDateTime fechaHora2Fin) {
    return fechaHora1Inicio.isBefore(fechaHora2Fin) && fechaHora2Inicio.isBefore(fechaHora1Fin);
  }

  public static JSONObject getConfig (String key) throws Exception {
    JSONObject config = new JSONObject(readText(".data/config.json"));
    return config.getJSONObject(key);
  }

}