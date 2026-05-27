package co.edu.uniquindio.eventexpress.strategy;

import co.edu.uniquindio.eventexpress.modelo.IMetodoPago;
import co.edu.uniquindio.eventexpress.modelo.MetodoPagoGuardado;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoMetodoPago;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PagoStrategyTest {

    @Test
    void pagoConTarjetaExitoso() {
        IMetodoPago tarjeta = new PagoTarjetaCredito(
                "4111111111111111", "Carlos Mejía", "123", "12/28", "Bancolombia");

        assertTrue(tarjeta.procesarPago(150000.0));
    }

    @Test
    void pagoPseExitoso() {
        IMetodoPago pse = new PagoPSE("Bancolombia", "1234567890", "Ahorros");

        assertTrue(pse.procesarPago(150000.0));
    }

    @Test
    void pagoEfectivoExitoso() {
        IMetodoPago efectivo = new PagoEfectivo("REF-001", 200000.0, "Punto Centro");

        assertTrue(efectivo.procesarPago(150000.0));
    }

    @Test
    void tarjetaInvalidaRetornaFalse() {
        IMetodoPago tarjeta = new PagoTarjetaCredito("123", "Carlos", "12", "12/28", "Banco");

        assertFalse(tarjeta.procesarPago(150000.0));
    }

    @Test
    void efectivoInsuficienteRetornaFalse() {
        IMetodoPago efectivo = new PagoEfectivo("REF-002", 50000.0, "Punto Norte");

        assertFalse(efectivo.procesarPago(150000.0));
    }

    @Test
    void cadaEstrategiaReportaSuTipo() {
        assertEquals(TipoMetodoPago.TARJETA,
                new PagoTarjetaCredito("4111111111111111", "C", "123", "12/28").getTipo());
        assertEquals(TipoMetodoPago.PSE,
                new PagoPSE("Banco", "123", "Ahorros").getTipo());
        assertEquals(TipoMetodoPago.EFECTIVO,
                new PagoEfectivo("REF", 100000.0).getTipo());
    }

    @Test
    void metodoPagoGuardadoConectaConTipoDeEstrategia() {
        MetodoPagoGuardado guardado = new MetodoPagoGuardado(
                "MP1", "Mi tarjeta", TipoMetodoPago.TARJETA, "****1111");
        IMetodoPago estrategia = new PagoTarjetaCredito(
                "4111111111111111", "C", "123", "12/28");

        assertEquals(guardado.getTipo(), estrategia.getTipo());
    }
}