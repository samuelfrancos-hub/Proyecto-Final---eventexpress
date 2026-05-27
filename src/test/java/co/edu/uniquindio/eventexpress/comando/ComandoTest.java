package co.edu.uniquindio.eventexpress.comando;

import co.edu.uniquindio.eventexpress.gestor.GestorComandos;
import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.Concierto;
import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.modelo.Usuario;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;
import co.edu.uniquindio.eventexpress.state.compra.EstadoCreada;
import co.edu.uniquindio.eventexpress.state.evento.EstadoBorrador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ComandoTest {

    GestorComandos gestor;
    Compra compra;
    Evento evento;

    @BeforeEach
    void preparar() {
        gestor = new GestorComandos();
        Usuario usuario = new Usuario("U1", "Carlos", "carlos@mail.co", "3001234567", "pass123", RolUsuario.CLIENTE);
        Recinto recinto = new Recinto("R1", "Estadio", "Dir", "Bogotá");
        evento = new Concierto("EV1", "Concierto", "desc", "Bogotá",
                LocalDateTime.of(2026, 6, 15, 20, 0), recinto, "Juanes", "Rock");
        evento.cambiarEstado(new EstadoBorrador());
        compra = Compra.builder()
                .usuario(usuario)
                .evento(evento)
                .estado(new EstadoCreada())
                .build();
    }

    @Test
    void comandoCancelarCompraEjecutaCancelacion() {
        IComando comando = new ComandoCancelarCompra(compra, "Solicitud del cliente");
        comando.ejecutar();

        assertEquals(EstadoCompra.CANCELADA, compra.getEstado().getEstadoActual());
    }

    @Test
    void comandoPublicarEventoEjecutaPublicacion() {
        IComando comando = new ComandoPublicarEvento(evento);
        comando.ejecutar();

        assertEquals(EstadoEvento.PUBLICADO, evento.getEstado().getEstadoActual());
    }

    @Test
    void gestorRegistraDosComandosEnHistorial() {
        gestor.ejecutar(new ComandoPublicarEvento(evento));
        gestor.ejecutar(new ComandoCancelarCompra(compra, "motivo"));

        assertEquals(2, gestor.obtenerHistorial().size());
    }

    @Test
    void historialEnOrdenCronologico() {
        IComando primero = new ComandoPublicarEvento(evento);
        IComando segundo = new ComandoCancelarCompra(compra, "motivo");
        gestor.ejecutar(primero);
        gestor.ejecutar(segundo);

        assertSame(primero, gestor.obtenerHistorial().get(0));
        assertSame(segundo, gestor.obtenerHistorial().get(1));
    }

    @Test
    void comandoGuardaDescripcionYFecha() {
        ComandoPublicarEvento comando = new ComandoPublicarEvento(evento);
        gestor.ejecutar(comando);

        assertNotNull(comando.obtenerDescripcion());
        assertFalse(comando.obtenerDescripcion().isBlank());
        assertNotNull(comando.obtenerFechaEjecucion());
    }

    @Test
    void comandoNuloNoSeRegistra() {
        gestor.ejecutar(null);

        assertEquals(0, gestor.obtenerHistorial().size());
    }

    @Test
    void gestorComandosDependeDeInterfazNoDeImplementacion() {
        IComando comando = new ComandoPublicarEvento(evento);
        gestor.ejecutar(comando);

        assertInstanceOf(IComando.class, gestor.obtenerUltimoComando());
    }
}