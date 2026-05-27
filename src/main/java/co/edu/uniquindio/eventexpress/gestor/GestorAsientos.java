package co.edu.uniquindio.eventexpress.gestor;

import co.edu.uniquindio.eventexpress.modelo.Asiento;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.modelo.Zona;

import java.util.List;

public class GestorAsientos {

    public boolean verificarDisponibilidad(Asiento asiento) {
        return asiento != null && asiento.estaDisponible();
    }

    public boolean verificarDisponibilidadTotal(List<Asiento> asientos) {
        if (asientos == null || asientos.isEmpty()) {
            return false;
        }
        for (Asiento asiento : asientos) {
            if (!verificarDisponibilidad(asiento)) {
                return false;
            }
        }
        return true;
    }

    public boolean reservar(Asiento asiento) {
        return asiento != null && asiento.reservar();
    }

    public boolean reservarAsientos(List<Asiento> asientos) {
        if (!verificarDisponibilidadTotal(asientos)) {
            return false;
        }
        for (Asiento asiento : asientos) {
            asiento.reservar();
        }
        return true;
    }

    public boolean vender(Asiento asiento) {
        return asiento != null && asiento.vender();
    }

    public void venderAsientos(List<Asiento> asientos) {
        if (asientos == null) {
            return;
        }
        for (Asiento asiento : asientos) {
            vender(asiento);
        }
    }

    public boolean liberar(Asiento asiento) {
        return asiento != null && asiento.liberar();
    }

    public void liberarAsientos(List<Asiento> asientos) {
        if (asientos == null) {
            return;
        }
        for (Asiento asiento : asientos) {
            liberar(asiento);
        }
    }

    public boolean bloquear(Asiento asiento) {
        return asiento != null && asiento.bloquear();
    }

    public Zona localizarZonaDeAsiento(Recinto recinto, Asiento asiento) {
        if (recinto == null || asiento == null) {
            return null;
        }
        for (Zona zona : recinto.getZonas()) {
            if (zona.getAsientos().contains(asiento)) {
                return zona;
            }
        }
        return null;
    }
}