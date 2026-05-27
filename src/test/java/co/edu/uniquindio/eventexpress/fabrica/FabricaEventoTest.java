package co.edu.uniquindio.eventexpress.fabrica;

import co.edu.uniquindio.eventexpress.modelo.Concierto;
import co.edu.uniquindio.eventexpress.modelo.Conferencia;
import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.modelo.Teatro;
import co.edu.uniquindio.eventexpress.modelo.enums.CategoriaEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FabricaEventoTest {

    Recinto recinto;
    LocalDateTime fecha;
    Map<String, Object> datos;

    @BeforeEach
    void preparar() {
        recinto = new Recinto("R1", "Estadio", "Dir 1", "Bogotá");
        fecha = LocalDateTime.of(2026, 6, 15, 20, 0);
        datos = new HashMap<>();
    }

    @Test
    void tipoConciertoRetornaConcierto() {
        datos.put("artistaPrincipal", "Juanes");
        datos.put("generoMusical", "Rock");
        Evento evento = FabricaEvento.crearEvento(CategoriaEvento.CONCIERTO,
                "EV1", "Concierto", "desc", "Bogotá", fecha, recinto, datos);

        assertInstanceOf(Concierto.class, evento);
    }

    @Test
    void tipoTeatroRetornaTeatro() {
        datos.put("compania", "Compañía X");
        datos.put("duracionMinutos", 120);
        Evento evento = FabricaEvento.crearEvento(CategoriaEvento.TEATRO,
                "EV2", "Obra", "desc", "Cali", fecha, recinto, datos);

        assertInstanceOf(Teatro.class, evento);
    }

    @Test
    void tipoConferenciaRetornaConferencia() {
        datos.put("ponente", "Dra. Ríos");
        datos.put("tema", "IA");
        datos.put("duracionMinutos", 90);
        Evento evento = FabricaEvento.crearEvento(CategoriaEvento.CONFERENCIA,
                "EV3", "Charla", "desc", "Medellín", fecha, recinto, datos);

        assertInstanceOf(Conferencia.class, evento);
    }

    @Test
    void tipoNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                FabricaEvento.crearEvento(null,
                        "EV4", "X", "desc", "Bogotá", fecha, recinto, datos));
    }

    @Test
    void atributosHeredadosQuedanAsignados() {
        datos.put("artistaPrincipal", "Juanes");
        datos.put("generoMusical", "Rock");
        Evento evento = FabricaEvento.crearEvento(CategoriaEvento.CONCIERTO,
                "EV5", "Concierto Juanes", "desc", "Bogotá", fecha, recinto, datos);

        assertEquals("EV5", evento.getIdEvento());
        assertEquals("Concierto Juanes", evento.getNombre());
        assertEquals("Bogotá", evento.getCiudad());
        assertEquals(fecha, evento.getFechaHora());
    }
}
