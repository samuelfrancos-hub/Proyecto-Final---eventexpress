package co.edu.uniquindio.eventexpress.adaptador;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.util.List;

public class AdaptadorPDFBox implements IGeneradorReporte {

    private static final String FORMATO = "pdf";
    private static final float MARGEN_X = 50f;
    private static final float MARGEN_Y_INICIAL = 750f;
    private static final float ESPACIADO_FILA = 18f;
    private static final float ALTURA_MINIMA = 50f;
    private static final int TAMANO_FUENTE = 10;
    private static final String SEPARADOR_COLUMNAS = " | ";

    @Override
    public void exportarDatos(List<Object[]> datos, String rutaArchivo) throws IOException {
        if (datos == null) {
            throw new IllegalArgumentException("Los datos no pueden ser nulos");
        }
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }

        try (PDDocument documento = new PDDocument()) {
            PDType1Font fuente = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            PDPageContentStream contenido = new PDPageContentStream(documento, pagina);
            contenido.beginText();
            contenido.setFont(fuente, TAMANO_FUENTE);
            contenido.newLineAtOffset(MARGEN_X, MARGEN_Y_INICIAL);

            float y = MARGEN_Y_INICIAL;

            for (Object[] fila : datos) {
                if (y < ALTURA_MINIMA) {
                    contenido.endText();
                    contenido.close();
                    pagina = new PDPage(PDRectangle.A4);
                    documento.addPage(pagina);
                    contenido = new PDPageContentStream(documento, pagina);
                    contenido.beginText();
                    contenido.setFont(fuente, TAMANO_FUENTE);
                    contenido.newLineAtOffset(MARGEN_X, MARGEN_Y_INICIAL);
                    y = MARGEN_Y_INICIAL;
                }
                contenido.showText(construirLinea(fila));
                contenido.newLineAtOffset(0, -ESPACIADO_FILA);
                y -= ESPACIADO_FILA;
            }

            contenido.endText();
            contenido.close();

            documento.save(rutaArchivo);
        }
    }

    @Override
    public String getFormatoSoportado() {
        return FORMATO;
    }

    private String construirLinea(Object[] fila) {
        if (fila == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < fila.length; j++) {
            if (j > 0) {
                sb.append(SEPARADOR_COLUMNAS);
            }
            sb.append(fila[j] == null ? "" : fila[j].toString());
        }
        return sb.toString();
    }
}