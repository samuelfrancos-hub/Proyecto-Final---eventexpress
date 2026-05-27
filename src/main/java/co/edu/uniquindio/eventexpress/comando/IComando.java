package co.edu.uniquindio.eventexpress.comando;

import java.time.LocalDateTime;

public interface IComando {

    void ejecutar();

    String obtenerDescripcion();

    LocalDateTime obtenerFechaEjecucion();
}