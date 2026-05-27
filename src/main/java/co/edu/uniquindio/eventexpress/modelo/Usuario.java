package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String idUsuario;
    private String nombreCompleto;
    private String correo;
    private String telefono;
    private String contrasena;
    private RolUsuario rol;
    private List<MetodoPagoGuardado> metodosPagoGuardados;
    private List<Compra> compras;

    public Usuario() {
        this.metodosPagoGuardados = new ArrayList<>();
        this.compras = new ArrayList<>();
        this.rol = RolUsuario.CLIENTE;
    }

    public Usuario(String idUsuario, String nombreCompleto, String correo, String telefono, String contrasena) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.rol = RolUsuario.CLIENTE;
        this.metodosPagoGuardados = new ArrayList<>();
        this.compras = new ArrayList<>();
    }

    public Usuario(String idUsuario, String nombreCompleto, String correo, String telefono,
                   String contrasena, RolUsuario rol) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.rol = rol;
        this.metodosPagoGuardados = new ArrayList<>();
        this.compras = new ArrayList<>();
    }

    public void agregarMetodoPago(MetodoPagoGuardado metodo) {
        this.metodosPagoGuardados.add(metodo);
    }

    public boolean eliminarMetodoPago(String idMetodo) {
        return this.metodosPagoGuardados.removeIf(m -> m.getIdMetodo().equals(idMetodo));
    }

    public MetodoPagoGuardado buscarMetodoPago(String idMetodo) {
        for (MetodoPagoGuardado metodo : metodosPagoGuardados) {
            if (metodo.getIdMetodo().equals(idMetodo)) {
                return metodo;
            }
        }
        return null;
    }

    public void agregarCompra(Compra compra) {
        this.compras.add(compra);
    }

    public List<Compra> consultarHistorialCompras() {
        return new ArrayList<>(this.compras);
    }

    public boolean esAdministrador() {
        return this.rol == RolUsuario.ADMINISTRADOR;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public List<MetodoPagoGuardado> getMetodosPagoGuardados() {
        return metodosPagoGuardados;
    }

    public void setMetodosPagoGuardados(List<MetodoPagoGuardado> metodosPagoGuardados) {
        this.metodosPagoGuardados = metodosPagoGuardados;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }
}
