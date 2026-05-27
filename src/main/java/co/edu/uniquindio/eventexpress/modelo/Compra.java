package co.edu.uniquindio.eventexpress.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Compra {

    private String idCompra;
    private Usuario usuario;
    private Evento evento;
    private LocalDateTime fechaCreacion;
    private List<IEntrada> entradas;
    private Pago pago;
    private IEstadoCompra estado;
    private List<IObservador> observadores;
    private String observaciones;
    private double total;

    private Compra() {
        this.entradas = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
        this.total = 0.0;
    }

    public Compra(String idCompra, Usuario usuario, Evento evento) {
        this.idCompra = idCompra;
        this.usuario = usuario;
        this.evento = evento;
        this.fechaCreacion = LocalDateTime.now();
        this.entradas = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.total = 0.0;
    }

    public Compra(String idCompra, Usuario usuario, Evento evento, LocalDateTime fechaCreacion,
                  List<IEntrada> entradas, Pago pago, IEstadoCompra estado,
                  List<IObservador> observadores, String observaciones) {
        this.idCompra = idCompra;
        this.usuario = usuario;
        this.evento = evento;
        this.fechaCreacion = fechaCreacion != null ? fechaCreacion : LocalDateTime.now();
        this.entradas = entradas != null ? entradas : new ArrayList<>();
        this.pago = pago;
        this.estado = estado;
        this.observadores = observadores != null ? observadores : new ArrayList<>();
        this.observaciones = observaciones;
        this.total = calcularTotal();
    }

    public double calcularTotal() {
        double suma = 0.0;
        if (entradas != null) {
            for (IEntrada entrada : entradas) {
                suma += entrada.calcularPrecioFinal();
            }
        }
        this.total = suma;
        return suma;
    }

    public void agregarEntrada(IEntrada entrada) {
        this.entradas.add(entrada);
        calcularTotal();
    }

    public void eliminarEntrada(IEntrada entrada) {
        this.entradas.remove(entrada);
        calcularTotal();
    }

    public void cambiarEstado(IEstadoCompra nuevoEstado) {
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

    public void pagar() {
        if (estado != null) {
            estado.pagar(this);
        }
    }

    public void confirmar() {
        if (estado != null) {
            estado.confirmar(this);
        }
    }

    public void cancelar() {
        if (estado != null) {
            estado.cancelar(this);
        }
    }

    public void reembolsar() {
        if (estado != null) {
            estado.reembolsar(this);
        }
    }

    public void registrarIncidencia() {
        if (estado != null) {
            estado.registrarIncidencia(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(String idCompra) {
        this.idCompra = idCompra;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<IEntrada> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<IEntrada> entradas) {
        this.entradas = entradas;
        calcularTotal();
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public IEstadoCompra getEstado() {
        return estado;
    }

    public void setEstado(IEstadoCompra estado) {
        this.estado = estado;
    }

    public List<IObservador> getObservadores() {
        return observadores;
    }

    public void setObservadores(List<IObservador> observadores) {
        this.observadores = observadores;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public static class Builder {

        private String idCompra;
        private Usuario usuario;
        private Evento evento;
        private LocalDateTime fechaCreacion;
        private List<IEntrada> entradas = new ArrayList<>();
        private Pago pago;
        private IEstadoCompra estado;
        private List<IObservador> observadores = new ArrayList<>();
        private String observaciones;

        private Builder() {
        }

        public Builder idCompra(String idCompra) {
            this.idCompra = idCompra;
            return this;
        }

        public Builder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder evento(Evento evento) {
            this.evento = evento;
            return this;
        }

        public Builder fechaCreacion(LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        public Builder entradas(List<IEntrada> entradas) {
            this.entradas = new ArrayList<>(entradas);
            return this;
        }

        public Builder agregarEntrada(IEntrada entrada) {
            this.entradas.add(entrada);
            return this;
        }

        public Builder pago(Pago pago) {
            this.pago = pago;
            return this;
        }

        public Builder estado(IEstadoCompra estado) {
            this.estado = estado;
            return this;
        }

        public Builder observadores(List<IObservador> observadores) {
            this.observadores = new ArrayList<>(observadores);
            return this;
        }

        public Builder agregarObservador(IObservador observador) {
            this.observadores.add(observador);
            return this;
        }

        public Builder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        public Compra build() {
            if (usuario == null) {
                throw new IllegalStateException("La compra requiere un usuario");
            }
            if (evento == null) {
                throw new IllegalStateException("La compra requiere un evento");
            }
            return new Compra(idCompra, usuario, evento, fechaCreacion, entradas, pago, estado, observadores, observaciones);
        }
    }
}
