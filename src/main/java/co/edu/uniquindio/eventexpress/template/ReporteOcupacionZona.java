package co.edu.uniquindio.eventexpress.template;

import co.edu.uniquindio.eventexpress.modelo.Zona;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteOcupacionZona extends GeneradorReporteBase<Zona> {

    private List<Zona> zonas;

    public ReporteOcupacionZona(List<Zona> zonas) {
        super("Reporte de Ocupación por Zona");
        this.zonas = zonas != null ? zonas : new ArrayList<>();
    }

    @Override
    protected List<Zona> obtenerDatos() {
        return new ArrayList<>(zonas);
    }

    @Override
    protected List<Zona> filtrarPorFechas(List<Zona> datos, LocalDate desde, LocalDate hasta) {
        return new ArrayList<>(datos);
    }

    @Override
    protected Map<String, Object> calcularMetricas(List<Zona> datos) {
        Map<String, Object> metricas = new LinkedHashMap<>();
        for (Zona zona : datos) {
            int capacidad = zona.consultarCapacidad();
            int disponibles = zona.consultarDisponibles();
            int ocupados = capacidad - disponibles;
            double porcentaje = capacidad == 0 ? 0.0 : (ocupados * 100.0 / capacidad);
            Map<String, Object> detalle = new LinkedHashMap<>();
            detalle.put("capacidad", capacidad);
            detalle.put("ocupados", ocupados);
            detalle.put("porcentaje", porcentaje);
            metricas.put(zona.getNombre() != null ? zona.getNombre() : zona.getIdZona(), detalle);
        }
        return metricas;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<Object[]> formatearFilas(Map<String, Object> metricas) {
        List<Object[]> filas = new ArrayList<>();
        filas.add(new Object[]{titulo});
        filas.add(new Object[]{"Zona", "Capacidad", "Ocupados", "% Ocupación"});
        for (Map.Entry<String, Object> entrada : metricas.entrySet()) {
            Map<String, Object> detalle = (Map<String, Object>) entrada.getValue();
            filas.add(new Object[]{
                    entrada.getKey(),
                    detalle.get("capacidad"),
                    detalle.get("ocupados"),
                    detalle.get("porcentaje")
            });
        }
        return filas;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
    }
}