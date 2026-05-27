package co.edu.uniquindio.eventexpress.observer;

import co.edu.uniquindio.eventexpress.modelo.IObservador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificadorApp implements IObservador {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String idDispositivo;
    private List<String> notificacionesPush;
    private boolean activo;

    public NotificadorApp() {
        this.notificacionesPush = new ArrayList<>();
        this.activo = true;
    }

    public NotificadorApp(String idDispositivo) {
        this.idDispositivo = idDispositivo;
        this.notificacionesPush = new ArrayList<>();
        this.activo = true;
    }

    @Override
    public void actualizar(String evento, Object datos) {
        if (!activo) {
            return;
        }
        String mensaje = construirMensaje(evento, datos);
        notificacionesPush.add(mensaje);
        System.out.println(mensaje);
    }

    private String construirMensaje(String evento, Object datos) {
        StringBuilder sb = new StringBuilder();
        sb.append("[APP] ");
        sb.append(LocalDateTime.now().format(FORMATO_FECHA));
        if (idDispositivo != null && !idDispositivo.isBlank()) {
            sb.append(" - Dispositivo ").append(idDispositivo);
        }
        sb.append(" - Push: ").append(evento);
        if (datos != null) {
            sb.append(" (").append(datos).append(")");
        }
        return sb.toString();
    }

    public void marcarComoLeidas() {
        this.notificacionesPush.clear();
    }

    public int contarNoLeidas() {
        return notificacionesPush.size();
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(String idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public List<String> getNotificacionesPush() {
        return notificacionesPush;
    }

    public void setNotificacionesPush(List<String> notificacionesPush) {
        this.notificacionesPush = notificacionesPush;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}