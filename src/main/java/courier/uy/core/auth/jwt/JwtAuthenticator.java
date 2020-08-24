package courier.uy.core.auth.jwt;

import java.util.Optional;

import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.db.UsuarioEmpresaDAO;
import courier.uy.core.db.UsuariosDAO;
import courier.uy.core.resources.dto.UsuarioJwt;
import courier.uy.core.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.Authenticator;

public class JwtAuthenticator implements Authenticator<JwtContext, UsuarioJwt> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticator.class);

	private SessionFactory sessionFactory;

	private UsuariosDAO usuariosDAO;
	private UsuarioEmpresaDAO usuarioEmpresaDAO;

	public JwtAuthenticator(SessionFactory sessionFactory, UsuariosDAO usuariosDAO,
			UsuarioEmpresaDAO usuarioEmpresaDAO) {
		this.sessionFactory = sessionFactory;
		this.usuariosDAO = usuariosDAO;
		this.usuarioEmpresaDAO = usuarioEmpresaDAO;
	}

	@Override
	public Optional<UsuarioJwt> authenticate(JwtContext context) {
		Session session = sessionFactory.openSession();
		try {
			ManagedSessionContext.bind(session);

			JwtClaims jwtClaims = context.getJwtClaims();
			Usuario u = usuariosDAO.findById(jwtClaims.getSubject());

			if (u == null) {
				throw new Exception("No se encuentra usuario empresa");
			}

			UsuarioEmpresa usuarioEmpresa = null;

			if (jwtClaims.getClaimValue("usuario_empresa") == null) {
				usuarioEmpresa = null;
			} else {
				usuarioEmpresa = this.usuarioEmpresaDAO
						.obtenerUsuarioEmpresa(u.getId(),jwtClaims.getStringClaimValue("usuario_empresa"))
						.orElseThrow(() -> new Exception("No se encuentra usuario empresa"));
			}

			return Optional.of(new UsuarioJwt(u.getId(), u, usuarioEmpresa));
		} catch (Exception e) {
			LOGGER.warn("msg=Failed to authorise user: {}", e.getMessage(), e);
			return Optional.empty();
		} finally {
			ManagedSessionContext.unbind(sessionFactory);
			session.close();
		}
	}

}
