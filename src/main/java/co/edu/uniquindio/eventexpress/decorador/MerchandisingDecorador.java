package co.edu.uniquindio.eventexpress.decorador;

import co.edu.uniquindio.eventexpress.modelo.IEntrada;

public class MerchandisingDecorador extends EntradaDecorador {

    private static final double COSTO_MERCHANDISING = 35000.0;

    private double costoAdicional;
    private String articulo;

    public MerchandisingDecorador(IEntrada entradaEnvuelta) {
        super(entradaEnvuelta);
        this.costoAdicional = COSTO_MERCHANDISING;
        this.articulo = "Paquete Merchandising";
    }

    public MerchandisingDecorador(IEntrada entradaEnvuelta, double costoAdicional, String articulo) {
        super(entradaEnvuelta);
        this.costoAdicional = costoAdicional;
        this.articulo = articulo;
    }

    @Override
    public double calcularPrecioFinal() {
        return super.calcularPrecioFinal() + costoAdicional;
    }

    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion() + " + Merchandising (" + articulo + ")";
    }

    public double getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(double costoAdicional) {
        this.costoAdicional = costoAdicional;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }
}