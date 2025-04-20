package poo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import poo.helpers.Color;
import poo.helpers.Keyboard;
import poo.helpers.Utils;
import poo.model.Alquiler;
import poo.model.CanchaMultiproposito;
import poo.model.CanchaTenis;
import poo.model.InstalacionDeportiva;
import poo.model.Piscina;
import poo.model.Socio;
import poo.model.TipoCancha;

public class App {

    private static ArrayList<InstalacionDeportiva> instalaciones;
    private static ArrayList<Socio> socios;
    private static ArrayList<Alquiler> alquileres;
    private static List<InstalacionDeportiva> piscinas;
    private static List<InstalacionDeportiva> canchasTenis;
    private static List<InstalacionDeportiva> canchasMultiproposito;

    private static JSONObject archivos; // nombres archivos que se utilizan
    private static JSONObject tarifas; // tarifas de las instalaciones
    private static JSONObject config; // configuración de las tarifas y rutas de acceso
    private static JSONObject funcionamiento;

    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
        try {
            inicializar();
        } catch (Exception e) {
            e.printStackTrace();
        }

        do {
            try {
                int opcion = leerOpcion();
                switch (opcion) {
                    case 1:
                        ingresarSocios();
                        break;
                    case 2:
                        listarSocios();
                        break;
                    case 3:
                        crearInstalaciones();
                        break;
                    case 4:
                        listarInstalacionesPorTipo("Piscina");
                        break;
                    case 5:
                        listarInstalacionesPorTipo("CanchaTenis");
                        break;
                    case 6:
                        listarInstalacionesPorTipo("CanchaMultiproposito");
                        break;
                    case 7:
                        alquilarInstalacion();
                        break;
                    case 8: 
                        listarAlquileres();
                        break;
                    case 9: 
                        listarCostosInstalaciones();
                        break;
                    case 0:
                        salir();
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);
    }

    // inicializar socios
    public static void inicializarSocios() throws Exception {
        String nombreArchivo = archivos.getString("socios");
        socios = new ArrayList<>();

        if (Utils.fileExists(nombreArchivo)) {
            JSONArray jsonArr = new JSONArray(Utils.readText(nombreArchivo));
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject json = jsonArr.getJSONObject(i);
                socios.add(new Socio(json));
            }
        }
    }

    public static void ingresarSocios() throws Exception {
        boolean agregar = true;
        Socio socio;
        System.out.println("Ingreso de Socios\n");

        do {
            String id = Keyboard.readString("Ingrese identificación del socio o presione Enter para guardar: ");
            if (id.length() == 0) {
                break;
            }

            if (buscarSocioId(id) != null) {
                System.out.println("Error: Ya existe un socio con esa identificación.");
                continue;
            }

            String nombre = Keyboard.readString(1, 50, "Ingrese el nombre del socio:");
            String direccion = Keyboard.readString("Ingrese la dirección del socio:");
            String telefono = Keyboard.readString("Ingrese el teléfono del socio:");

            socio = new Socio(id, nombre, direccion, telefono);
            socios.add(socio);

            System.out.printf("Se agregó el socio con identificación: %s%n%n", id);
            agregar = true;
        } while (true);

        if (agregar) {
            Utils.writeJSON(socios, "./data/socios.json");
        }
        System.out.println("Lista de Socios guardada");
    }

    private static void buscarSocioId() {
        String id = Keyboard.readString(0, 10, "Ingrese identificación del Socio a buscar: ");
        if (id.isEmpty()) {
            System.out.println("Operación cancelada.");
            return;
        }

        // Buscar persona por identificación
        Socio encontrado = null;
        for (Socio s : socios) {
            if (s.getId().equals(id)) {
                encontrado = s;
                break;
            }
        }

        if (encontrado != null) {
            System.out.println("\nSocio encontrado:");
            System.out.printf(
                    "%nId: %s%nNombre: %s%nDirección: %s%nTelefono: %s%n",
                    encontrado.getId(),
                    encontrado.getNombre(),
                    encontrado.getDireccion(),
                    encontrado.getTelefono());

        } else {
            System.out.println("No se encontró la persona con identificación: " + id);
        }
        System.out.println();
    }

    private static Socio buscarSocioId(String id) {
        for (Socio s : socios) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    // Listar socios
    private static void listarSocios() {
        if (socios.isEmpty()) {
            System.out.println("No hay socios registrados.");
            return;
        }
        System.out.println("\nListado de Socios:");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-4s %-12s %-20s%n", "#", "ID", "Nombre");
        System.out.println("--------------------------------------------------");

        int index = 1;
        for (Socio socio : socios) {
            System.out.printf("%-4d %-12s %-20s%n",
                    index++,
                    socio.getId(),
                    socio.getNombre());
        }

        System.out.println("--------------------------------------------------\n");

    }

    // inicializar instalaciones
    // inicializar instalaciones
    private static void inicializarInstalaciones(Class<? extends InstalacionDeportiva> c) throws Exception {
        List<InstalacionDeportiva> instalaciones = new ArrayList();
        String nombreArchivo;

        if (Piscina.class.isAssignableFrom(c)) {
            nombreArchivo = archivos.getString("piscina");
            instalaciones = piscinas = new ArrayList<>();
        } else if (CanchaTenis.class.isAssignableFrom(c)) {
            nombreArchivo = archivos.getString("canchaTenis");
            instalaciones = canchasTenis = new ArrayList<>();
        } else if (CanchaMultiproposito.class.isAssignableFrom(c)) {
            nombreArchivo = archivos.getString("canchaMultiproposito");
            instalaciones = canchasMultiproposito = new ArrayList<>();
        } else {
            throw new IllegalArgumentException("Se esperaba una subclase de InstalacionDeportivas");
        }

        if (Utils.fileExists(nombreArchivo)) {
            JSONArray jsonArr = new JSONArray(Utils.readText(nombreArchivo));
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject json = jsonArr.getJSONObject(i);
                InstalacionDeportiva instalacion = null;

                if (Piscina.class.isAssignableFrom(c)) {
                    instalacion = new Piscina(json);
                } else if (CanchaTenis.class.isAssignableFrom(c)) {
                    instalacion = new CanchaTenis(json);
                } else if (CanchaMultiproposito.class.isAssignableFrom(c)) {
                    instalacion = new CanchaMultiproposito(json);
                }

                if (instalacion != null) {
                    instalaciones.add(instalacion);
                    App.instalaciones.add(instalacion);
                }
            }
        }
    }

    public static void crearInstalaciones() throws Exception {
        String nombreArchivo = "";
        boolean agregarPiscina = false;
        boolean agregarCanchaTenis = false;
        boolean agregarCanchaMultiproposito = false;
        InstalacionDeportiva instalacion;
        System.out.println("Ingreso de Instalaciones Deportivas\n");

        do {
            System.out.println("\nSeleccione el tipo de instalación:");
            System.out.println("0. Salir");
            System.out.println("1. Piscina");
            System.out.println("2. Cancha de Tenis");
            System.out.println("3. Cancha Multipropósito");
            int tipoInstalacion = Keyboard.readInt(0, 3, "Ingrese el tipo (0-3): ");

            if (tipoInstalacion == 0) {
                break;
            }

            String descripcion = Keyboard.readString(1, 50, "Ingrese la descripción de la instalación:");
            double ancho = Keyboard.readDouble("Ingrese el ancho de la instalación (metros):");
            double largo = Keyboard.readDouble("Ingrese el largo de la instalación (metros):");

            // Menú para seleccionar el tipo de instalación
            switch (tipoInstalacion) {
                case 1: // Piscina
                    boolean esOlimpica = Keyboard.readBoolean("¿Es olímpica? (S/N): ");
                    instalacion = new Piscina(descripcion, ancho, largo, esOlimpica);
                    instalacion.setValorHora(tarifas.getDouble("piscina")); // Para actualizar con la tarifa del JSON

                    piscinas.add(instalacion);
                    instalaciones.add(instalacion);
                    agregarPiscina = true;
                    break;

                case 2: // Cancha de Tenis
                    System.out.println("\nSeleccione el tipo de cancha:");
                    System.out.println("1. Ladrillo");
                    System.out.println("2. Césped");
                    int tipoCancha = Keyboard.readInt("Ingrese el tipo (1-2): ");

                    TipoCancha tipo = (tipoCancha == 2) ? TipoCancha.CESPED : TipoCancha.LADRILLO;
                    instalacion = new CanchaTenis(descripcion, ancho, largo, tipo);
                    instalacion.setValorHora(tarifas.getDouble("canchaTenis"));

                    canchasTenis.add(instalacion);
                    instalaciones.add(instalacion);
                    agregarCanchaTenis = true;
                    break;

                case 3: // Cancha Multipropósito
                    boolean graderia = Keyboard.readBoolean("¿La cancha tiene graderia? (S/N): ");
                    instalacion = new CanchaMultiproposito(descripcion, ancho, largo, graderia);
                    instalacion.setValorHora(tarifas.getDouble("canchaMultiproposito"));

                    canchasMultiproposito.add(instalacion);
                    instalaciones.add(instalacion);
                    agregarCanchaMultiproposito = true;
                    break;

                default:
                    System.out.println("Tipo de instalación no válido.");
                    continue;
            }

            System.out.printf("Se agregó la instalación con ID: %s%n%n", instalacion.getId());

        } while (true);

        if (agregarPiscina) {
            Utils.writeJSON(piscinas, archivos.getString("piscina"));
            System.out.println("Lista de Piscinas guardada");
        }

        if (agregarCanchaTenis) {
            Utils.writeJSON(canchasTenis, archivos.getString("canchaTenis"));
            System.out.println("Lista de Canchas de Tenis guardada");
        }

        if (agregarCanchaMultiproposito) {
            Utils.writeJSON(canchasMultiproposito, archivos.getString("canchaMultiproposito"));
            System.out.println("Lista de Canchas Multipropósito guardada");
        }
    }

    private static InstalacionDeportiva buscarInstalacionPorId(String id) {
        for (InstalacionDeportiva instalacion : instalaciones) {
            if (instalacion.getId().equals(id)) {
                return instalacion;
            }
        }
        return null;
    }

    // Buscar instalación por ID
    private static void buscarInstalacionPorId() {
        String id = Keyboard.readString(0, 10, "Ingrese ID de la Instalación a buscar: ");
        if (id.isEmpty()) {
            System.out.println("Operación cancelada.");
            return;
        }

        // Buscar instalación por ID
        InstalacionDeportiva encontrada = buscarInstalacionPorId(id);

        if (encontrada != null) {
            System.out.println("\nInstalación encontrada:");
            System.out.printf(
                    "%nId: %s%nDescripción: %s%nAncho: %.2f m%nLargo: %.2f m%nTipo: %s%n",
                    encontrada.getId(),
                    encontrada.getDescripcion(),
                    encontrada.getAncho(),
                    encontrada.getLargo(),
                    encontrada.getClass().getSimpleName());

            // Mostrar información específica según el tipo de instalación
            if (encontrada instanceof Piscina) {
                Piscina piscina = (Piscina) encontrada;
                System.out.printf("Olímpica: %s%n", piscina.getOlimpica() ? "Sí" : "No");
            } else if (encontrada instanceof CanchaTenis) {
                CanchaTenis cancha = (CanchaTenis) encontrada;
                System.out.printf("Tipo de cancha: %s%n", cancha.getTipo());
            } else if (encontrada instanceof CanchaMultiproposito) {
                CanchaMultiproposito cancha = (CanchaMultiproposito) encontrada;
                System.out.printf("graderia: %s%n", cancha.getGraderia() ? "Sí" : "No");
            }
        } else {
            System.out.println("No se encontró la instalación con ID: " + id);
        }
        System.out.println();
    }

    // Listar instalaciones por tipo
    private static void listarInstalacionesPorTipo(String tipo) {
        ArrayList<InstalacionDeportiva> instalacionesFiltradas = new ArrayList<>();

        // Filtrar instalaciones por tipo
        for (InstalacionDeportiva instalacion : instalaciones) {
            if (instalacion.getClass().getSimpleName().equals(tipo)) {
                instalacionesFiltradas.add(instalacion);
            }
        }

        if (instalacionesFiltradas.isEmpty()) {
            System.out.println("No hay instalaciones del tipo " + tipo + " registradas.");
            return;
        }

        System.out.println("\nListado de instalaciones de tipo " + tipo + ":");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-4s %-10s %-30s%n", "#", "ID", "Descripción");
        System.out.println("--------------------------------------------------");

        int index = 1;
        for (InstalacionDeportiva instalacion : instalacionesFiltradas) {
            System.out.printf("%-4d %-10s %-30s%n",
                    index++,
                    instalacion.getId(),
                    instalacion.getDescripcion());
        }

        System.out.println("--------------------------------------------------\n");

    }

    // Alquilar instalación
    private static void alquilarInstalacion() throws Exception {
        System.out.println("\nAlquiler de Instalaciones\n");

        // Verificar que haya socios e instalaciones
        if (socios.isEmpty()) {
            System.out.println("Error: No hay socios registrados.");
            return;
        }

        if (instalaciones.isEmpty()) {
            System.out.println("Error: No hay instalaciones registradas.");
            return;
        }

        // Seleccionar socio
        System.out.println("Elija una de las siguientes personas:");
        int index = 1;
        for (Socio socio : socios) {
            System.out.printf("%d) %s - %s%n",
                    index++,
                    socio.getId(),
                    socio.getNombre());
        }
        System.out.println("0) Salir sin elegir");

        int opcionSocio = Keyboard.readInt("Ingrese el índice de la persona: ");
        if (opcionSocio == 0) {
            System.out.println("Operación cancelada.");
            return;
        }

        if (opcionSocio < 1 || opcionSocio > socios.size()) {
            System.out.println("Índice de socio inválido.");
            return;
        }

        Socio socioSeleccionado = socios.get(opcionSocio - 1);

        // Seleccionar instalación
        System.out.println("Elija una de las siguientes instalaciones:");
        index = 1;
        for (InstalacionDeportiva instalacion : instalaciones) {
            System.out.printf("%d) %s - %s%n",
                    index++,
                    instalacion.getId(),
                    instalacion.getDescripcion());
        }
        System.out.println("0) Salir sin elegir");

        int opcionInstalacion = Keyboard.readInt("Ingrese el índice de la instalación: ");
        if (opcionInstalacion == 0) {
            System.out.println("Operación cancelada.");
            return;
        }

        if (opcionInstalacion < 1 || opcionInstalacion > instalaciones.size()) {
            System.out.println("Índice de instalación inválido.");
            return;
        }

        InstalacionDeportiva instalacionSeleccionada = instalaciones.get(opcionInstalacion - 1);

        // Ingresar fechas de inicio y fin del alquiler
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String fechaInicioStr = Keyboard.readString("Ingrese AAAA-MM-DD HH:MM en formato de 24 horas: ");
        LocalDateTime fechaInicio;
        try {
            fechaInicio = LocalDateTime.parse(fechaInicioStr, formatter);
        } catch (Exception e) {
            System.out.println("Formato de fecha y hora incorrecto.");
            return;
        }

        System.out.println("Fecha y hora de inicio: " + fechaInicio.format(formatter));

        String fechaFinStr = Keyboard.readString("Fecha y hora de finalización: ");
        LocalDateTime fechaFin;
        try {
            fechaFin = LocalDateTime.parse(fechaFinStr, formatter);
        } catch (Exception e) {
            System.out.println("Formato de fecha y hora incorrecto.");
            return;
        }

        // Validar que la fecha de fin sea posterior a la de inicio
        if (fechaFin.isBefore(fechaInicio)) {
            System.out.println("Error: La fecha de finalización debe ser posterior a la de inicio.");
            return;
        }

        // Verificar hora con otros alquileres de la misma instalación
        for (Alquiler alquiler : alquileres) {
            if (alquiler.getInstalacionDeportiva().getId().equals(instalacionSeleccionada.getId())) {
                if (Utils.OverlapSchedules(
                        fechaInicio, fechaFin,
                        alquiler.getFechaInicioHora(), alquiler.getFechaFinHora())) {
                    throw new IllegalArgumentException(
                            "Error: El horario solicitado se cruza con una reserva existente.");

                }
            }
        }
        // Crear y guardar el alquiler
        Alquiler nuevoAlquiler = new Alquiler(
                socioSeleccionado,
                instalacionSeleccionada,
                fechaInicio,
                fechaFin);

        if (alquileres == null) {
            alquileres = new ArrayList<>();
        }

        alquileres.add(nuevoAlquiler);
        Utils.writeJSON(alquileres, archivos.getString("alquileres"));

        System.out.printf("Se alquiló la %s - %s%n",
                instalacionSeleccionada.getClass().getSimpleName(),
                instalacionSeleccionada.getId());
    }

    // Inicializar alquileres
    private static void inicializarAlquileres() throws Exception {
        String fileName = archivos.getString("alquileres");
        alquileres = new ArrayList<>();
    
        if (Utils.fileExists(fileName)) {
            JSONArray jsonArr = new JSONArray(Utils.readText(fileName));
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject json = jsonArr.getJSONObject(i);
                alquileres.add(new Alquiler(json));
            }
        }
    }

    
    public static void listarCostosInstalaciones() {
        List<InstalacionDeportiva> instalaciones = new ArrayList<>();
        instalaciones.addAll(piscinas);
        instalaciones.addAll(canchasTenis);
        instalaciones.addAll(canchasMultiproposito);
        System.out.printf("%sCostos de funcionamiento%s%n", Color.YELLOW, Color.RESET);
        for (InstalacionDeportiva i : instalaciones) {
            System.out.printf("%s - %-40s%9.2f%n", i.getId(), i.getInstalacion(),
                    i.getCostoFuncionamiento(funcionamiento));
        }
        System.out.println();
    }

    private static void listarAlquileres() {
        if (alquileres == null || alquileres.isEmpty()) {
            System.out.println("No hay alquileres registrados.");
            return;
        }
    
        System.out.println("\nListado de Alquileres:");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("%-6s %-10s %-20s %-20s %-10s%n", "ID", "Socio", "Instalación", "Fecha Inicio", "Costo");
        System.out.println("---------------------------------------------------------------------");
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Alquiler alquiler : alquileres) {
            System.out.printf("%-6s %-10s %-20s %-20s $%-9.2f%n",
                    alquiler.getId(),
                    alquiler.getSocio().getId(),
                    alquiler.getInstalacionDeportiva().getId(),
                    alquiler.getFechaInicioHora().format(formatter),
                    alquiler.getCosto());
        }
    
        System.out.println("---------------------------------------------------------------------\n");
        
        // Opcional: mostrar el total de ingresos por alquileres
        double totalIngresos = 0;
        for (Alquiler alquiler : alquileres) {
            totalIngresos += alquiler.getCosto();
        }
        System.out.printf("Total de ingresos por alquileres: $%.2f%n%n", totalIngresos);
    }


    private static void inicializar() throws Exception {
        // limpiar la terminal
        System.out.print("\033[H\033[2J");

        // Separador de decimales: punto
        Locale.setDefault(Locale.of("es_CO"));

        config = new JSONObject(Utils.readText("./data/config.json"));
        tarifas = config.getJSONObject("tarifas");
        archivos = config.getJSONObject("archivos");
        funcionamiento = config.getJSONObject("funcionamiento");

        instalaciones = new ArrayList<>();

        
        inicializarInstalaciones(Piscina.class);
        inicializarInstalaciones(CanchaTenis.class);
        inicializarInstalaciones(CanchaMultiproposito.class);
        inicializarSocios();
        inicializarAlquileres();
    }

    private static void salir() {
        // limpiar pantalla y salir
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.exit(0);
    }

    static int leerOpcion() {
        String opciones = String.format("\n%sMenú de opciones:%s\n", Color.GREEN, Color.RESET)
                + "  1 - Ingresar socios\n"
                + "  2 - Listar socios\n"
                + "  3 - Crear instalaciones\n"
                + "  4 - Listar piscinas\n"
                + "  5 - Listar canchas de tenis\n"
                + "  6 - Listar canchas multipropósito\n"
                + "  7 - Alquilar instalaciones\n"
                + "  8 - Listar alquileres\n"
                + "  9 - Listar costos de instalaciones\n"
                + " 0 - Salir\n"
                + String.format("\nElija una opción (%s0 para salir%s) > ", Color.RED, Color.RESET);

        int opcion = Keyboard.readInt(opciones);
        System.out.println();
        return opcion;
    }

    
}
