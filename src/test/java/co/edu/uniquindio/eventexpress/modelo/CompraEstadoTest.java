package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;
import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;
import co.edu.uniquindio.eventexpress.state.compra.EstadoCreada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CompraEstadoTest {

    Compra compra;

    @BeforeEach
    void preparar() {
        Usuario usuario = new Usuario("U1", "Carlos", "carlos@mail.co", "3001234567", "pass123", RolUsuario.CLIENTE);
        Recinto recinto = new Recinto("R1", "Estadio", "Dir", "Bogotá");
        Evento evento = new Concierto("EV1", "Concierto", "desc", "Bogotá",
                LocalDateTime.of(2026, 6, 15, 20, 0), recinto, "Juanes", "Rock");
        compra = Compra.builder()
                .usuario(usuario)
                .evento(evento)
                .estado(new EstadoCreada())
                .build();
    }

    @Test
    void creadaAPagada() {
        compra.pagar();

        assertEquals(EstadoCompra.PAGADA, compra.getEstado().getEstadoActual());
    }

    @Test
    void pagadaAConfirmada() {
        compra.pagar();
        compra.confirmar();

        assertEquals(EstadoCompra.CONFIRMADA, compra.getEstado().getEstadoActual());
    }

    @Test
    void creadaACancelada() {
        compra.cancelar();

        assertEquals(EstadoCompra.CANCELADA, compra.getEstado().getEstadoActual());
    }

    @Test
    void pagadaACancelada() {
        compra.pagar();
        compra.cancelar();

        assertEquals(EstadoCompra.CANCELADA, compra.getEstado().getEstadoActual());
    }

    @Test
    void confirmarSinPagarLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> compra.confirmar());
    }

    @Test
    void confirmadaNoSePuedePagarDeNuevo() {
        compra.pagar();
        compra.confirmar();

        assertThrows(IllegalStateException.class, () -> compra.pagar());
    }

    @Test
    void cambioDeEstadoNotificaObservadores() {
        ObservadorMock observador = new ObservadorMock();
        compra.agregarObservador(observador);
        compra.pagar();

        assertTrue(observador.fueNotificado());
    }

    static class ObservadorMock implements IObservador {
        private boolean notificado = false;

        @Override
        public void actualizar(String tipoEvento, Object datos) {
            this.notificado = true;
        }

        public boolean fueNotificado() {
            return notificado;
        }
    }
}
