package co.edu.uniquindio.eventexpress.template;

import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.enums.EstadoCompra;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteTasaCancelacion extends GeneradorReporteBase<Compra> {

    private List<Compra> compras;

    public ReporteTasaCancelacion(List<Compra> compras) {
        super("Reporte de Tasa de Cancelación");
        this.compras = compras != null ? compras : new ArrayList<>();
    }

    @Override
    protected List<Compra> obtenerDatos() {
        return new ArrayList<>(compras);
    }

    @Override
    protected List<Compra> filtrarPorFechas(List<Compra> datos, LocalDate desde, LocalDate hasta) {
        if (desde == null && hasta == null) {
            return new ArrayList<>(datos);
        }
        List<Compra> resultado = new ArrayList<>();
        for (Compra compra : datos) {
            if (compra.getFechaCreacion() == null) {
                continue;
            }
            LocalDate fecha = compra.getFechaCreacion().toLocalDate();
            if (desde != null && fecha.isBefore(desde)) {
                continue;
            }
            if (hasta != null && fecha.isAfter(hasta)) {
                continue;
            }
            resultado.add(compra);
        }
        return resultado;
    }

    @Override
    protected Map<String, Object> calcularMetricas(List<Compra> datos) {
        int total = datos.size();
        int canceladas = 0;
        int reembolsadas = 0;
        int confirmadas = 0;
        int incidencia = 0;
        for (Compra compra : datos) {
            if (compra.getEstado() == null) {
                continue;
            }
            EstadoCompra actual = compra.getEstado().getEstadoActual();
            if (actual == EstadoCompra.CANCELADA) {
                canceladas++;
            } else if (actual == EstadoCompra.REEMBOLSADA) {
                reembolsadas++;
            } else if (actual == EstadoCompra.CONFIRMADA) {
                confirmadas++;
            } else if (actual == EstadoCompra.INCIDENCIA) {
                incidencia++;
            }
        }
        double tasaCancelacion = total == 0 ? 0.0 : (canceladas * 100.0 / total);
        double tasaReembolso = total == 0 ? 0.0 : (reembolsadas * 100.0 / total);

        Map<String, Object> metricas = new LinkedHashMap<>();
        metricas.put("Total Compras", total);
        metricas.put("Confirmadas", confirmadas);
        metricas.put("Canceladas", canceladas);
        metricas.put("Reembolsadas", reembolsadas);
        metricas.put("Con Incidencia", incidencia);
        metricas.put("Tasa de Cancelación (%)", tasaCancelacion);
        metricas.put("Tasa de Reembolso (%)", tasaReembolso);
        return metricas;
    }

    @Override
    protected List<Object[]> formatearFilas(Map<String, Object> metricas) {
        List<Object[]> filas = new ArrayList<>();
        filas.add(new Object[]{titulo});
        filas.add(new Object[]{"Métrica", "Valor"});
        for (Map.Entry<String, Object> entrada : metricas.entrySet()) {
            filas.add(new Object[]{entrada.getKey(), entrada.getValue()});
        }
        return filas;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }
}