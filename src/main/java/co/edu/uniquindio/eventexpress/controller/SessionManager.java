package co.edu.uniquindio.eventexpress.controller;

import co.edu.uniquindio.eventexpress.modelo.Asiento;
import co.edu.uniquindio.eventexpress.modelo.Concierto;
import co.edu.uniquindio.eventexpress.modelo.Conferencia;
import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.modelo.Teatro;
import co.edu.uniquindio.eventexpress.modelo.Usuario;
import co.edu.uniquindio.eventexpress.modelo.Zona;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoAsiento;
import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;
import co.edu.uniquindio.eventexpress.state.evento.EstadoBorrador;
import co.edu.uniquindio.eventexpress.state.evento.EstadoPublicado;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager instancia;

    private Usuario usuarioActual;
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Evento> eventos = new ArrayList<>();
    private final List<Recinto> recintos = new ArrayList<>();

    private SessionManager() {
        inicializarUsuarios();
        inicializarRecintos();
        inicializarEventos();
    }

    public static SessionManager getInstancia() {
        if (instancia == null) {
            instancia = new SessionManager();
        }
        return instancia;
    }

    private void inicializarUsuarios() {
        usuarios.add(new Usuario("U1", "admin", "admin@event.co",
                "3000000000", "admin123", RolUsuario.ADMINISTRADOR));
        usuarios.add(new Usuario("U2", "Carlos Mejía", "carlos@mail.co",
                "3101234567", "pass123", RolUsuario.CLIENTE));
        usuarios.add(new Usuario("U3", "Laura Ríos", "laura@mail.co",
                "3157654321", "pass456", RolUsuario.CLIENTE));
    }

    private void inicializarRecintos() {
        recintos.add(construirEstadioCentenario());
        recintos.add(construirTeatroMunicipal());
    }

    private Recinto construirEstadioCentenario() {
        Recinto recinto = new Recinto("R1", "Estadio Centenario", "Av. Principal 100", "Bogotá");
        Zona z1 = new Zona("Z1", "VIP", 24, 350000.0);
        agregarFilaAsientos(z1, "A", 12);
        agregarFilaAsientos(z1, "B", 12);
        z1.getAsientos().get(0).setEstado(EstadoAsiento.VENDIDO);
        z1.getAsientos().get(1).setEstado(EstadoAsiento.VENDIDO);
        z1.getAsientos().get(2).setEstado(EstadoAsiento.RESERVADO);
        z1.getAsientos().get(16).setEstado(EstadoAsiento.VENDIDO);
        recinto.agregarZona(z1);
        Zona z2 = new Zona("Z2", "Preferencial", 42, 220000.0);
        agregarFilaAsientos(z2, "C", 14);
        agregarFilaAsientos(z2, "D", 14);
        agregarFilaAsientos(z2, "E", 14);
        recinto.agregarZona(z2);
        Zona z3 = new Zona("Z3", "General", 64, 120000.0);
        agregarFilaAsientos(z3, "F", 16);
        agregarFilaAsientos(z3, "G", 16);
        agregarFilaAsientos(z3, "H", 16);
        agregarFilaAsientos(z3, "I", 16);
        recinto.agregarZona(z3);
        return recinto;
    }

    private Recinto construirTeatroMunicipal() {
        Recinto recinto = new Recinto("R2", "Teatro Municipal", "Cl. 5 N. 6-25", "Cali");
        Zona z4 = new Zona("Z4", "Platea", 30, 180000.0);
        agregarFilaAsientos(z4, "A", 10);
        agregarFilaAsientos(z4, "B", 10);
        agregarFilaAsientos(z4, "C", 10);
        recinto.agregarZona(z4);
        Zona z5 = new Zona("Z5", "Balcón", 20, 90000.0);
        agregarFilaAsientos(z5, "D", 10);
        agregarFilaAsientos(z5, "E", 10);
        recinto.agregarZona(z5);
        return recinto;
    }

    private void agregarFilaAsientos(Zona zona, String fila, int cantidad) {
        for (int n = 1; n <= cantidad; n++) {
            String idAsiento = zona.getIdZona() + "-" + fila + n;
            zona.agregarAsiento(new Asiento(idAsiento, fila, n));
        }
    }

    private void inicializarEventos() {
        Concierto ev1 = new Concierto(
                "EV1", "Concierto Juanes", "Gran concierto", "Bogotá",
                LocalDateTime.of(2026, 6, 15, 20, 0),
                construirEstadioCentenario(), // ← instancia propia
                "Juanes", "Rock/Pop");
        ev1.cambiarEstado(new EstadoPublicado());
        eventos.add(ev1);

        Teatro ev2 = new Teatro(
                "EV2", "La Celestina", "Obra clásica", "Cali",
                LocalDateTime.of(2026, 6, 22, 19, 0),
                construirTeatroMunicipal(), // ← instancia propia
                "Compañía Nacional de Teatro", 120);
        ev2.cambiarEstado(new EstadoPublicado());
        eventos.add(ev2);

        Conferencia ev3 = new Conferencia(
                "EV3", "DevFest Colombia", "Conferencia tech", "Medellín",
                LocalDateTime.of(2026, 7, 3, 9, 0),
                construirEstadioCentenario(), // ← nueva instancia independiente
                "Comunidad GDG", "Desarrollo de software", 480);
        ev3.cambiarEstado(new EstadoBorrador());
        eventos.add(ev3);

    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario u) {
        this.usuarioActual = u;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public List<Recinto> getRecintos() {
        return recintos;
    }

    public Usuario buscarUsuario(String correo, String contrasena) {
        if (correo == null || contrasena == null) {
            return null;
        }
        for (Usuario u : usuarios) {
            if (correo.equals(u.getCorreo()) && contrasena.equals(u.getContrasena())) {
                return u;
            }
        }
        return null;
    }

    public boolean registrarUsuario(Usuario nuevo) {
        if (nuevo == null || nuevo.getCorreo() == null || nuevo.getCorreo().isBlank()) {
            return false;
        }
        if (buscarUsuarioPorCorreo(nuevo.getCorreo()) != null) {
            return false;
        }
        usuarios.add(nuevo);
        return true;
    }

    public Usuario buscarUsuarioPorCorreo(String correo) {
        if (correo == null) {
            return null;
        }
        for (Usuario u : usuarios) {
            if (correo.equalsIgnoreCase(u.getCorreo())) {
                return u;
            }
        }
        return null;
    }
}
