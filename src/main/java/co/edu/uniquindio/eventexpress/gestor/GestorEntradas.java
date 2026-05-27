package co.edu.uniquindio.eventexpress.gestor;

import co.edu.uniquindio.eventexpress.modelo.Asiento;
import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.EntradaBase;
import co.edu.uniquindio.eventexpress.modelo.IEntrada;
import co.edu.uniquindio.eventexpress.modelo.Zona;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GestorEntradas {

    public EntradaBase crearEntradaBase(Zona zona, Asiento asiento) {
        String id = UUID.randomUUID().toString();
        return new EntradaBase(id, zona, asiento);
    }

    public List<IEntrada> generarEntradas(Compra compra) {
        if (compra == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(compra.getEntradas());
    }

    public List<EntradaBase> extraerEntradasBase(Compra compra) {
        List<EntradaBase> resultado = new ArrayList<>();
        if (compra == null) {
            return resultado;
        }
        for (IEntrada entrada : compra.getEntradas()) {
            EntradaBase base = extraerEntradaBase(entrada);
            if (base != null) {
                resultado.add(base);
            }
        }
        return resultado;
    }

    public EntradaBase extraerEntradaBase(IEntrada entrada) {
        if (entrada == null) {
            return null;
        }
        if (entrada instanceof EntradaBase) {
            return (EntradaBase) entrada;
        }
        if (entrada instanceof co.edu.uniquindio.eventexpress.decorador.EntradaDecorador) {
            co.edu.uniquindio.eventexpress.decorador.EntradaDecorador decorador =
                    (co.edu.uniquindio.eventexpress.decorador.EntradaDecorador) entrada;
            return extraerEntradaBase(decorador.getEntradaEnvuelta());
        }
        return null;
    }
}