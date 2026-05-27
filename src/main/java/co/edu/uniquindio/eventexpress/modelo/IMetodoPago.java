package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.TipoMetodoPago;

public interface IMetodoPago {

    boolean procesarPago(double monto);

    boolean validar();

    TipoMetodoPago getTipo();
}
