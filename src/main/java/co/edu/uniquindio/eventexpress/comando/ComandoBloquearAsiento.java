package co.edu.uniquindio.eventexpress.comando;

import co.edu.uniquindio.eventexpress.modelo.Asiento;

import java.time.LocalDateTime;

public class ComandoBloquearAsiento implements IComando {

    private Asiento asiento;
    private String motivo;
    private LocalDateTime fechaEjecucion;
    private boolean ejecutado;

    public ComandoBloquearAsiento(Asiento asiento) {
        this.asiento = asiento;
        this.ejecutado = false;
    }

    public ComandoBloquearAsiento(Asiento asiento, String motivo) {
        this.asiento = asiento;
        this.motivo = motivo;
        this.ejecutado = false;
    }

    @Override
    public void ejecutar() {
        if (asiento == null) {
            return;
        }
        asiento.bloquear();
        this.fechaEjecucion = LocalDateTime.now();
        this.ejecutado = true;
    }

    @Override
    public String obtenerDescripcion() {
        StringBuilder sb = new StringBuilder("Bloqueo de asiento");
        if (asiento != null) {
            sb.append(" ").append(asiento.getIdAsiento());
            sb.append(" (Fila ").append(asiento.getFila());
            sb.append(", Número ").append(asiento.getNumero()).append(")");
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

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
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