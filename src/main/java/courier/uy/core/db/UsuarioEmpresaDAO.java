package courier.uy.core.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.repository.IUserRepository;
import courier.uy.core.repository.IEmpresaRepository;
import courier.uy.core.repository.IUsuarioEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEmpresaDAO {

	@Autowired
	private IUsuarioEmpresaRepository usuarioEmpresaRepository;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private IEmpresaRepository empresaRepository;

	private final MongoOperations mongoOperations;

	public UsuarioEmpresaDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public UsuarioEmpresa save(UsuarioEmpresa e) {
		e = usuarioEmpresaRepository.save(e);
		e.setSId(e.getId());
		usuarioEmpresaRepository.save(e);
		return e;
	}

	/**
	 * Devuelve el {@link UsuarioEmpresa} que coincida con los criterios pasados por
	 * parámetros.
	 * 
	 * TODO: Evaluar si realmente es necesario chequear el Usuario si ya se está
	 * pasando el id del {@link UsuarioEmpresa}. Es este el lugar correcto para
	 * hacer esa verificación??
	 * 
	 * @param idUsuario
	 * @param idUsuarioEmpresa
	 * @return
	 */
	public Optional<UsuarioEmpresa> obtenerUsuarioEmpresa(String idUsuario, String idUsuarioEmpresa) {
		Optional<UsuarioEmpresa> usuarioEmpresa = usuarioEmpresaRepository.findById(idUsuarioEmpresa);
		return Optional.ofNullable(usuarioEmpresa.isPresent() ? usuarioEmpresa.get() : null);
	}

	/**
	 * 
	 * Devuelve un {@link Optional}<{@link UsuarioEmpresa}> conteniendo el
	 * {@link UsuarioEmpresa} predeterminado para el {@link Usuario} pasado por
	 * parámetros. Se toma como criterio que el {@link UsuarioEmpresa} debe estar
	 * validado y activo. De existir varios en este estado se procede a seleccionar
	 * el primero ordenado de forma ascendente por Fecha de Creación
	 * 
	 * @param idUsuario
	 * @return
	 */
	public Optional<UsuarioEmpresa> obtenerUsuarioEmpresaPredeterminado(String idUsuario) {
		return this.obtenerUsuarioEmpresa(idUsuario, null, true, true);
	}

	/**
	 * Devuelve un {@link Optional}<{@link UsuarioEmpresa}> conteniendo un
	 * {@link UsuarioEmpresa} que cumpla con los requisitos establecidos por los
	 * parámetros pasados. De existir varios en este estado se procede a seleccionar
	 * el primero ordenado de forma ascendente por Fecha de Creación
	 * 
	 * @param idUsuario
	 * @param idEmpresa
	 * @param activo
	 * @param validado
	 * @return
	 */
	public Optional<UsuarioEmpresa> obtenerUsuarioEmpresa(String idUsuario, String idEmpresa, Boolean activo,
			Boolean validado) {
		List<UsuarioEmpresa> listaEmpresas = new ArrayList<>();
		Query query = new Query();
		if(idEmpresa != null) {
			listaEmpresas = mongoOperations.find(query.addCriteria(Criteria.where("susuario").is(idUsuario).andOperator(Criteria.where("sempresa").is(idEmpresa), Criteria.where("activo").is(true), Criteria.where("validado").is(true), Criteria.where("eliminado").is(false))), UsuarioEmpresa.class);
		}else{
			listaEmpresas = mongoOperations.find(query.addCriteria(Criteria.where("susuario").is(idUsuario).andOperator(Criteria.where("activo").is(true), Criteria.where("validado").is(true), Criteria.where("eliminado").is(false))), UsuarioEmpresa.class);
		}
		return Optional.ofNullable(listaEmpresas.size() > 0 ? listaEmpresas.get(0) : null);
	}

	/**
	 * Devuelve un {@link List}<{@link UsuarioEmpresa}> relacionado a la
	 * {@link Company} con el id pasado por parámetros. Se verfifica que el
	 * {@link Usuario} y el {@link UsuarioEmpresa} no estén eliminados
	 * 
	 * 
	 * @param idEmpresa
	 * @return
	 */
	public List<UsuarioEmpresa> obtenerUsuariosEmpresaPorEmpresa(String idEmpresa) throws ModelException {

		Company empresa = empresaRepository.findById(idEmpresa).get();
		List<UsuarioEmpresa> usuarioEmpresas = usuarioEmpresaRepository.findAllByEmpresa(empresa);
		return usuarioEmpresas;
	}

	/**
	 * Devuelve un {@link List}<{@link UsuarioEmpresa}> relacionado a la
	 * {@link Company} con el id pasado por parámetros. Se verfifica que el
	 * {@link Usuario} y el {@link UsuarioEmpresa} no estén eliminados
	 *
	 *
	 * @param
	 * @return
	 */
	public List<UsuarioEmpresa> obtenerUsuariosEmpresaPorUsuario(Usuario usuario) throws ModelException {
		return usuarioEmpresaRepository.findByUsuario(usuario);
	}

	public List<UsuarioEmpresa> getAll() {
		return usuarioEmpresaRepository.findAll();
	}

}