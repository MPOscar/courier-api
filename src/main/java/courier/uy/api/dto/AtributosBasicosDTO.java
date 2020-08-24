package courier.uy.api.dto;

import courier.uy.core.entity.Producto;


public class AtributosBasicosDTO {

	private String gtin;

	private String descripcion;

	private String marca;

	private ContenidoDTO contenidoNeto;

	private String foto;

	public AtributosBasicosDTO(Producto producto) {
		this.gtin = producto.getGtin();
		this.descripcion = producto.getDescripcion();
		this.marca = producto.getMarca();
		if(!producto.getAtributosLaboratorio().getContenidoNetoPorUsoUnidad().equals("")){
			contenidoNeto = new ContenidoDTO(producto.getAtributosLaboratorio().getContenidoNetoPorUsoCantidad(), producto.getAtributosLaboratorio().getContenidoNetoPorUsoUnidad());
		}else {
			contenidoNeto = new ContenidoDTO(producto.getAtributosLaboratorio().getContenidoNetoCantidad(), producto.getAtributosLaboratorio().getContenidoNetoUnidad());
		}
	}


	public void crear(Producto producto){
		this.gtin = producto.getGtin();
		this.descripcion = producto.getDescripcion();
		this.marca = producto.getMarca();
		contenidoNeto = new ContenidoDTO(producto.getAtributosLaboratorio().getContenidoNetoCantidad(), producto.getAtributosLaboratorio().getContenidoNetoUnidad());
	}

	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public ContenidoDTO getContenidoNeto() {
		return contenidoNeto;
	}

	public void setContenidoNeto(ContenidoDTO contenidoNeto) {
		this.contenidoNeto = contenidoNeto;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
}
