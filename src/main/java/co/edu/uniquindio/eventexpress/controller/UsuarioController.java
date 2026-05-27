package co.edu.uniquindio.eventexpress.controller;

import co.edu.uniquindio.eventexpress.facade.FachadaCompra;
import co.edu.uniquindio.eventexpress.modelo.*;
import co.edu.uniquindio.eventexpress.modelo.enums.CategoriaEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoAsiento;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoMetodoPago;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoServicioAdicional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;

public class UsuarioController {

    @FXML private Label lblNombreUsuario;
    @FXML private VBox vistaListado;
    @FXML private VBox vistaAuditorio;
    @FXML private VBox vistaCarrito;
    @FXML private VBox vistaHistorial;
    @FXML private VBox vistaPerfil;

    @FXML private TextField txtBusqueda;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private ComboBox<String> cmbCiudad;
    @FXML private FlowPane panelEventos;

    @FXML private Label lblEvento;
    @FXML private Label lblZona1;
    @FXML private Label lblZona2;
    @FXML private Label lblZona3;
    @FXML private GridPane gridZona1;
    @FXML private GridPane gridZona2;
    @FXML private GridPane gridZona3;
    @FXML private ListView<String> listAsientos;
    @FXML private CheckBox chkVip;
    @FXML private CheckBox chkSeguro;
    @FXML private CheckBox chkParqueadero;
    @FXML private Label lblTotal;
    @FXML private Button btnPagar;

    @FXML private TableView<IEntrada> tablaEntradas;
    @FXML private TableColumn<IEntrada, String> colDescripcion;
    @FXML private TableColumn<IEntrada, String> colPrecio;
    @FXML private ComboBox<TipoMetodoPago> cmbMetodoPago;
    @FXML private VBox panelCamposPago;
    @FXML private Label lblTotalCarrito;

    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private TableView<Compra> tablaHistorial;
    @FXML private TableColumn<Compra, String> colHistEvento;
    @FXML private TableColumn<Compra, String> colHistFecha;
    @FXML private TableColumn<Compra, String> colHistTotal;
    @FXML private TableColumn<Compra, String> colHistEstado;

    @FXML private TextField txtPerfilNombre;
    @FXML private TextField txtPerfilCorreo;
    @FXML private TextField txtPerfilTelefono;
    @FXML private ListView<String> listMetodosPago;

    private final FachadaCompra fachada = new FachadaCompra();
    private final SessionManager session = SessionManager.getInstancia();

    private Evento eventoActual;
    private Compra compraActual;
    private final List<Asiento> asientosSeleccionados = new ArrayList<>();
    private int extrasTotal = 0;
    private MetodoPagoGuardado metodoGuardadoSeleccionado = null;
    private final Map<Asiento, Button> botonesPorAsiento = new HashMap<>();
    private final Map<Asiento, Double> precioPorAsiento = new HashMap<>();

    private final NumberFormat formatoMoneda = NumberFormat.getNumberInstance(new Locale("es", "CO"));
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        Usuario actual = session.getUsuarioActual();
        if (actual != null) {
            lblNombreUsuario.setText(actual.getNombreCompleto());
        }

        cmbCategoria.getItems().add("Todas");
        for (CategoriaEvento c : CategoriaEvento.values()) {
            cmbCategoria.getItems().add(c.name());
        }
        cmbCategoria.getSelectionModel().selectFirst();

        cmbCiudad.getItems().add("Todas");
        for (Evento e : session.getEventos()) {
            if (esPublicado(e) && !cmbCiudad.getItems().contains(e.getCiudad())) {
                cmbCiudad.getItems().add(e.getCiudad());
            }
        }
        cmbCiudad.getSelectionModel().selectFirst();

        cmbMetodoPago.setItems(FXCollections.observableArrayList(TipoMetodoPago.values()));
        cmbFiltroEstado.getItems().add("Todos");
        for (EstadoCompra ec : EstadoCompra.values()) {
            cmbFiltroEstado.getItems().add(ec.name());
        }
        cmbFiltroEstado.getSelectionModel().selectFirst();

        txtBusqueda.textProperty().addListener((obs, viejo, nuevo) -> aplicarFiltros());
        cmbCategoria.valueProperty().addListener((obs, viejo, nuevo) -> aplicarFiltros());
        cmbCiudad.valueProperty().addListener((obs, viejo, nuevo) -> aplicarFiltros());

        configurarTablaEntradas();
        configurarTablaHistorial();
        cargarEventos();
        cargarPerfil();
    }

    private boolean esPublicado(Evento e) {
        return e.getEstado() != null && e.getEstado().getEstadoActual() == EstadoEvento.PUBLICADO;
    }

    private void cargarEventos() {
        panelEventos.getChildren().clear();
        String texto = txtBusqueda.getText() == null ? "" : txtBusqueda.getText().trim().toLowerCase();
        String categoria = cmbCategoria.getValue();
        String ciudad = cmbCiudad.getValue();

        for (Evento e : session.getEventos()) {
            if (!esPublicado(e)) {
                continue;
            }
            if (!texto.isEmpty() && !e.getNombre().toLowerCase().contains(texto)) {
                continue;
            }
            if (categoria != null && !"Todas".equals(categoria)
                    && e.getCategoria() != null && !e.getCategoria().name().equals(categoria)) {
                continue;
            }
            if (ciudad != null && !"Todas".equals(ciudad) && !ciudad.equals(e.getCiudad())) {
                continue;
            }
            panelEventos.getChildren().add(crearCard(e));
        }
    }

    private VBox crearCard(Evento evento) {
        VBox card = new VBox(8);
        card.getStyleClass().add("event-card");
        card.setPadding(new Insets(14));
        card.setPrefWidth(260);

        Label nombre = new Label(evento.getNombre());
        nombre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Label badge = new Label(evento.getCategoria() == null ? "" : evento.getCategoria().name());
        badge.getStyleClass().add(claseBadge(evento.getCategoria()));

        Label ciudad = new Label(evento.getCiudad());
        Label fecha = new Label(evento.getFechaHora() == null ? "" : evento.getFechaHora().format(formatoFecha));
        Label descripcion = new Label(evento.getDescripcion());
        descripcion.setWrapText(true);
        descripcion.setStyle("-fx-text-fill: #555;");

        card.getChildren().addAll(badge, nombre, ciudad, fecha, descripcion);
        card.setOnMouseClicked(ev -> abrirEvento(evento));
        return card;
    }

    private String claseBadge(CategoriaEvento categoria) {
        if (categoria == null) {
            return "badge-conferencia";
        }
        switch (categoria) {
            case CONCIERTO:
                return "badge-concierto";
            case TEATRO:
                return "badge-teatro";
            default:
                return "badge-conferencia";
        }
    }

    private void abrirEvento(Evento evento) {
        this.eventoActual = evento;
        this.compraActual = fachada.iniciarCompra(session.getUsuarioActual(), evento);
        asientosSeleccionados.clear();
        botonesPorAsiento.clear();
        precioPorAsiento.clear();
        listAsientos.getItems().clear();
        chkVip.setSelected(false);
        chkSeguro.setSelected(false);
        chkParqueadero.setSelected(false);
        extrasTotal = 0;

        lblEvento.setText(evento.getNombre());
        construirGrids(evento.getRecinto());
        recalcularTotal();
        mostrar(vistaAuditorio);
    }

    private void construirGrids(Recinto recinto) {
        Label[] labels = {lblZona1, lblZona2, lblZona3};
        GridPane[] grids = {gridZona1, gridZona2, gridZona3};
        for (GridPane g : grids) {
            g.getChildren().clear();
        }
        for (Label l : labels) {
            l.getParent().setVisible(false);
            l.getParent().setManaged(false);
        }
        if (recinto == null) {
            return;
        }
        List<Zona> zonas = recinto.getZonas();
        for (int i = 0; i < zonas.size() && i < grids.length; i++) {
            Zona zona = zonas.get(i);
            labels[i].setText(zona.getNombre() + "  ($" + formatoMoneda.format(zona.getPrecioBase()) + ")");
            labels[i].getParent().setVisible(true);
            labels[i].getParent().setManaged(true);
            llenarGrid(grids[i], zona);
        }
    }

    private void llenarGrid(GridPane grid, Zona zona) {
        int col = 0;
        int row = 0;
        int maxCols = 14;
        for (Asiento asiento : zona.getAsientos()) {
            Button btn = new Button(asiento.getFila() + asiento.getNumero());
            btn.setPrefWidth(46);
            btn.getStyleClass().add(claseAsiento(asiento.getEstado()));
            precioPorAsiento.put(asiento, zona.getPrecioBase());
            botonesPorAsiento.put(asiento, btn);
            if (asiento.getEstado() == EstadoAsiento.DISPONIBLE) {
                btn.setOnAction(ev -> alternarAsiento(asiento, btn));
            }
            grid.add(btn, col, row);
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    private String claseAsiento(EstadoAsiento estado) {
        switch (estado) {
            case DISPONIBLE:
                return "seat-disponible";
            case VENDIDO:
                return "seat-vendido";
            case RESERVADO:
                return "seat-reservado";
            case BLOQUEADO:
                return "seat-bloqueado";
            default:
                return "seat-disponible";
        }
    }

    private void alternarAsiento(Asiento asiento, Button btn) {
        if (asientosSeleccionados.contains(asiento)) {
            asientosSeleccionados.remove(asiento);
            btn.getStyleClass().removeAll("seat-seleccionado");
            if (!btn.getStyleClass().contains("seat-disponible")) {
                btn.getStyleClass().add("seat-disponible");
            }
        } else {
            asientosSeleccionados.add(asiento);
            btn.getStyleClass().removeAll("seat-disponible");
            btn.getStyleClass().add("seat-seleccionado");
        }
        refrescarListaAsientos();
        recalcularTotal();
    }

    private void refrescarListaAsientos() {
        listAsientos.getItems().clear();
        for (Asiento a : asientosSeleccionados) {
            listAsientos.getItems().add(a.getFila() + a.getNumero());
        }
    }

    @FXML
    private void recalcularTotal() {
        double base = 0.0;
        for (Asiento a : asientosSeleccionados) {
            Double p = precioPorAsiento.get(a);
            base += p != null ? p : 0.0;
        }
        extrasTotal = 0;
        int cantidad = asientosSeleccionados.size();
        if (chkVip.isSelected()) {
            extrasTotal += 50000 * cantidad;
        }
        if (chkSeguro.isSelected()) {
            extrasTotal += 15000 * cantidad;
        }
        if (chkParqueadero.isSelected()) {
            extrasTotal += 12000 * cantidad;
        }
        double total = base + extrasTotal;
        lblTotal.setText("Total: $" + formatoMoneda.format(total));
        btnPagar.setDisable(asientosSeleccionados.isEmpty());
    }

    @FXML
    private void irAlPago() {
        if (asientosSeleccionados.isEmpty()) {
            return;
        }
        boolean ok = fachada.seleccionarAsientos(compraActual, new ArrayList<>(asientosSeleccionados));
        if (!ok) {
            alerta(Alert.AlertType.ERROR, "No se pudieron reservar los asientos seleccionados.");
            return;
        }
        List<TipoServicioAdicional> servicios = new ArrayList<>();
        if (chkVip.isSelected()) {
            servicios.add(TipoServicioAdicional.VIP);
        }
        if (chkSeguro.isSelected()) {
            servicios.add(TipoServicioAdicional.SEGURO);
        }
        if (chkParqueadero.isSelected()) {
            servicios.add(TipoServicioAdicional.PARQUEADERO);
        }
        if (!servicios.isEmpty()) {
            int numEntradas = compraActual.getEntradas().size();
            for (int i = 0; i < numEntradas; i++) {
                fachada.agregarServicios(compraActual, i, servicios);
            }
        }
        compraActual.calcularTotal();
        tablaEntradas.setItems(FXCollections.observableArrayList(compraActual.getEntradas()));
        lblTotalCarrito.setText("Total: $" + formatoMoneda.format(compraActual.getTotal()));

        metodoGuardadoSeleccionado = null;
        panelCamposPago.getChildren().clear();

        List<MetodoPagoGuardado> guardados = session.getUsuarioActual().getMetodosPagoGuardados();
        if (!guardados.isEmpty()) {
            Label lblGuardados = new Label("Usar método guardado:");
            lblGuardados.setStyle("-fx-font-weight: bold;");
            ComboBox<String> cmbGuardados = new ComboBox<>();
            cmbGuardados.getItems().add("-- Ingresar datos nuevos --");
            for (MetodoPagoGuardado m : guardados) {
                cmbGuardados.getItems().add(m.getAlias() + " (" + m.getTipo().name() + ")");
            }
            cmbGuardados.getSelectionModel().selectFirst();
            cmbGuardados.setMaxWidth(Double.MAX_VALUE);
            cmbGuardados.setOnAction(e -> {
                int idx = cmbGuardados.getSelectionModel().getSelectedIndex();
                if (idx <= 0) {
                    metodoGuardadoSeleccionado = null;
                    cmbMetodoPago.setDisable(false);
                    while (panelCamposPago.getChildren().size() > 2) {
                        panelCamposPago.getChildren().remove(2);
                    }
                    construirCamposPago(cmbMetodoPago.getValue());
                } else {
                    metodoGuardadoSeleccionado = guardados.get(idx - 1);
                    cmbMetodoPago.setValue(metodoGuardadoSeleccionado.getTipo());
                    cmbMetodoPago.setDisable(true);
                    while (panelCamposPago.getChildren().size() > 2) {
                        panelCamposPago.getChildren().remove(2);
                    }
                    Label lbl = new Label("Se usará: " + metodoGuardadoSeleccionado.getAlias());
                    lbl.setStyle("-fx-text-fill: #1D9E75; -fx-font-weight: bold;");
                    panelCamposPago.getChildren().add(lbl);
                }
            });
            panelCamposPago.getChildren().addAll(lblGuardados, cmbGuardados);
            // Con métodos guardados NO se muestran campos manuales hasta que el usuario elija "datos nuevos"
        } else {
            construirCamposPago(cmbMetodoPago.getValue());
        }

        mostrar(vistaCarrito);
    }

    @FXML
    private void cambiarMetodoPago() {
        construirCamposPago(cmbMetodoPago.getValue());
    }

    private void construirCamposPago(TipoMetodoPago tipo) {
        panelCamposPago.getChildren().clear();
        if (tipo == null) {
            return;
        }
        switch (tipo) {
            case TARJETA:
                panelCamposPago.getChildren().addAll(
                        campo("numeroTarjeta", "Número de tarjeta"),
                        campo("titular", "Titular"),
                        campo("cvv", "CVV"),
                        campo("fechaVencimiento", "Vencimiento (MM/AA)"),
                        campo("banco", "Banco"));
                break;
            case PSE:
                panelCamposPago.getChildren().addAll(
                        campo("banco", "Banco"),
                        campo("numeroCuenta", "Número de cuenta"),
                        campo("tipoCuenta", "Tipo de cuenta"),
                        campo("tipoDocumento", "Tipo de documento"),
                        campo("numeroDocumento", "Número de documento"));
                break;
            case EFECTIVO:
                panelCamposPago.getChildren().addAll(
                        campo("referenciaPago", "Referencia de pago"),
                        campo("montoEntregado", "Monto entregado"),
                        campo("puntoPago", "Punto de pago"));
                break;
            default:
                break;
        }
    }

    private VBox campo(String id, String etiqueta) {
        VBox caja = new VBox(4);
        Label lbl = new Label(etiqueta);
        TextField tf = new TextField();
        tf.setId(id);
        tf.getStyleClass().add("form-field");
        caja.getChildren().addAll(lbl, tf);
        return caja;
    }

    @FXML
    private void confirmarPago() {
        TipoMetodoPago tipo = cmbMetodoPago.getValue();
        if (tipo == null) {
            alerta(Alert.AlertType.ERROR, "Selecciona un método de pago.");
            return;
        }
        Map<String, Object> datos = new HashMap<>();

        if (metodoGuardadoSeleccionado != null) {
            String datosStr = metodoGuardadoSeleccionado.getDatos();
            if (datosStr != null && !datosStr.isBlank()) {
                for (String par : datosStr.split(";")) {
                    String[] kv = par.split("=", 2);
                    if (kv.length == 2) {
                        datos.put(kv[0].trim(), kv[1].trim());
                    }
                }
            }
            // Para tarjeta guardada, reconstruir numeroTarjeta desde ultimos4
            if (tipo == TipoMetodoPago.TARJETA && datos.containsKey("ultimos4")) {
                datos.put("numeroTarjeta", "0000000000000" + datos.get("ultimos4"));
            }
        } else {
            for (Node n : panelCamposPago.getChildren()) {
                if (n instanceof VBox) {
                    for (Node hijo : ((VBox) n).getChildren()) {
                        if (hijo instanceof TextField) {
                            TextField tf = (TextField) hijo;
                            datos.put(tf.getId(), tf.getText());
                        }
                    }
                }
            }
            if (tipo == TipoMetodoPago.TARJETA) {
                String num = datos.getOrDefault("numeroTarjeta", "").toString().replaceAll("\\s+", "");
                if (num.isEmpty()) {
                    alerta(Alert.AlertType.ERROR, "Ingresa el número de tarjeta.");
                    return;
                }
                if (num.length() < 13 || !num.chars().allMatch(Character::isDigit)) {
                    alerta(Alert.AlertType.ERROR, "Número de tarjeta inválido. Debe tener entre 13 y 19 dígitos.");
                    return;
                }
            }
        }

        IMetodoPago metodo = fachada.construirMetodoPago(tipo, datos);
        boolean elegido = fachada.elegirMetodoPago(compraActual, metodo);
        if (!elegido) {
            alerta(Alert.AlertType.ERROR, "No se pudo registrar el método de pago.");
            return;
        }
        boolean ok = fachada.confirmarCompra(compraActual);
        if (ok) {
            session.getUsuarioActual().agregarCompra(compraActual);
            alerta(Alert.AlertType.INFORMATION, "¡Compra confirmada con éxito!");
            limpiarEstadoCompra();
            cargarHistorial();
            mostrar(vistaHistorial);
        } else {
            alerta(Alert.AlertType.ERROR, "El pago no pudo procesarse. Verifica los datos e inténtalo de nuevo.");
        }
    }

    private void limpiarEstadoCompra() {
        if (compraActual != null) {
            for (IEntrada e : compraActual.getEntradas()) {
                EntradaBase base = fachada.getGestorEntradas().extraerEntradaBase(e);
                if (base != null && base.getAsiento() != null) {
                    base.getAsiento().liberar();
                }
            }
        }

        eventoActual = null;
        compraActual = null;
        metodoGuardadoSeleccionado = null;
        cmbMetodoPago.setDisable(false);
        asientosSeleccionados.clear();
        botonesPorAsiento.clear();
        precioPorAsiento.clear();
        listAsientos.getItems().clear();
        panelCamposPago.getChildren().clear();
        chkVip.setSelected(false);
        chkSeguro.setSelected(false);
        chkParqueadero.setSelected(false);
        extrasTotal = 0;
    }

    private void configurarTablaEntradas() {
        colDescripcion.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().obtenerDescripcion()));
        colPrecio.setCellValueFactory(c ->
                new SimpleStringProperty("$" + formatoMoneda.format(c.getValue().calcularPrecioFinal())));
    }

    private void configurarTablaHistorial() {
        colHistEvento.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getEvento() == null ? "" : c.getValue().getEvento().getNombre()));
        colHistFecha.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getFechaCreacion() == null ? "" : c.getValue().getFechaCreacion().format(formatoFecha)));
        colHistTotal.setCellValueFactory(c ->
                new SimpleStringProperty("$" + formatoMoneda.format(c.getValue().getTotal())));
        colHistEstado.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getEstado() == null ? "" : c.getValue().getEstado().getEstadoActual().name()));
    }

    private void cargarHistorial() {
        Usuario actual = session.getUsuarioActual();
        if (actual == null) {
            return;
        }
        String filtro = cmbFiltroEstado.getValue();
        List<Compra> resultado = new ArrayList<>();
        for (Compra c : actual.getCompras()) {
            if (filtro == null || "Todos".equals(filtro)) {
                resultado.add(c);
            } else if (c.getEstado() != null && c.getEstado().getEstadoActual().name().equals(filtro)) {
                resultado.add(c);
            }
        }
        tablaHistorial.setItems(FXCollections.observableArrayList(resultado));
    }

    @FXML
    private void aplicarFiltroHistorial() {
        cargarHistorial();
    }

    @FXML
    private void cancelarCompraSeleccionada() {
        Compra seleccionada = tablaHistorial.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            alerta(Alert.AlertType.WARNING, "Selecciona una compra del historial.");
            return;
        }
        boolean ok = fachada.cancelarCompraConAuditoria(seleccionada, "Cancelado por usuario");
        if (ok) {
            cargarHistorial();
            tablaHistorial.refresh();
            alerta(Alert.AlertType.INFORMATION, "Compra cancelada.");
        } else {
            alerta(Alert.AlertType.ERROR, "No se pudo cancelar la compra en su estado actual.");
        }
    }

    @FXML
    private void descargarPdf() {
        alerta(Alert.AlertType.INFORMATION,
                "La generación de PDF se gestiona desde el panel de reportes del administrador.");
    }

    private void cargarPerfil() {
        Usuario actual = session.getUsuarioActual();
        if (actual == null) {
            return;
        }
        txtPerfilNombre.setText(actual.getNombreCompleto());
        txtPerfilCorreo.setText(actual.getCorreo());
        txtPerfilTelefono.setText(actual.getTelefono());
        refrescarMetodosPago();
    }

    private void refrescarMetodosPago() {
        listMetodosPago.getItems().clear();
        Usuario actual = session.getUsuarioActual();
        if (actual == null) {
            return;
        }
        for (MetodoPagoGuardado m : actual.getMetodosPagoGuardados()) {
            listMetodosPago.getItems().add(m.getAlias() + " - " + (m.getTipo() == null ? "" : m.getTipo().name()));
        }
    }

    @FXML
    private void guardarPerfil() {
        Usuario actual = session.getUsuarioActual();
        if (actual == null) {
            return;
        }
        actual.setNombreCompleto(txtPerfilNombre.getText());
        actual.setCorreo(txtPerfilCorreo.getText());
        actual.setTelefono(txtPerfilTelefono.getText());
        lblNombreUsuario.setText(actual.getNombreCompleto());
        alerta(Alert.AlertType.INFORMATION, "Perfil actualizado.");
    }

    @FXML
    private void agregarMetodoPago() {
        Usuario actual = session.getUsuarioActual();
        if (actual == null) return;

        ChoiceDialog<TipoMetodoPago> dialogo = new ChoiceDialog<>(
                TipoMetodoPago.TARJETA, TipoMetodoPago.values());
        dialogo.setTitle("Agregar método de pago");
        dialogo.setHeaderText("Selecciona el tipo:");
        dialogo.setContentText("Tipo:");
        Optional<TipoMetodoPago> resultado = dialogo.showAndWait();
        if (resultado.isEmpty()) return;

        TipoMetodoPago tipo = resultado.get();

        Dialog<MetodoPagoGuardado> dataDialog = new Dialog<>();
        dataDialog.setTitle("Datos del método de pago");
        dataDialog.setHeaderText("Ingresa los datos de tu " + tipo.name().toLowerCase());

        ButtonType guardarBtnType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dataDialog.getDialogPane().getButtonTypes().addAll(guardarBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfAlias = new TextField("Mi " + tipo.name().toLowerCase());
        TextField tfNumero = new TextField();
        TextField tfTitular = new TextField();
        TextField tfCvv = new TextField();
        TextField tfVencimiento = new TextField();
        TextField tfBanco = new TextField();
        TextField tfCuenta = new TextField();
        TextField tfTipoCuenta = new TextField();
        TextField tfTipoDoc = new TextField();
        TextField tfNumDoc = new TextField();
        TextField tfReferencia = new TextField();
        TextField tfPunto = new TextField();

        int row = 0;
        grid.add(new Label("Alias:"), 0, row);
        grid.add(tfAlias, 1, row++);

        switch (tipo) {
            case TARJETA:
                tfNumero.setPromptText("ej. 4111 1111 1111 1111");
                tfTitular.setPromptText("Nombre como aparece en la tarjeta");
                tfCvv.setPromptText("3 o 4 dígitos");
                tfVencimiento.setPromptText("MM/AA");
                tfBanco.setPromptText("Nombre del banco");
                grid.add(new Label("Número de tarjeta:"), 0, row); grid.add(tfNumero, 1, row++);
                grid.add(new Label("Titular:"), 0, row); grid.add(tfTitular, 1, row++);
                grid.add(new Label("CVV:"), 0, row); grid.add(tfCvv, 1, row++);
                grid.add(new Label("Vencimiento (MM/AA):"), 0, row); grid.add(tfVencimiento, 1, row++);
                grid.add(new Label("Banco:"), 0, row); grid.add(tfBanco, 1, row++);
                break;
            case PSE:
                grid.add(new Label("Banco:"), 0, row); grid.add(tfBanco, 1, row++);
                grid.add(new Label("Número de cuenta:"), 0, row); grid.add(tfCuenta, 1, row++);
                grid.add(new Label("Tipo de cuenta:"), 0, row); grid.add(tfTipoCuenta, 1, row++);
                grid.add(new Label("Tipo de documento:"), 0, row); grid.add(tfTipoDoc, 1, row++);
                grid.add(new Label("Número de documento:"), 0, row); grid.add(tfNumDoc, 1, row++);
                break;
            case EFECTIVO:
                grid.add(new Label("Referencia de pago:"), 0, row); grid.add(tfReferencia, 1, row++);
                grid.add(new Label("Punto de pago:"), 0, row); grid.add(tfPunto, 1, row++);
                break;
        }

        dataDialog.getDialogPane().setContent(grid);

        dataDialog.setResultConverter(buttonType -> {
            if (buttonType != guardarBtnType) return null;
            if (tipo == TipoMetodoPago.TARJETA) {
                String num = tfNumero.getText().replaceAll("\\s+", "");
                if (num.isEmpty() || num.length() < 13 || !num.chars().allMatch(Character::isDigit)) {
                    alerta(Alert.AlertType.ERROR, "Número de tarjeta inválido. Debe tener entre 13 y 19 dígitos.");
                    return null;
                }
                if (tfTitular.getText().isBlank()) {
                    alerta(Alert.AlertType.ERROR, "Ingresa el nombre del titular.");
                    return null;
                }
                if (tfCvv.getText().isBlank()) {
                    alerta(Alert.AlertType.ERROR, "Ingresa el CVV.");
                    return null;
                }
                if (tfVencimiento.getText().isBlank()) {
                    alerta(Alert.AlertType.ERROR, "Ingresa la fecha de vencimiento.");
                    return null;
                }
            }
            String resumenDatos;
            switch (tipo) {
                case TARJETA:
                    String num = tfNumero.getText().replaceAll("\\s+", "");
                    String ultimos4 = num.length() >= 4 ? num.substring(num.length() - 4) : num;
                    resumenDatos = "titular=" + tfTitular.getText()
                            + ";ultimos4=" + ultimos4
                            + ";vencimiento=" + tfVencimiento.getText()
                            + ";banco=" + tfBanco.getText();
                    break;
                case PSE:
                    resumenDatos = "banco=" + tfBanco.getText()
                            + ";cuenta=" + tfCuenta.getText()
                            + ";tipoCuenta=" + tfTipoCuenta.getText()
                            + ";tipoDoc=" + tfTipoDoc.getText()
                            + ";numDoc=" + tfNumDoc.getText();
                    break;
                case EFECTIVO:
                    resumenDatos = "referencia=" + tfReferencia.getText()
                            + ";punto=" + tfPunto.getText();
                    break;
                default:
                    resumenDatos = "";
            }
            String alias = tfAlias.getText().isBlank()
                    ? "Método " + (actual.getMetodosPagoGuardados().size() + 1)
                    : tfAlias.getText().trim();
            return new MetodoPagoGuardado(
                    "MP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                    alias, tipo, resumenDatos);
        });

        Optional<MetodoPagoGuardado> metodoOpt = dataDialog.showAndWait();
        metodoOpt.ifPresent(metodo -> {
            actual.agregarMetodoPago(metodo);
            refrescarMetodosPago();
            alerta(Alert.AlertType.INFORMATION, "Método de pago guardado: " + metodo.getAlias());
        });
    }

    @FXML
    private void mostrarVistaListado() {
        cargarEventos();
        mostrar(vistaListado);
    }

    @FXML
    private void mostrarVistaCarrito() {
        mostrar(vistaCarrito);
    }

    @FXML
    private void mostrarVistaHistorial() {
        cargarHistorial();
        mostrar(vistaHistorial);
    }

    @FXML
    private void mostrarVistaPerfil() {
        cargarPerfil();
        mostrar(vistaPerfil);
    }

    @FXML
    private void volverAListado() {
        mostrar(vistaListado);
    }

    @FXML
    private void volverAlAuditorio() {
        mostrar(vistaAuditorio);
    }

    @FXML
    private void aplicarFiltros() {
        cargarEventos();
    }

    @FXML
    private void cerrarSesion() {
        session.setUsuarioActual(null);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/co/edu/uniquindio/eventexpress/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblNombreUsuario.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 620));
            stage.centerOnScreen();
        } catch (IOException e) {
            alerta(Alert.AlertType.ERROR, "No se pudo volver al login.");
        }
    }

    private void mostrar(VBox vistaActiva) {
        VBox[] vistas = {vistaListado, vistaAuditorio, vistaCarrito, vistaHistorial, vistaPerfil};
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
