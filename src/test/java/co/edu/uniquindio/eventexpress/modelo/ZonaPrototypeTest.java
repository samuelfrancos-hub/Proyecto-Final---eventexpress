package co.edu.uniquindio.eventexpress.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZonaPrototypeTest {

    Zona original;

    @BeforeEach
    void preparar() {
        original = new Zona("Z1", "VIP", 24, 350000.0);
        original.agregarAsiento(new Asiento("A1", "A", 1));
    }

    @Test
    void clonEsObjetoDistinto() {
        Zona clon = original.clone();

        assertNotSame(original, clon);
    }

    @Test
    void clonTieneMismosValores() {
        Zona clon = original.clone();

        assertEquals(original.getNombre(), clon.getNombre());
        assertEquals(original.getCapacidad(), clon.getCapacidad());
        assertEquals(original.getPrecioBase(), clon.getPrecioBase());
    }

    @Test
    void independenciaDelClonAlCambiarPrecio() {
        Zona clon = original.clone();
        clon.setPrecioBase(999999.0);

        assertEquals(350000.0, original.getPrecioBase());
        assertNotEquals(original.getPrecioBase(), clon.getPrecioBase());
    }

    @Test
    void listaDeAsientosEnClonEsIndependiente() {
        Zona clon = original.clone();
        clon.agregarAsiento(new Asiento("A2", "A", 2));

        assertEquals(1, original.getAsientos().size());
        assertEquals(2, clon.getAsientos().size());
    }

    @Test
    void clonarConPrecioAplicaNuevoPrecioYNoAfectaOriginal() {
        Zona clon = original.clonarConPrecio(120000.0);

        assertEquals(120000.0, clon.getPrecioBase());
        assertEquals(350000.0, original.getPrecioBase());
    }
}