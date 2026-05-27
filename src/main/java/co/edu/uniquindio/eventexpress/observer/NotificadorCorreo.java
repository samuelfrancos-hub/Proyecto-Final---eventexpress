package co.edu.uniquindio.eventexpress.observer;

import co.edu.uniquindio.eventexpress.modelo.IObservador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificadorCorreo implements IObservador {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String destinatario;
    private List<String> bandejaEnviados;

    public NotificadorCorreo() {
        this.bandejaEnviados = new ArrayList<>();
    }

    public NotificadorCorreo(String destinatario) {
        this.destinatario = destinatario;
        this.bandejaEnviados = new ArrayList<>();
    }

    @Override
    public void actualizar(String evento, Object datos) {
        String mensaje = construirMensaje(evento, datos);
        bandejaEnviados.add(mensaje);
        System.out.println(mensaje);
    }

    private String construirMensaje(String evento, Object datos) {
        StringBuilder sb = new StringBuilder();
        sb.append("[CORREO] ");
        sb.append(LocalDateTime.now().format(FORMATO_FECHA));
        sb.append(" - ");
        if (destinatario != null && !destinatario.isBlank()) {
            sb.append("Para: ").append(destinatario).append(" - ");
        }
        sb.append("Asunto: ").append(evento);
        if (datos != null) {
            sb.append(" - Detalle: ").append(datos);
        }
        return sb.toString();
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public List<String> getBandejaEnviados() {
        return bandejaEnviados;
    }

    public void setBandejaEnviados(List<String> bandejaEnviados) {
        this.bandejaEnviados = bandejaEnviados;
    }
}