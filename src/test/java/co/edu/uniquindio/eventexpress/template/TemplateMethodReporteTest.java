package co.edu.uniquindio.eventexpress.template;

import co.edu.uniquindio.eventexpress.adaptador.IGeneradorReporte;
import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.Concierto;
import co.edu.uniquindio.eventexpress.modelo.EntradaBase;
import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.modelo.Usuario;
import co.edu.uniquindio.eventexpress.modelo.Zona;
import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateMethodReporteTest {

    Zona zona;
    Usuario usuario;
    Evento evento;

    @BeforeEach
    void preparar() {
        zona = new Zona("Z1", "VIP", 24, 100000.0);
        usuario = new Usuario("U1", "Carlos", "carlos@mail.co", "3001234567", "pass123", RolUsuario.CLIENTE);
        Recinto recinto = new Recinto("R1", "Estadio", "Dir", "Bogotá");
        evento = new Concierto("EV1", "Concierto", "desc", "Bogotá",
                LocalDateTime.of(2026, 6, 15, 20, 0), recinto, "Juanes", "Rock");
    }

    @Test
    void esqueletoEjecutaPasosEnOrden() {
        List<String> orden = new ArrayList<>();
        ReporteEspia reporte = new ReporteEspia(orden);
        reporte.generarReporte(null, null, new AdaptadorOrden(orden), "salida.tmp");

        assertEquals(List.of("obtener", "filtrar", "calcular", "formatear", "exportar"), orden);
    }

    @Test
    void reporteVentasCalculaTotalCorrecto() {
        Compra compra = Compra.builder()
                .usuario(usuario)
                .evento(evento)
                .agregarEntrada(new EntradaBase("E1", zona))
                .agregarEntrada(new EntradaBase("E2", zona))
                .build();
        List<Compra> compras = new ArrayList<>();
        compras.add(compra);

        ReporteVentas reporte = new ReporteVentas(compras);
        AdaptadorEspia espia = new AdaptadorEspia();
        reporte.generarReporte(null, null, espia, "salida.tmp");

        boolean contieneIngreso = espia.getFilas().stream()
                .anyMatch(fila -> fila.length >= 2
                        && "Ingreso Total".equals(fila[0])
                        && fila[1] instanceof Number
                        && ((Number) fila[1]).doubleValue() == 200000.0);
        assertTrue(contieneIngreso);
    }

    @Test
    void filtroPorRangoDejaFueraLosDeFueraDelRango() {
        Compra compra = Compra.builder().usuario(usuario).evento(evento)
                .agregarEntrada(new EntradaBase("E1", zona)).build();
        compra.setFechaCreacion(LocalDateTime.of(2026, 1, 1, 10, 0));
        List<Compra> compras = new ArrayList<>();
        compras.add(compra);

        ReporteVentas reporte = new ReporteVentas(compras);
        AdaptadorEspia espia = new AdaptadorEspia();
        reporte.generarReporte(
                java.time.LocalDate.of(2026, 6, 1),
                java.time.LocalDate.of(2026, 6, 30),
                espia, "salida.tmp");

        boolean totalCero = espia.getFilas().stream()
                .anyMatch(fila -> fila.length >= 2
                        && "Total de Compras".equals(fila[0])
                        && fila[1] instanceof Number
                        && ((Number) fila[1]).intValue() == 0);
        assertTrue(totalCero);
    }

    @Test
    void variabilidadReportesDistintosMismaEntrada() {
        Compra compra = Compra.builder().usuario(usuario).evento(evento)
                .agregarEntrada(new EntradaBase("E1", zona)).build();
        List<Compra> compras = new ArrayList<>();
        compras.add(compra);
        List<Zona> zonas = new ArrayList<>();
        zonas.add(zona);

        ReporteVentas ventas = new ReporteVentas(compras);
        ReporteOcupacionZona ocupacion = new ReporteOcupacionZona(zonas);

        AdaptadorEspia espiaVentas = new AdaptadorEspia();
        AdaptadorEspia espiaOcupacion = new AdaptadorEspia();
        ventas.generarReporte(null, null, espiaVentas, "v.tmp");
        ocupacion.generarReporte(null, null, espiaOcupacion, "o.tmp");

        assertNotEquals(ventas.getTitulo(), ocupacion.getTitulo());
        assertFalse(espiaVentas.getFilas().isEmpty());
        assertFalse(espiaOcupacion.getFilas().isEmpty());
    }

    static class ReporteEspia extends GeneradorReporteBase<String> {
        private final List<String> orden;

        ReporteEspia(List<String> orden) {
            super("Espía");
            this.orden = orden;
        }

        @Override
        protected List<String> obtenerDatos() {
            orden.add("obtener");
            return new ArrayList<>();
        }

        @Override
        protected List<String> filtrarPorFechas(List<String> datos, java.time.LocalDate desde, java.time.LocalDate hasta) {
            orden.add("filtrar");
            return datos;
        }

        @Override
        protected Map<String, Object> calcularMetricas(List<String> datos) {
            orden.add("calcular");
            return new java.util.LinkedHashMap<>();
        }

        @Override
        protected List<Object[]> formatearFilas(Map<String, Object> metricas) {
            orden.add("formatear");
            return new ArrayList<>();
        }
    }

    static class AdaptadorEspia implements IGeneradorReporte {
        private List<Object[]> filas = new ArrayList<>();

        @Override
        public void exportarDatos(List<Object[]> datos, String rutaArchivo) throws IOException {
            this.filas = datos;
        }

        @Override
        public String getFormatoSoportado() {
            return "espia";
        }

        public List<Object[]> getFilas() {
            return filas;
        }
    }

    static class AdaptadorOrden implements IGeneradorReporte {
        private final List<String> orden;

        AdaptadorOrden(List<String> orden) {
            this.orden = orden;
        }

        @Override
        public void exportarDatos(List<Object[]> datos, String rutaArchivo) throws IOException {
            orden.add("exportar");
        }

        @Override
        public String getFormatoSoportado() {
            return "orden";
        }
    }
}