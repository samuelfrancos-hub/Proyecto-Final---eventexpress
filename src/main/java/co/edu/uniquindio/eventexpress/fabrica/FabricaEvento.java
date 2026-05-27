package co.edu.uniquindio.eventexpress.fabrica;

import co.edu.uniquindio.eventexpress.modelo.Concierto;
import co.edu.uniquindio.eventexpress.modelo.Conferencia;
import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.modelo.Teatro;
import co.edu.uniquindio.eventexpress.modelo.enums.CategoriaEvento;

import java.time.LocalDateTime;
import java.util.Map;

public class FabricaEvento {

    public static Evento crearEvento(CategoriaEvento categoria,
                                     String idEvento,
                                     String nombre,
                                     String descripcion,
                                     String ciudad,
                                     LocalDateTime fechaHora,
                                     Recinto recinto,
                                     Map<String, Object> datosEspecificos) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría del evento no puede ser nula");
        }

        switch (categoria) {
            case CONCIERTO:
                return crearConcierto(idEvento, nombre, descripcion, ciudad, fechaHora, recinto, datosEspecificos);
            case TEATRO:
                return crearTeatro(idEvento, nombre, descripcion, ciudad, fechaHora, recinto, datosEspecificos);
            case CONFERENCIA:
                return crearConferencia(idEvento, nombre, descripcion, ciudad, fechaHora, recinto, datosEspecificos);
            default:
                throw new IllegalArgumentException("Categoría de evento desconocida: " + categoria);
        }
    }

    public static Concierto crearConcierto(String idEvento, String nombre, String descripcion, String ciudad,
                                           LocalDateTime fechaHora, Recinto recinto,
                                           Map<String, Object> datos) {
        String artistaPrincipal = obtenerString(datos, "artistaPrincipal", "");
        String generoMusical = obtenerString(datos, "generoMusical", "");
        return new Concierto(idEvento, nombre, descripcion, ciudad, fechaHora, recinto,
                artistaPrincipal, generoMusical);
    }

    public static Teatro crearTeatro(String idEvento, String nombre, String descripcion, String ciudad,
                                     LocalDateTime fechaHora, Recinto recinto,
                                     Map<String, Object> datos) {
        String compania = obtenerString(datos, "compania", "");
        int duracion = obtenerEntero(datos, "duracionMinutos", 0);
        return new Teatro(idEvento, nombre, descripcion, ciudad, fechaHora, recinto,
                compania, duracion);
    }

    public static Conferencia crearConferencia(String idEvento, String nombre, String descripcion, String ciudad,
                                               LocalDateTime fechaHora, Recinto recinto,
                                               Map<String, Object> datos) {
        String ponente = obtenerString(datos, "ponente", "");
        String tema = obtenerString(datos, "tema", "");
        int duracion = obtenerEntero(datos, "duracionMinutos", 0);
        return new Conferencia(idEvento, nombre, descripcion, ciudad, fechaHora, recinto,
                ponente, tema, duracion);
    }

    private static String obtenerString(Map<String, Object> datos, String clave, String porDefecto) {
        if (datos == null) {
            return porDefecto;
        }
        Object valor = datos.get(clave);
        return valor instanceof String ? (String) valor : porDefecto;
    }

    private static int obtenerEntero(Map<String, Object> datos, String clave, int porDefecto) {
        if (datos == null) {
            return porDefecto;
        }
        Object valor = datos.get(clave);
        if (valor instanceof Integer) {
            return (Integer) valor;
        }
        if (valor instanceof Number) {
            return ((Number) valor).intValue();
        }
        return porDefecto;
    }
}
