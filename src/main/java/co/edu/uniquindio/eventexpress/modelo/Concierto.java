package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.CategoriaEvento;

import java.time.LocalDateTime;

public class Concierto extends Evento {

    private String artistaPrincipal;
    private String generoMusical;

    public Concierto() {
        super();
        this.categoria = CategoriaEvento.CONCIERTO;
    }

    public Concierto(String idEvento, String nombre, String descripcion, String ciudad,
                     LocalDateTime fechaHora, Recinto recinto,
                     String artistaPrincipal, String generoMusical) {
        super(idEvento, nombre, CategoriaEvento.CONCIERTO, descripcion, ciudad, fechaHora, recinto);
        this.artistaPrincipal = artistaPrincipal;
        this.generoMusical = generoMusical;
    }

    @Override
    public double calcularPrecioBase() {
        if (recinto == null || recinto.getZonas() == null || recinto.getZonas().isEmpty()) {
            return 0.0;
        }
        double suma = 0.0;
        for (Zona zona : recinto.getZonas()) {
            suma += zona.getPrecioBase();
        }
        return suma / recinto.getZonas().size();
    }

    public String getArtistaPrincipal() {
        return artistaPrincipal;
    }

    public void setArtistaPrincipal(String artistaPrincipal) {
        this.artistaPrincipal = artistaPrincipal;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public void setGeneroMusical(String generoMusical) {
        this.generoMusical = generoMusical;
    }
}
