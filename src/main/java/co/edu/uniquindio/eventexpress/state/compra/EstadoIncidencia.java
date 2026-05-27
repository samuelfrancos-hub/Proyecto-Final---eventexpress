package co.edu.uniquindio.eventexpress.state.compra;

import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.IEstadoCompra;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;

public class EstadoIncidencia implements IEstadoCompra {

    @Override
    public void pagar(Compra compra) {
        throw new IllegalStateException("No se puede pagar una compra con incidencia activa");
    }

    @Override
    public void confirmar(Compra compra) {
        throw new IllegalStateException("No se puede confirmar una compra con incidencia activa");
    }

    @Override
    public void cancelar(Compra compra) {
        compra.cambiarEstado(new EstadoCancelada());
        compra.notificar("COMPRA_CANCELADA", compra);
    }

    @Override
    public void reembolsar(Compra compra) {
        compra.cambiarEstado(new EstadoReembolsada());
        compra.notificar("COMPRA_REEMBOLSADA", compra);
    }

    @Override
    public void registrarIncidencia(Compra compra) {
    }

    @Override
    public EstadoCompra getEstadoActual() {
        return EstadoCompra.INCIDENCIA;
    }
}