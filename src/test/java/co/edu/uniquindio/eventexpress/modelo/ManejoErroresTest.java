package co.edu.uniquindio.eventexpress.modelo;

import co.edu.uniquindio.eventexpress.modelo.enums.EstadoAsiento;
import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;
import co.edu.uniquindio.eventexpress.state.compra.EstadoCreada;
import co.edu.uniquindio.eventexpress.state.evento.EstadoBorrador;
import co.edu.uniquindio.eventexpress.strategy.PagoTarjetaCredito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ManejoErroresTest {

    Usuario usuario;
    Evento evento;

    @BeforeEach
    void preparar() {
        usuario = new Usuario("U1", "Carlos", "carlos@mail.co", "3001234567", "pass123", RolUsuario.CLIENTE);
        Recinto recinto = new Recinto("R1", "Estadio", "Dir", "Bogotá");
        evento = new Concierto("EV1", "Concierto", "desc", "Bogotá",
                LocalDateTime.of(2026, 6, 15, 20, 0), recinto, "Juanes", "Rock");
        evento.cambiarEstado(new EstadoBorrador());
    }

    @Test
    void reservarAsientoVendidoRetornaFalse() {
        Asiento asiento = new Asiento("A1", "A", 1);
        asiento.setEstado(EstadoAsiento.VENDIDO);

        assertFalse(asiento.reservar());
    }

    @Test
    void venderAsientoYaVendidoRetornaFalse() {
        Asiento asiento = new Asiento("A1", "A", 1);
        asiento.vender();

        assertFalse(asiento.vender());
    }

    @Test
    void bloquearAsientoVendidoRetornaFalse() {
        Asiento asiento = new Asiento("A1", "A", 1);
        asiento.vender();

        assertFalse(asiento.bloquear());
    }

    @Test
    void transicionInvalidaDeCompraLanzaIllegalState() {
        Compra compra = Compra.builder()
                .usuario(usuario)
                .evento(evento)
                .estado(new EstadoCreada())
                .build();

        assertThrows(IllegalStateException.class, () -> compra.confirmar());
    }

    @Test
    void transicionInvalidaDeEventoLanzaIllegalState() {
        evento.publicar();
        evento.cancelar();

        assertThrows(IllegalStateException.class, () -> evento.publicar());
    }

    @Test
    void compraSinUsuarioLanzaIllegalState() {
        assertThrows(IllegalStateException.class, () ->
                Compra.builder().evento(evento).build());
    }

    @Test
    void pagoConDatosInvalidosRetornaFalse() {
        PagoTarjetaCredito tarjeta = new PagoTarjetaCredito("123", "C", "1", "12/28");

        assertFalse(tarjeta.procesarPago(100000.0));
    }

    @Test
    void pagoConMontoNegativoRetornaFalse() {
        PagoTarjetaCredito tarjeta = new PagoTarjetaCredito(
                "4111111111111111", "Carlos", "123", "12/28");

        assertFalse(tarjeta.procesarPago(-5000.0));
    }
}