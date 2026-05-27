package co.edu.uniquindio.eventexpress.modelo;

import java.util.ArrayList;
import java.util.List;

public class Recinto implements IComponenteRecinto {

    private String idRecinto;
    private String nombre;
    private String direccion;
    private String ciudad;
    private List<Zona> zonas;

    public Recinto() {
        this.zonas = new ArrayList<>();
    }

    public Recinto(String idRecinto, String nombre, String direccion, String ciudad) {
        this.idRecinto = idRecinto;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.zonas = new ArrayList<>();
    }

    public Recinto(String idRecinto, String nombre, String direccion, String ciudad, List<Zona> zonas) {
        this.idRecinto = idRecinto;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.zonas = zonas != null ? zonas : new ArrayList<>();
    }

    public void agregarZona(Zona zona) {
        this.zonas.add(zona);
    }

    public boolean eliminarZona(String idZona) {
        return this.zonas.removeIf(z -> z.getIdZona().equals(idZona));
    }

    public Zona buscarZona(String idZona) {
        for (Zona zona : zonas) {
            if (zona.getIdZona().equals(idZona)) {
                return zona;
            }
        }
        return null;
    }

    @Override
    public int consultarCapacidad() {
        int total = 0;
        for (Zona zona : zonas) {
            total += zona.consultarCapacidad();
        }
        return total;
    }

    @Override
    public int consultarDisponibles() {
        int disponibles = 0;
        for (Zona zona : zonas) {
            disponibles += zona.consultarDisponibles();
        }
        return disponibles;
    }

    public String getIdRecinto() {
        return idRecinto;
    }

    public void setIdRecinto(String idRecinto) {
        this.idRecinto = idRecinto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
    }
}
