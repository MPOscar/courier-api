package courier.uy.core.resources.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {

	@JsonProperty("token")
	public String token;
	
	@JsonProperty("business")
	public Company business;
	
	@JsonProperty("businesses")
	public Set<Company> businesses = new HashSet<>();

	@JsonProperty("roles")
	public List<String> roles;
	
	@JsonProperty("user")
	public Usuario user;

	public LoginResponse(String token) {
		this.token = token;
	}

	
	public LoginResponse(String token, List <String>roles, Usuario u, Set<Company> businesses, Company business) {
		this.token = token;
		this.roles = roles;
		this.user = u;
		this.businesses = businesses;
		this.business = business;
	}
	
}