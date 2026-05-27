package co.edu.uniquindio.eventexpress.comando;

import co.edu.uniquindio.eventexpress.modelo.Evento;

import java.time.LocalDateTime;

public class ComandoPublicarEvento implements IComando {

    private Evento evento;
    private LocalDateTime fechaEjecucion;
    private boolean ejecutado;

    public ComandoPublicarEvento(Evento evento) {
        this.evento = evento;
        this.ejecutado = false;
    }

    @Override
    public void ejecutar() {
        if (evento == null) {
            return;
        }
        evento.publicar();
        evento.notificar("EVENTO_PUBLICADO", evento);
        this.fechaEjecucion = LocalDateTime.now();
        this.ejecutado = true;
    }

    @Override
    public String obtenerDescripcion() {
        StringBuilder sb = new StringBuilder("Publicación de evento");
        if (evento != null) {
            sb.append(" ").append(evento.getIdEvento());
            if (evento.getNombre() != null) {
                sb.append(" (").append(evento.getNombre()).append(")");
            }
        }
        return sb.toString();
    }

    @Override
    public LocalDateTime obtenerFechaEjecucion() {
        return fechaEjecucion;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public boolean isEjecutado() {
        return ejecutado;
    }
}