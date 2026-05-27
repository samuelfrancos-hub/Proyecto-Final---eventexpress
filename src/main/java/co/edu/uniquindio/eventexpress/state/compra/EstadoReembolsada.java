package co.edu.uniquindio.eventexpress.state.compra;

import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.IEstadoCompra;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;

public class EstadoReembolsada implements IEstadoCompra {

    @Override
    public void pagar(Compra compra) {
        throw new IllegalStateException("No se puede pagar una compra reembolsada");
    }

    @Override
    public void confirmar(Compra compra) {
        throw new IllegalStateException("No se puede confirmar una compra reembolsada");
    }

    @Override
    public void cancelar(Compra compra) {
        throw new IllegalStateException("No se puede cancelar una compra reembolsada");
    }

    @Override
    public void reembolsar(Compra compra) {
    }

    @Override
    public void registrarIncidencia(Compra compra) {
        compra.cambiarEstado(new EstadoIncidencia());
        compra.notificar("INCIDENCIA_REGISTRADA", compra);
    }

    @Override
    public EstadoCompra getEstadoActual() {
        return EstadoCompra.REEMBOLSADA;
    }
}