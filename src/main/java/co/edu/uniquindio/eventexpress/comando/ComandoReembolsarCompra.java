package co.edu.uniquindio.eventexpress.comando;

import co.edu.uniquindio.eventexpress.config.ConfiguracionPlataforma;
import co.edu.uniquindio.eventexpress.modelo.Compra;

import java.time.LocalDateTime;

public class ComandoReembolsarCompra implements IComando {

    private Compra compra;
    private double montoReembolso;
    private LocalDateTime fechaEjecucion;
    private boolean ejecutado;

    public ComandoReembolsarCompra(Compra compra) {
        this.compra = compra;
        this.ejecutado = false;
    }

    public ComandoReembolsarCompra(Compra compra, double montoReembolso) {
        this.compra = compra;
        this.montoReembolso = montoReembolso;
        this.ejecutado = false;
    }

    @Override
    public void ejecutar() {
        if (compra == null) {
            return;
        }
        if (montoReembolso == 0.0) {
            montoReembolso = compra.calcularTotal() * ConfiguracionPlataforma.getInstancia().getPorcentajeReembolso();
        }
        compra.reembolsar();
        compra.notificar("COMPRA_REEMBOLSADA", montoReembolso);
        this.fechaEjecucion = LocalDateTime.now();
        this.ejecutado = true;
    }

    @Override
    public String obtenerDescripcion() {
        StringBuilder sb = new StringBuilder("Reembolso de compra");
        if (compra != null) {
            sb.append(" ").append(compra.getIdCompra());
        }
        sb.append(" - Monto: ").append(montoReembolso);
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

    public double getMontoReembolso() {
        return montoReembolso;
    }

    public void setMontoReembolso(double montoReembolso) {
        this.montoReembolso = montoReembolso;
    }

    public boolean isEjecutado() {
        return ejecutado;
    }
}