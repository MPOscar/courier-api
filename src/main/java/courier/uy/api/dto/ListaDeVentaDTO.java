package courier.uy.api.dto;

import courier.uy.core.entity.*;

import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListaDeVentaDTO extends Entidad {

	private Company empresa;

	private String sempresa;

	private Ubicacion ubicacion;

	private String subicacion;

	private String nombre;

	private String descripcion;

	private Set<Company> empresas = new HashSet<Company>();

	private Set<String> sempresas = new HashSet<String>();

	private List<Company> listaEmpresas = new ArrayList<>();

	private List<Grupo> listaGrupos = new ArrayList<>();

	private List<Producto> listaProductos = new ArrayList<>();

	private Set<Grupo> grupos = new HashSet<Grupo>();

	private Set<String> sgrupos = new HashSet<String>();

	private Set<Producto> productos = new HashSet<Producto>();

	private Set<String> sproductos = new HashSet<String>();

	private Set<ListaDeVentaVisibilidad> listaDeVentaProductosVisibilidad = new HashSet<ListaDeVentaVisibilidad>();

	private Set<String> slistaDeVentaProductosVisibilidad = new HashSet<String>();

	public ListaDeVentaDTO(String nombre, String desc) {
		super();
		this.nombre = nombre;
		this.descripcion = desc;
	}

	public ListaDeVentaDTO(ListaDeVenta listaDeVenta) {
		super();
		this.id = listaDeVenta.getId();
		this.sid = listaDeVenta.getSId();
		this.ubicacion = listaDeVenta.getUbicacion();
		this.subicacion = listaDeVenta.getSubicacion();
		this.nombre = listaDeVenta.getNombre();
		this.descripcion = listaDeVenta.getDescripcion();
		this.listaEmpresas.addAll(listaDeVenta.getEmpresas()) ;
		this.sempresas = listaDeVenta.getSempresas();
		this.listaGrupos.addAll(listaDeVenta.getGrupos());
		this.sgrupos = listaDeVenta.getSgrupos();
		this.listaProductos.addAll(listaDeVenta.getProductos());
		this.sproductos = listaDeVenta.getSproductos();
	}

	public ListaDeVentaDTO() {
		super();
	}

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setNombre(String nom) {
		this.nombre = nom;
	}

	public void setDescripcion(String desc) {
		this.descripcion = desc;
	}

	public Company getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Company empresa) {
		this.empresa = empresa;
	}

	public String getSempresa() {
		return sempresa;
	}

	public void setSempresa(String sempresa) {
		this.sempresa = sempresa;
	}

	public Ubicacion getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(Ubicacion ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getSubicacion() {
		return subicacion;
	}

	public void setSubicacion(String subicacion) {
		this.subicacion = subicacion;
	}

	public Set<Company> getEmpresas() {
		return empresas;
	}

	public void setEmpresas(Set<Company> empresas) {
		this.empresas = empresas;
	}

	public Set<String> getSempresas() {
		return sempresas;
	}

	public void setSempresas(Set<String> sempresas) {
		this.sempresas = sempresas;
	}

	public Set<Grupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(Set<Grupo> grupos) {
		this.grupos = grupos;
	}

	public Set<String> getSgrupos() {
		return sgrupos;
	}

	public void setSgrupos(Set<String> sgrupos) {
		this.sgrupos = sgrupos;
	}

	public Set<Producto> getProductos() {
		return productos;
	}

	public void setProductos(Set<Producto> productos) {
		this.productos = productos;
	}

	public Set<String> getSproductos() {
		return sproductos;
	}

	public void setSproductos(Set<String> sproductos) {
		this.sproductos = sproductos;
	}

	public Set<ListaDeVentaVisibilidad> getListaDeVentaProductosVisibilidad() {
		return listaDeVentaProductosVisibilidad;
	}

	public void setListaDeVentaProductosVisibilidad(Set<ListaDeVentaVisibilidad> listaDeVentaProductosVisibilidad) {
		this.listaDeVentaProductosVisibilidad = listaDeVentaProductosVisibilidad;
	}

	public Set<String> getSlistaDeVentaProductosVisibilidad() {
		return slistaDeVentaProductosVisibilidad;
	}

	public void setSlistaDeVentaProductosVisibilidad(Set<String> slistaDeVentaProductosVisibilidad) {
		this.slistaDeVentaProductosVisibilidad = slistaDeVentaProductosVisibilidad;
	}

	public List<Company> getListaEmpresas() {
		return listaEmpresas;
	}

	public void setListaEmpresas(List<Company> listaEmpresas) {
		this.listaEmpresas = listaEmpresas;
	}

	public List<Grupo> getListaGrupos() {
		return listaGrupos;
	}

	public void setListaGrupos(List<Grupo> listaGrupos) {
		this.listaGrupos = listaGrupos;
	}

	public List<Producto> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(List<Producto> listaProductos) {
		this.listaProductos = listaProductos;
	}

	@Override
	public void inicializarLinkData() {
		Link self = Link.fromUri("/listasDeVenta/" + this.id).rel("self").title("Obtener Lista de Venta").type("GET")
				.build();
		//this.links.add(self);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ListaDeVentaDTO other = (ListaDeVentaDTO) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}

}
