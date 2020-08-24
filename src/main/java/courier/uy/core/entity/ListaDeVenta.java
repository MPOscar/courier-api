package courier.uy.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.ws.rs.core.Link;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "ListaDeVenta")
public class ListaDeVenta extends Entidad {

	@DBRef(lazy = true)
	private Company empresa;

	private String sempresa;

	//ManyToOne
	@DBRef(lazy = true)
	private Ubicacion ubicacion;

	private String subicacion;

	private String nombre;

	private String descripcion;

	@DBRef(lazy = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Set<Company> empresas = new HashSet<Company>();

	private Set<String> sempresas = new HashSet<String>();

	@DBRef(lazy = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Set<Grupo> grupos = new HashSet<Grupo>();

	private Set<String> sgrupos = new HashSet<String>();

	@DBRef(lazy = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Set<Producto> productos = new HashSet<Producto>();

	private Set<String> sproductos = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<ListaDeVentaVisibilidad> listaDeVentaProductosVisibilidad = new HashSet<ListaDeVentaVisibilidad>();

	private Set<String> slistaDeVentaProductosVisibilidad = new HashSet<String>();

	public ListaDeVenta(String nombre, String desc) {
		super();
		this.nombre = nombre;
		this.descripcion = desc;
	}

	public ListaDeVenta(ListaDeVenta listaDeVenta) {
		super();
		this.nombre = listaDeVenta.getNombre();
		this.descripcion = listaDeVenta.getDescripcion();
	}

	public ListaDeVenta() {
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

	@JsonIgnore()
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

	@JsonIgnore()
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

	@JsonIgnore()
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

	@JsonIgnore()
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
		ListaDeVenta other = (ListaDeVenta) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}

}
