package co.edu.uniquindio.eventexpress.state.evento;

import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.IEstadoEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEvento;

public class EstadoCancelado implements IEstadoEvento {

    @Override
    public void publicar(Evento evento) {
        throw new IllegalStateException("No se puede publicar un evento cancelado");
    }

    @Override
    public void pausar(Evento evento) {
        throw new IllegalStateException("No se puede pausar un evento cancelado");
    }

    @Override
    public void cancelar(Evento evento) {
    }

    @Override
    public void finalizar(Evento evento) {
        throw new IllegalStateException("No se puede finalizar un evento cancelado");
    }

    @Override
    public EstadoEvento getEstadoActual() {
        return EstadoEvento.CANCELADO;
    }
}