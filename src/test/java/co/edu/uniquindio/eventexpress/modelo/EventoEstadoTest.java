package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEvento;
import co.edu.uniquindio.eventexpress.state.evento.EstadoBorrador;
import co.edu.uniquindio.eventexpress.state.evento.EstadoFinalizado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventoEstadoTest {

    Evento evento;

    @BeforeEach
    void preparar() {
        Recinto recinto = new Recinto("R1", "Estadio", "Dir 1", "Bogotá");
        evento = new Concierto("EV1", "Concierto", "desc", "Bogotá",
                LocalDateTime.of(2026, 6, 15, 20, 0), recinto, "Juanes", "Rock");
        evento.cambiarEstado(new EstadoBorrador());
    }

    @Test
    void transicionBorradorAPublicado() {
        evento.publicar();

        assertEquals(EstadoEvento.PUBLICADO, evento.getEstado().getEstadoActual());
    }

    @Test
    void transicionPublicadoAPausado() {
        evento.publicar();
        evento.pausar();

        assertEquals(EstadoEvento.PAUSADO, evento.getEstado().getEstadoActual());
    }

    @Test
    void transicionPublicadoACancelado() {
        evento.publicar();
        evento.cancelar();

        assertEquals(EstadoEvento.CANCELADO, evento.getEstado().getEstadoActual());
    }

    @Test
    void transicionInvalidaCanceladoAPublicado() {
        evento.publicar();
        evento.cancelar();

        assertThrows(IllegalStateException.class, () -> evento.publicar());
    }

    @Test
    void estadoFinalizadoNoPermiteTransicion() {
        evento.cambiarEstado(new EstadoFinalizado());

        assertThrows(IllegalStateException.class, () -> evento.publicar());
        assertThrows(IllegalStateException.class, () -> evento.pausar());
        assertThrows(IllegalStateException.class, () -> evento.cancelar());
    }

    @Test
    void cancelarEventoNotificaObservadores() {
        ObservadorMock observador = new ObservadorMock();
        evento.agregarObservador(observador);
        evento.publicar();
        evento.cancelar();

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