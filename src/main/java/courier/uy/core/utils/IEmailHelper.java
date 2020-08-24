package courier.uy.core.utils;

import courier.uy.core.entity.Company;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.entity.Usuario;

public interface IEmailHelper {
	public void SendRegisterFromAdminEmail(String email,String nombreEmpresa, String nombreAdmin, String codigo) throws ServiceException;
	public void SendNewBusinessFromAdminEmail(String email,String nombre,String nombreEmpresa, String nombreAdmin, String codigo) throws ServiceException;
	public void SendRegisterEmail(String email, String nombre, String codigo) throws ServiceException;
	public void SendInvitationEmail(String email, String nombreEmpresa, String codigo) throws ServiceException;
	public void SendPasswordReset(String email, String nombre, String codigo) throws ServiceException;
	public void SendSystemAdminInvitation(String email,String nombreAdmin, String codigo) throws ServiceException;
	public void SendRemoveFromBusiness(Company business, Usuario admin, Usuario toRemove) throws ServiceException;
	public void SendExcelErrorsEmail(String email, String nombreEmpresa, String codigo) throws ServiceException;
}
