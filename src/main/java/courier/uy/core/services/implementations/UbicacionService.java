package courier.uy.core.services.implementations;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.MultivaluedMap;

import courier.uy.core.db.UbicacionesDAO;
import courier.uy.core.entity.Ubicacion;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.services.interfaces.IUbicacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UbicacionService implements IUbicacionService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UbicacionService.class);

	@Autowired
	private UbicacionesDAO ubicacionesRepository;

	public UbicacionService(UbicacionesDAO ubicacionesRepository) {
		this.ubicacionesRepository = ubicacionesRepository;
	}

	/**
	 * Chequea que la Ubicación pasada por parámetro pertenezca a la Empresa a la
	 * que se hace referencia en el objeto UsuarioEmpresa.
	 * 
	 * @param ubicacion
	 * @param usuarioEmpresa
	 * @return Objeto Ubicacion encontrado en la BD o excepción de no existir.
	 * @throws ServiceException
	 */
	private Ubicacion perteneceEmpresa(Ubicacion ubicacion, UsuarioEmpresa usuarioEmpresa, Boolean eliminado)
			throws ServiceException {
		Optional<Ubicacion> optional = ubicacionesRepository.perteneceEmpresa(ubicacion, usuarioEmpresa.getEmpresa(),
				eliminado);
		if (!optional.isPresent())
			throw new ServiceException(
					"La Ubicación no pertenece a la Empresa actual o no existe. Verifique haber seleccionado la Empresa correcta");
		return optional.get();
	}

	@Override
	public Ubicacion upsert(Ubicacion ubicacion, UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		if (ubicacion.getEmpresa() != null) {
			if (!usuarioEmpresa.getUsuario().esAdministradorSistema()
					&& ubicacion.getEmpresa().getId() != usuarioEmpresa.getEmpresa().getId())
				throw new ServiceException("La Empresa a la que hace referencia no es la activa en el token actual");
		} else {
			ubicacion.setEmpresa(usuarioEmpresa.getEmpresa());
		}
		return ubicacionesRepository.upsert(ubicacion);
	}

	@Override
	public Ubicacion insert(Ubicacion ubicacion, UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		ubicacion.setEmpresa(usuarioEmpresa.getEmpresa());
		return ubicacionesRepository.insert(ubicacion);
	}

	@Override
	public Ubicacion delete(Ubicacion ubicacion, UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		if (!usuarioEmpresa.getUsuario().esAdministradorSistema())
			ubicacion = perteneceEmpresa(ubicacion, usuarioEmpresa, false);
		return ubicacionesRepository.delete(ubicacion);
	}

	@Override
	public List<Ubicacion> getAll(MultivaluedMap<String, String> parametros, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException {
		// FIXME Revisar implementación apropiada de los permisos por Roles para
		// permitir al Administrador ver Todo
		return ubicacionesRepository.getAll(usuarioEmpresa);
	}

	@Override
	public Ubicacion getUbicacion(String codigo, UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		Optional<Ubicacion> optional = ubicacionesRepository.findByKey("codigo", codigo, usuarioEmpresa);

		if (!optional.isPresent()) {
			throw new ServiceException("La Ubicación solicitada no existe");
		}

		return optional.get();
	}

}
