package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.EstadoAsiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeRecintoTest {

    Zona zona;

    @BeforeEach
    void preparar() {
        zona = new Zona("Z1", "VIP", 0, 100000.0);
    }

    @Test
    void capacidadDeAsientoEsUno() {
        Asiento asiento = new Asiento("A1", "A", 1);

        assertEquals(1, asiento.consultarCapacidad());
    }

    @Test
    void capacidadDeZonaSumaSusAsientos() {
        zona.agregarAsiento(new Asiento("A1", "A", 1));
        zona.agregarAsiento(new Asiento("A2", "A", 2));
        zona.agregarAsiento(new Asiento("A3", "A", 3));

        assertEquals(3, zona.consultarCapacidad());
    }

    @Test
    void capacidadDeRecintoSumaSusZonas() {
        zona.agregarAsiento(new Asiento("A1", "A", 1));
        zona.agregarAsiento(new Asiento("A2", "A", 2));
        Zona zona2 = new Zona("Z2", "General", 0, 50000.0);
        zona2.agregarAsiento(new Asiento("B1", "B", 1));
        Recinto recinto = new Recinto("R1", "Estadio", "Dir", "Bogotá");
        recinto.agregarZona(zona);
        recinto.agregarZona(zona2);

        assertEquals(3, recinto.consultarCapacidad());
    }

    @Test
    void disponibilidadDeAsientoSegunEstado() {
        Asiento disponible = new Asiento("A1", "A", 1);
        Asiento vendido = new Asiento("A2", "A", 2);
        vendido.setEstado(EstadoAsiento.VENDIDO);

        assertEquals(1, disponible.consultarDisponibles());
        assertEquals(0, vendido.consultarDisponibles());
    }

    @Test
    void disponibilidadDeZonaConMezcla() {
        Asiento a1 = new Asiento("A1", "A", 1);
        Asiento a2 = new Asiento("A2", "A", 2);
        Asiento a3 = new Asiento("A3", "A", 3);
        a2.setEstado(EstadoAsiento.VENDIDO);
        zona.agregarAsiento(a1);
        zona.agregarAsiento(a2);
        zona.agregarAsiento(a3);

        assertEquals(2, zona.consultarDisponibles());
    }

    @Test
    void agregarAsientoApareceEnDisponibilidad() {
        assertEquals(0, zona.consultarDisponibles());
        zona.agregarAsiento(new Asiento("A1", "A", 1));

        assertEquals(1, zona.consultarDisponibles());
    }

    @Test
    void recintoSinZonasRetornaCero() {
        Recinto recinto = new Recinto("R1", "Vacío", "Dir", "Bogotá");

        assertEquals(0, recinto.consultarCapacidad());
        assertEquals(0, recinto.consultarDisponibles());
    }
}
