package co.edu.uniquindio.eventexpress.adaptador;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AdaptadorPOI implements IGeneradorReporte {

    private static final String FORMATO = "xlsx";
    private static final String NOMBRE_HOJA = "Reporte";

    @Override
    public void exportarDatos(List<Object[]> datos, String rutaArchivo) throws IOException {
        if (datos == null) {
            throw new IllegalArgumentException("Los datos no pueden ser nulos");
        }
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet(NOMBRE_HOJA);

            for (int i = 0; i < datos.size(); i++) {
                Row fila = hoja.createRow(i);
                Object[] valores = datos.get(i);
                if (valores == null) {
                    continue;
                }
                for (int j = 0; j < valores.length; j++) {
                    escribirCelda(fila.createCell(j), valores[j]);
                }
            }

            if (!datos.isEmpty() && datos.get(0) != null) {
                for (int j = 0; j < datos.get(0).length; j++) {
                    hoja.autoSizeColumn(j);
                }
            }

            try (FileOutputStream salida = new FileOutputStream(rutaArchivo)) {
                workbook.write(salida);
            }
        }
    }

    @Override
    public String getFormatoSoportado() {
        return FORMATO;
    }

    private void escribirCelda(Cell celda, Object valor) {
        if (valor == null) {
            celda.setBlank();
        } else if (valor instanceof Number) {
            celda.setCellValue(((Number) valor).doubleValue());
        } else if (valor instanceof Boolean) {
            celda.setCellValue((Boolean) valor);
        } else {
            celda.setCellValue(valor.toString());
        }
    }
}