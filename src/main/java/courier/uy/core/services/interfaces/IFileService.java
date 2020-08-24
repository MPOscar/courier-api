package courier.uy.core.services.interfaces;

import java.io.IOException;
import java.util.List;

import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ServiceException;


import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

	public String SaveFileForProduct(MultipartFile file, String productId) throws ServiceException, IOException;

	public String SaveFileForBussisnes(MultipartFile file, UsuarioEmpresa usuarioEmpresa) throws ServiceException, IOException;

	public  List<String> SaveFileByRutBussisnes(Long rut, MultipartFile[] files) throws ServiceException, IOException;

}

