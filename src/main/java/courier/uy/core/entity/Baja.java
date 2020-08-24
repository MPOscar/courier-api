package courier.uy.core.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;

@Document(collection = "Baja")
public class Baja extends Entidad {
	private String motivo;

	@Column(name = "activo")
	private Boolean activo;

	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	@DBRef(lazy = true)
	private Company empresa;

	private String sempresa;

	@DBRef(lazy = true)
	private Usuario admin;

	private String sadmin;

	public Baja() {
		super();
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
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

	public Usuario getAdmin() {
		return admin;
	}

	public void setAdmin(Usuario admin) {
		this.admin = admin;
	}

	public String getSadmin() {
		return sadmin;
	}

	public void setSadmin(String sadmin) {
		this.sadmin = sadmin;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Baja other = (Baja) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}

}