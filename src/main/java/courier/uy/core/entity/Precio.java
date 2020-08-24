package courier.uy.core.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;

@Document(collection = "Precio")
public class Precio extends Entidad{

	@Column(name = "producto_cpp")
	private String productoCpp;

	@Column(name = "gln_Lista_venta")
	private String glnListaVenta;

	@Column(name = "moneda")
	private String moneda;

	@Column(name = "precio")
	private String precio;

	public Precio() {

	}

	public Precio(String productoCpp, String glnListaVenta, String moneda, String precio) {
		this.productoCpp = productoCpp;
		this.glnListaVenta = glnListaVenta;
		this.moneda = moneda;
		this.precio = precio;
	}

	public String getProductoCpp() {
		return productoCpp;
	}

	public void setProductoCpp(String productoCpp) {
		this.productoCpp = productoCpp;
	};

	public String getGlnListaVenta() {
		return glnListaVenta;
	}

	public void setGlnListaVenta(String productoCpp) {
		this.glnListaVenta = glnListaVenta;
	};

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getPrecio() {
		return precio;
	}

	public void setPrecio(String precio) {
		this.precio = precio;
	};

}