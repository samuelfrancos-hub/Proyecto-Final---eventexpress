package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.EstadoEntrada;

public interface IEntrada {

    double calcularPrecioFinal();

    String obtenerDescripcion();

    EstadoEntrada getEstado();
}
