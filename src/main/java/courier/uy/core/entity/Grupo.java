package courier.uy.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "Grupo")
public class Grupo extends Entidad {

	@DBRef(lazy = true)
	private Set<Producto> productosVisibles = new HashSet<Producto>();

	private Set<String> sproductosVisibles = new HashSet<String>();

	@DBRef(lazy = true)
	private Company empresa;

	private String sEmpresa;

	private String nombre;

	private String descripcion;

	@DBRef(lazy = true)
	private Set<Company> empresas = new HashSet<Company>();

	private Set<String> sempresas = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<ListaDeVenta> listasDeVenta = new HashSet<ListaDeVenta>();

	private Set<String> slistasDeVenta = new HashSet<String>();

	public Grupo(String nombre, String desc) {
		super();
		this.nombre = nombre;
		this.descripcion = desc;
	}

	public Grupo(Grupo grupo) {
		super();
		this.oldId = grupo.getOldId();
		this.nombre = grupo.getNombre();
		this.descripcion = grupo.getDescripcion();
		this.fechaCreacion = grupo.getFechaCreacion();
		this.fechaEdicion = grupo.getFechaEdicion();
		this.eliminado = grupo.getEliminado();
	}

	public Grupo() {
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

	@JsonIgnore()
	public Set<Producto> getProductosVisibles() {
		return productosVisibles;
	}

	public void setProductosVisibles(Set<Producto> productosVisibles) {
		this.productosVisibles = productosVisibles;
	}

	public Set<String> getSproductosVisibles() {
		return sproductosVisibles;
	}

	public void setSproductosVisibles(Set<String> sproductosVisibles) {
		this.sproductosVisibles = sproductosVisibles;
	}

	public Company getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Company empresa) {
		this.empresa = empresa;
	}

	public String getsEmpresa() {
		return sEmpresa;
	}

	public void setsEmpresa(String sEmpresa) {
		this.sEmpresa = sEmpresa;
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

	@JsonIgnore()
	public Set<ListaDeVenta> getListasDeVenta() {
		return listasDeVenta;
	}

	public void setListasDeVenta(Set<ListaDeVenta> listasDeVenta) {
		this.listasDeVenta = listasDeVenta;
	}

	public Set<String> getSlistasDeVenta() {
		return slistasDeVenta;
	}

	public void setSlistasDeVenta(Set<String> slistasDeVenta) {
		this.slistasDeVenta = slistasDeVenta;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grupo other = (Grupo) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}
}
