package co.edu.uniquindio.eventexpress.strategy;

import co.edu.uniquindio.eventexpress.modelo.IMetodoPago;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoMetodoPago;

public class PagoEfectivo implements IMetodoPago {

    private String referenciaPago;
    private double montoEntregado;
    private String puntoPago;

    public PagoEfectivo() {
    }

    public PagoEfectivo(String referenciaPago, double montoEntregado) {
        this.referenciaPago = referenciaPago;
        this.montoEntregado = montoEntregado;
    }

    public PagoEfectivo(String referenciaPago, double montoEntregado, String puntoPago) {
        this.referenciaPago = referenciaPago;
        this.montoEntregado = montoEntregado;
        this.puntoPago = puntoPago;
    }

    @Override
    public boolean procesarPago(double monto) {
        if (monto <= 0) {
            return false;
        }
        if (!validar()) {
            return false;
        }
        return montoEntregado >= monto;
    }

    @Override
    public boolean validar() {
        if (referenciaPago == null || referenciaPago.isBlank()) {
            return false;
        }
        return montoEntregado >= 0;
    }

    @Override
    public TipoMetodoPago getTipo() {
        return TipoMetodoPago.EFECTIVO;
    }

    public double calcularDevuelta(double monto) {
        if (montoEntregado < monto) {
            return 0.0;
        }
        return montoEntregado - monto;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }

    public double getMontoEntregado() {
        return montoEntregado;
    }

    public void setMontoEntregado(double montoEntregado) {
        this.montoEntregado = montoEntregado;
    }

    public String getPuntoPago() {
        return puntoPago;
    }

    public void setPuntoPago(String puntoPago) {
        this.puntoPago = puntoPago;
    }
}