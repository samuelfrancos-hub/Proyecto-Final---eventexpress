package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.CategoriaEvento;

import java.time.LocalDateTime;

public class Teatro extends Evento {

    private String compania;
    private int duracionMinutos;

    public Teatro() {
        super();
        this.categoria = CategoriaEvento.TEATRO;
    }

    public Teatro(String idEvento, String nombre, String descripcion, String ciudad,
                  LocalDateTime fechaHora, Recinto recinto,
                  String compania, int duracionMinutos) {
        super(idEvento, nombre, CategoriaEvento.TEATRO, descripcion, ciudad, fechaHora, recinto);
        this.compania = compania;
        this.duracionMinutos = duracionMinutos;
    }

    @Override
    public double calcularPrecioBase() {
        if (recinto == null || recinto.getZonas() == null || recinto.getZonas().isEmpty()) {
            return 0.0;
        }
        double minimo = Double.MAX_VALUE;
        for (Zona zona : recinto.getZonas()) {
            if (zona.getPrecioBase() < minimo) {
                minimo = zona.getPrecioBase();
            }
        }
        return minimo == Double.MAX_VALUE ? 0.0 : minimo;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
}
