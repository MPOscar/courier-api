package courier.uy.core.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "UsuarioEmpresa")
public class UsuarioEmpresa extends Entidad {

	private static final long serialVersionUID = -9011390041907300773L;

	@DBRef(lazy = true)
	private Usuario usuario;

	private String susuario;

	@DBRef(lazy = true)
	private Company empresa;

	private String sempresa;

	private String rol;

	@Column(name = "activo")
	private Boolean activo;

	private Boolean validado;

	@DBRef(lazy = true)
	private Set<Rol> roles = new HashSet<Rol>();

	private Set<String> sroles = new HashSet<String>();

	public UsuarioEmpresa() {
		super();
	}

	public UsuarioEmpresa(UsuarioEmpresa usuarioEmpresa) {
		super();
		this.oldId = usuarioEmpresa.getOldId();
		this.validado = usuarioEmpresa.getValidado();
		this.activo = usuarioEmpresa.getActivo();
		this.rol = usuarioEmpresa.getRol();
		this.fechaCreacion = usuarioEmpresa.getFechaCreacion();
		this.fechaEdicion = usuarioEmpresa.getFechaEdicion();
		this.eliminado = usuarioEmpresa.getEliminado();
	}


	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;

	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getActivo() {
		return this.activo;
	}

	public void setValidado(Boolean validado) {
		this.validado = validado;
	}

	public Boolean getValidado() {
		return this.validado;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}

	public Set<String> getSroles() {
		return sroles;
	}

	public void setSroles(Set<String> sroles) {
		this.sroles = sroles;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioEmpresa other = (UsuarioEmpresa) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}
}