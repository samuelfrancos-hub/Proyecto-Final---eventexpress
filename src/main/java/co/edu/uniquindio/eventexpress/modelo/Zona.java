package co.edu.uniquindio.eventexpress.modelo;

import java.util.ArrayList;
import java.util.List;

public class Zona implements IComponenteRecinto, Cloneable {

    private String idZona;
    private String nombre;
    private int capacidad;
    private double precioBase;
    private List<Asiento> asientos;

    public Zona() {
        this.asientos = new ArrayList<>();
    }

    public Zona(String idZona, String nombre, int capacidad, double precioBase) {
        this.idZona = idZona;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.asientos = new ArrayList<>();
    }

    public Zona(String idZona, String nombre, int capacidad, double precioBase, List<Asiento> asientos) {
        this.idZona = idZona;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.asientos = asientos != null ? asientos : new ArrayList<>();
    }

    public void agregarAsiento(Asiento asiento) {
        this.asientos.add(asiento);
    }

    public boolean eliminarAsiento(String idAsiento) {
        return this.asientos.removeIf(a -> a.getIdAsiento().equals(idAsiento));
    }

    public Asiento buscarAsiento(String idAsiento) {
        for (Asiento asiento : asientos) {
            if (asiento.getIdAsiento().equals(idAsiento)) {
                return asiento;
            }
        }
        return null;
    }

    @Override
    public int consultarCapacidad() {
        if (asientos.isEmpty()) {
            return capacidad;
        }
        int total = 0;
        for (Asiento asiento : asientos) {
            total += asiento.consultarCapacidad();
        }
        return total;
    }

    @Override
    public int consultarDisponibles() {
        int disponibles = 0;
        for (Asiento asiento : asientos) {
            disponibles += asiento.consultarDisponibles();
        }
        return disponibles;
    }

    @Override
    public Zona clone() {
        try {
            Zona copia = (Zona) super.clone();
            List<Asiento> asientosClonados = new ArrayList<>();
            for (Asiento asiento : this.asientos) {
                asientosClonados.add(asiento.clone());
            }
            copia.asientos = asientosClonados;
            return copia;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public Zona clonarConPrecio(double nuevoPrecio) {
        Zona copia = this.clone();
        copia.setPrecioBase(nuevoPrecio);
        return copia;
    }

    public Zona clonarConCapacidad(int nuevaCapacidad) {
        Zona copia = this.clone();
        copia.setCapacidad(nuevaCapacidad);
        return copia;
    }

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public List<Asiento> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<Asiento> asientos) {
        this.asientos = asientos;
    }
}
