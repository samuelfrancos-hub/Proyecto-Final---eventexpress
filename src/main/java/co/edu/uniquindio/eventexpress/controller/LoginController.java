package co.edu.uniquindio.eventexpress.controller;

import co.edu.uniquindio.eventexpress.config.ConfiguracionPlataforma;
import co.edu.uniquindio.eventexpress.modelo.Usuario;
import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.UUID;

public class LoginController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    private final SessionManager sesion = SessionManager.getInstancia();

    @FXML
    private void initialize() {
        lblTitulo.setText(ConfiguracionPlataforma.getInstancia().getNombrePlataforma());
        ocultarError();

    }

    @FXML
    private void handleLogin() {
        String correo = txtCorreo.getText() == null ? "" : txtCorreo.getText().trim();
        String pass = txtContrasena.getText() == null ? "" : txtContrasena.getText();
        if (correo.isEmpty() || pass.isEmpty()) {
            mostrarError("Correo o contraseña incorrectos");
            return;
        }
        Usuario usuario = sesion.buscarUsuario(correo, pass);
        if (usuario == null) {
            mostrarError("Correo o contraseña incorrectos");
            return;
        }
        ocultarError();
        sesion.setUsuarioActual(usuario);
        if (usuario.esAdministrador()) {
            navegar("/co/edu/uniquindio/eventexpress/admin.fxml");
        } else {
            navegar("/co/edu/uniquindio/eventexpress/usuario.fxml");
        }
    }

    @FXML
    private void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/co/edu/uniquindio/eventexpress/registro.fxml"));
            Parent root = loader.load();
            Stage ventana = new Stage();
            ventana.setTitle("Crear cuenta - EventExpress");
            ventana.setScene(new Scene(root, 420, 480));
            ventana.centerOnScreen();
            ventana.show();
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de registro.");
        }
    }


    private void navegar(String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();
            Stage stage = (Stage) txtCorreo.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 620));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace(); // o usa un logger
            mostrarError("Error: " + e.getMessage() + (e.getCause() != null ? " | Causa: " + e.getCause().getMessage() : ""));
        }
    }

    private void mostrarError(String texto) {
        lblError.setText(texto);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    private String generarId() {
        return "U-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
