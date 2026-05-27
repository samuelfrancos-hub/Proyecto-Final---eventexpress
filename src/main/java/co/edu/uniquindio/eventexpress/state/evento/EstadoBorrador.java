package co.edu.uniquindio.eventexpress.state.evento;

import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.IEstadoEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEvento;

public class EstadoBorrador implements IEstadoEvento {

    @Override
    public void publicar(Evento evento) {
        evento.cambiarEstado(new EstadoPublicado());
        evento.notificar("EVENTO_PUBLICADO", evento);
    }

    @Override
    public void pausar(Evento evento) {
        throw new IllegalStateException("No se puede pausar un evento que aún no se ha publicado");
    }

    @Override
    public void cancelar(Evento evento) {
        evento.cambiarEstado(new EstadoCancelado());
        evento.notificar("EVENTO_CANCELADO", evento);
    }

    @Override
    public void finalizar(Evento evento) {
        throw new IllegalStateException("No se puede finalizar un evento en borrador");
    }

    @Override
    public EstadoEvento getEstadoActual() {
        return EstadoEvento.BORRADOR;
    }
}