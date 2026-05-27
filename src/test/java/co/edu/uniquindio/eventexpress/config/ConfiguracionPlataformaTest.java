package co.edu.uniquindio.eventexpress.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static co.edu.uniquindio.eventexpress.config.ConfiguracionPlataforma.getInstancia;
import static org.junit.jupiter.api.Assertions.*;

class ConfiguracionPlataformaTest {

    ConfiguracionPlataforma config;

    @BeforeEach
    void obtenerinstancia() {
        config = getInstancia();
    }

    // Verifica que siempre haya un mismo objeto en memoria
    @Test
    void instanciaUnica() {
        ConfiguracionPlataforma aux = getInstancia();

        assertSame(config, aux);
    }

    // Verifica que no haya copias ocultas
    @Test
    void datosConsistentesEntreReferencias() {
        ConfiguracionPlataforma aux = getInstancia();

        config.setNombrePlataforma("evento");

        assertEquals("evento", aux.getNombrePlataforma());
    }

    @AfterEach
    void restaurarValores() {
       config.setNombrePlataforma("EventExpress");
    }

    // Verifica que los valores iniciales sean correctos
    @Test
    void parametrosGlobalesCorrectos() {
        assertEquals("EventExpress", config.getNombrePlataforma());
        assertEquals("1.0.0", config.getVersion());
        assertEquals("COP" , config.getMonedaPredeterminada());
    }


    // Verifica que el constructor sea privadoList
    @Test
    void noInstanciableDesdeFuera() throws NoSuchMethodException{
        Constructor<ConfiguracionPlataforma> constructor =
                ConfiguracionPlataforma.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
}