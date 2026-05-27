package co.edu.uniquindio.eventexpress.state.compra;

import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.IEstadoCompra;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;

public class EstadoPagada implements IEstadoCompra {

    @Override
    public void pagar(Compra compra) {
        throw new IllegalStateException("La compra ya se encuentra pagada");
    }

    @Override
    public void confirmar(Compra compra) {
        compra.cambiarEstado(new EstadoConfirmada());
        compra.notificar("COMPRA_CONFIRMADA", compra);
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
        compra.cambiarEstado(new EstadoIncidencia());
        compra.notificar("INCIDENCIA_REGISTRADA", compra);
    }

    @Override
    public EstadoCompra getEstadoActual() {
        return EstadoCompra.PAGADA;
    }
}