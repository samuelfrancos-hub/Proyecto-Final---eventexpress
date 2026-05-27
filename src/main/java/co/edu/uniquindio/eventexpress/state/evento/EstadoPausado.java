package co.edu.uniquindio.eventexpress.state.evento;

import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.IEstadoEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEvento;

public class EstadoPausado implements IEstadoEvento {

    @Override
    public void publicar(Evento evento) {
        evento.cambiarEstado(new EstadoPublicado());
        evento.notificar("EVENTO_REANUDADO", evento);
    }

    @Override
    public void pausar(Evento evento) {
        throw new IllegalStateException("El evento ya está pausado");
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
        return EstadoEvento.PAUSADO;
    }
}