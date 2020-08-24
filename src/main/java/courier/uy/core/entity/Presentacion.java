package courier.uy.core.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Presentacion")
public class Presentacion extends Entidad{

	private String nombre;

	private Boolean paraMostrar;

	public Boolean getParaMostrar() {
		return this.paraMostrar;
	}

	public void setParaMostrar(Boolean mostrar) {
		this.paraMostrar = mostrar;
	}

	public Presentacion() {

	}

	public Presentacion(String nombre) {
		super();
		this.nombre = nombre;
	}

	public Presentacion(Presentacion presentacion) {
		super();
		this.oldId = presentacion.getOldId();
		this.nombre = presentacion.getNombre();
		this.paraMostrar =presentacion.getParaMostrar();
	}


	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Presentacion other = (Presentacion) obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}

}
