package co.edu.uniquindio.eventexpress.gestor;

import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.IMetodoPago;
import co.edu.uniquindio.eventexpress.modelo.Pago;

import java.util.UUID;

public class GestorPagos {

    public Pago registrarPago(Compra compra, IMetodoPago metodo) {
        if (compra == null || metodo == null) {
            return null;
        }
        Pago pago = new Pago(UUID.randomUUID().toString(), compra.calcularTotal(), metodo);
        compra.setPago(pago);
        return pago;
    }

    public boolean procesarPago(Compra compra) {
        if (compra == null || compra.getPago() == null) {
            return false;
        }
        return compra.getPago().ejecutar();
    }

    public boolean procesarPago(Compra compra, IMetodoPago metodo) {
        if (metodo == null || !metodo.validar()) {
            return false;
        }
        registrarPago(compra, metodo);
        return procesarPago(compra);
    }
}