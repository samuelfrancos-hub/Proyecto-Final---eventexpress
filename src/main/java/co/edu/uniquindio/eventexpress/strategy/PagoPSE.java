package co.edu.uniquindio.eventexpress.strategy;

import co.edu.uniquindio.eventexpress.modelo.IMetodoPago;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoMetodoPago;

public class PagoPSE implements IMetodoPago {

    private String banco;
    private String numeroCuenta;
    private String tipoCuenta;
    private String tipoDocumento;
    private String numeroDocumento;

    public PagoPSE() {
    }

    public PagoPSE(String banco, String numeroCuenta, String tipoCuenta) {
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
    }

    public PagoPSE(String banco, String numeroCuenta, String tipoCuenta,
                   String tipoDocumento, String numeroDocumento) {
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
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
        if (banco == null || banco.isBlank()) {
            return false;
        }
        if (numeroCuenta == null || numeroCuenta.isBlank()) {
            return false;
        }
        return tipoCuenta != null && !tipoCuenta.isBlank();
    }

    @Override
    public TipoMetodoPago getTipo() {
        return TipoMetodoPago.PSE;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
}