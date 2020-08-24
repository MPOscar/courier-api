package courier.uy.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import javax.ws.rs.core.Link;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "Empresa")
public class Company extends Entidad {

	private String gln;

	private String razonSocial;

	private String nombre;

	private long rut;

	private Boolean validado;

	private Boolean activo;

	private String foto;

	@DBRef(lazy = true)
	private Set<Company> empresasConCatalogo = new HashSet<Company>();
	private Set<String> sempresasConCatalogo = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<ProductoAccion> productosAcciones = new HashSet<ProductoAccion>();
	private Set<String> sproductosAcciones = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<ProductoAccion> productosAccionesRealizar = new HashSet<ProductoAccion>();
	private Set<String> sproductosAccionesRealizar = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<Grupo> grupos = new HashSet<Grupo>();
	private Set<String> sgrupos = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<Grupo> empresaGrupos = new HashSet<Grupo>();
	private Set<String> sempresaGrupos = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<Producto> productosEmpresa = new HashSet<Producto>();
	private Set<String> sproductosEmpresa = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<ListaDeVenta> listasDeVenta = new HashSet<ListaDeVenta>();
	private Set<String> slistasDeVenta = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<Producto> productosVisibles = new HashSet<Producto>();
	private Set<String> sproductosVisibles = new HashSet<String>();


	@DBRef(lazy = true)
	private Set<Categoria> categorias = new HashSet<Categoria>();
	private Set<String> scategorias = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<Baja> bajas = new HashSet<Baja>();
	private Set<String> sbajas = new HashSet<String>();

	@DBRef(lazy = true)
	private Set<Ubicacion> ubicaciones = new HashSet<Ubicacion>();
	private Set<String> subicaciones = new HashSet<String>();

	@Transient
	@JsonProperty
	private Baja nuevaBaja;

	public Baja getNuevaBaja() {
		return nuevaBaja;
	}

	public void setNuevaBaja(Baja nuevaBaja) {
		this.nuevaBaja = nuevaBaja;
	}

	@Transient
	@JsonProperty
	private String email;

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Transient
	@JsonProperty
	private int wishlistSize;

	@JsonIgnore()
	public int getWishlistSize() {
		return wishlistSize;
	}

	public void setWishlistSize(int size) {
		this.wishlistSize = size;
	}

	@JsonIgnore()
	public Set<Ubicacion> getUbicaciones() {
		return ubicaciones;
	}

	public void setUbicaciones(Set<Ubicacion> ubicaciones) {
		this.ubicaciones = ubicaciones;
	}

	@JsonIgnore()
	public Set<String> getSubicaciones() {
		return subicaciones;
	}

	public void setSubicaciones(Set<String> subicaciones) {
		this.subicaciones = subicaciones;
	}

	public Company() {
		super();
	}

	public Company(String gln, String razonSocial, String nombre, Long rut, Boolean activo, Boolean eliminado,
				   String fechaCreacion, String fechaEdicion) {
		super();
		this.gln = gln;
		this.razonSocial = razonSocial;
		this.nombre = nombre;
		this.rut = rut;
		this.activo = activo;
		this.eliminado = eliminado;
		this.fechaCreacion = new DateTime(fechaCreacion);
		this.fechaEdicion = new DateTime(fechaEdicion);
	}

	public Company(Company empresa) {
		super();
		this.oldId = empresa.getOldId();
		this.gln = empresa.getGln();
		this.razonSocial = empresa.getRazonSocial();
		this.nombre = empresa.getNombre();
		this.rut = empresa.getRut();
		this.email = empresa.getEmail();
		this.wishlistSize = empresa.getWishlistSize();
		this.activo = empresa.getActivo();
		this.validado = empresa.getValidado();
		this.eliminado = empresa.getEliminado();
		this.fechaCreacion = empresa.getFechaCreacion();
		this.fechaEdicion = empresa.getFechaEdicion();
		this.fechaCreacion = empresa.getFechaCreacion();
		this.fechaEdicion = empresa.getFechaEdicion();
		this.eliminado = empresa.getEliminado();
	}

	public String getGln() {
		return gln;
	}

	public void setGln(String gln) {
		this.gln = gln;
	}

	public String getFoto() {
		return this.foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getRut() {
		return rut;
	}

	public void setRut(Long rut) {
		this.rut = rut;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getEliminado() {
		return eliminado;
	}

	public void setEliminado(Boolean eliminado) {
		this.eliminado = eliminado;
	}

	public boolean getValidado() {
		return validado;
	}

	public void setValidado(boolean validado) {
		this.validado = validado;
	}

	public void setRut(long rut) {
		this.rut = rut;
	}

	public void setValidado(Boolean validado) {
		this.validado = validado;
	}

	@JsonIgnore()
	public Set<Company> getEmpresasConCatalogo() {
		return empresasConCatalogo;
	}

	public void setEmpresasConCatalogo(Set<Company> empresasConCatalogo) {
		this.empresasConCatalogo = empresasConCatalogo;
	}

	@JsonIgnore()
	public Set<String> getSempresasConCatalogo() {
		return sempresasConCatalogo;
	}

	public void setSempresasConCatalogo(Set<String> sempresasConCatalogo) {
		this.sempresasConCatalogo = sempresasConCatalogo;
	}

	@JsonIgnore()
	public Set<ProductoAccion> getProductosAcciones() {
		return productosAcciones;
	}

	public void setProductosAcciones(Set<ProductoAccion> productosAcciones) {
		this.productosAcciones = productosAcciones;
	}

	@JsonIgnore()
	public Set<String> getSproductosAcciones() {
		return sproductosAcciones;
	}

	public void setSproductosAcciones(Set<String> sproductosAcciones) {
		this.sproductosAcciones = sproductosAcciones;
	}

	@JsonIgnore()
	public Set<ProductoAccion> getProductosAccionesRealizar() {
		return productosAccionesRealizar;
	}

	public void setProductosAccionesRealizar(Set<ProductoAccion> productosAccionesRealizar) {
		this.productosAccionesRealizar = productosAccionesRealizar;
	}

	@JsonIgnore()
	public Set<String> getSproductosAccionesRealizar() {
		return sproductosAccionesRealizar;
	}

	public void setSproductosAccionesRealizar(Set<String> sproductosAccionesRealizar) {
		this.sproductosAccionesRealizar = sproductosAccionesRealizar;
	}

	@JsonIgnore()
	public Set<Grupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(Set<Grupo> grupos) {
		this.grupos = grupos;
	}

	@JsonIgnore()
	public Set<String> getSgrupos() {
		return sgrupos;
	}

	public void setSgrupos(Set<String> sgrupos) {
		this.sgrupos = sgrupos;
	}

	@JsonIgnore()
	public Set<Producto> getProductosEmpresa() {
		return productosEmpresa;
	}

	public void setProductosEmpresa(Set<Producto> productosEmpresa) {
		this.productosEmpresa = productosEmpresa;
	}

	@JsonIgnore()
	public Set<String> getSproductosEmpresa() {
		return sproductosEmpresa;
	}

	public void setSproductosEmpresa(Set<String> sproductosEmpresa) {
		this.sproductosEmpresa = sproductosEmpresa;
	}

	@JsonIgnore()
	public Set<ListaDeVenta> getListasDeVenta() {
		return listasDeVenta;
	}

	public void setListasDeVenta(Set<ListaDeVenta> listasDeVenta) {
		this.listasDeVenta = listasDeVenta;
	}

	@JsonIgnore()
	public Set<String> getSlistasDeVenta() {
		return slistasDeVenta;
	}

	public void setSlistasDeVenta(Set<String> slistasDeVenta) {
		this.slistasDeVenta = slistasDeVenta;
	}

	@JsonIgnore()
	public Set<Producto> getProductosVisibles() {
		return productosVisibles;
	}

	public void setProductosVisibles(Set<Producto> productosVisibles) {
		this.productosVisibles = productosVisibles;
	}

	@JsonIgnore()
	public Set<String> getSproductosVisibles() {
		return sproductosVisibles;
	}

	public void setSproductosVisibles(Set<String> sproductosVisibles) {
		this.sproductosVisibles = sproductosVisibles;
	}

	@JsonIgnore()
	public Set<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(Set<Categoria> categorias) {
		this.categorias = categorias;
	}

	@JsonIgnore()
	public Set<String> getScategorias() {
		return scategorias;
	}

	public void setScategorias(Set<String> scategorias) {
		this.scategorias = scategorias;
	}

	public Set<Baja> getBajas() {
		return bajas;
	}

	public void setBajas(Set<Baja> bajas) {
		this.bajas = bajas;
	}

	@JsonIgnore()
	public Set<String> getSbajas() {
		return sbajas;
	}

	public void setSbajas(Set<String> sbajas) {
		this.sbajas = sbajas;
	}

	@JsonIgnore()
	public Set<Grupo> getEmpresaGrupos() {
		return empresaGrupos;
	}

	public void setEmpresaGrupos(Set<Grupo> empresaGrupos) {
		this.empresaGrupos = empresaGrupos;
	}

	@JsonIgnore()
	public Set<String> getSempresaGrupos() {
		return sempresaGrupos;
	}

	public void setSempresaGrupos(Set<String> sempresaGrupos) {
		this.sempresaGrupos = sempresaGrupos;
	}

	@JsonIgnore()
	public Baja getBaja() {
		if (this.bajas != null) {
			for (Baja baja : this.bajas) {
				if (!baja.getEliminado() && baja.getActivo()) {
					return baja;
				}
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Company other = (Company) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}

	@Override
	public void inicializarLinkData() {
		Link self = Link.fromUri("/empresas/" + this.id).rel("self").title("Obtener Empresa").type("GET").build();
		//this.links.add(self);
	}
}

