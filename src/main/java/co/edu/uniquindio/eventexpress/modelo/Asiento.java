package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.EstadoAsiento;

public class Asiento implements IComponenteRecinto, Cloneable {

    private String idAsiento;
    private String fila;
    private int numero;
    private EstadoAsiento estado;

    public Asiento() {
        this.estado = EstadoAsiento.DISPONIBLE;
    }

    public Asiento(String idAsiento, String fila, int numero) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.estado = EstadoAsiento.DISPONIBLE;
    }

    public Asiento(String idAsiento, String fila, int numero, EstadoAsiento estado) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.estado = estado;
    }

    public boolean reservar() {
        if (this.estado == EstadoAsiento.DISPONIBLE) {
            this.estado = EstadoAsiento.RESERVADO;
            return true;
        }
        return false;
    }

    public boolean vender() {
        if (this.estado == EstadoAsiento.DISPONIBLE || this.estado == EstadoAsiento.RESERVADO) {
            this.estado = EstadoAsiento.VENDIDO;
            return true;
        }
        return false;
    }

    public boolean bloquear() {
        if (this.estado != EstadoAsiento.VENDIDO) {
            this.estado = EstadoAsiento.BLOQUEADO;
            return true;
        }
        return false;
    }

    public boolean liberar() {
        if (this.estado == EstadoAsiento.RESERVADO || this.estado == EstadoAsiento.BLOQUEADO) {
            this.estado = EstadoAsiento.DISPONIBLE;
            return true;
        }
        return false;
    }

    public boolean estaDisponible() {
        return this.estado == EstadoAsiento.DISPONIBLE;
    }

    @Override
    public int consultarCapacidad() {
        return 1;
    }

    @Override
    public int consultarDisponibles() {
        return this.estado == EstadoAsiento.DISPONIBLE ? 1 : 0;
    }

    @Override
    public Asiento clone() {
        try {
            Asiento copia = (Asiento) super.clone();
            copia.estado = EstadoAsiento.DISPONIBLE;
            return copia;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public String getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(String idAsiento) {
        this.idAsiento = idAsiento;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public EstadoAsiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsiento estado) {
        this.estado = estado;
    }
}
