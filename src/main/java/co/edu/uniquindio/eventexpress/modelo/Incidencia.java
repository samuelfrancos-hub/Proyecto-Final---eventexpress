package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.TipoIncidencia;

import java.time.LocalDateTime;

public class Incidencia {

    private String idIncidencia;
    private TipoIncidencia tipo;
    private String descripcion;
    private LocalDateTime fecha;
    private String entidadAfectada;

    public Incidencia() {
    }

    public Incidencia(String idIncidencia, TipoIncidencia tipo, String descripcion, String entidadAfectada) {
        this.idIncidencia = idIncidencia;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.entidadAfectada = entidadAfectada;
        this.fecha = LocalDateTime.now();
    }

    public Incidencia(String idIncidencia, TipoIncidencia tipo, String descripcion,
                      LocalDateTime fecha, String entidadAfectada) {
        this.idIncidencia = idIncidencia;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.entidadAfectada = entidadAfectada;
    }

    public String getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(String idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public TipoIncidencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoIncidencia tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEntidadAfectada() {
        return entidadAfectada;
    }

    public void setEntidadAfectada(String entidadAfectada) {
        this.entidadAfectada = entidadAfectada;
    }
}
