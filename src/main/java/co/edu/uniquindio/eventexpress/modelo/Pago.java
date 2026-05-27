package co.edu.uniquindio.eventexpress.modelo;

import java.time.LocalDateTime;

public class Pago {

    private String idPago;
    private double monto;
    private LocalDateTime fechaTransaccion;
    private String estado;
    private IMetodoPago metodo;

    public Pago() {
    }

    public Pago(String idPago, double monto, IMetodoPago metodo) {
        this.idPago = idPago;
        this.monto = monto;
        this.metodo = metodo;
        this.fechaTransaccion = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    public Pago(String idPago, double monto, IMetodoPago metodo, LocalDateTime fechaTransaccion, String estado) {
        this.idPago = idPago;
        this.monto = monto;
        this.metodo = metodo;
        this.fechaTransaccion = fechaTransaccion;
        this.estado = estado;
    }

    public boolean ejecutar() {
        if (metodo == null) {
            this.estado = "FALLIDO";
            return false;
        }
        boolean exito = metodo.procesarPago(monto);
        this.estado = exito ? "EXITOSO" : "FALLIDO";
        this.fechaTransaccion = LocalDateTime.now();
        return exito;
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDateTime fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public IMetodoPago getMetodo() {
        return metodo;
    }

    public void setMetodo(IMetodoPago metodo) {
        this.metodo = metodo;
    }
}
