package co.edu.uniquindio.eventexpress.gestor;

import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.Concierto;
import co.edu.uniquindio.eventexpress.modelo.EntradaBase;
import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.IMetodoPago;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.modelo.Usuario;
import co.edu.uniquindio.eventexpress.modelo.Zona;
import co.edu.uniquindio.eventexpress.modelo.enums.RolUsuario;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoMetodoPago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GestorPagosTest {

    GestorPagos gestor;
    Compra compra;

    @BeforeEach
    void preparar() {
        gestor = new GestorPagos();
        Usuario usuario = new Usuario("U1", "Carlos", "carlos@mail.co", "3001234567", "pass123", RolUsuario.CLIENTE);
        Recinto recinto = new Recinto("R1", "Estadio", "Dir", "Bogotá");
        Zona zona = new Zona("Z1", "VIP", 24, 100000.0);
        Evento evento = new Concierto("EV1", "Concierto", "desc", "Bogotá",
                LocalDateTime.of(2026, 6, 15, 20, 0), recinto, "Juanes", "Rock");
        compra = Compra.builder()
                .usuario(usuario)
                .evento(evento)
                .agregarEntrada(new EntradaBase("E1", zona))
                .build();
    }

    @Test
    void gestorDelegaEnLaEstrategiaInyectada() {
        EstrategiaMock mock = new EstrategiaMock(true);
        boolean resultado = gestor.procesarPago(compra, mock);

        assertTrue(resultado);
        assertTrue(mock.fueLlamado());
    }

    @Test
    void cambioDeEstrategiaUsaLaNueva() {
        EstrategiaMock exitosa = new EstrategiaMock(true);
        EstrategiaMock fallida = new EstrategiaMock(false);

        assertTrue(gestor.procesarPago(compra, exitosa));
        assertFalse(gestor.procesarPago(compra, fallida));
    }

    @Test
    void pagoFallidoSePropagaComoFalse() {
        EstrategiaMock fallida = new EstrategiaMock(false);

        assertFalse(gestor.procesarPago(compra, fallida));
    }

    @Test
    void metodoNuloRetornaFalse() {
        assertFalse(gestor.procesarPago(compra, null));
    }

    static class EstrategiaMock implements IMetodoPago {
        private final boolean exito;
        private boolean llamado = false;

        EstrategiaMock(boolean exito) {
            this.exito = exito;
        }

        @Override
        public boolean procesarPago(double monto) {
            this.llamado = true;
            return exito;
        }

        @Override
        public boolean validar() {
            return true;
        }

        @Override
        public TipoMetodoPago getTipo() {
            return TipoMetodoPago.TARJETA;
        }

        public boolean fueLlamado() {
            return llamado;
        }
    }
}