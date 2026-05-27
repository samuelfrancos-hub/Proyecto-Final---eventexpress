package co.edu.uniquindio.eventexpress.controller;

import co.edu.uniquindio.eventexpress.modelo.Usuario;
import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.UUID;

public class RegistroController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    private final SessionManager sesion = SessionManager.getInstancia();

    @FXML
    private void handleCrearCuenta() {
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        String correo = txtCorreo.getText() == null ? "" : txtCorreo.getText().trim();
        String telefono = txtTelefono.getText() == null ? "" : txtTelefono.getText().trim();
        String pass = txtContrasena.getText() == null ? "" : txtContrasena.getText();

        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || pass.isEmpty()) {
            mostrarError("Todos los campos son obligatorios");
            return;
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            mostrarError("El correo no tiene un formato válido");
            return;
        }
        if (pass.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        if (sesion.buscarUsuarioPorCorreo(correo) != null) {
            mostrarError("Ya existe una cuenta con ese correo");
            return;
        }

        Usuario nuevo = new Usuario(
                "U-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                nombre, correo, telefono, pass, RolUsuario.CLIENTE);
        boolean ok = sesion.registrarUsuario(nuevo);
        if (!ok) {
            mostrarError("No se pudo crear la cuenta");
            return;
        }

        sesion.setUsuarioActual(nuevo);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/co/edu/uniquindio/eventexpress/usuario.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 620));
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarError("Error al navegar: " + e.getMessage());
        }
    }

    private void mostrarError(String texto) {
        lblError.setText(texto);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}