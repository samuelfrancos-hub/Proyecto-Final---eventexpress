package co.edu.uniquindio.eventexpress.template;

import co.edu.uniquindio.eventexpress.modelo.Compra;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteVentas extends GeneradorReporteBase<Compra> {

    private List<Compra> compras;

    public ReporteVentas(List<Compra> compras) {
        super("Reporte de Ventas");
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
        Map<String, Object> metricas = new LinkedHashMap<>();
        int totalCompras = datos.size();
        double ingresoTotal = 0.0;
        for (Compra compra : datos) {
            ingresoTotal += compra.calcularTotal();
        }
        double promedio = totalCompras == 0 ? 0.0 : ingresoTotal / totalCompras;

        metricas.put("Total de Compras", totalCompras);
        metricas.put("Ingreso Total", ingresoTotal);
        metricas.put("Ticket Promedio", promedio);
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