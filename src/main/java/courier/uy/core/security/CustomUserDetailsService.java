package courier.uy.core.security;

import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.entity.Rol;
import courier.uy.core.entity.Usuario;
import courier.uy.core.repository.IUserRepository;
import courier.uy.core.repository.IUsuarioEmpresaRepository;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IUsuarioEmpresaRepository usuarioEmpresaRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameUsuarioEmpresa) {
        String username = usernameUsuarioEmpresa;
        List<String> roles = new ArrayList<String>();
        UsuarioEmpresa usuarioEmpresa = null;
        if(usernameUsuarioEmpresa.indexOf("+") > -1){
            username = usernameUsuarioEmpresa.substring(0,usernameUsuarioEmpresa.indexOf("+"));
            String usuarioEmpresaId = usernameUsuarioEmpresa.substring(usernameUsuarioEmpresa.indexOf("+") + 1);
            if(!usuarioEmpresaId.equals("null"))
                usuarioEmpresa = usuarioEmpresaRepository.findById(usuarioEmpresaId).get();
            if (usuarioEmpresa != null) {
                for (Rol rol : usuarioEmpresa.getRoles()) {
                    roles.add(rol.getRol());
                }
            }
        }

        Optional<Usuario> usuario = this.userRepository.findByEmail(username);
        if (!usuario.isPresent()){
            usuario = this.userRepository.findByUsuario(username);
        }
        if(!usuario.isPresent()) {
            throw new UsernameNotFoundException("Username: " + username + " not found");
        }

        if (usuario.get().esAdministradorSistema() != null && usuario.get().esAdministradorSistema())
            roles.add("systemAdmin");

        UsuarioPrincipal usuarioPrincipal = new UsuarioPrincipal(usuario.get(), usuarioEmpresa, roles);
        return usuarioPrincipal;
    }
}
