package courier.uy.core.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import courier.uy.core.db.ParamsDAO;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;

public class NativeEmailHelper implements IEmailHelper {
	@SuppressWarnings("unused")
	@Autowired
	private ParamsDAO parameterRepo;


	private MandrillConfiguration mandrillConfig;

	public NativeEmailHelper(ParamsDAO params, MandrillConfiguration mandrillConfig) {
		this.parameterRepo = params;
		this.mandrillConfig = mandrillConfig;
	}

	@Override
	public void SendRegisterEmail(String email, String nombre, String codigo) throws ServiceException {
		final String username = "matigru@gmail.com";// parameterRepo.findByNombre("email").getValor();
		final String password = "Size1794";// parameterRepo.findByNombre("password").getValor();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Bienvenido al catálogo de Rondanet");
			message.setText("Hola " + nombre + ","
					+ "\n\n Para registrarte al sistema de rondanet debes clickear el siguiente link: "
					+ "http://test.catalogo.rondanet.com/confirm-user/" + codigo);// http://127.0.0.1:8080/usuarios/confirmar/

			Transport.send(message);
		} catch (MessagingException e) {
			throw new ServiceException("email exception " + e.getMessage());
		}
	}

	@Override
	public void SendInvitationEmail(String email, String nombreEmpresa, String codigo) throws ServiceException {
		final String username = "matigru@gmail.com";// parameterRepo.findByNombre("email").getValor();
		final String password = "Size1794";// parameterRepo.findByNombre("password").getValor();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Invitación de " + nombreEmpresa + " al catálogo de Rondanet");
			message.setText("Hola " + email + "," + "\n\n La empresa " + nombreEmpresa
					+ " te invitó al catálogo de Rondanet. Para acceder debes clickear el siguiente link: "
					+ this.mandrillConfig.getUrl() + "/accept-invitation/" + codigo);// http://127.0.0.1:8080/usuarios/confirmar/

			Transport.send(message);
		} catch (MessagingException e) {
			throw new ServiceException("email exception " + e.getMessage());
		}
	}

	@Override
	public void SendPasswordReset(String email, String nombre, String codigo) throws ServiceException {
		final String username = "matigru@gmail.com";// parameterRepo.findByNombre("email").getValor();
		final String password = "Size1794";// parameterRepo.findByNombre("password").getValor();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Reseteo de contraseña para el catálogo de Rondanet");
			message.setText("Hola " + nombre + ","
					+ "\n\n Recibimos una solicitud de reseteo de contraseña. Si fue pedido por usted entre al siguiente link: "
					+ "http://test.catalogo.rondanet.com/password-reset/" + codigo);// localhost:4200

			Transport.send(message);
		} catch (MessagingException e) {
			throw new ServiceException("email exception " + e.getMessage());
		}
	}

	@Override
	public void SendSystemAdminInvitation(String email, String nombreAdminAnterior, String codigo)
			throws ServiceException {
		final String username = "matigru@gmail.com";// parameterRepo.findByNombre("email").getValor();
		final String password = "Size1794";// parameterRepo.findByNombre("password").getValor();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Invitación de " + nombreAdminAnterior + " al catálogo de Rondanet");
			message.setText("Hola " + email + "," + "\n\n " + nombreAdminAnterior
					+ " te invitó a la administración del catálogo de Rondanet. Para acceder debes clickear el siguiente link: "
					+ this.mandrillConfig.getUrl() + "/accept-invitation/" + codigo);// http://127.0.0.1:8080/usuarios/confirmar/

			Transport.send(message);
		} catch (MessagingException e) {
			throw new ServiceException("email exception " + e.getMessage());
		}
	}

	@Override
	public void SendRegisterFromAdminEmail(String email, String nombreEmpresa, String nombreAdmin, String codigo)
			throws ServiceException {
		final String username = "matigru@gmail.com";// parameterRepo.findByNombre("email").getValor();
		final String password = "Size1794";// parameterRepo.findByNombre("password").getValor();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Bienvenido al catálogo de Rondanet");
			message.setText("Hola " + email + "," + "\n\n El administrador " + nombreAdmin + " creó la empresa "
					+ nombreEmpresa + " en el catálogo de rondanet y lo registró como administrador de la misma."
					+ "\n\n Para acceder debes clickear el siguiente link: "
					+ "http://test.catalogo.rondanet.com/confirm-user/" + codigo);// http://127.0.0.1:8080/usuarios/confirmar/

			Transport.send(message);
		} catch (MessagingException e) {
			throw new ServiceException("email exception " + e.getMessage());
		}

	}

	@Override
	public void SendNewBusinessFromAdminEmail(String email, String nombre, String nombreEmpresa, String nombreAdmin,
			String codigo) throws ServiceException {
		final String username = "matigru@gmail.com";// parameterRepo.findByNombre("email").getValor();
		final String password = "Size1794";// parameterRepo.findByNombre("password").getValor();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Invitación de " + nombreEmpresa + " al catálogo de Rondanet");
			message.setText("Hola " + nombre + "," + "\n\n El administrador " + nombreAdmin + " creó la empresa "
					+ nombreEmpresa + " en el catálogo de rondanet y lo registró como administrador de la misma."
					+ "\n\n Para acceder debes clickear el siguiente link: " + this.mandrillConfig.getUrl()
					+ "/accept-invitation/" + codigo);// http://127.0.0.1:8080/usuarios/confirmar/

			Transport.send(message);
		} catch (MessagingException e) {
			throw new ServiceException("email exception " + e.getMessage());
		}

	}

	@Override
	public void SendRemoveFromBusiness(Company business, Usuario admin, Usuario toRemove) throws ServiceException {
		final String username = "matigru@gmail.com";// parameterRepo.findByNombre("email").getValor();
		final String password = "Size1794";// parameterRepo.findByNombre("password").getValor();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toRemove.getEmail()));
			message.setSubject("Desvinculación de " + business.getNombre());
			message.setText("Hola " + toRemove.getNombre() + "," + "\n\n El administrador " + admin.getNombre() + " "
					+ admin.getApellido() + " te desvinculó de la empresa " + business.getNombre()
					+ " en el catálogo de rondanet.");

			Transport.send(message);
		} catch (MessagingException e) {
			throw new ServiceException("email exception " + e.getMessage());
		}
	}

	@Override
	public void SendExcelErrorsEmail(String email, String nombreEmpresa, String excelConErrores)
			throws ServiceException {
		final String username = "matigru@gmail.com";// parameterRepo.findByNombre("email").getValor();
		final String password = "Size1794";// parameterRepo.findByNombre("password").getValor();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Invitación de " + nombreEmpresa + " al catálogo de Rondanet");
			message.setText("Hola " + email + "," //$NON-NLS-1$ //$NON-NLS-2$
					+ "en el link puede encontrar un archivo excel con los errores detectados durante la carga de produtos. </br>"
					+ excelConErrores);

			Transport.send(message);
		} catch (MessagingException e) {
			throw new ServiceException("email exception " + e.getMessage());
		}
	}

}
