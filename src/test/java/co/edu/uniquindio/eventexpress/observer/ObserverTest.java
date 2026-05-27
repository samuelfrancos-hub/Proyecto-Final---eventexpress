package co.edu.uniquindio.eventexpress.observer;

import co.edu.uniquindio.eventexpress.modelo.Concierto;
import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.IObservador;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.state.evento.EstadoBorrador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ObserverTest {

    Evento evento;

    @BeforeEach
    void preparar() {
        Recinto recinto = new Recinto("R1", "Estadio", "Dir", "Bogotá");
        evento = new Concierto("EV1", "Concierto", "desc", "Bogotá",
                LocalDateTime.of(2026, 6, 15, 20, 0), recinto, "Juanes", "Rock");
        evento.cambiarEstado(new EstadoBorrador());
    }

    @Test
    void notificacionAlCambiarEstado() {
        ContadorObservador obs = new ContadorObservador();
        evento.agregarObservador(obs);
        evento.publicar();

        assertEquals(1, obs.getConteo());
    }

    @Test
    void multiplesObservadoresNotificados() {
        ContadorObservador obs1 = new ContadorObservador();
        ContadorObservador obs2 = new ContadorObservador();
        ContadorObservador obs3 = new ContadorObservador();
        evento.agregarObservador(obs1);
        evento.agregarObservador(obs2);
        evento.agregarObservador(obs3);
        evento.publicar();

        assertTrue(obs1.getConteo() > 0);
        assertTrue(obs2.getConteo() > 0);
        assertTrue(obs3.getConteo() > 0);
    }

    @Test
    void desregistrarObservadorNoRecibeNotificacion() {
        ContadorObservador obs = new ContadorObservador();
        evento.agregarObservador(obs);
        evento.eliminarObservador(obs);
        evento.publicar();

        assertEquals(0, obs.getConteo());
    }

    @Test
    void listaVaciaNoLanzaError() {
        assertDoesNotThrow(() -> evento.publicar());
    }

    @Test
    void notificadoresEstandarImplementanIObservador() {
        assertInstanceOf(IObservador.class, new NotificadorApp());
        assertInstanceOf(IObservador.class, new NotificadorCorreo());
        assertInstanceOf(IObservador.class, new RegistradorIncidencias());
    }

    static class ContadorObservador implements IObservador {
        private int conteo = 0;

        @Override
        public void actualizar(String evento, Object datos) {
            this.conteo++;
        }

        public int getConteo() {
            return conteo;
        }
    }
}