package co.edu.uniquindio.eventexpress.config;

public class ConfiguracionPlataforma {

    private static volatile ConfiguracionPlataforma instancia;

    private String nombrePlataforma;
    private String version;
    private double porcentajeReembolso;
    private int tiempoLimiteCancelacionHoras;
    private String politicaCancelacion;
    private String politicaReembolso;
    private String mensajeBienvenida;
    private String monedaPredeterminada;

    private ConfiguracionPlataforma() {
        if (instancia != null) {
            throw new IllegalStateException("ConfiguracionPlataforma ya ha sido inicializada. Use getInstancia().");
        }
        this.nombrePlataforma = "EventExpress";
        this.version = "1.0.0";
        this.porcentajeReembolso = 0.85;
        this.tiempoLimiteCancelacionHoras = 24;
        this.politicaCancelacion = "Cancelación gratuita hasta 24 horas antes del evento.";
        this.politicaReembolso = "Reembolso del 85% del valor pagado dentro del plazo permitido.";
        this.mensajeBienvenida = "Bienvenido a EventExpress";
        this.monedaPredeterminada = "COP";
    }

    public static ConfiguracionPlataforma getInstancia() {
        if (instancia == null) {
            synchronized (ConfiguracionPlataforma.class) {
                if (instancia == null) {
                    instancia = new ConfiguracionPlataforma();
                }
            }
        }
        return instancia;
    }

    public String getNombrePlataforma() {
        return nombrePlataforma;
    }

    public void setNombrePlataforma(String nombrePlataforma) {
        this.nombrePlataforma = nombrePlataforma;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public double getPorcentajeReembolso() {
        return porcentajeReembolso;
    }

    public void setPorcentajeReembolso(double porcentajeReembolso) {
        this.porcentajeReembolso = porcentajeReembolso;
    }

    public int getTiempoLimiteCancelacionHoras() {
        return tiempoLimiteCancelacionHoras;
    }

    public void setTiempoLimiteCancelacionHoras(int tiempoLimiteCancelacionHoras) {
        this.tiempoLimiteCancelacionHoras = tiempoLimiteCancelacionHoras;
    }

    public String getPoliticaCancelacion() {
        return politicaCancelacion;
    }

    public void setPoliticaCancelacion(String politicaCancelacion) {
        this.politicaCancelacion = politicaCancelacion;
    }

    public String getPoliticaReembolso() {
        return politicaReembolso;
    }

    public void setPoliticaReembolso(String politicaReembolso) {
        this.politicaReembolso = politicaReembolso;
    }

    public String getMensajeBienvenida() {
        return mensajeBienvenida;
    }

    public void setMensajeBienvenida(String mensajeBienvenida) {
        this.mensajeBienvenida = mensajeBienvenida;
    }

    public String getMonedaPredeterminada() {
        return monedaPredeterminada;
    }

    public void setMonedaPredeterminada(String monedaPredeterminada) {
        this.monedaPredeterminada = monedaPredeterminada;
    }
}
