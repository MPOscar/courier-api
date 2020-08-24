package courier.uy.core.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import courier.uy.core.utils.Helpers;
import courier.uy.core.utils.serializer.CustomDateTimeDeserializer;
import courier.uy.core.utils.serializer.CustomDateTimeSerializer;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CodigoUsuario")
public class CodigoUsuario extends Entidad {

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime fechaUso;

	@DBRef(lazy = true)
	private Usuario usuario;

	private String susuario;

	@DBRef(lazy = true)
	private Company empresa;

	private String sempresa;

	private String codigo;

	private Boolean usado;

	private Boolean expirado;

	public CodigoUsuario() {
		super();
	}

	public CodigoUsuario(CodigoUsuario codigoUsuario) {
		super();
		this.oldId = codigoUsuario.getOldId();
		this.fechaUso = codigoUsuario.getFechaUso();
		this.codigo = codigoUsuario.getCodigo();
		this.usado = codigoUsuario.getUsado();
		this.expirado = codigoUsuario.getExpirado();
		this.fechaCreacion = codigoUsuario.getFechaCreacion();
		this.fechaEdicion = codigoUsuario.getFechaEdicion();
		this.eliminado = codigoUsuario.getEliminado();
	}

	public CodigoUsuario(Usuario usuario) {
		super();
		this.usuario = usuario;
		this.susuario = usuario.getId();
		this.codigo = Helpers.randomString(8);
		this.usado = false;
		this.expirado = false;
	}

	public void usar() {
		this.usado = true;
		this.fechaUso = new DateTime();
	}

	public void expirar() {
		this.expirado = true;
	}

	public String getCodigo() {
		return codigo;
	}

	public Boolean getUsado() {
		return usado;
	}

	public void setUsado(Boolean usado) {
		this.usado = usado;
	}

	public Boolean getExpirado() {
		return expirado;
	}

	public void setExpirado(Boolean expirado) {
		this.expirado = expirado;
	}

	public DateTime getFechaUso() {
		return fechaUso;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setFechaUso(DateTime fechaUso) {
		this.fechaUso = fechaUso;
	}

	public Boolean fueUsado() {
		return usado;
	}

	public Boolean estaExpirado() {
		return expirado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getSusuario() {
		return susuario;
	}

	public void setSusuario(String susuario) {
		this.susuario = susuario;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodigoUsuario other = (CodigoUsuario) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}
}
