package co.edu.uniquindio.eventexpress.decorador;

import co.edu.uniquindio.eventexpress.modelo.IEntrada;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEntrada;

public abstract class EntradaDecorador implements IEntrada {

    protected IEntrada entradaEnvuelta;

    protected EntradaDecorador(IEntrada entradaEnvuelta) {
        if (entradaEnvuelta == null) {
            throw new IllegalArgumentException("La entrada envuelta no puede ser nula");
        }
        this.entradaEnvuelta = entradaEnvuelta;
    }

    @Override
    public double calcularPrecioFinal() {
        return entradaEnvuelta.calcularPrecioFinal();
    }

    @Override
    public String obtenerDescripcion() {
        return entradaEnvuelta.obtenerDescripcion();
    }

    @Override
    public EstadoEntrada getEstado() {
        return entradaEnvuelta.getEstado();
    }

    public IEntrada getEntradaEnvuelta() {
        return entradaEnvuelta;
    }

    public void setEntradaEnvuelta(IEntrada entradaEnvuelta) {
        this.entradaEnvuelta = entradaEnvuelta;
    }
}