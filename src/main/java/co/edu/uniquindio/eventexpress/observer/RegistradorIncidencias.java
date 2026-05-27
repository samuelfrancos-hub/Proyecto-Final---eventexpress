package co.edu.uniquindio.eventexpress.observer;

import co.edu.uniquindio.eventexpress.modelo.IObservador;
import co.edu.uniquindio.eventexpress.modelo.Incidencia;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoIncidencia;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegistradorIncidencias implements IObservador {

    private List<Incidencia> incidenciasRegistradas;

    public RegistradorIncidencias() {
        this.incidenciasRegistradas = new ArrayList<>();
    }

    public RegistradorIncidencias(List<Incidencia> incidenciasRegistradas) {
        this.incidenciasRegistradas = incidenciasRegistradas != null ? incidenciasRegistradas : new ArrayList<>();
    }

    @Override
    public void actualizar(String evento, Object datos) {
        TipoIncidencia tipo = clasificar(evento);
        if (tipo == null) {
            return;
        }
        String descripcion = "Evento detectado: " + evento + (datos != null ? " - " + datos : "");
        String entidadAfectada = datos != null ? datos.getClass().getSimpleName() : "DESCONOCIDA";
        Incidencia incidencia = new Incidencia(
                UUID.randomUUID().toString(),
                tipo,
                descripcion,
                entidadAfectada
        );
        incidenciasRegistradas.add(incidencia);
    }

    private TipoIncidencia clasificar(String evento) {
        if (evento == null) {
            return null;
        }
        String e = evento.toUpperCase();
        if (e.contains("PAGO_FALLIDO") || e.contains("ERROR_PAGO")) {
            return TipoIncidencia.ERROR_PAGO;
        }
        if (e.contains("DOBLE_COMPRA")) {
            return TipoIncidencia.DOBLE_COMPRA;
        }
        if (e.contains("CANCELACION_MASIVA")) {
            return TipoIncidencia.CANCELACION_MASIVA;
        }
        if (e.contains("FALLO") || e.contains("ERROR")) {
            return TipoIncidencia.FALLO_TECNICO;
        }
        if (e.contains("INCIDENCIA")) {
            return TipoIncidencia.OTRO;
        }
        return null;
    }

    public List<Incidencia> getIncidenciasRegistradas() {
        return incidenciasRegistradas;
    }

    public void setIncidenciasRegistradas(List<Incidencia> incidenciasRegistradas) {
        this.incidenciasRegistradas = incidenciasRegistradas;
    }

    public int contarIncidencias() {
        return incidenciasRegistradas.size();
    }

    public void limpiar() {
        this.incidenciasRegistradas.clear();
    }
}