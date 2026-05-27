package co.edu.uniquindio.eventexpress.state.compra;

import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.IEstadoCompra;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;

public class EstadoCancelada implements IEstadoCompra {

    @Override
    public void pagar(Compra compra) {
        throw new IllegalStateException("No se puede pagar una compra cancelada");
    }

    @Override
    public void confirmar(Compra compra) {
        throw new IllegalStateException("No se puede confirmar una compra cancelada");
    }

    @Override
    public void cancelar(Compra compra) {
    }

    @Override
    public void reembolsar(Compra compra) {
        throw new IllegalStateException("No se puede reembolsar una compra cancelada");
    }

    @Override
    public void registrarIncidencia(Compra compra) {
        compra.cambiarEstado(new EstadoIncidencia());
        compra.notificar("INCIDENCIA_REGISTRADA", compra);
    }

    @Override
    public EstadoCompra getEstadoActual() {
        return EstadoCompra.CANCELADA;
    }
}