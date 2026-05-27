package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.CategoriaEvento;

import java.time.LocalDateTime;

public class Conferencia extends Evento {

    private String ponente;
    private String tema;
    private int duracionMinutos;

    public Conferencia() {
        super();
        this.categoria = CategoriaEvento.CONFERENCIA;
    }

    public Conferencia(String idEvento, String nombre, String descripcion, String ciudad,
                       LocalDateTime fechaHora, Recinto recinto,
                       String ponente, String tema, int duracionMinutos) {
        super(idEvento, nombre, CategoriaEvento.CONFERENCIA, descripcion, ciudad, fechaHora, recinto);
        this.ponente = ponente;
        this.tema = tema;
        this.duracionMinutos = duracionMinutos;
    }

    @Override
    public double calcularPrecioBase() {
        if (recinto == null || recinto.getZonas() == null || recinto.getZonas().isEmpty()) {
            return 0.0;
        }
        double suma = 0.0;
        int contador = 0;
        for (Zona zona : recinto.getZonas()) {
            suma += zona.getPrecioBase();
            contador++;
        }
        return contador == 0 ? 0.0 : (suma / contador) * 0.8;
    }

    public String getPonente() {
        return ponente;
    }

    public void setPonente(String ponente) {
        this.ponente = ponente;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
}
