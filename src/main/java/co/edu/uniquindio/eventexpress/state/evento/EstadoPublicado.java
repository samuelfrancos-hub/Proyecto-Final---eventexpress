package co.edu.uniquindio.eventexpress.state.evento;

import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.IEstadoEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEvento;

public class EstadoPublicado implements IEstadoEvento {

    @Override
    public void publicar(Evento evento) {
        throw new IllegalStateException("El evento ya se encuentra publicado");
    }

    @Override
    public void pausar(Evento evento) {
        evento.cambiarEstado(new EstadoPausado());
        evento.notificar("EVENTO_PAUSADO", evento);
    }

    @Override
    public void cancelar(Evento evento) {
        evento.cambiarEstado(new EstadoCancelado());
        evento.notificar("EVENTO_CANCELADO", evento);
    }

    @Override
    public void finalizar(Evento evento) {
        evento.cambiarEstado(new EstadoFinalizado());
        evento.notificar("EVENTO_FINALIZADO", evento);
    }

    @Override
    public EstadoEvento getEstadoActual() {
        return EstadoEvento.PUBLICADO;
    }
}