package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.CategoriaEvento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Evento {

    protected String idEvento;
    protected String nombre;
    protected CategoriaEvento categoria;
    protected String descripcion;
    protected String ciudad;
    protected LocalDateTime fechaHora;
    protected IEstadoEvento estado;
    protected Recinto recinto;
    protected List<IObservador> observadores;

    protected Evento() {
        this.observadores = new ArrayList<>();
    }

    protected Evento(String idEvento, String nombre, CategoriaEvento categoria, String descripcion,
                     String ciudad, LocalDateTime fechaHora, Recinto recinto) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.ciudad = ciudad;
        this.fechaHora = fechaHora;
        this.recinto = recinto;
        this.observadores = new ArrayList<>();
    }

    public abstract double calcularPrecioBase();

    public void publicar() {
        if (estado != null) {
            estado.publicar(this);
        }
    }

    public void pausar() {
        if (estado != null) {
            estado.pausar(this);
        }
    }

    public void cancelar() {
        if (estado != null) {
            estado.cancelar(this);
        }
    }

    public void finalizar() {
        if (estado != null) {
            estado.finalizar(this);
        }
    }

    public void cambiarEstado(IEstadoEvento nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void agregarObservador(IObservador observador) {
        this.observadores.add(observador);
    }

    public void eliminarObservador(IObservador observador) {
        this.observadores.remove(observador);
    }

    public void notificar(String tipoEvento, Object datos) {
        for (IObservador observador : observadores) {
            observador.actualizar(tipoEvento, datos);
        }
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaEvento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaEvento categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public IEstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(IEstadoEvento estado) {
        this.estado = estado;
    }

    public Recinto getRecinto() {
        return recinto;
    }

    public void setRecinto(Recinto recinto) {
        this.recinto = recinto;
    }

    public List<IObservador> getObservadores() {
        return observadores;
    }

    public void setObservadores(List<IObservador> observadores) {
        this.observadores = observadores;
    }
}
