package co.edu.uniquindio.eventexpress.gestor;

import co.edu.uniquindio.eventexpress.comando.IComando;

import java.util.ArrayList;
import java.util.List;

public class GestorComandos {

    private List<IComando> historial;

    public GestorComandos() {
        this.historial = new ArrayList<>();
    }

    public GestorComandos(List<IComando> historial) {
        this.historial = historial != null ? historial : new ArrayList<>();
    }

    public void ejecutar(IComando comando) {
        if (comando == null) {
            return;
        }
        comando.ejecutar();
        historial.add(comando);
    }

    public void ejecutarLote(List<IComando> comandos) {
        if (comandos == null) {
            return;
        }
        for (IComando comando : comandos) {
            ejecutar(comando);
        }
    }

    public List<IComando> obtenerHistorial() {
        return new ArrayList<>(historial);
    }

    public IComando obtenerUltimoComando() {
        if (historial.isEmpty()) {
            return null;
        }
        return historial.get(historial.size() - 1);
    }

    public int contarComandos() {
        return historial.size();
    }

    public void limpiarHistorial() {
        this.historial.clear();
    }

    public List<IComando> getHistorial() {
        return historial;
    }

    public void setHistorial(List<IComando> historial) {
        this.historial = historial;
    }
}