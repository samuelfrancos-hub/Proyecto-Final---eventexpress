package co.edu.uniquindio.eventexpress.template;

import co.edu.uniquindio.eventexpress.decorador.AccesoPreferencialDecorador;
import co.edu.uniquindio.eventexpress.decorador.EntradaDecorador;
import co.edu.uniquindio.eventexpress.decorador.MerchandisingDecorador;
import co.edu.uniquindio.eventexpress.decorador.ParqueaderoDecorador;
import co.edu.uniquindio.eventexpress.decorador.SeguroCancelacionDecorador;
import co.edu.uniquindio.eventexpress.decorador.VIPDecorador;
import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.IEntrada;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteIngresoServicios extends GeneradorReporteBase<Compra> {

    private List<Compra> compras;

    public ReporteIngresoServicios(List<Compra> compras) {
        super("Reporte de Ingresos por Servicios Adicionales");
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
        Map<String, Double> acumulado = new LinkedHashMap<>();
        acumulado.put("VIP", 0.0);
        acumulado.put("Seguro Cancelación", 0.0);
        acumulado.put("Merchandising", 0.0);
        acumulado.put("Parqueadero", 0.0);
        acumulado.put("Acceso Preferencial", 0.0);

        for (Compra compra : datos) {
            for (IEntrada entrada : compra.getEntradas()) {
                acumularServicios(entrada, acumulado);
            }
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : acumulado.entrySet()) {
            resultado.put(e.getKey(), e.getValue());
        }
        return resultado;
    }

    private void acumularServicios(IEntrada entrada, Map<String, Double> acumulado) {
        IEntrada actual = entrada;
        while (actual instanceof EntradaDecorador) {
            EntradaDecorador decorador = (EntradaDecorador) actual;
            String clave = clasificar(decorador);
            double costo = obtenerCosto(decorador);
            if (clave != null) {
                acumulado.merge(clave, costo, Double::sum);
            }
            actual = decorador.getEntradaEnvuelta();
        }
    }

    private String clasificar(EntradaDecorador decorador) {
        if (decorador instanceof VIPDecorador) return "VIP";
        if (decorador instanceof SeguroCancelacionDecorador) return "Seguro Cancelación";
        if (decorador instanceof MerchandisingDecorador) return "Merchandising";
        if (decorador instanceof ParqueaderoDecorador) return "Parqueadero";
        if (decorador instanceof AccesoPreferencialDecorador) return "Acceso Preferencial";
        return null;
    }

    private double obtenerCosto(EntradaDecorador decorador) {
        if (decorador instanceof VIPDecorador) return ((VIPDecorador) decorador).getCostoAdicional();
        if (decorador instanceof SeguroCancelacionDecorador) return ((SeguroCancelacionDecorador) decorador).getCostoAdicional();
        if (decorador instanceof MerchandisingDecorador) return ((MerchandisingDecorador) decorador).getCostoAdicional();
        if (decorador instanceof ParqueaderoDecorador) return ((ParqueaderoDecorador) decorador).getCostoAdicional();
        if (decorador instanceof AccesoPreferencialDecorador) return ((AccesoPreferencialDecorador) decorador).getCostoAdicional();
        return 0.0;
    }

    @Override
    protected List<Object[]> formatearFilas(Map<String, Object> metricas) {
        List<Object[]> filas = new ArrayList<>();
        filas.add(new Object[]{titulo});
        filas.add(new Object[]{"Servicio", "Ingreso"});
        double totalGlobal = 0.0;
        for (Map.Entry<String, Object> entrada : metricas.entrySet()) {
            filas.add(new Object[]{entrada.getKey(), entrada.getValue()});
            if (entrada.getValue() instanceof Number) {
                totalGlobal += ((Number) entrada.getValue()).doubleValue();
            }
        }
        filas.add(new Object[]{"TOTAL", totalGlobal});
        return filas;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }
}