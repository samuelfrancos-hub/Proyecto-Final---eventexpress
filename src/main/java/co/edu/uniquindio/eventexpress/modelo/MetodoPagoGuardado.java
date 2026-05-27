package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.TipoMetodoPago;

public class MetodoPagoGuardado {

    private String idMetodo;
    private String alias;
    private TipoMetodoPago tipo;
    private String datos;
    private boolean activo;

    public MetodoPagoGuardado() {
        this.activo = true;
    }

    public MetodoPagoGuardado(String idMetodo, String alias, TipoMetodoPago tipo, String datos) {
        this.idMetodo = idMetodo;
        this.alias = alias;
        this.tipo = tipo;
        this.datos = datos;
        this.activo = true;
    }

    public MetodoPagoGuardado(String idMetodo, String alias, TipoMetodoPago tipo, String datos, boolean activo) {
        this.idMetodo = idMetodo;
        this.alias = alias;
        this.tipo = tipo;
        this.datos = datos;
        this.activo = activo;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public String getIdMetodo() {
        return idMetodo;
    }

    public void setIdMetodo(String idMetodo) {
        this.idMetodo = idMetodo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public TipoMetodoPago getTipo() {
        return tipo;
    }

    public void setTipo(TipoMetodoPago tipo) {
        this.tipo = tipo;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
