package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CompraBuilderTest {

    Usuario usuario;
    Evento evento;
    Zona zona;

    @BeforeEach
    void preparar() {
        usuario = new Usuario("U1", "Carlos", "carlos@mail.co", "3001234567", "pass123", RolUsuario.CLIENTE);
        Recinto recinto = new Recinto("R1", "Estadio", "Dir", "Bogotá");
        zona = new Zona("Z1", "VIP", 24, 100000.0);
        evento = new Concierto("EV1", "Concierto", "desc", "Bogotá",
                LocalDateTime.of(2026, 6, 15, 20, 0), recinto, "Juanes", "Rock");
    }

    @Test
    void compraMinimaConUsuarioYEvento() {
        Compra compra = Compra.builder()
                .usuario(usuario)
                .evento(evento)
                .build();

        assertNotNull(compra);
        assertSame(usuario, compra.getUsuario());
        assertSame(evento, compra.getEvento());
    }

    @Test
    void compraSinUsuarioLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () ->
                Compra.builder().evento(evento).build());
    }

    @Test
    void compraSinEventoLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () ->
                Compra.builder().usuario(usuario).build());
    }

    @Test
    void compraConEntradasEnCadena() {
        Compra compra = Compra.builder()
                .usuario(usuario)
                .evento(evento)
                .agregarEntrada(new EntradaBase("E1", zona))
                .agregarEntrada(new EntradaBase("E2", zona))
                .build();

        assertEquals(2, compra.getEntradas().size());
    }

    @Test
    void totalCalculadoCorrectamente() {
        Compra compra = Compra.builder()
                .usuario(usuario)
                .evento(evento)
                .agregarEntrada(new EntradaBase("E1", zona))
                .agregarEntrada(new EntradaBase("E2", zona))
                .build();

        assertEquals(200000.0, compra.calcularTotal());
    }

    @Test
    void compraSinEntradasTieneListaVacia() {
        Compra compra = Compra.builder()
                .usuario(usuario)
                .evento(evento)
                .build();

        assertTrue(compra.getEntradas().isEmpty());
        assertEquals(0.0, compra.calcularTotal());
    }
}
