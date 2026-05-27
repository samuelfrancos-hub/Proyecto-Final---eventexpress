package co.edu.uniquindio.eventexpress.adaptador;

import java.io.IOException;
import java.util.List;

public interface IGeneradorReporte {

    void exportarDatos(List<Object[]> datos, String rutaArchivo) throws IOException;

    String getFormatoSoportado();
}