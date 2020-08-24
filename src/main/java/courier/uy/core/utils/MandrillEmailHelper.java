package courier.uy.core.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;

import courier.uy.CourierConfiguration;
import courier.uy.core.db.ParamsDAO;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public class MandrillEmailHelper implements IEmailHelper {

    MandrillApi mandrillApi; // $NON-NLS-1$

    @SuppressWarnings("unused")
    @Autowired
    private ParamsDAO parameterRepo;
    @SuppressWarnings("unused")
    @Autowired
    private CourierConfiguration configuration;
    @SuppressWarnings("unused")
    @Autowired
    private MandrillConfiguration mandrillConfig;

    public MandrillEmailHelper(ParamsDAO params, CourierConfiguration configuration) {
        parameterRepo = params;
        this.configuration = configuration;
        this.mandrillConfig = configuration.getConfiguracionDespliegue().getMandrill();
        this.mandrillApi = new MandrillApi(this.mandrillConfig.getMandrillApiKey());
    }

    @Override
    public void SendRegisterFromAdminEmail(String email, String nombreEmpresa, String nombreAdmin, String codigo)
            throws ServiceException {
        try {
            MandrillMessage message = new MandrillMessage();
            message.setSubject("Bienvenido al catálogo de Rondanet"); //$NON-NLS-1$
            message.setHtml("Hola " + email + "," //$NON-NLS-1$ //$NON-NLS-2$
                    + "\n\n El administrador " + nombreAdmin + " creó la empresa " + nombreEmpresa //$NON-NLS-1$ //$NON-NLS-2$
                    + " en el catálogo de rondanet y lo registró como administrador de la misma." //$NON-NLS-1$
                    + "\n\n Para acceder debes clickear el siguiente link: " //$NON-NLS-1$
                    + this.mandrillConfig.getUrl() + "/confirm-user/" + codigo); //$NON-NLS-1$ //$NON-NLS-2$
            message.setAutoText(true);
            message.setAutoHtml(true);
            message.setFromEmail(this.mandrillConfig.getFromEmail()); // $NON-NLS-1$
            message.setFromName("Rondanet"); //$NON-NLS-1$

            ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
            recipient.setEmail(email);
            recipient.setName(nombreEmpresa);
            recipients.add(recipient);

            message.setTo(recipients);
            message.setPreserveRecipients(false);

            mandrillApi.messages().send(message, false);
        } catch (final IOException e) {
            throw new ServiceException(e.getMessage());
        } catch (final MandrillApiError e) {
            throw new ServiceException(e.getMandrillErrorAsJson());
        }
    }

    @Override
    public void SendNewBusinessFromAdminEmail(String email, String nombre, String nombreEmpresa, String nombreAdmin,
            String codigo) throws ServiceException {
        try {
            MandrillMessage message = new MandrillMessage();
            message.setSubject("Invitación de " + nombreEmpresa + " al catálogo de Rondanet"); //$NON-NLS-1$ //$NON-NLS-2$
            message.setHtml("Hola " + nombre + "," //$NON-NLS-1$ //$NON-NLS-2$
                    + "\n\n El administrador " + nombreAdmin + " creó la empresa " + nombreEmpresa //$NON-NLS-1$ //$NON-NLS-2$
                    + " en el catálogo de rondanet y lo registró como administrador de la misma." //$NON-NLS-1$
                    + "\n\n Para acceder debes clickear el siguiente link: " //$NON-NLS-1$
                    + this.mandrillConfig.getUrl() + "/accept-invitation/" + codigo); //$NON-NLS-1$ //$NON-NLS-2$
            message.setAutoText(true);
            message.setAutoHtml(true);
            message.setFromEmail(this.mandrillConfig.getFromEmail()); // $NON-NLS-1$
            message.setFromName("Rondanet"); //$NON-NLS-1$

            ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
            recipient.setEmail(email);
            recipient.setName(nombreEmpresa);
            recipients.add(recipient);

            message.setTo(recipients);
            message.setPreserveRecipients(false);

            mandrillApi.messages().send(message, false);
        } catch (final IOException e) {
            throw new ServiceException(e.getMessage());
        } catch (final MandrillApiError e) {
            throw new ServiceException(e.getMandrillErrorAsJson());
        }
    }

    @Override
    public void SendRegisterEmail(String email, String nombre, String codigo) throws ServiceException {
        try {
            Configuration config = new Configuration();
            Template template = config.getTemplate("registro-email-template.ftl");
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("nombre", nombre);
            String link = this.mandrillConfig.getUrl() + "/confirm-user/" + codigo;
            model.put("link", link);
            // model.put("texto", texto)
            Writer out = new StringWriter();
            String htmlString = "";
            try {
                template.process(model, out);
                htmlString = out.toString();
            } catch (TemplateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            MandrillMessage message = new MandrillMessage();
            message.setSubject("Bienvenido al catálogo de Rondanet"); //$NON-NLS-1$
            message.setHtml(htmlString);
            // message.setHtml("Hola " + nombre + "," //$NON-NLS-1$ //$NON-NLS-2$
            // + "\n\n Para registrarte al sistema de rondanet debes clickear el siguiente
            // link: " //$NON-NLS-1$
            // + this.mandrillConfig.getUrl() + "/confirm-user/" + codigo + "\n\n " +
            // htmlString); // $NON-NLS-1$
            // //$NON-NLS-2$
            message.setAutoText(true);
            message.setAutoHtml(true);
            message.setFromEmail(this.mandrillConfig.getFromEmail()); // $NON-NLS-1$
            message.setFromName("Rondanet"); //$NON-NLS-1$

            ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
            recipient.setEmail(email);
            recipient.setName(nombre);
            recipients.add(recipient);

            message.setTo(recipients);
            message.setPreserveRecipients(false);

            mandrillApi.messages().send(message, false);
        } catch (final IOException e) {
            throw new ServiceException(e.getMessage());
        } catch (final MandrillApiError e) {
            throw new ServiceException(e.getMandrillErrorAsJson());
        }
    }

    @Override
    public void SendInvitationEmail(String email, String nombreEmpresa, String codigo) throws ServiceException {
        try {
            Configuration config = new Configuration();
            Template template = config.getTemplate("invitacion-email-template.ftl");
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("empresa", nombreEmpresa);
            model.put("link", this.mandrillConfig.getUrl() + "/accept-invitation/" + codigo);
            // model.put("texto", texto)
            Writer out = new StringWriter();
            String htmlString = "";
            try {
                template.process(model, out);
                htmlString = out.toString();
            } catch (TemplateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            MandrillMessage message = new MandrillMessage();
            message.setSubject("Invitación de " + nombreEmpresa + " al catálogo de Rondanet"); //$NON-NLS-1$ //$NON-NLS-2$
            message.setHtml(htmlString);
            // message.setHtml("Hola " + email + "," //$NON-NLS-1$ //$NON-NLS-2$
            // + "\n\n La empresa " + nombreEmpresa //$NON-NLS-1$
            // + " te invitó al catálogo de Rondanet. Para acceder debes clickear el
            // siguiente link: " //$NON-NLS-1$
            // + this.mandrillConfig.getUrl() + "/accept-invitation/" + codigo);
            // //$NON-NLS-1$ //$NON-NLS-2$
            message.setAutoText(true);
            message.setAutoHtml(true);
            message.setFromEmail(this.mandrillConfig.getFromEmail()); // $NON-NLS-1$
            message.setFromName("Rondanet"); //$NON-NLS-1$

            ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
            recipient.setEmail(email);
            recipient.setName(nombreEmpresa);
            recipients.add(recipient);

            message.setTo(recipients);
            message.setPreserveRecipients(false);

            mandrillApi.messages().send(message, false);
        } catch (final IOException e) {
            throw new ServiceException(e.getMessage());
        } catch (final MandrillApiError e) {
            throw new ServiceException(e.getMandrillErrorAsJson());
        }
    }

    @Override
    public void SendPasswordReset(String email, String nombre, String codigo) throws ServiceException {
        try {

            Configuration config = new Configuration();
            Template template = config.getTemplate("reset-password-email-template.ftl");
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("nombre", nombre);
            model.put("link", this.mandrillConfig.getUrl() + "/password-reset/" + codigo);
            // model.put("texto", texto)
            Writer out = new StringWriter();
            String htmlString = "";
            try {
                template.process(model, out);
                htmlString = out.toString();
            } catch (TemplateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            MandrillMessage message = new MandrillMessage();
            message.setSubject("Reseteo de contraseña para el catálogo de Rondanet"); //$NON-NLS-1$
            message.setHtml(htmlString);
            /*
             * message.setHtml("Hola " + nombre + "," //$NON-NLS-1$ //$NON-NLS-2$ +
             * "\n\n Recibimos una solicitud de reseteo de contraseña. Si fue pedido por usted entre al siguiente link: "
             * //$NON-NLS-1$ + this.mandrillConfig.getUrl() + "/password-reset/" + codigo);
             * //$NON-NLS-1$ //$NON-NLS-2$
             */
            message.setAutoText(true);
            message.setAutoHtml(true);
            message.setFromEmail(this.mandrillConfig.getFromEmail()); // $NON-NLS-1$
            message.setFromName("Rondanet"); //$NON-NLS-1$

            ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
            recipient.setEmail(email);
            recipient.setName(nombre);
            recipients.add(recipient);

            message.setTo(recipients);
            message.setPreserveRecipients(false);

            mandrillApi.messages().send(message, false);
        } catch (final IOException e) {
            throw new ServiceException(e.getMessage());
        } catch (final MandrillApiError e) {
            throw new ServiceException(e.getMandrillErrorAsJson());
        }
    }

    @Override
    public void SendSystemAdminInvitation(String email, String nombreAdmin, String codigo) throws ServiceException {
        try {
            MandrillMessage message = new MandrillMessage();
            message.setSubject("Invitación de " + nombreAdmin + " al catálogo de Rondanet"); //$NON-NLS-1$ //$NON-NLS-2$
            message.setHtml("Hola " + email + "," //$NON-NLS-1$ //$NON-NLS-2$
                    + "\n\n " + nombreAdmin //$NON-NLS-1$
                    + " te invitó a la administración del catálogo de Rondanet. Para acceder debes clickear el siguiente link: " //$NON-NLS-1$
                    + this.mandrillConfig.getUrl() + "/accept-invitation/" + codigo); //$NON-NLS-1$ //$NON-NLS-2$
            message.setAutoText(true);
            message.setAutoHtml(true);
            message.setFromEmail(this.mandrillConfig.getFromEmail()); // $NON-NLS-1$
            message.setFromName("Rondanet"); //$NON-NLS-1$

            ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
            recipient.setEmail(email);
            recipient.setName(email);
            recipients.add(recipient);

            message.setTo(recipients);
            message.setPreserveRecipients(false);

            mandrillApi.messages().send(message, false);
        } catch (final IOException e) {
            throw new ServiceException(e.getMessage());
        } catch (final MandrillApiError e) {
            throw new ServiceException(e.getMandrillErrorAsJson());
        }
    }

    @Override
    public void SendRemoveFromBusiness(Company business, Usuario admin, Usuario toRemove) throws ServiceException {
        try {
            MandrillMessage message = new MandrillMessage();
            message.setSubject("Desvinculación de " + business.getNombre()); //$NON-NLS-1$
            message.setHtml("Hola " + (toRemove.getNombre() != null ? toRemove.getNombre() : "") + "," //$NON-NLS-1$ //$NON-NLS-2$
                    + "\n\n El administrador " + admin.getNombre() + " " + admin.getApellido() //$NON-NLS-1$ //$NON-NLS-2$
                    + " te desvinculó de la empresa " + business.getNombre() + " en el catálogo de rondanet."); //$NON-NLS-1$ //$NON-NLS-2$
            message.setAutoText(true);
            message.setAutoHtml(true);
            message.setFromEmail(this.mandrillConfig.getFromEmail()); // $NON-NLS-1$
            message.setFromName("Rondanet"); //$NON-NLS-1$

            ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
            recipient.setEmail(toRemove.getEmail());
            recipient.setName(toRemove.getNombre() + " " + toRemove.getApellido()); //$NON-NLS-1$
            recipients.add(recipient);

            message.setTo(recipients);
            message.setPreserveRecipients(false);

            mandrillApi.messages().send(message, false);
        } catch (final IOException e) {
            throw new ServiceException(e.getMessage());
        } catch (final MandrillApiError e) {
            throw new ServiceException(e.getMandrillErrorAsJson());
        }
    }

    @Override
    public void SendExcelErrorsEmail(String email, String nombreEmpresa, String excelConErroresLink)
            throws ServiceException {
        try {
            MandrillMessage message = new MandrillMessage();
            message.setSubject("Errores de la carga de produtos al catálogo de Rondanet"); //$NON-NLS-1$ //$NON-NLS-2$
            message.setHtml("Hola " + email + "," //$NON-NLS-1$ //$NON-NLS-2$
                    + "en el siguiente enlace de descarga puede encontrar un archivo excel con los errores detectados durante la carga de produtos al catalogo. </br>"
                    + "https://s3.us-east-2.amazonaws.com/" //$NON-NLS-1$
                    + "/" + excelConErroresLink); //$NON-NLS-1$
            message.setAutoText(true);
            message.setAutoHtml(true);
            message.setFromEmail(this.mandrillConfig.getFromEmail()); // $NON-NLS-1$
            message.setFromName("Rondanet"); //$NON-NLS-1$

            ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
            recipient.setEmail(email);
            recipient.setName(nombreEmpresa);
            recipients.add(recipient);

            message.setTo(recipients);
            message.setPreserveRecipients(false);

            mandrillApi.messages().send(message, false);
        } catch (final IOException e) {
            throw new ServiceException(e.getMessage());
        } catch (final MandrillApiError e) {
            throw new ServiceException(e.getMandrillErrorAsJson());
        }
    }
}
