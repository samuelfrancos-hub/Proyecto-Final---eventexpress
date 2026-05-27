package co.edu.uniquindio.eventexpress.adaptador;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdapterReporteTest {

    private List<Object[]> datosPrueba() {
        List<Object[]> datos = new ArrayList<>();
        datos.add(new Object[]{"Reporte de Prueba"});
        datos.add(new Object[]{"Métrica", "Valor"});
        datos.add(new Object[]{"Total de Compras", 3});
        datos.add(new Object[]{"Ingreso Total", 450000.0});
        return datos;
    }

    @Test
    void adaptadorPoiImplementaInterfaz() {
        assertInstanceOf(IGeneradorReporte.class, new AdaptadorPOI());
    }

    @Test
    void adaptadorPdfBoxImplementaInterfaz() {
        assertInstanceOf(IGeneradorReporte.class, new AdaptadorPDFBox());
    }

    @Test
    void exportarExcelGeneraArchivoNoVacio(@TempDir Path carpeta) throws IOException {
        Path archivo = carpeta.resolve("reporte.xlsx");
        IGeneradorReporte adaptador = new AdaptadorPOI();
        adaptador.exportarDatos(datosPrueba(), archivo.toString());

        assertTrue(Files.exists(archivo));
        assertTrue(Files.size(archivo) > 0);
    }

    @Test
    void exportarPdfGeneraArchivoNoVacio(@TempDir Path carpeta) throws IOException {
        Path archivo = carpeta.resolve("reporte.pdf");
        IGeneradorReporte adaptador = new AdaptadorPDFBox();
        adaptador.exportarDatos(datosPrueba(), archivo.toString());

        assertTrue(Files.exists(archivo));
        assertTrue(Files.size(archivo) > 0);
    }

    @Test
    void cadaAdaptadorReportaSuFormato() {
        assertEquals("xlsx", new AdaptadorPOI().getFormatoSoportado());
        assertNotNull(new AdaptadorPDFBox().getFormatoSoportado());
    }
}