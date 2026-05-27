package co.edu.uniquindio.eventexpress.comando;

import co.edu.uniquindio.eventexpress.modelo.Compra;

import java.time.LocalDateTime;

public class ComandoCancelarCompra implements IComando {

    private Compra compra;
    private String motivo;
    private LocalDateTime fechaEjecucion;
    private boolean ejecutado;

    public ComandoCancelarCompra(Compra compra) {
        this.compra = compra;
        this.ejecutado = false;
    }

    public ComandoCancelarCompra(Compra compra, String motivo) {
        this.compra = compra;
        this.motivo = motivo;
        this.ejecutado = false;
    }

    @Override
    public void ejecutar() {
        if (compra == null) {
            return;
        }
        try {
            compra.cancelar();
            compra.notificar("COMPRA_CANCELADA", compra);
            this.fechaEjecucion = LocalDateTime.now();
            this.ejecutado = true;
        } catch (IllegalStateException e) {
            this.ejecutado = false;
        }
    }

    @Override
    public String obtenerDescripcion() {
        StringBuilder sb = new StringBuilder("Cancelación de compra");
        if (compra != null) {
            sb.append(" ").append(compra.getIdCompra());
        }
        if (motivo != null && !motivo.isBlank()) {
            sb.append(" - Motivo: ").append(motivo);
        }
        return sb.toString();
    }

    @Override
    public LocalDateTime obtenerFechaEjecucion() {
        return fechaEjecucion;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public boolean isEjecutado() {
        return ejecutado;
    }
}