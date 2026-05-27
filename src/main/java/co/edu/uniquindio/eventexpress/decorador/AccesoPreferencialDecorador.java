package co.edu.uniquindio.eventexpress.decorador;

import co.edu.uniquindio.eventexpress.modelo.IEntrada;

public class AccesoPreferencialDecorador extends EntradaDecorador {

    private static final double COSTO_ACCESO_PREFERENCIAL = 25000.0;

    private double costoAdicional;

    public AccesoPreferencialDecorador(IEntrada entradaEnvuelta) {
        super(entradaEnvuelta);
        this.costoAdicional = COSTO_ACCESO_PREFERENCIAL;
    }

    public AccesoPreferencialDecorador(IEntrada entradaEnvuelta, double costoAdicional) {
        super(entradaEnvuelta);
        this.costoAdicional = costoAdicional;
    }

    @Override
    public double calcularPrecioFinal() {
        return super.calcularPrecioFinal() + costoAdicional;
    }

    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion() + " + Acceso Preferencial";
    }

    public double getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(double costoAdicional) {
        this.costoAdicional = costoAdicional;
    }
}