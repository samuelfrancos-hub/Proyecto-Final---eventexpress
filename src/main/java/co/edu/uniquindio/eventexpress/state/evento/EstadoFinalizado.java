package co.edu.uniquindio.eventexpress.state.evento;

import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.IEstadoEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEvento;

public class EstadoFinalizado implements IEstadoEvento {

    @Override
    public void publicar(Evento evento) {
        throw new IllegalStateException("No se puede publicar un evento finalizado");
    }

    @Override
    public void pausar(Evento evento) {
        throw new IllegalStateException("No se puede pausar un evento finalizado");
    }

    @Override
    public void cancelar(Evento evento) {
        throw new IllegalStateException("No se puede cancelar un evento finalizado");
    }

    @Override
    public void finalizar(Evento evento) {
    }

    @Override
    public EstadoEvento getEstadoActual() {
        return EstadoEvento.FINALIZADO;
    }
}