package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;

public interface IEstadoCompra {

    void pagar(Compra compra);

    void confirmar(Compra compra);

    void cancelar(Compra compra);

    void reembolsar(Compra compra);

    void registrarIncidencia(Compra compra);

    EstadoCompra getEstadoActual();
}
