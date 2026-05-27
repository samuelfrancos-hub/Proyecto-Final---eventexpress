package co.edu.uniquindio.eventexpress.decorador;

import co.edu.uniquindio.eventexpress.modelo.EntradaBase;
import co.edu.uniquindio.eventexpress.modelo.IEntrada;
import co.edu.uniquindio.eventexpress.modelo.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecoradorEntradaTest {

    Zona zona;
    EntradaBase entradaBase;

    @BeforeEach
    void preparar() {
        zona = new Zona("Z1", "VIP", 24, 100000.0);
        entradaBase = new EntradaBase("E1", zona);
    }

    @Test
    void precioBaseSinServicios() {
        assertEquals(100000.0, entradaBase.calcularPrecioFinal());
    }

    @Test
    void agregarUnServicioSumaSuCosto() {
        IEntrada conVip = new VIPDecorador(entradaBase);

        assertEquals(150000.0, conVip.calcularPrecioFinal());
    }

    @Test
    void apilarDosServicios() {
        IEntrada decorada = new SeguroCancelacionDecorador(new VIPDecorador(entradaBase));

        assertEquals(165000.0, decorada.calcularPrecioFinal());
    }

    @Test
    void cincoServiciosApilados() {
        IEntrada decorada = new AccesoPreferencialDecorador(
                new ParqueaderoDecorador(
                        new MerchandisingDecorador(
                                new SeguroCancelacionDecorador(
                                        new VIPDecorador(entradaBase)), 35000.0, "Camiseta")));

        double esperado = 100000.0 + 50000.0 + 15000.0 + 35000.0 + 12000.0 + 25000.0;
        assertEquals(esperado, decorada.calcularPrecioFinal());
    }

    @Test
    void descripcionAcumulaServicios() {
        IEntrada decorada = new SeguroCancelacionDecorador(new VIPDecorador(entradaBase));
        String descripcion = decorada.obtenerDescripcion();

        assertTrue(descripcion.contains("VIP"));
        assertTrue(descripcion.contains("Seguro"));
    }

    @Test
    void ordenDeDecoradoresNoAfectaPrecio() {
        IEntrada vipSeguro = new SeguroCancelacionDecorador(new VIPDecorador(entradaBase));
        IEntrada seguroVip = new VIPDecorador(new SeguroCancelacionDecorador(entradaBase));

        assertEquals(vipSeguro.calcularPrecioFinal(), seguroVip.calcularPrecioFinal());
    }

    @Test
    void entradaBaseImplementaIEntrada() {
        assertInstanceOf(IEntrada.class, entradaBase);
    }

    @Test
    void entradaDecoradaImplementaIEntrada() {
        IEntrada decorada = new VIPDecorador(entradaBase);

        assertInstanceOf(IEntrada.class, decorada);
    }
}
