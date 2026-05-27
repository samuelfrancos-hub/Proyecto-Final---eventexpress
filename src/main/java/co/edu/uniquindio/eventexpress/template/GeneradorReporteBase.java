package co.edu.uniquindio.eventexpress.template;

import co.edu.uniquindio.eventexpress.adaptador.IGeneradorReporte;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public abstract class GeneradorReporteBase<T> {

    protected String titulo;

    protected GeneradorReporteBase(String titulo) {
        this.titulo = titulo;
    }

    public final void generarReporte(LocalDate desde, LocalDate hasta,
                                     IGeneradorReporte adaptador, String rutaArchivo) {
        if (adaptador == null) {
            throw new IllegalArgumentException("El adaptador de exportación no puede ser nulo");
        }
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }

        List<T> datos = obtenerDatos();
        List<T> filtrados = filtrarPorFechas(datos, desde, hasta);
        Map<String, Object> metricas = calcularMetricas(filtrados);
        List<Object[]> filas = formatearFilas(metricas);

        try {
            adaptador.exportarDatos(filas, rutaArchivo);
        } catch (IOException e) {
            throw new RuntimeException("Error al exportar el reporte: " + e.getMessage(), e);
        }
    }

    protected abstract List<T> obtenerDatos();

    protected abstract List<T> filtrarPorFechas(List<T> datos, LocalDate desde, LocalDate hasta);

    protected abstract Map<String, Object> calcularMetricas(List<T> datos);

    protected abstract List<Object[]> formatearFilas(Map<String, Object> metricas);

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}