package courier.uy.core.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "ListaDeVentaVisibilidad")
public class ListaDeVentaVisibilidad extends Entidad {

    @DBRef(lazy = true)
    private ListaDeVenta listaDeVenta;

    private String slistaDeVenta;

    @DBRef(lazy = true)
    private Producto producto;

    private String sproducto;

    //ManyToMany
    @DBRef(lazy = true)
    private Set<Company> empresasConVisibilidad = new HashSet<Company>();

    private Set<String> sempresasConVisibilidad = new HashSet<String>();

    //ManyToMany
    @DBRef(lazy = true)
    private Set<Grupo> gruposConVisibilidad = new HashSet<Grupo>();

    private Set<String> sgruposConVisibilidad = new HashSet<String>();

    private Boolean esPrivado = false;

    private Boolean esPublico = true;

    public Boolean getEsPublico() {
        return this.esPublico;
    }

    public void setEsPublico(Boolean publico) {
        this.esPublico = publico;
    }

    public Boolean getEsPrivado() {
        return this.esPrivado;
    }

    public void setEsPrivado(Boolean esPrivado) {
        this.esPrivado = esPrivado;
    }


    public ListaDeVentaVisibilidad(){}

    public ListaDeVentaVisibilidad(ListaDeVenta listaDeVenta, Set<Company> empresasConVisibilidad, Set<Grupo> gruposConVisibilidad, Producto producto){
        super();
        this.listaDeVenta = listaDeVenta;
        this.producto = producto;
        this.empresasConVisibilidad = empresasConVisibilidad;
        this.gruposConVisibilidad = gruposConVisibilidad;
    }

    public ListaDeVentaVisibilidad(Long id, Long id1) {
    }

    public ListaDeVenta getListaDeVenta() {
        return listaDeVenta;
    }

    public void setListaDeVenta(ListaDeVenta listaDeVenta) {
        this.listaDeVenta = listaDeVenta;
    }

    public String getSlistaDeVenta() {
        return slistaDeVenta;
    }

    public void setSlistaDeVenta(String slistaDeVenta) {
        this.slistaDeVenta = slistaDeVenta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getSproducto() {
        return sproducto;
    }

    public void setSproducto(String sproducto) {
        this.sproducto = sproducto;
    }

    public Set<Company> getEmpresasConVisibilidad() {
        return empresasConVisibilidad;
    }

    public void setEmpresasConVisibilidad(Set<Company> empresasConVisibilidad) {
        this.empresasConVisibilidad = empresasConVisibilidad;
    }

    public Set<String> getSempresasConVisibilidad() {
        return sempresasConVisibilidad;
    }

    public void setSempresasConVisibilidad(Set<String> sempresasConVisibilidad) {
        this.sempresasConVisibilidad = sempresasConVisibilidad;
    }

    public Set<Grupo> getGruposConVisibilidad() {
        return gruposConVisibilidad;
    }

    public void setGruposConVisibilidad(Set<Grupo> gruposConVisibilidad) {
        this.gruposConVisibilidad = gruposConVisibilidad;
    }

    public Set<String> getSgruposConVisibilidad() {
        return sgruposConVisibilidad;
    }

    public void setSgruposConVisibilidad(Set<String> sgruposConVisibilidad) {
        this.sgruposConVisibilidad = sgruposConVisibilidad;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ListaDeVentaVisibilidad other = (ListaDeVentaVisibilidad) obj;
        if (other.getId().equals(this.getId()))
            return true;
        return false;
    }
}