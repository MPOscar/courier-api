package courier.uy.core.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "Wishlist")
public class Wishlist extends Entidad {

	@DBRef(lazy = true)
	private Usuario usuario;

	private String susuario;

	@DBRef(lazy = true)
	private Company empresa;

	private String sempresa;

	@DBRef(lazy = true)
	private Set<Producto> productos = new HashSet<Producto>();

	private Set<String> sproductos = new HashSet<String>();

	private Boolean enWishlist;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Company getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Company empresa) {
		this.empresa = empresa;
	}

	public Set<Producto> getProductos() {
		return productos;
	}

	public void setProductos(Set<Producto> productos) {
		this.productos = productos;
	}

	public Boolean getEnWishlist() {
		return enWishlist;
	}

	public void setEnWishlist(Boolean enWishlist) {
		this.enWishlist = enWishlist;
	}

	public String getSusuario() {
		return susuario;
	}

	public void setSusuario(String susuario) {
		this.susuario = susuario;
	}

	public String getSempresa() {
		return sempresa;
	}

	public void setSempresa(String sempresa) {
		this.sempresa = sempresa;
	}

	public Set<String> getSproductos() {
		return sproductos;
	}

	public void setSproductos(Set<String> sproductos) {
		this.sproductos = sproductos;
	}

	public Wishlist() {
		super();
	}

}