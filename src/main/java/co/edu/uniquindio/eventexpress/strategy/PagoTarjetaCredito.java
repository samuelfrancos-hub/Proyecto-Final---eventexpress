package co.edu.uniquindio.eventexpress.strategy;

import co.edu.uniquindio.eventexpress.modelo.IMetodoPago;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoMetodoPago;

public class PagoTarjetaCredito implements IMetodoPago {

    private String numeroTarjeta;
    private String titular;
    private String cvv;
    private String fechaVencimiento;
    private String banco;

    public PagoTarjetaCredito() {
    }

    public PagoTarjetaCredito(String numeroTarjeta, String titular, String cvv, String fechaVencimiento) {
        this.numeroTarjeta = numeroTarjeta;
        this.titular = titular;
        this.cvv = cvv;
        this.fechaVencimiento = fechaVencimiento;
    }

    public PagoTarjetaCredito(String numeroTarjeta, String titular, String cvv,
                              String fechaVencimiento, String banco) {
        this.numeroTarjeta = numeroTarjeta;
        this.titular = titular;
        this.cvv = cvv;
        this.fechaVencimiento = fechaVencimiento;
        this.banco = banco;
    }

    @Override
    public boolean procesarPago(double monto) {
        if (monto <= 0) {
            return false;
        }
        if (!validar()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validar() {
        if (numeroTarjeta == null || numeroTarjeta.length() < 13 || numeroTarjeta.length() > 19) {
            return false;
        }
        if (!numeroTarjeta.chars().allMatch(Character::isDigit)) {
            return false;
        }
        if (titular == null || titular.isBlank()) {
            return false;
        }
        if (cvv == null || cvv.length() < 3 || cvv.length() > 4) {
            return false;
        }
        if (!cvv.chars().allMatch(Character::isDigit)) {
            return false;
        }
        return fechaVencimiento != null && !fechaVencimiento.isBlank();
    }

    @Override
    public TipoMetodoPago getTipo() {
        return TipoMetodoPago.TARJETA;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }
}