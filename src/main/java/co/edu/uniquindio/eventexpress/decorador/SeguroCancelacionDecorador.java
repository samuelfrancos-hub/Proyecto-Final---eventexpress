package co.edu.uniquindio.eventexpress.decorador;

import co.edu.uniquindio.eventexpress.modelo.IEntrada;

public class SeguroCancelacionDecorador extends EntradaDecorador {

    private static final double COSTO_SEGURO = 15000.0;

    private double costoAdicional;

    public SeguroCancelacionDecorador(IEntrada entradaEnvuelta) {
        super(entradaEnvuelta);
        this.costoAdicional = COSTO_SEGURO;
    }

    public SeguroCancelacionDecorador(IEntrada entradaEnvuelta, double costoAdicional) {
        super(entradaEnvuelta);
        this.costoAdicional = costoAdicional;
    }

    @Override
    public double calcularPrecioFinal() {
        return super.calcularPrecioFinal() + costoAdicional;
    }

    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion() + " + Seguro de Cancelación";
    }

    public double getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(double costoAdicional) {
        this.costoAdicional = costoAdicional;
    }
}