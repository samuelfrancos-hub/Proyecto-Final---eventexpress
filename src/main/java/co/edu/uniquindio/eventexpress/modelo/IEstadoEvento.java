package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEvento;

public interface IEstadoEvento {

    void publicar(Evento evento);

    void pausar(Evento evento);

    void cancelar(Evento evento);

    void finalizar(Evento evento);

    EstadoEvento getEstadoActual();
}
