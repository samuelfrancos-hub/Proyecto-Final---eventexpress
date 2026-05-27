package co.edu.uniquindio.eventexpress.state.compra;

import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.IEstadoCompra;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;

public class EstadoCreada implements IEstadoCompra {

    @Override
    public void pagar(Compra compra) {
        compra.cambiarEstado(new EstadoPagada());
        compra.notificar("COMPRA_PAGADA", compra);
    }

    @Override
    public void confirmar(Compra compra) {
        throw new IllegalStateException("No se puede confirmar una compra que no ha sido pagada");
    }

    @Override
    public void cancelar(Compra compra) {
        compra.cambiarEstado(new EstadoCancelada());
        compra.notificar("COMPRA_CANCELADA", compra);
    }

    @Override
    public void reembolsar(Compra compra) {
        throw new IllegalStateException("No se puede reembolsar una compra que no ha sido pagada");
    }

    @Override
    public void registrarIncidencia(Compra compra) {
        compra.cambiarEstado(new EstadoIncidencia());
        compra.notificar("INCIDENCIA_REGISTRADA", compra);
    }

    @Override
    public EstadoCompra getEstadoActual() {
        return EstadoCompra.CREADA;
    }
}