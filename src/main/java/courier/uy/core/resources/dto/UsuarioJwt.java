package courier.uy.core.resources.dto;

import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;

public class UsuarioJwt implements UsuarioInterface {

	private String id;
	private Usuario usuario;
	private UsuarioEmpresa usuarioEmpresa;
	
	public UsuarioJwt() {
		
	}

	public UsuarioJwt(Usuario usuario, UsuarioEmpresa usuarioEmpresa) {
		this.usuario = usuario;
		this.usuarioEmpresa = usuarioEmpresa;
	}
	
	public UsuarioJwt(String id, Usuario usuario, UsuarioEmpresa usuarioEmpresa) {
		this.id = id;
		this.usuario = usuario;
		this.usuarioEmpresa = usuarioEmpresa;
	}
	
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return usuario.getNombre();
	}
	
	public Usuario getUsuario() {
		return this.usuario;
	}
	
	public UsuarioEmpresa getUsuarioEmpresa() {
		return this.usuarioEmpresa;
	}

}
