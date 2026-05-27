package co.edu.uniquindio.eventexpress.controller;

import co.edu.uniquindio.eventexpress.adaptador.AdaptadorPDFBox;
import co.edu.uniquindio.eventexpress.adaptador.AdaptadorPOI;
import co.edu.uniquindio.eventexpress.adaptador.IGeneradorReporte;
import co.edu.uniquindio.eventexpress.config.ConfiguracionPlataforma;
import co.edu.uniquindio.eventexpress.facade.FachadaCompra;
import co.edu.uniquindio.eventexpress.fabrica.FabricaEvento;
import co.edu.uniquindio.eventexpress.modelo.Asiento;
import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.IEntrada;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.modelo.Usuario;
import co.edu.uniquindio.eventexpress.modelo.Zona;
import co.edu.uniquindio.eventexpress.modelo.enums.CategoriaEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;
import co.edu.uniquindio.eventexpress.state.evento.EstadoBorrador;
import co.edu.uniquindio.eventexpress.template.GeneradorReporteBase;
import co.edu.uniquindio.eventexpress.template.ReporteIngresoServicios;
import co.edu.uniquindio.eventexpress.template.ReporteOcupacionZona;
import co.edu.uniquindio.eventexpress.template.ReporteTasaCancelacion;
import co.edu.uniquindio.eventexpress.template.ReporteVentas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AdminController {

    @FXML private Label lblAdmin;
    @FXML private VBox vistaGestionEventos;
    @FXML private VBox vistaGestionRecintos;
    @FXML private VBox vistaGestionCompras;
    @FXML private VBox vistaMetricas;

    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, String> colEvNombre;
    @FXML private TableColumn<Evento, String> colEvCategoria;
    @FXML private TableColumn<Evento, String> colEvCiudad;
    @FXML private TableColumn<Evento, String> colEvFecha;
    @FXML private TableColumn<Evento, String> colEvEstado;
    @FXML private VBox panelFormEvento;
    @FXML private TextField txtEvNombre;
    @FXML private TextArea txtEvDescripcion;
    @FXML private TextField txtEvCiudad;
    @FXML private ComboBox<CategoriaEvento> cmbEvCategoria;
    @FXML private DatePicker dpEvFecha;
    @FXML private ComboBox<Recinto> cmbEvRecinto;

    @FXML private TreeView<Object> treeRecintos;
    @FXML private Label lblDetalleNodo;

    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colCoId;
    @FXML private TableColumn<Compra, String> colCoUsuario;
    @FXML private TableColumn<Compra, String> colCoEvento;
    @FXML private TableColumn<Compra, String> colCoTotal;
    @FXML private TableColumn<Compra, String> colCoEstado;
    @FXML private TextField txtMotivoCancel;

    @FXML private Label lblTotalVentas;
    @FXML private Label lblComprasActivas;
    @FXML private Label lblTasaCancelacion;
    @FXML private Label lblTopEvento;
    @FXML private LineChart<String, Number> chartVentas;
    @FXML private BarChart<String, Number> chartOcupacion;
    @FXML private PieChart chartServicios;
    @FXML private ComboBox<String> cmbTipoReporte;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;

    private final FachadaCompra fachada = new FachadaCompra();
    private final SessionManager session = SessionManager.getInstancia();

    private final NumberFormat formatoMoneda = NumberFormat.getNumberInstance(new Locale("es", "CO"));
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        Usuario actual = session.getUsuarioActual();
        if (actual != null) {
            lblAdmin.setText(actual.getNombreCompleto());
        }

        cmbEvCategoria.setItems(FXCollections.observableArrayList(CategoriaEvento.values()));
        cmbEvRecinto.setItems(FXCollections.observableArrayList(session.getRecintos()));
        cmbTipoReporte.setItems(FXCollections.observableArrayList(
                "Ventas", "Ocupación por zona", "Ingreso por servicios", "Tasa de cancelación"));
        cmbTipoReporte.getSelectionModel().selectFirst();

        configurarTablaEventos();
        configurarTablaCompras();
        cargarEventos();
        cargarCompras();
        construirArbolRecintos();
        cargarMetricas();
    }

    private void configurarTablaEventos() {
        colEvNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colEvCategoria.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCategoria() == null ? "" : c.getValue().getCategoria().name()));
        colEvCiudad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCiudad()));
        colEvFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaHora() == null ? "" : c.getValue().getFechaHora().format(formatoFecha)));
        colEvEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() == null ? "" : c.getValue().getEstado().getEstadoActual().name()));
    }

    private void configurarTablaCompras() {
        colCoId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdCompra()));
        colCoUsuario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getUsuario() == null ? "" : c.getValue().getUsuario().getNombreCompleto()));
        colCoEvento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEvento() == null ? "" : c.getValue().getEvento().getNombre()));
        colCoTotal.setCellValueFactory(c -> new SimpleStringProperty(
                "$" + formatoMoneda.format(c.getValue().getTotal())));
        colCoEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() == null ? "" : c.getValue().getEstado().getEstadoActual().name()));
    }

    private void cargarEventos() {
        tablaEventos.setItems(FXCollections.observableArrayList(session.getEventos()));
    }

    private void cargarCompras() {
        List<Compra> todas = new ArrayList<>();
        for (Usuario u : session.getUsuarios()) {
            for (Compra c : u.getCompras()) {
                if (!todas.contains(c)) {
                    todas.add(c);
                }
            }
        }
        tablaCompras.setItems(FXCollections.observableArrayList(todas));
    }

    @FXML
    private void publicarEvento() {
        Evento ev = tablaEventos.getSelectionModel().getSelectedItem();
        if (ev == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona un evento.");
            return;
        }
        boolean ok = fachada.publicarEventoConAuditoria(ev);
        if (ok) {
            alerta(Alert.AlertType.INFORMATION, "Evento publicado.");
            tablaEventos.refresh();
        } else {
            alerta(Alert.AlertType.ERROR, "No se pudo publicar (revisa el estado actual del evento).");
        }
    }

    @FXML
    private void pausarEvento() {
        Evento ev = tablaEventos.getSelectionModel().getSelectedItem();
        if (ev == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona un evento.");
            return;
        }
        try {
            ev.pausar();
            alerta(Alert.AlertType.INFORMATION, "Evento pausado.");
            tablaEventos.refresh();
        } catch (IllegalStateException e) {
            alerta(Alert.AlertType.ERROR, "No se puede pausar: " + e.getMessage());
        }
    }

    @FXML
    private void cancelarEvento() {
        Evento ev = tablaEventos.getSelectionModel().getSelectedItem();
        if (ev == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona un evento.");
            return;
        }
        try {
            ev.cancelar();
            alerta(Alert.AlertType.INFORMATION, "Evento cancelado.");
            tablaEventos.refresh();
        } catch (IllegalStateException e) {
            alerta(Alert.AlertType.ERROR, "No se puede cancelar: " + e.getMessage());
        }
    }

    @FXML
    private void nuevoEvento() {
        txtEvNombre.clear();
        txtEvDescripcion.clear();
        txtEvCiudad.clear();
        cmbEvCategoria.getSelectionModel().clearSelection();
        cmbEvRecinto.getSelectionModel().clearSelection();
        dpEvFecha.setValue(null);
        panelFormEvento.setVisible(true);
        panelFormEvento.setManaged(true);
    }

    @FXML
    private void cerrarFormEvento() {
        panelFormEvento.setVisible(false);
        panelFormEvento.setManaged(false);
    }

    @FXML
    private void guardarEvento() {
        String nombre = txtEvNombre.getText() == null ? "" : txtEvNombre.getText().trim();
        String descripcion = txtEvDescripcion.getText() == null ? "" : txtEvDescripcion.getText().trim();
        String ciudad = txtEvCiudad.getText() == null ? "" : txtEvCiudad.getText().trim();
        CategoriaEvento categoria = cmbEvCategoria.getValue();
        Recinto recinto = cmbEvRecinto.getValue();
        LocalDate fecha = dpEvFecha.getValue();

        if (nombre.isEmpty() || ciudad.isEmpty() || categoria == null || recinto == null || fecha == null) {
            alerta(Alert.AlertType.WARNING, "Completa nombre, ciudad, categoría, recinto y fecha.");
            return;
        }

        Map<String, Object> datos = datosPorDefecto(categoria);
        Evento nuevo = FabricaEvento.crearEvento(
                categoria,
                "EV-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                nombre, descripcion, ciudad,
                fecha.atTime(LocalTime.of(20, 0)),
                recinto, datos);
        nuevo.cambiarEstado(new EstadoBorrador());
        session.getEventos().add(nuevo);
        cargarEventos();
        cerrarFormEvento();
        alerta(Alert.AlertType.INFORMATION, "Evento creado en estado borrador.");
    }

    private Map<String, Object> datosPorDefecto(CategoriaEvento categoria) {
        Map<String, Object> datos = new HashMap<>();
        switch (categoria) {
            case CONCIERTO:
                datos.put("artistaPrincipal", "Por definir");
                datos.put("generoMusical", "Variado");
                break;
            case TEATRO:
                datos.put("compania", "Por definir");
                datos.put("duracionMinutos", 120);
                break;
            case CONFERENCIA:
                datos.put("ponente", "Por definir");
                datos.put("tema", "Por definir");
                datos.put("duracionMinutos", 90);
                break;
            default:
                break;
        }
        return datos;
    }

    private void construirArbolRecintos() {
        TreeItem<Object> raiz = new TreeItem<>("Recintos");
        raiz.setExpanded(true);
        for (Recinto r : session.getRecintos()) {
            TreeItem<Object> nodoRecinto = new TreeItem<>(r);
            nodoRecinto.setExpanded(false);
            for (Zona z : r.getZonas()) {
                TreeItem<Object> nodoZona = new TreeItem<>(z);
                for (Asiento a : z.getAsientos()) {
                    nodoZona.getChildren().add(new TreeItem<>(a));
                }
                nodoRecinto.getChildren().add(nodoZona);
            }
            raiz.getChildren().add(nodoRecinto);
        }
        treeRecintos.setRoot(raiz);
        treeRecintos.setShowRoot(true);
        treeRecintos.getSelectionModel().selectedItemProperty().addListener(
                (obs, viejo, nuevo) -> mostrarDetalleNodo(nuevo));
    }

    private void mostrarDetalleNodo(TreeItem<Object> item) {
        if (item == null || item.getValue() == null) {
            lblDetalleNodo.setText("Selecciona un nodo del árbol");
            return;
        }
        Object valor = item.getValue();
        if (valor instanceof Recinto) {
            Recinto r = (Recinto) valor;
            lblDetalleNodo.setText("Recinto: " + r.getNombre() + " - " + r.getCiudad()
                    + " | Capacidad: " + r.consultarCapacidad()
                    + " | Disponibles: " + r.consultarDisponibles());
        } else if (valor instanceof Zona) {
            Zona z = (Zona) valor;
            lblDetalleNodo.setText("Zona: " + z.getNombre()
                    + " | Precio: $" + formatoMoneda.format(z.getPrecioBase())
                    + " | Disponibles: " + z.consultarDisponibles());
        } else if (valor instanceof Asiento) {
            Asiento a = (Asiento) valor;
            lblDetalleNodo.setText("Asiento: " + a.getFila() + a.getNumero()
                    + " | Estado: " + a.getEstado().name());
        } else {
            lblDetalleNodo.setText(valor.toString());
        }
    }

    private Asiento asientoSeleccionado() {
        TreeItem<Object> item = treeRecintos.getSelectionModel().getSelectedItem();
        if (item != null && item.getValue() instanceof Asiento) {
            return (Asiento) item.getValue();
        }
        return null;
    }

    @FXML
    private void bloquearAsiento() {
        Asiento a = asientoSeleccionado();
        if (a == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona un asiento en el árbol.");
            return;
        }
        boolean ok = fachada.bloquearAsientoConAuditoria(a, "Bloqueo administrativo");
        if (ok) {
            alerta(Alert.AlertType.INFORMATION, "Asiento bloqueado.");
            treeRecintos.refresh();
            mostrarDetalleNodo(treeRecintos.getSelectionModel().getSelectedItem());
        } else {
            alerta(Alert.AlertType.ERROR, "No se pudo bloquear (puede estar vendido).");
        }
    }

    @FXML
    private void liberarAsiento() {
        Asiento a = asientoSeleccionado();
        if (a == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona un asiento en el árbol.");
            return;
        }
        boolean ok = a.liberar();
        if (ok) {
            alerta(Alert.AlertType.INFORMATION, "Asiento liberado.");
            treeRecintos.refresh();
            mostrarDetalleNodo(treeRecintos.getSelectionModel().getSelectedItem());
        } else {
            alerta(Alert.AlertType.ERROR, "No se pudo liberar (debe estar reservado o bloqueado).");
        }
    }

    @FXML
    private void cancelarCompraAuditoria() {
        Compra compra = tablaCompras.getSelectionModel().getSelectedItem();
        if (compra == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona una compra.");
            return;
        }
        String motivo = txtMotivoCancel.getText() == null || txtMotivoCancel.getText().isBlank()
                ? "Cancelación administrativa" : txtMotivoCancel.getText().trim();
        boolean ok = fachada.cancelarCompraConAuditoria(compra, motivo);
        if (ok) {
            alerta(Alert.AlertType.INFORMATION, "Compra cancelada con auditoría.");
            tablaCompras.refresh();
        } else {
            alerta(Alert.AlertType.ERROR, "No se pudo cancelar la compra.");
        }
    }

    @FXML
    private void reembolsarCompra() {
        Compra compra = tablaCompras.getSelectionModel().getSelectedItem();
        if (compra == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona una compra.");
            return;
        }
        double porcentaje = ConfiguracionPlataforma.getInstancia().getPorcentajeReembolso();
        double monto = compra.getTotal() * porcentaje;
        boolean ok = fachada.reembolsarCompraConAuditoria(compra, monto);
        if (ok) {
            alerta(Alert.AlertType.INFORMATION,
                    "Compra reembolsada por $" + formatoMoneda.format(monto));
            tablaCompras.refresh();
        } else {
            alerta(Alert.AlertType.ERROR, "No se pudo reembolsar la compra.");
        }
    }

    private void cargarMetricas() {
        List<Compra> compras = new ArrayList<>(tablaCompras.getItems());
        double totalVentas = 0.0;
        int activas = 0;
        int canceladas = 0;
        Map<String, Integer> conteoEventos = new LinkedHashMap<>();

        for (Compra c : compras) {
            totalVentas += c.getTotal();
            EstadoCompra estado = c.getEstado() == null ? null : c.getEstado().getEstadoActual();
            if (estado == EstadoCompra.CONFIRMADA || estado == EstadoCompra.PAGADA) {
                activas++;
            }
            if (estado == EstadoCompra.CANCELADA) {
                canceladas++;
            }
            if (c.getEvento() != null) {
                String n = c.getEvento().getNombre();
                conteoEventos.put(n, conteoEventos.getOrDefault(n, 0) + 1);
            }
        }

        lblTotalVentas.setText("$" + formatoMoneda.format(totalVentas));
        lblComprasActivas.setText(String.valueOf(activas));
        double tasa = compras.isEmpty() ? 0.0 : (canceladas * 100.0 / compras.size());
        lblTasaCancelacion.setText(String.format(Locale.US, "%.1f%%", tasa));

        String topEvento = "-";
        int max = -1;
        for (Map.Entry<String, Integer> e : conteoEventos.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                topEvento = e.getKey();
            }
        }
        lblTopEvento.setText(topEvento);

        construirChartVentas();
        construirChartOcupacion();
        construirChartServicios(compras);
    }

    private void construirChartVentas() {
        chartVentas.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ventas simuladas");
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun"};
        double[] valores = {1200000, 1850000, 1500000, 2200000, 2600000, 3100000};
        for (int i = 0; i < meses.length; i++) {
            serie.getData().add(new XYChart.Data<>(meses[i], valores[i]));
        }
        chartVentas.getData().add(serie);
    }

    private void construirChartOcupacion() {
        chartOcupacion.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ocupación %");
        for (Recinto r : session.getRecintos()) {
            for (Zona z : r.getZonas()) {
                int capacidad = z.consultarCapacidad();
                int disponibles = z.consultarDisponibles();
                double ocupacion = capacidad == 0 ? 0.0 : ((capacidad - disponibles) * 100.0 / capacidad);
                serie.getData().add(new XYChart.Data<>(z.getNombre(), ocupacion));
            }
        }
        chartOcupacion.getData().add(serie);
    }

    private void construirChartServicios(List<Compra> compras) {
        chartServicios.getData().clear();
        int vip = 0;
        int seguro = 0;
        int parqueadero = 0;
        int otros = 0;
        for (Compra c : compras) {
            for (IEntrada e : c.getEntradas()) {
                String desc = e.obtenerDescripcion() == null ? "" : e.obtenerDescripcion().toLowerCase();
                if (desc.contains("vip")) {
                    vip++;
                }
                if (desc.contains("seguro")) {
                    seguro++;
                }
                if (desc.contains("parqueadero")) {
                    parqueadero++;
                }
                if (!desc.contains("vip") && !desc.contains("seguro") && !desc.contains("parqueadero")) {
                    otros++;
                }
            }
        }
        if (vip + seguro + parqueadero + otros == 0) {
            chartServicios.getData().add(new PieChart.Data("Sin servicios", 1));
            return;
        }
        chartServicios.getData().add(new PieChart.Data("VIP", vip));
        chartServicios.getData().add(new PieChart.Data("Seguro", seguro));
        chartServicios.getData().add(new PieChart.Data("Parqueadero", parqueadero));
        chartServicios.getData().add(new PieChart.Data("Base", otros));
    }

    @FXML
    private void exportarPdf() {
        exportar(new AdaptadorPDFBox(), "pdf");
    }

    @FXML
    private void exportarExcel() {
        exportar(new AdaptadorPOI(), "xlsx");
    }

    private void exportar(IGeneradorReporte adaptador, String extension) {
        GeneradorReporteBase<?> reporte = construirReporte(cmbTipoReporte.getValue());
        if (reporte == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona un tipo de reporte.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("reporte." + extension);
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(extension.toUpperCase(), "*." + extension));
        Stage stage = (Stage) lblAdmin.getScene().getWindow();
        File archivo = chooser.showSaveDialog(stage);
        if (archivo == null) {
            return;
        }
        try {
            fachada.generarReporte(reporte, dpDesde.getValue(), dpHasta.getValue(),
                    adaptador, archivo.getAbsolutePath());
            alerta(Alert.AlertType.INFORMATION, "Reporte exportado en:\n" + archivo.getAbsolutePath());
        } catch (RuntimeException e) {
            alerta(Alert.AlertType.ERROR, "Error al exportar: " + e.getMessage());
        }
    }

    private GeneradorReporteBase<?> construirReporte(String tipo) {
        if (tipo == null) {
            return null;
        }
        List<Compra> compras = new ArrayList<>(tablaCompras.getItems());
        switch (tipo) {
            case "Ventas":
                return new ReporteVentas(compras);
            case "Ocupación por zona":
                List<Zona> zonas = new ArrayList<>();
                for (Recinto r : session.getRecintos()) {
                    zonas.addAll(r.getZonas());
                }
                return new ReporteOcupacionZona(zonas);
            case "Ingreso por servicios":
                return new ReporteIngresoServicios(compras);
            case "Tasa de cancelación":
                return new ReporteTasaCancelacion(compras);
            default:
                return null;
        }
    }

    @FXML
    private void mostrarVistaEventos() {
        cargarEventos();
        mostrar(vistaGestionEventos);
    }

    @FXML
    private void mostrarVistaRecintos() {
        mostrar(vistaGestionRecintos);
    }

    @FXML
    private void mostrarVistaCompras() {
        cargarCompras();
        mostrar(vistaGestionCompras);
    }

    @FXML
    private void mostrarVistaMetricas() {
        cargarCompras();
        cargarMetricas();
        mostrar(vistaMetricas);
    }

    @FXML
    private void cerrarSesion() {
        session.setUsuarioActual(null);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/co/edu/uniquindio/eventexpress/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblAdmin.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 620));
            stage.centerOnScreen();
        } catch (IOException e) {
            alerta(Alert.AlertType.ERROR, "No se pudo volver al login.");
        }
    }

    private void mostrar(VBox vistaActiva) {
        VBox[] vistas = {vistaGestionEventos, vistaGestionRecintos, vistaGestionCompras, vistaMetricas};
        for (VBox v : vistas) {
            boolean activa = v == vistaActiva;
            v.setVisible(activa);
            v.setManaged(activa);
        }
    }

    private void alerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
