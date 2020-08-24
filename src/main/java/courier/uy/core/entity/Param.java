package courier.uy.core.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Param")
public class Param extends Entidad{

	private String nombre;

	private String valor;

	public Param() {
		super();
	}

	public Param(Param param) {
		super();
		this.nombre = param.getNombre();
		this.valor = param.getValor();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	};

}
