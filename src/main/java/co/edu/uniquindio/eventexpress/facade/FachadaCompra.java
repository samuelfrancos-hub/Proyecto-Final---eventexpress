package co.edu.uniquindio.eventexpress.facade;

import co.edu.uniquindio.eventexpress.adaptador.IGeneradorReporte;
import co.edu.uniquindio.eventexpress.comando.ComandoBloquearAsiento;
import co.edu.uniquindio.eventexpress.comando.ComandoCancelarCompra;
import co.edu.uniquindio.eventexpress.comando.ComandoPublicarEvento;
import co.edu.uniquindio.eventexpress.comando.ComandoReembolsarCompra;
import co.edu.uniquindio.eventexpress.comando.IComando;
import co.edu.uniquindio.eventexpress.decorador.AccesoPreferencialDecorador;
import co.edu.uniquindio.eventexpress.decorador.MerchandisingDecorador;
import co.edu.uniquindio.eventexpress.decorador.ParqueaderoDecorador;
import co.edu.uniquindio.eventexpress.decorador.SeguroCancelacionDecorador;
import co.edu.uniquindio.eventexpress.decorador.VIPDecorador;
import co.edu.uniquindio.eventexpress.fabrica.FabricaEvento;
import co.edu.uniquindio.eventexpress.gestor.GestorAsientos;
import co.edu.uniquindio.eventexpress.gestor.GestorComandos;
import co.edu.uniquindio.eventexpress.gestor.GestorEntradas;
import co.edu.uniquindio.eventexpress.gestor.GestorPagos;
import co.edu.uniquindio.eventexpress.modelo.Asiento;
import co.edu.uniquindio.eventexpress.modelo.Compra;
import co.edu.uniquindio.eventexpress.modelo.EntradaBase;
import co.edu.uniquindio.eventexpress.modelo.Evento;
import co.edu.uniquindio.eventexpress.modelo.IEntrada;
import co.edu.uniquindio.eventexpress.modelo.IMetodoPago;
import co.edu.uniquindio.eventexpress.modelo.IObservador;
import co.edu.uniquindio.eventexpress.modelo.Recinto;
import co.edu.uniquindio.eventexpress.modelo.Usuario;
import co.edu.uniquindio.eventexpress.modelo.Zona;
import co.edu.uniquindio.eventexpress.modelo.enums.CategoriaEvento;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoMetodoPago;
import co.edu.uniquindio.eventexpress.modelo.enums.TipoServicioAdicional;
import co.edu.uniquindio.eventexpress.state.compra.EstadoCreada;
import co.edu.uniquindio.eventexpress.state.evento.EstadoBorrador;
import co.edu.uniquindio.eventexpress.strategy.PagoEfectivo;
import co.edu.uniquindio.eventexpress.strategy.PagoPSE;
import co.edu.uniquindio.eventexpress.strategy.PagoTarjetaCredito;
import co.edu.uniquindio.eventexpress.template.GeneradorReporteBase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FachadaCompra {

    private GestorAsientos gestorAsientos;
    private GestorPagos gestorPagos;
    private GestorEntradas gestorEntradas;
    private GestorComandos gestorComandos;

    public FachadaCompra() {
        this.gestorAsientos = new GestorAsientos();
        this.gestorPagos = new GestorPagos();
        this.gestorEntradas = new GestorEntradas();
        this.gestorComandos = new GestorComandos();
    }

    public FachadaCompra(GestorAsientos gestorAsientos, GestorPagos gestorPagos,
                         GestorEntradas gestorEntradas, GestorComandos gestorComandos) {
        this.gestorAsientos = gestorAsientos;
        this.gestorPagos = gestorPagos;
        this.gestorEntradas = gestorEntradas;
        this.gestorComandos = gestorComandos;
    }

    public Compra iniciarCompra(Usuario usuario, Evento evento) {
        if (usuario == null || evento == null) {
            throw new IllegalArgumentException("Usuario y evento son obligatorios para iniciar la compra");
        }
        Compra compra = Compra.builder()
                .idCompra(UUID.randomUUID().toString())
                .usuario(usuario)
                .evento(evento)
                .estado(new EstadoCreada())
                .build();
        return compra;
    }

    public boolean seleccionarAsientos(Compra compra, List<Asiento> asientos) {
        if (compra == null || asientos == null || asientos.isEmpty()) {
            return false;
        }
        if (compra.getEvento() == null || compra.getEvento().getRecinto() == null) {
            return false;
        }
        if (!gestorAsientos.verificarDisponibilidadTotal(asientos)) {
            return false;
        }
        gestorAsientos.reservarAsientos(asientos);
        for (Asiento asiento : asientos) {
            Zona zona = gestorAsientos.localizarZonaDeAsiento(compra.getEvento().getRecinto(), asiento);
            EntradaBase entrada = gestorEntradas.crearEntradaBase(zona, asiento);
            compra.agregarEntrada(entrada);
        }
        return true;
    }

    public boolean agregarServicio(Compra compra, int indiceEntrada, TipoServicioAdicional servicio) {
        if (compra == null || servicio == null) {
            return false;
        }
        List<IEntrada> entradas = compra.getEntradas();
        if (indiceEntrada < 0 || indiceEntrada >= entradas.size()) {
            return false;
        }
        IEntrada original = entradas.get(indiceEntrada);
        IEntrada decorada = aplicarDecorador(original, servicio);
        entradas.set(indiceEntrada, decorada);
        compra.calcularTotal();
        return true;
    }

    public boolean agregarServicios(Compra compra, int indiceEntrada, List<TipoServicioAdicional> servicios) {
        if (servicios == null || servicios.isEmpty()) {
            return false;
        }
        boolean todosOk = true;
        for (TipoServicioAdicional servicio : servicios) {
            todosOk = agregarServicio(compra, indiceEntrada, servicio) && todosOk;
        }
        return todosOk;
    }

    public IMetodoPago construirMetodoPago(TipoMetodoPago tipo, Map<String, Object> datos) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de método de pago obligatorio");
        }
        switch (tipo) {
            case TARJETA:
                return crearPagoTarjeta(datos);
            case PSE:
                return crearPagoPSE(datos);
            case EFECTIVO:
                return crearPagoEfectivo(datos);
            default:
                throw new IllegalArgumentException("Tipo de método de pago no soportado: " + tipo);
        }
    }

    public boolean elegirMetodoPago(Compra compra, IMetodoPago metodo) {
        if (compra == null || metodo == null) {
            return false;
        }
        return gestorPagos.registrarPago(compra, metodo) != null;
    }

    public boolean confirmarCompra(Compra compra) {
        if (compra == null || compra.getPago() == null || compra.getEstado() == null) {
            return false;
        }
        boolean pagoExitoso = gestorPagos.procesarPago(compra);
        if (!pagoExitoso) {
            compra.notificar("PAGO_FALLIDO", compra);
            return false;
        }
        try {
            compra.pagar();
            for (IEntrada entrada : compra.getEntradas()) {
                EntradaBase base = gestorEntradas.extraerEntradaBase(entrada);
                if (base != null && base.getAsiento() != null) {
                    gestorAsientos.vender(base.getAsiento());
                }
            }
            gestorEntradas.generarEntradas(compra);
            compra.confirmar();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public boolean cancelarCompra(Compra compra) {
        if (compra == null || compra.getEstado() == null) {
            return false;
        }
        List<Asiento> asientosLiberar = new ArrayList<>();
        for (IEntrada entrada : compra.getEntradas()) {
            EntradaBase base = gestorEntradas.extraerEntradaBase(entrada);
            if (base != null && base.getAsiento() != null) {
                asientosLiberar.add(base.getAsiento());
            }
        }
        gestorAsientos.liberarAsientos(asientosLiberar);
        try {
            compra.cancelar();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public void registrarObservador(Compra compra, IObservador observador) {
        if (compra != null && observador != null) {
            compra.agregarObservador(observador);
        }
    }

    public void registrarObservadorEvento(Evento evento, IObservador observador) {
        if (evento != null && observador != null) {
            evento.agregarObservador(observador);
        }
    }

    public void suscribirVariosAEvento(Evento evento, List<IObservador> suscriptores) {
        if (evento == null || suscriptores == null) {
            return;
        }
        for (IObservador observador : suscriptores) {
            registrarObservadorEvento(evento, observador);
        }
    }

    public Evento crearEventoConSuscriptores(CategoriaEvento categoria, String idEvento, String nombre,
                                             String descripcion, String ciudad, LocalDateTime fechaHora,
                                             Recinto recinto, Map<String, Object> datosEspecificos,
                                             List<IObservador> suscriptores) {
        Evento evento = FabricaEvento.crearEvento(categoria, idEvento, nombre, descripcion, ciudad,
                fechaHora, recinto, datosEspecificos);
        evento.cambiarEstado(new EstadoBorrador());
        suscribirVariosAEvento(evento, suscriptores);
        evento.notificar("EVENTO_CREADO", evento);
        return evento;
    }

    public void notificarCambioEvento(Evento evento, String tipoCambio, Object datos) {
        if (evento != null && tipoCambio != null) {
            evento.notificar(tipoCambio, datos);
        }
    }

    public boolean cancelarCompraConAuditoria(Compra compra, String motivo) {
        if (compra == null) return false;

        for (IEntrada entrada : compra.getEntradas()) {
            EntradaBase base = gestorEntradas.extraerEntradaBase(entrada);
            if (base != null && base.getAsiento() != null) {
                gestorAsientos.liberar(base.getAsiento());
            }
        }
        ComandoCancelarCompra comando = new ComandoCancelarCompra(compra, motivo);
        gestorComandos.ejecutar(comando);
        return comando.isEjecutado();
    }

    public boolean publicarEventoConAuditoria(Evento evento) {
        if (evento == null) {
            return false;
        }
        ComandoPublicarEvento comando = new ComandoPublicarEvento(evento);
        gestorComandos.ejecutar(comando);
        return comando.isEjecutado();
    }

    public boolean reembolsarCompraConAuditoria(Compra compra, double monto) {
        if (compra == null) {
            return false;
        }
        ComandoReembolsarCompra comando = new ComandoReembolsarCompra(compra, monto);
        gestorComandos.ejecutar(comando);
        return comando.isEjecutado();
    }

    public boolean bloquearAsientoConAuditoria(Asiento asiento, String motivo) {
        if (asiento == null) {
            return false;
        }
        ComandoBloquearAsiento comando = new ComandoBloquearAsiento(asiento, motivo);
        gestorComandos.ejecutar(comando);
        return comando.isEjecutado();
    }

    public List<IComando> obtenerHistorialComandos() {
        return gestorComandos.obtenerHistorial();
    }

    public void generarReporte(GeneradorReporteBase<?> reporte, LocalDate desde, LocalDate hasta,
                               IGeneradorReporte adaptador, String rutaArchivo) {
        if (reporte == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo");
        }
        reporte.generarReporte(desde, hasta, adaptador, rutaArchivo);
    }

    private IEntrada aplicarDecorador(IEntrada entrada, TipoServicioAdicional servicio) {
        switch (servicio) {
            case VIP:
                return new VIPDecorador(entrada);
            case SEGURO:
                return new SeguroCancelacionDecorador(entrada);
            case MERCHANDISING:
                return new MerchandisingDecorador(entrada);
            case PARQUEADERO:
                return new ParqueaderoDecorador(entrada);
            case ACCESO_PREFERENCIAL:
                return new AccesoPreferencialDecorador(entrada);
            default:
                throw new IllegalArgumentException("Servicio adicional no soportado: " + servicio);
        }
    }

    private PagoTarjetaCredito crearPagoTarjeta(Map<String, Object> datos) {
        PagoTarjetaCredito pago = new PagoTarjetaCredito();
        if (datos == null) return pago;
        Object numero = datos.get("numeroTarjeta");
        Object titular = datos.get("titular");
        Object cvv = datos.get("cvv");
        Object fechaVencimiento = datos.get("fechaVencimiento");
        Object banco = datos.get("banco");

        if (numero != null) pago.setNumeroTarjeta(numero.toString().replaceAll("\\s+", ""));
        if (titular != null) pago.setTitular(titular.toString());
        if (cvv != null) pago.setCvv(cvv.toString().trim());
        if (fechaVencimiento != null) pago.setFechaVencimiento(fechaVencimiento.toString().trim());
        if (banco != null) pago.setBanco(banco.toString());
        return pago;
    }

    private PagoPSE crearPagoPSE(Map<String, Object> datos) {
        PagoPSE pago = new PagoPSE();
        if (datos == null) {
            return pago;
        }
        Object banco = datos.get("banco");
        Object numeroCuenta = datos.get("numeroCuenta");
        Object tipoCuenta = datos.get("tipoCuenta");
        Object tipoDocumento = datos.get("tipoDocumento");
        Object numeroDocumento = datos.get("numeroDocumento");
        if (banco != null) pago.setBanco(banco.toString());
        if (numeroCuenta != null) pago.setNumeroCuenta(numeroCuenta.toString());
        if (tipoCuenta != null) pago.setTipoCuenta(tipoCuenta.toString());
        if (tipoDocumento != null) pago.setTipoDocumento(tipoDocumento.toString());
        if (numeroDocumento != null) pago.setNumeroDocumento(numeroDocumento.toString());
        return pago;
    }

    private PagoEfectivo crearPagoEfectivo(Map<String, Object> datos) {
        PagoEfectivo pago = new PagoEfectivo();
        if (datos == null) {
            return pago;
        }
        Object referencia = datos.get("referenciaPago");
        Object monto = datos.get("montoEntregado");
        Object punto = datos.get("puntoPago");
        if (referencia != null) pago.setReferenciaPago(referencia.toString());
        if (monto instanceof Number) pago.setMontoEntregado(((Number) monto).doubleValue());
        if (punto != null) pago.setPuntoPago(punto.toString());
        return pago;
    }

    public GestorAsientos getGestorAsientos() {
        return gestorAsientos;
    }

    public void setGestorAsientos(GestorAsientos gestorAsientos) {
        this.gestorAsientos = gestorAsientos;
    }

    public GestorPagos getGestorPagos() {
        return gestorPagos;
    }

    public void setGestorPagos(GestorPagos gestorPagos) {
        this.gestorPagos = gestorPagos;
    }

    public GestorEntradas getGestorEntradas() {
        return gestorEntradas;
    }

    public void setGestorEntradas(GestorEntradas gestorEntradas) {
        this.gestorEntradas = gestorEntradas;
    }

    public GestorComandos getGestorComandos() {
        return gestorComandos;
    }

    public void setGestorComandos(GestorComandos gestorComandos) {
        this.gestorComandos = gestorComandos;
    }
}