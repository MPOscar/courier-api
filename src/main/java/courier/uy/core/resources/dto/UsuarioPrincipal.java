package courier.uy.core.resources.dto;

import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import java.util.*;

public class UsuarioPrincipal implements UserDetails {

	private Long id;
	private Usuario usuario;
	private UsuarioEmpresa usuarioEmpresa;

	public UsuarioPrincipal() {
	}

	public UsuarioPrincipal(Usuario usuario, UsuarioEmpresa usuarioEmpresa, List<String> roles) {
		this.usuario = usuario;
		this.usuarioEmpresa = usuarioEmpresa;
		this.roles = roles;
	}

	public UsuarioPrincipal(Long id, Usuario usuario, UsuarioEmpresa usuarioEmpresa) {
		this.id = id;
		this.usuario = usuario;
		this.usuarioEmpresa = usuarioEmpresa;
	}

	public Long getId() {
		return this.id;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public UsuarioEmpresa getUsuarioEmpresa() {
		return this.usuarioEmpresa;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles = new ArrayList<>();


	/*public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<? extends GrantedAuthority> roles = this.roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
		return roles;
	}*/
	@Override
	public Set<? extends GrantedAuthority> getAuthorities() {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		this.roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
		return authorities;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}

	@JsonIgnore
	public String getPassword() {
		return this.usuario.getPassword();
	}

	@JsonIgnore
	public String getUsername() {
		return this.usuario.getUsuario();
	}
}
