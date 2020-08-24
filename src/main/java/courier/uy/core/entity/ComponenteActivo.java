package courier.uy.core.entity;

public class ComponenteActivo {

	private String nombre;

	private String cantidad;

	private String unidad;

	private String enCantidad;

	private String enUnidad;

	public String getNombre() {
		return nombre;
	}

	public ComponenteActivo(String nombre, String cantidad, String unidad, String enCantidad, String enUnidad) {
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.unidad = unidad;
		this.enCantidad = enCantidad;
		this.enUnidad = enUnidad;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	public String getEnCantidad() {
		return enCantidad;
	}

	public void setEnCantidad(String enCantidad) {
		this.enCantidad = enCantidad;
	}

	public String getEnUnidad() {
		return enUnidad;
	}

	public void setEnUnidad(String enUnidad) {
		this.enUnidad = enUnidad;
	}

}
