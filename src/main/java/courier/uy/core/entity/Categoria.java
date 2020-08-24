package courier.uy.core.entity;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Link;

import courier.uy.core.exceptions.ModelException;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Categoria")
public class Categoria extends Entidad {
	private String nombre;

	private Long nivel;

	private String descripcion;

	private Long posicion;

	@DBRef(lazy = true)
	private Company empresa;

	private String sempresa;

	@DBRef(lazy = true)
	private Categoria padre;

	private String spadre;

	//ManyToOne
	@DBRef(lazy = true)
	private Set<Producto> productos = new HashSet<Producto>();

	private Set<String> sproductos = new HashSet<String>();

	public Categoria() {
		super();
	}

	public Categoria(String nombre, Long nivel) {
		super();
		this.nombre = nombre;
		this.nivel = nivel;
	}

	public Categoria(Categoria categoria) {
		super();
		this.oldId = categoria.getOldId();
		this.nombre = categoria.getNombre();
		this.nivel = categoria.getNivel();
		this.descripcion = categoria.getDescripcion();
		this.posicion = categoria.getPosicion();
		this.fechaCreacion = categoria.getFechaCreacion();
		this.fechaEdicion = categoria.getFechaEdicion();
		this.eliminado = categoria.getEliminado();
	}

	public void copyForExcel(String nombre, Long nivel, Categoria padre) throws ModelException {
		if (padre.getPadre() != null) {
			if (this.getId() == padre.getPadre().getId()) {
				throw new ModelException("La categoría " + nombre + " tiene a la cateogría " + padre.getNombre()
						+ " como padre y vice versa");
			}
		}
		this.nombre = nombre;
		this.nivel = nivel;
		this.padre = padre;
	}

	public Categoria(String nombre, Long nivel, Categoria padre) {
		this.nombre = nombre;
		this.nivel = nivel;
		this.padre = padre;
	}

	public void checkForParentLoop(Categoria padre) throws ModelException {
		if (padre.getPadre() != null) {
			if (this.getId() == padre.getPadre().getId() || this.getNombre().equals(padre.getNombre())) {
				throw new ModelException("La categoría" + this.getNombre() + "tiene a la cateogría " + padre.getNombre()
						+ " como padre y vice versa");
			}
		}
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getNivel() {
		return nivel;
	}

	public void setNivel(Long nivel) {
		this.nivel = nivel;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getPosicion() {
		return posicion;
	}

	public void setPosicion(Long posicion) {
		this.posicion = posicion;
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

	public Categoria getPadre() {
		return padre;
	}

	public void setPadre(Categoria padre) {
		this.padre = padre;
	}

	public String getSpadre() {
		return spadre;
	}

	public void setSpadre(String spadre) {
		this.spadre = spadre;
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

	@Override
	public void inicializarLinkData() {
		Link self = Link.fromUri("/categorias/" + this.id).rel("self").title("Obtener Categoria").type("GET").build();
		//this.getLinks().add(self);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Categoria other = (Categoria) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}

}