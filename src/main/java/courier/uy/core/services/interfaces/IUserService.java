package courier.uy.core.services.interfaces;

import java.util.List;
import java.util.Optional;

import courier.uy.core.resources.dto.UsuarioBasic;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.LoginResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import org.jose4j.lang.JoseException;

public interface IUserService {

	public Usuario Register(Usuario u) throws ServiceException;

	public LoginResponse Confirm(String code) throws ServiceException, JoseException;

	public Usuario InviteToBusiness(UsuarioPrincipal existing, Usuario newUser) throws ServiceException, ModelException;

	public void ModifyEmployee(UsuarioPrincipal existing, Usuario toEdit) throws ServiceException;

	public List<Usuario> GetAllFromBusiness(Company empresa) throws ServiceException;

	/**
	 * 
	 * Envía un <b>Mail de Confimación</b> nuevamente a un {@link Usuario} que no
	 * haya culminado su proceso de registro. En cualquier otro caso es ignorado el
	 * llamado a este método pues no se ejecuta ninguna acción.
	 * 
	 * @implNote En caso de que se haya inlcuido mal el correo inicialemente al
	 *           registrar el {@link Usuario} lo que debe realizarse es registrarlo
	 *           nuevamente. ESTE METODO NO SOBREESCRIBE NI CAMBIA EL CORREO
	 * 
	 * @param user
	 * @throws ServiceException
	 */
	void enviarEmailConfirmacion(Usuario user) throws ServiceException;

	public LoginResponse AcceptInvitation(String code) throws ServiceException, Exception;

	/**
	 * * Permite finalizar el <b>registro</b> de un {@link Usuario} en el sistema.
	 * Para determinar si un {@link Usuario} está pendiente de culminar el
	 * <b>registro</b> se verifica que su contraseña y usuario estén en NULL. Con
	 * esto será suficiente para determinar que este {@link Usuario} no está
	 * completamente <b>registrado</b> en el Sistema
	 * 
	 * @param usuario
	 * @throws ServiceException
	 */
	public Usuario FinishRegister(Usuario usuario) throws ServiceException;

	public void SendPasswordReset(UsuarioPrincipal existingUser, Usuario toReset) throws ServiceException, Exception;

	public LoginResponse ChangePassword(String code, String contrasena) throws ServiceException, Exception;

	public void SendPasswordReset(UsuarioBasic usuario) throws ServiceException, Exception;

	public void ReSendInvitation(UsuarioPrincipal existingUser, Usuario newUser) throws ServiceException, Exception;

	public void CancelInvitation(UsuarioPrincipal existingUser, Usuario newUser) throws ServiceException, Exception;

	public void Modify(UsuarioPrincipal existingUser, Usuario usuario) throws ServiceException;

	void RemoveFromBusiness(UsuarioPrincipal existingUser, String id) throws ServiceException;

	public Usuario InviteAdmin(UsuarioPrincipal existingUser, Usuario newUser) throws ServiceException;

	public List<Usuario> GetSystemAdmins();

	public void ReSendInvitationAdmin(UsuarioPrincipal existingUser, Usuario newUser) throws ServiceException, Exception;

	public void CancelInvitationAdmin(UsuarioPrincipal existingUser, Usuario newUser) throws ServiceException, Exception;

	/**
	 * Devuelve un {@link Optional<Usuario>} representando el Usuario encontrado o
	 * en caso de no existir devuelve el Opcional vacío. Para definir que existe el
	 * {@link Usuario} se toma como criterios los siguientes puntos:
	 * 
	 * 1 - Que exista el {@link Usuario} 2 - Que el {@link Usuario} pertenezca a la
	 * misma {@link Company} del {@link Usuario} logueado
	 * 
	 * @param idUsuario El Id de tipo {@link Long} del @{link Usuario}
	 * @param ue        El objeto de tipo {@link UsuarioEmpresa} perteneciente al
	 *                  {@link Usuario} logueado
	 * @return {@link Optional} de tipo {@link Usuario}
	 * @throws ServiceException
	 * @throws Exception
	 */
	public Optional<Usuario> obtenerDatosUsuarioEmpresa(String idUsuario, UsuarioEmpresa ue)
			throws ServiceException, Exception;

	void DeleteAdmin(String id) throws ServiceException;
}
