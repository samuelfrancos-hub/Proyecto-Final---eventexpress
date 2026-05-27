package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEntrada;

public class EntradaBase implements IEntrada {

    private String idEntrada;
    private Zona zona;
    private Asiento asiento;
    private EstadoEntrada estado;

    public EntradaBase() {
        this.estado = EstadoEntrada.ACTIVA;
    }

    public EntradaBase(String idEntrada, Zona zona) {
        this.idEntrada = idEntrada;
        this.zona = zona;
        this.estado = EstadoEntrada.ACTIVA;
    }

    public EntradaBase(String idEntrada, Zona zona, Asiento asiento) {
        this.idEntrada = idEntrada;
        this.zona = zona;
        this.asiento = asiento;
        this.estado = EstadoEntrada.ACTIVA;
    }

    public EntradaBase(String idEntrada, Zona zona, Asiento asiento, EstadoEntrada estado) {
        this.idEntrada = idEntrada;
        this.zona = zona;
        this.asiento = asiento;
        this.estado = estado;
    }

    @Override
    public double calcularPrecioFinal() {
        return zona != null ? zona.getPrecioBase() : 0.0;
    }

    @Override
    public String obtenerDescripcion() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entrada ");
        if (zona != null) {
            sb.append("zona ").append(zona.getNombre());
        }
        if (asiento != null) {
            sb.append(" - fila ").append(asiento.getFila());
            sb.append(" asiento ").append(asiento.getNumero());
        }
        return sb.toString();
    }

    @Override
    public EstadoEntrada getEstado() {
        return estado;
    }

    public void marcarComoUsada() {
        this.estado = EstadoEntrada.USADA;
    }

    public void anular() {
        this.estado = EstadoEntrada.ANULADA;
    }

    public String getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(String idEntrada) {
        this.idEntrada = idEntrada;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
    }

    public void setEstado(EstadoEntrada estado) {
        this.estado = estado;
    }
}
