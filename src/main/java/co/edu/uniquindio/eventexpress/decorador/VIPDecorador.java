package co.edu.uniquindio.eventexpress.decorador;

import co.edu.uniquindio.eventexpress.modelo.IEntrada;

public class VIPDecorador extends EntradaDecorador {

    private static final double COSTO_VIP = 50000.0;

    private double costoAdicional;

    public VIPDecorador(IEntrada entradaEnvuelta) {
        super(entradaEnvuelta);
        this.costoAdicional = COSTO_VIP;
    }

    public VIPDecorador(IEntrada entradaEnvuelta, double costoAdicional) {
        super(entradaEnvuelta);
        this.costoAdicional = costoAdicional;
    }

    @Override
    public double calcularPrecioFinal() {
        return super.calcularPrecioFinal() + costoAdicional;
    }

    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion() + " + VIP";
    }

    public double getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(double costoAdicional) {
        this.costoAdicional = costoAdicional;
    }
}