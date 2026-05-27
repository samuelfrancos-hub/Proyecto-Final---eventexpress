package co.edu.uniquindio.eventexpress.decorador;

import co.edu.uniquindio.eventexpress.modelo.IEntrada;

public class ParqueaderoDecorador extends EntradaDecorador {

    private static final double COSTO_PARQUEADERO = 12000.0;

    private double costoAdicional;

    public ParqueaderoDecorador(IEntrada entradaEnvuelta) {
        super(entradaEnvuelta);
        this.costoAdicional = COSTO_PARQUEADERO;
    }

    public ParqueaderoDecorador(IEntrada entradaEnvuelta, double costoAdicional) {
        super(entradaEnvuelta);
        this.costoAdicional = costoAdicional;
    }

    @Override
    public double calcularPrecioFinal() {
        return super.calcularPrecioFinal() + costoAdicional;
    }

    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion() + " + Parqueadero";
    }

    public double getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(double costoAdicional) {
        this.costoAdicional = costoAdicional;
    }
}