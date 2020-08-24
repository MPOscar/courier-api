package courier.uy.core.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "Team")
public class Team extends Entidad {

	private String nombre;

	private String descripcion;

	@DBRef(lazy = true)
	private Company empresa;

	private String sEmpresa;

	@DBRef(lazy = true)
	private Set<Driver> drivers = new HashSet<Driver>();

	private Set<String> sdrivers = new HashSet<String>();

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Team other = (Team) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}
}
