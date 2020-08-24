package courier.uy.core.resources.dto;

public class ListaVentaBasic {

	public String id;

	public String nombre;

	public String descripcion;

	public UbicacionCodigo ubicacion;

	public ListaVentaBasic(String id, String nombre, String codigo){
		this.id = id;
		this.nombre = nombre;
		UbicacionCodigo ubicacionCodigo = new UbicacionCodigo(codigo);
		this.ubicacion = ubicacionCodigo;
	}

	public ListaVentaBasic(String id, String nombre, String descripcion, String codigo){
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		UbicacionCodigo ubicacionCodigo = new UbicacionCodigo(codigo);
		this.ubicacion = ubicacionCodigo;
	}
}


