package courier.uy.core.services.interfaces;

import courier.uy.api.dto.ProductoLaboratorioDTO;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ServiceException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public interface ILaboratorioService {
    public HashMap<String, String> actualizarProductos(InputStream inputStream, UsuarioEmpresa usuarioEmpresa) throws ServiceException;
    public ProductoLaboratorioDTO getProductoLaboratorio(UsuarioEmpresa usuarioEmpresa, String gtin);
    public List<ProductoLaboratorioDTO> getAllProductoLaboratorio(UsuarioEmpresa usuarioEmpresa);
}
